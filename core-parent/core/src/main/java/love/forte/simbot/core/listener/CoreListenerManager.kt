/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

import love.forte.common.collections.concurrentSortedQueueOf
import love.forte.common.ioc.annotation.SpareBeans
import love.forte.simbot.LogAble
import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.sender.DefaultMsgSenderFactories
import love.forte.simbot.api.sender.MsgSender
import love.forte.simbot.api.sender.MsgSenderFactories
import love.forte.simbot.bot.BotManager
import love.forte.simbot.core.intercept.EmptyListenerInterceptorChain
import love.forte.simbot.core.intercept.EmptyMsgInterceptChain
import love.forte.simbot.core.listener.ListenerFunctionGroups.Companion.isEmpty
import love.forte.simbot.core.listener.ListenerFunctionGroups.Companion.isNotEmpty
import love.forte.simbot.core.listener.ListenerFunctionGroups.Companion.marge
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

/**
 * 监听函数自排序队列。
 */
private fun listenerFunctionQueue(vararg func: ListenerFunction): Queue<ListenerFunction> =
    concurrentSortedQueueOf(ListenerFunctionComparable, *func)


private data class ListenerFunctionGroups(
    val normal: Collection<ListenerFunction>,
    val spare: Collection<ListenerFunction>,
) {
    companion object {
        val empty = ListenerFunctionGroups(emptyList(), emptyList())
        fun ListenerFunctionGroups.marge(): Collection<ListenerFunction> =
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
@SpareBeans("coreListenerManager")
public class CoreListenerManager @OptIn(SimbotExperimentalApi::class) constructor(
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

    ) : ListenerManager, ListenerRegistrar {

    private val logger: Logger = LoggerFactory.getLogger(CoreListenerManager::class.java)

    /**
     * 监听函数集合，通过对应的监听类型进行分类。
     *
     * 此为主集合，所有的监听函数均存在于此。
     */
    private val mainListenerFunctionMap: MutableMap<Class<out MsgGet>, Queue<ListenerFunction>> = ConcurrentHashMap()

    /**
     * 监听函数缓冲区，对后续出现的消息类型进行记录并缓存。
     * 当 [register] 了新的监听函数后对应相关类型将会被清理。
     */
    private val cacheListenerFunctionMap: MutableMap<Class<out MsgGet>, ListenerFunctionGroups> = ConcurrentHashMap()
    // private val cacheListenerFunctionMap: MutableMap<Class<out MsgGet>, Queue<ListenerFunction>> = ConcurrentHashMap()


    /**
     * 注册一个 [监听函数][ListenerFunction]。
     *
     * 每次注册一个新的监听函数的时候，会刷新内置的 **全部** 缓存，会一定程度上影响到其他应用。
     *
     */
    override fun register(listenerFunction: ListenerFunction) {
        // 获取其监听类型，并作为key存入map
        val listenTypes = listenerFunction.listenTypes

        listenTypes.forEach { listenType ->
            // merge into map.
            mainListenerFunctionMap.merge(listenType, listenerFunctionQueue(listenerFunction)) { oldValue, value ->
                oldValue.apply { addAll(value) }
            }

            // clear cache map.
            // 清除缓存
            if (cacheListenerFunctionMap.isNotEmpty()) {
                cacheListenerFunctionMap.clear()
                logger.debug("Listener cache cleaned.")
            }


        }
    }


    /**
     * 接收到消息监听并进行处理。
     */
    @OptIn(SimbotExperimentalApi::class)
    override fun onMsg(msgGet: MsgGet): ListenResult<*> {
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
                return ListenResult
            }

            // 筛选并执行监听函数
            return onMsg0(msgInterceptContext?.msgGet ?: msgGet, context)
        } catch (e: Throwable) {
            logger.error("Some unexpected errors occurred in the execution of the listener: ${e.localizedMessage}", e)
            return ListenResult
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
    private fun onMsg0(msgGet: MsgGet, context: ListenerContext): ListenResult<*> {
        val funcs = getListenerFunctions(msgGet.javaClass, true)
        var invokeData: ListenerFunctionInvokeData? = null
        return if (funcs.isEmpty()) {
            ListenResult
        } else {
            var finalResult: ListenResult<*> = ListenResult

            var anySuccess = false
            var doBreak = false

            // do listen function
            fun doListen(func: ListenerFunction): ListenResult<*> {

                // val listenerInterceptContext =
                // val interceptorChain = listenerInterceptChainFactory.getInterceptorChain(listenerInterceptContext)

                val interceptorChain = listenerInterceptChainFactory.getInterceptorChainOnNonEmpty {
                    listenerInterceptContextFactory.getListenerInterceptContext(func, msgGet, context)
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

                    func(invokeData!!)
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


            for (func: ListenerFunction in funcs.normal) {

                finalResult = doListen(func)

                if (finalResult.isSuccess()) {
                    anySuccess = true
                }

                // if ex
                finalResult = doResultIfFail(func, finalResult)

                // if break, break.
                if (finalResult.isBreak()) {
                    doBreak = true
                    logger.debug("Normal Listener chain break on ${func.name} ( ${func.id} )")
                    break
                }
            }


            // 如果没有break，也没有任何函数成功，执行spare函数
            if (!anySuccess && !doBreak) {
                for (func: ListenerFunction in funcs.spare) {
                    finalResult = doListen(func)
                    finalResult = doResultIfFail(func, finalResult)
                    if (finalResult.isBreak()) {
                        logger.debug("Spare Listener chain break on ${func.name} ( ${func.id} )")
                        break
                    }
                }
            }

            // do processor


            return finalResult
        }
    }

    /**
     * 根据监听类型获取所有对应的监听函数。
     */
    override fun <T : MsgGet> getListenerFunctions(type: Class<out T>?): Collection<ListenerFunction> {
        return if (type == null) {
            mainListenerFunctionMap.values.asSequence().flatMap { it.asSequence() }.toList()
        } else {
            getListenerFunctions(type, false).marge()
        }
    }


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
        val fastball = cacheListenerFunctionMap[type]

        return if (fastball != null) {
            // 有缓存，return
            fastball
        } else {
            if (mainListenerFunctionMap.isEmpty()) {
                return ListenerFunctionGroups.empty
            }

            if (cache) {
                cacheListenerFunctionMap.computeIfAbsent(type) {
                    val typeNormalList = LinkedList<ListenerFunction>()
                    val typeSpareList = LinkedList<ListenerFunction>()
                    mainListenerFunctionMap.forEach { (k, v) ->
                        if (k.isAssignableFrom(type)) {
                            v.forEach { lis ->
                                if (lis.spare) typeSpareList.add(lis)
                                else typeNormalList.add(lis)
                            }
                        }
                    }

                    ListenerFunctionGroups(
                        typeNormalList.also { normalLs -> normalLs.sortBy { it.priority } },
                        typeSpareList.also { spareLs -> spareLs.sortBy { it.priority } },
                    ).also {
                        logger.debug("Init Listener function caches for event type '${type.name}'. normal listeners ${typeNormalList.size}, spare listeners ${typeSpareList.size}.")
                    }
                }
            } else {
                // val typeList = LinkedList<ListenerFunction>()
                val typeNormalList = LinkedList<ListenerFunction>()
                val typeSpareList = LinkedList<ListenerFunction>()
                mainListenerFunctionMap.forEach { (k, v) ->
                    if (k.isAssignableFrom(type)) {
                        v.forEach { lis ->
                            if (lis.spare) typeSpareList.add(lis)
                            else typeNormalList.add(lis)
                        }
                    }
                }
                // typeList.also { listener -> listener.sortBy { it.priority } }
                ListenerFunctionGroups(
                    typeNormalList.also { normalLs -> normalLs.sortBy { it.priority } },
                    typeSpareList.also { spareLs -> spareLs.sortBy { it.priority } },
                )
            }
        }


    }
}

