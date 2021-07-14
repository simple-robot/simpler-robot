/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */
@file:JvmName("CoreListenerManagers")

package love.forte.simbot.core.listener

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import love.forte.common.collections.concurrentSortedQueueOf
import love.forte.common.ioc.annotation.SpareBeans
import love.forte.simbot.LogAble
import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.SimbotInternalApi
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.sender.DefaultMsgSenderFactories
import love.forte.simbot.api.sender.MsgSender
import love.forte.simbot.api.sender.MsgSenderFactories
import love.forte.simbot.bot.BotManager
import love.forte.simbot.core.SimbotContext
import love.forte.simbot.core.SimbotContextClosedHandle
import love.forte.simbot.core.intercept.EmptyListenerInterceptorChain
import love.forte.simbot.core.intercept.EmptyMsgInterceptChain
import love.forte.simbot.core.listener.ListenerFunctionGroups.Companion.isEmpty
import love.forte.simbot.core.listener.ListenerFunctionGroups.Companion.isNotEmpty
import love.forte.simbot.core.listener.ListenerFunctionGroups.Companion.marge
import love.forte.simbot.dispatcher.EventDispatcherFactory
import love.forte.simbot.exception.ExceptionHandleContext
import love.forte.simbot.exception.ExceptionProcessor
import love.forte.simbot.filter.AtDetectionFactory
import love.forte.simbot.listener.*
import love.forte.simbot.processor.ListenResultProcessorManager
import love.forte.simbot.processor.context
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

private val ListenerInvokerComparable: Comparator<ListenerInvoker> = Comparator { f1, f2 -> f1.function.priority.compareTo(f2.function.priority) }


/**
 * 监听函数自排序队列。
 */
// private fun listenerFunctionQueue(vararg func: ListenerFunction): Queue<ListenerFunction> =
//     concurrentSortedQueueOf(ListenerFunctionComparable, *func)

/**
 * 监听函数自排序队列。
 */
private fun listenerInvokerQueue(vararg func: ListenerFunction): Queue<ListenerInvoker> =
    concurrentSortedQueueOf(ListenerInvokerComparable, *func.map(::ListenerInvokerImpl).toTypedArray())

/**
 * 监听函数自排序队列。
 */
private fun listenerInvokerQueue(vararg invoker: ListenerInvoker): Queue<ListenerInvoker> =
    concurrentSortedQueueOf(ListenerInvokerComparable, *invoker)


private data class ListenerFunctionGroups(
    val normal: Collection<ListenerInvoker>,
    val spare: Collection<ListenerInvoker>,
) {
    companion object {
        val empty = ListenerFunctionGroups(emptyList(), emptyList())
        fun ListenerFunctionGroups.marge(): Collection<ListenerInvoker> =
            if (isEmpty()) emptyList() else normal + spare

        fun ListenerFunctionGroups.isEmpty(): Boolean = normal.isEmpty() && spare.isEmpty()
        fun ListenerFunctionGroups.isNotEmpty(): Boolean = !isEmpty()
    }
}


/**
 * 核心对于 [ListenerManager] 的实现。
 *
 * @property atDetectionFactory at检测器工厂
 * @property exceptionManager 异常处理器
 *
 */
