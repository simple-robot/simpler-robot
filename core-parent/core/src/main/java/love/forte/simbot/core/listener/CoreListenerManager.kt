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
import love.forte.simbot.LogAble
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.sender.MsgSender
import love.forte.simbot.api.sender.MsgSenderFactories
import love.forte.simbot.bot.BotManager
import love.forte.simbot.exception.ExceptionHandleContext
import love.forte.simbot.exception.ExceptionProcessor
import love.forte.simbot.filter.AtDetectionFactory
import love.forte.simbot.listener.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 监听函数自排序队列。
 */
private fun listenerFunctionQueue(vararg func: ListenerFunction): Queue<ListenerFunction> =
    concurrentSortedQueueOf(ListenerFunctionComparable, *func)


/**
 * 消息拦截相关所需内容。
 */
public data class MsgInterceptData(
    val contextFactory: MsgInterceptContextFactory,
    val chainFactory: MsgInterceptChainFactory,
)

/**
 * 函数拦截相关所需内容。
 */
public data class ListenerInterceptData(
    val contextFactory: ListenerInterceptContextFactory,
    val chainFactory: ListenerInterceptChainFactory,
)


/**
 * 监听上下文所需内容。
 */
public data class ListenerContextData(
    val contextFactory: ListenerContextFactory,
    val contextMapFactory: ContextMapFactory,
) {
    fun getContext(msgGet: MsgGet): ListenerContext {
        return contextMapFactory.contextMap.let {
            contextFactory.getListenerContext(msgGet, it)
        }
    }
}


/**
 * 核心对于 [ListenerManager] 的实现。
 *
 * @property atDetectionFactory at检测器工厂
 * @property exceptionManager 异常处理器
 *
 * @property msgInterceptData 消息拦截相关所需内容。
 * @property listenerInterceptData 函数拦截相关所需内容。
 * @property listenerContextData 监听上下文所需内容。
 */
public class CoreListenerManager(
    private val atDetectionFactory: AtDetectionFactory,
    private val exceptionManager: ExceptionProcessor,

    private val msgInterceptData: MsgInterceptData,

    private val listenerInterceptData: ListenerInterceptData,

    private val listenerContextData: ListenerContextData,

    private val msgSenderFactories: MsgSenderFactories,

    private val botManager: BotManager,

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
    private val cacheListenerFunctionMap: MutableMap<Class<out MsgGet>, Queue<ListenerFunction>> = ConcurrentHashMap()


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
            // 寻找并更新缓存监听
            // no. 直接清除缓存。
            cacheListenerFunctionMap.clear()

        }
    }


    /**
     * 接收到消息监听并进行处理。
     */
    override fun onMsg(msgGet: MsgGet): ListenResult<*> {
        try {
            // not empty, intercept.
            val context: ListenerContext = listenerContextData.getContext(msgGet)

            // 构建一个消息拦截器context
            val msgContext = msgInterceptData.contextFactory.getMsgInterceptContext(msgGet, context)
            val msgChain = msgInterceptData.chainFactory.getInterceptorChain(msgContext)


            // 如果被拦截, 返回默认值
            if (msgChain.intercept().isPrevent) {
                return NothingResult
            }
            // 筛选并执行监听函数
            return onMsg0(msgContext.msgGet, context)
        } catch (e: Throwable) {
            logger.error("Some unexpected errors occurred in the execution of the listener: ${e.localizedMessage}", e)
            return NothingResult
        }
    }


    /**
     * 筛选监听函数
     */
    private fun onMsg0(msgGet: MsgGet, context: ListenerContext): ListenResult<*> {
        val funcs = getListenerFunctions(msgGet.javaClass, true)
        return if (funcs.isEmpty()) {
            NothingResult
        } else {
            // // not empty, intercept.
            // val context: ListenerContext = listenerContextData.getContext(msgGet)

            var finalResult: ListenResult<*> = NothingResult

            for (func: ListenerFunction in funcs) {

                val listenerInterceptContext =
                    listenerInterceptData.contextFactory.getListenerInterceptContext(func, msgGet, context)

                val interceptorChain = listenerInterceptData.chainFactory.getInterceptorChain(listenerInterceptContext)

                // invoke with try.
                finalResult = try {
                    val invokeData = ListenerFunctionInvokeDataImpl(
                        msgGet,
                        context,
                        atDetectionFactory.getAtDetection(msgGet),
                        botManager.getBot(msgGet.botInfo),
                        MsgSender(msgGet, msgSenderFactories),
                        interceptorChain
                    )
                    func(invokeData)
                } catch (funcRunEx: Throwable) {
                    (if (func is LogAble) func.log else logger).error("Listener '${func.name}' execution exception: $funcRunEx", funcRunEx)
                    NothingResult
                }


                // if ex
                finalResult = with(finalResult) {
                    val ex = this.cause
                    if (ex != null) {
                        val handle = exceptionManager.getHandle(ex.javaClass)
                        handle?.runCatching {
                            doHandle(ExceptionHandleContext(ex, msgGet, func, context))
                        }?.getOrElse {
                            // 异常处理报错
                            (if(handle is LogAble) handle.log else logger).error("Exception handle failed: $it", it)
                            null
                        } ?: run {
                            (if (func is LogAble) func.log else logger).error("Listener execution exception: $ex", ex)
                            NothingResult
                        }
                    } else this
                }

                // if break, break.
                if (finalResult.isBreak()) {
                    break
                }
            }

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
            getListenerFunctions(type, false).toList()
        }

    }


    /**
     * 根据一个监听器类型获取对应监听函数。
     *
     * 寻找 main funcs 中为 [type] 的父类的类型。
     *
     */
    private fun <T : MsgGet> getListenerFunctions(type: Class<out T>, cache: Boolean): Collection<ListenerFunction> {
        // 尝试直接获取
        // fastball n. 直球
        // 只取缓存，main listener中的内容用于遍历检测。
        val fastball = cacheListenerFunctionMap[type]

        return if (fastball != null) {
            // 有缓存，return
            fastball
        } else {
            if (mainListenerFunctionMap.isEmpty()) {
                return emptyList()
            }

            // // 获取不到缓存信息，则遍历类型。
            // // 获取类型对应函数列表
            // val typeList = LinkedList<ListenerFunction>()
            //
            // mainListenerFunctionMap.forEach { (k, v)->
            //     if (k.isAssignableFrom(type)) {
            //         typeList.addAll(v)
            //     }
            // }

            if (cache) {
                cacheListenerFunctionMap.computeIfAbsent(type) {
                    val typeList = LinkedList<ListenerFunction>()
                    mainListenerFunctionMap.forEach { (k, v) ->
                        if (k.isAssignableFrom(type)) {
                            typeList.addAll(v)
                        }
                    }
                    typeList
                }
            } else {
                val typeList = LinkedList<ListenerFunction>()
                mainListenerFunctionMap.forEach { (k, v) ->
                    if (k.isAssignableFrom(type)) {
                        typeList.addAll(v)
                    }
                }
                typeList
            }
        }


    }

}