@OptIn(SimbotInternalApi::class, ExperimentalCoroutinesApi::class)
@SpareBeans("coreListenerManager")
public class CoreListenerManager @OptIn(SimbotExperimentalApi::class) constructor(
    eventDispatcherFactory: EventDispatcherFactory,
    private val listenerGroupManager: ListenerGroupManager,

    private val atDetectionFactory: AtDetectionFactory,
    private val exceptionManager: ExceptionProcessor,

    // private val msgInterceptData: MsgInterceptData,
    private val msgInterceptContextFactory: MsgInterceptContextFactory,
    private val msgInterceptChainFactory: MsgInterceptChainFactory,

    // private val listenerInterceptData: ListenerInterceptData,
    private val listenerInterceptContextFactory: ListenerInterceptContextFactory,
    private val listenerInterceptChainFactory: ListenerInterceptChainFactory,

    private val listenerContextFactory: ListenerContextFactory,
    // private val contextMapFactory: ContextMapFactory,

    private val msgSenderFactories: MsgSenderFactories,
    private val defMsgSenderFactories: DefaultMsgSenderFactories,

    private val botManager: BotManager,

    private val resultProcessorManager: ListenResultProcessorManager,

    ) : ListenerManager, ListenerRegistrar, SimbotContextClosedHandle {

    private val eventDispatcher = eventDispatcherFactory.dispatcher
    private val collectScope = CoroutineScope(Dispatchers.Default)
    private val eventCoroutineScope = CoroutineScope(
        eventDispatcher +
                CoroutineName("CoreMsgProcessor-Event")
    )

    private lateinit var producerScope: ProducerScope<MsgGet>

    init {
        val flow = channelFlow<MsgGet> {
            producerScope = this
            awaitClose {
                // close.
                logger.info("Core msgGet processor closed.")
            }
        }

        flow.buffer(1024)
            .filter { contains(it::class.java) }
            .also {
                collectScope.launch {
                    it.collect {
                        eventCoroutineScope.launch { onMsg1(it) }
                    }
                }
            }


    }


    override fun simbotClose(context: SimbotContext) {
        producerScope.close()
        collectScope.cancel()
        eventCoroutineScope.cancel()
    }


    private val logger: Logger = LoggerFactory.getLogger(CoreListenerManager::class.java)

    /**
     * 监听函数集合，通过对应的监听类型进行分类。
     *
     * 此为主集合，所有的监听函数均存在于此。
     */
    private val mainListenerFunctionMap: MutableMap<Class<out MsgGet>, Queue<ListenerInvoker>> = ConcurrentHashMap()
    // private val mainListenerFunctionMap: MutableMap<Class<out MsgGet>, Queue<ListenerFunction>> = ConcurrentHashMap()


    private val listenerFunctionIdMap: MutableMap<String, ListenerInvoker> = ConcurrentHashMap()

    /**
     * 监听函数缓冲区，对后续出现的消息类型进行记录并缓存。
     * 当 [register] 了新的监听函数后对应相关类型将会被清理。
     */
    private val cacheListenerFunctionMap: MutableMap<Class<out MsgGet>, ListenerFunctionGroups> = ConcurrentHashMap()
    // private val cacheListenerFunctionMap: MutableMap<Class<out MsgGet>, ListenerFunctionGroups> = ConcurrentHashMap()


    /**
     * 注册一个 [监听函数][ListenerFunction]。
     *
     * 每次注册一个新的监听函数的时候，会刷新内置的 **全部** 缓存，会一定程度上影响到其他应用。
     *
     */
    @Synchronized
    override fun register(listenerFunction: ListenerFunction) {
        // 获取其监听类型，并作为key存入map
        val listenTypes = listenerFunction.listenTypes

        val id = listenerFunction.id

        val invoker = ListenerInvokerImpl(listenerFunction)

        listenerFunctionIdMap.merge(id, invoker) { _, _ ->
            throw IllegalStateException("Listener id $id was already exists.")
        }

        listenTypes.forEach { listenType ->
            // merge into map.
            mainListenerFunctionMap.merge(listenType, listenerInvokerQueue(invoker)) { oldValue, value ->
                oldValue.apply { addAll(value) }
            }

            // // groups.
            // val groups = listenerFunction.groups
            // if (groups.isEmpty()) {
            //     noGroupListenerGroup.add(listenerFunction)
            // } else {
            //     groups.forEach { g ->
            //         listenerGroups.compute(g) { g0, v ->
            //             v?.apply { add(listenerFunction) } ?: MutableListenerGroup(g0, mutableListOf(listenerFunction))
            //         }
            //     }
            // }

            // clear cache map.
            // 清除缓存
            if (cacheListenerFunctionMap.isNotEmpty()) {
                cacheListenerFunctionMap.clear()
                logger.debug("Listener cache cleaned.")
            }


        }
    }

    /**
     * 通过内部的事件调度器触发事件。
     */
    override suspend fun onMsg(msgGet: MsgGet) {
        producerScope.send(msgGet)
    }


    /**
     * 接收到消息监听并进行处理。
     */
    @OptIn(SimbotExperimentalApi::class)
    private suspend fun onMsg1(msgGet: MsgGet) {
        try {
            // not empty, intercept.
            // val context: ListenerContext = getContext(msgGet)
            val context: ListenerContext = listenerContextFactory.getListenerContext(msgGet)
            //
            // // val context: ListenerContext = getContext(msgGet)
            //
            // // 构建一个消息拦截器context
            // val msgContext = msgInterceptContextFactory.getMsgInterceptContext(msgGet, context)
            // val msgChain = msgInterceptChainFactory.getInterceptorChain(msgContext)

            var msgInterceptContext: MsgInterceptContext? = null

            val msgChain = msgInterceptChainFactory.getInterceptorChainOnNonEmpty {
                // 构建一个消息拦截器context
                msgInterceptContextFactory.getMsgInterceptContext(msgGet, context).also {
                    msgInterceptContext = it
                }
            } ?: EmptyMsgInterceptChain


            // 如果被拦截, 返回默认值
            if (msgChain.intercept().prevent) {
                return
                // ListenResult
            }

            // 筛选并执行监听函数
            onMsg0(msgInterceptContext?.msgGet ?: msgGet, context)
        } catch (e: Throwable) {
            logger.error("Some unexpected errors occurred in the execution of the listener: ${e.localizedMessage}", e)
            // ListenResult
        }
    }


    /**
     * 判断当前是否存在某个类型的监听函数。
     */
    override fun <T : MsgGet> contains(type: Class<out T>): Boolean {
        return getListenerFunctions(type, true).isNotEmpty()
    }


    /**
     * 筛选监听函数
     */
    @OptIn(SimbotExperimentalApi::class)
    private suspend fun onMsg0(msgGet: MsgGet, context: ListenerContext) {
        val botCode = msgGet.botInfo.botCode
        val eventLogger = if (msgGet is LogAble) msgGet.log else logger

        val funcs = getListenerFunctions(msgGet.javaClass, true)
        var invokeData: ListenerFunctionInvokeData? = null
        if (funcs.isEmpty()) {
            return
        } else {
            var finalResult: ListenResult<*>

            var anySuccess = false
            var doBreak = false

            // do listen function
            suspend fun doListen(invoker: ListenerInvoker): ListenResult<*> {
                val func = invoker.function
                // val listenerInterceptContext =
                // val interceptorChain = listenerInterceptChainFactory.getInterceptorChain(listenerInterceptContext)

                val interceptorChain = listenerInterceptChainFactory.getInterceptorChainOnNonEmpty {
                    listenerInterceptContextFactory.getListenerInterceptContext(invoker.function, msgGet, context)
                } ?: EmptyListenerInterceptorChain

                // invoke with try.
                return try {
                    invokeData = ListenerFunctionInvokeDataImpl(
                        // LazyThreadSafetyMode.NONE,
                        msgGet,
                        context,
                        atDetectionFactory.getAtDetection(msgGet),
                        botManager.getBot(msgGet.botInfo),
                        MsgSender(msgGet, msgSenderFactories, defMsgSenderFactories),
                        interceptorChain
                    )

                    invoker(invokeData!!)
                } catch (funcRunEx: Throwable) {
                    (if (func is LogAble) func.log else logger).error("Listener '${func.name}' execution exception: $funcRunEx",
                        funcRunEx)
                    ListenResult
                }.also { result ->
                    if (result != ListenResult) {
                        invokeData?.run {
                            resultProcessorManager.processor(context(result, this))
                        }
                    }
                }
            }

            // 如果出现异常，处理
            fun doResultIfFail(func: ListenerFunction, result: ListenResult<*>): ListenResult<*> {
                return with(result) {
                    val ex = this.cause
                    if (ex != null) {
                        val handle = exceptionManager.getHandle(ex.javaClass)
                        handle?.runCatching {
                            doHandle(ExceptionHandleContext(ex, msgGet, func, context))
                        }?.getOrElse {
                            // 异常处理报错
                            (if (handle is LogAble) handle.log else logger).error("Exception handle failed: $it", it)
                            null
                        } ?: run {
                            (if (func is LogAble) func.log else logger).error("Listener execution exception: $ex", ex)
                            ListenResult
                        }
                    } else this
                }
            }


            // for (func: ListenerFunction in funcs.normal) {

            val normals = funcs.normal

            val iter = normals.iterator()
            var i = 0

            for (invoker in iter) {

                val func = invoker.function

                finalResult = doListen(invoker)

                if (finalResult.isSuccess()) {
                    eventLogger.trace("{} -> Normal listener chain[{}] success on {}({})", botCode, i, func.name, func.id)
                    anySuccess = true
                }

                // if ex
                finalResult = doResultIfFail(func, finalResult)

                // if break, break.
                if (finalResult.isBreak()) {
                    doBreak = true
                    eventLogger.trace("{} -> Normal Listener chain[{}] break on {}({})", botCode, i, func.name, func.id)
                    break
                }
                i++
            }
            eventLogger.trace("{} -> Normal listener invoked {}.", botCode, i + 1)


            // 如果没有break，也没有任何函数成功，执行spare函数
            if (!anySuccess && !doBreak) {
                eventLogger.trace("{} -> No normal success or break, to spares.", botCode)
                var si = 0
                for (invoker: ListenerInvoker in funcs.spare) {
                    val func = invoker.function
                    finalResult = doListen(invoker)
                    finalResult = doResultIfFail(func, finalResult)
                    if (finalResult.isBreak()) {
                        eventLogger.trace("{} -> Spare Listener chain[{}] break on {}({})", botCode, si, func.name, func.id)
                        break
                    }
                    si++
                }
                eventLogger.trace("{}, Spare Listener invoked {}.", botCode, si)
            }

            // do processor


            // return finalResult
        }
    }

    /**
     * 根据监听类型获取所有对应的监听函数。
     */
    override fun <T : MsgGet> getListenerFunctions(type: Class<out T>?): Collection<ListenerFunction> {
        return if (type == null) {
            mainListenerFunctionMap.values.asSequence().flatMap { it.asSequence().map { invoker -> invoker.function } }.toList()
        } else {
            getListenerFunctions(type, false).marge().map { it.function }
        }
    }

    override fun getListenerFunctionById(id: String): ListenerFunction? = listenerFunctionIdMap[id]?.function

    /**
     * 根据一个监听器类型获取对应监听函数。
     *
     * 寻找 main funcs 中为 [type] 的父类的类型。
     *
     */
    private fun <T : MsgGet> getListenerFunctions(type: Class<out T>, cache: Boolean): ListenerFunctionGroups {
        // 尝试直接获取
        // fastball n. 直球
        // 只取缓存，main listener中的内容用于遍历检测。
        val fastball: ListenerFunctionGroups? = cacheListenerFunctionMap[type]

        return if (fastball != null) {
            // 有缓存，return
            fastball
        } else {
            if (mainListenerFunctionMap.isEmpty()) {
                return ListenerFunctionGroups.empty
            }

            if (cache) {
                cacheListenerFunctionMap.computeIfAbsent(type) {
                    val typeNormalList = LinkedList<ListenerInvoker>()
                    val typeSpareList = LinkedList<ListenerInvoker>()
                    mainListenerFunctionMap.forEach { (k, v) ->
                        if (k.isAssignableFrom(type)) {
                            v.forEach { lis ->
                                if (lis.function.spare) typeSpareList.add(lis)
                                else typeNormalList.add(lis)
                            }
                        }
                    }

                    ListenerFunctionGroups(
                        typeNormalList.also { normalLs -> normalLs.sortBy { it.function.priority } },
                        typeSpareList.also { spareLs -> spareLs.sortBy { it.function.priority } },
                    ).also {
                        logger.debug("Init Listener function caches for event type '${type.name}'. normal listeners ${typeNormalList.size}, spare listeners ${typeSpareList.size}.")
                    }
                }
            } else {
                // val typeList = LinkedList<ListenerFunction>()
                val typeNormalList = LinkedList<ListenerInvoker>()
                val typeSpareList = LinkedList<ListenerInvoker>()
                mainListenerFunctionMap.forEach { (k, v) ->
                    if (k.isAssignableFrom(type)) {
                        v.forEach { lis ->
                            if (lis.function.spare) typeSpareList.add(lis)
                            else typeNormalList.add(lis)
                        }
                    }
                }
                // typeList.also { listener -> listener.sortBy { it.priority } }
                ListenerFunctionGroups(
                    typeNormalList.also { normalLs -> normalLs.sortBy { it.function.priority } },
                    typeSpareList.also { spareLs -> spareLs.sortBy { it.function.priority } },
                )
            }
        }


    }
}

