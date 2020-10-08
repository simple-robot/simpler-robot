/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerManagerImpl.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.common.collections.concurrentQueueOf
import love.forte.common.collections.concurrentSortedQueueOf
import love.forte.common.sequences.distinctByMerger
import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.exception.ExceptionHandleContext
import love.forte.simbot.core.exception.ExceptionProcessor
import love.forte.simbot.core.filter.AtDetectionFactory
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
    val chainFactory: ListenerInterceptChainFactory
)


/**
 * 监听上下文所需内容。
 */
public data class ListenerContextData(
    val contextFactory: ListenerContextFactory,
    val contextMapFactory: ContextMapFactory
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

    private val listenerContextData: ListenerContextData

) : ListenerManager, ListenerRegistrar {

    private val logger: Logger = LoggerFactory.getLogger("CoreListenerManager")

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
     * 目前所有监听中的类型。
     */
    private val allListenTypes: MutableSet<Class<out MsgGet>> get() = mainListenerFunctionMap.keys



    /**
     * 注册一个 [监听函数][ListenerFunction]。
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
            cacheListenerFunctionMap.forEach {
                if (listenType.isAssignableFrom(it.key)) {
                    it.value.add(listenerFunction)
                }
            }
        }
    }


    /**
     * 接收到消息监听并进行处理。
     */
    override fun onMsg(msgGet: MsgGet): ListenResult<*> {
        try {
            // 构建一个消息拦截器context
            val msgContext = msgInterceptData.contextFactory.getMsgInterceptContext(msgGet)
            val msgChain = msgInterceptData.chainFactory.getInterceptorChain(msgContext)

            // 如果被拦截, 返回默认值
            if (msgChain.intercept().isPrevent) {
                return NothingResult
            }
            // 筛选并执行监听函数
            return onMsg0(msgContext.msgGet)
        } catch (e: Throwable) {
            logger.error("Some unexpected errors occurred in the execution of the listener: ${e.localizedMessage}", e)
            return NothingResult
        }
    }


    /**
     * 筛选监听函数
     */
    private fun onMsg0(msgGet: MsgGet): ListenResult<*> {

        val funcs = getListenerFunctions(msgGet.javaClass, true)
        return if (funcs.isEmpty()) {
            NothingResult
        } else {
            // not empty, intercept.
            val context: ListenerContext = listenerContextData.getContext(msgGet)

            var finalResult: ListenResult<*> = NothingResult

            for (func: ListenerFunction in funcs) {

                val listenerInterceptContext =
                    listenerInterceptData.contextFactory.getListenerInterceptContext(func, msgGet, context)

                val interceptType =
                    listenerInterceptData.chainFactory.getInterceptorChain(listenerInterceptContext).intercept()

                // invoke with try.
                finalResult = try {
                    if (interceptType.isPrevent) {
                        NothingResult
                    } else {
                        val invokeData = ListenerFunctionInvokeDataImpl(
                            msgGet, context, atDetectionFactory.getAtDetection(msgGet)
                        )
                        // invoke func.
                        func(invokeData)
                    }
                } catch (funcRunEx: Throwable) {
                    logger.error("Listener execution exception: ${funcRunEx.localizedMessage}", funcRunEx)
                    NothingResult
                }


                // if ex
                finalResult = with(finalResult) {
                    val ex = this.throwable
                    if (ex != null) {
                        exceptionManager.getHandle(ex.javaClass)?.run {
                            doHandle(ExceptionHandleContext(ex, msgGet, func, context))
                        } ?: run {
                            ex.printStackTrace()
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
            getListenerFunctions(type, false)
        }

    }

    /**
     * 根据一个监听器类型获取对应监听函数。
     *
     * 寻找 main funcs 中为 [type] 的父类的类型。
     *
     */
    private fun <T : MsgGet> getListenerFunctionSequence(
        type: Class<out T>,
        cache: Boolean
    ): Sequence<ListenerFunction> {
        // 尝试直接获取
        // fastball n. 直球
        val fastball = mainListenerFunctionMap[type] ?: cacheListenerFunctionMap[type]
        return if (fastball != null) {
            fastball.asSequence()
        } else {
            // 无法直接获取，则遍历类型并合并。
            // 获取类型对应函数列表
            val typeSeq: Sequence<ListenerFunction> = mainListenerFunctionMap.asSequence().flatMap { (k, v) ->
                if (k.isAssignableFrom(type)) {
                    v.asSequence()
                } else {
                    emptySequence()
                }
            }

            if (cache) {
                val typeQueue: Queue<ListenerFunction> = typeSeq.toCollection(concurrentQueueOf())
                // lock and cache it.
                synchronized(type) {
                    // merge.
                    cacheListenerFunctionMap.merge(type, typeQueue) { oldValue, value ->
                        (oldValue.asSequence() + value.asSequence()).distinctByMerger(
                            {
                                ListenerFunctionDistinction(it)
                            }, { _, func ->
                                throw ListenerAlreadyExistsException("Duplicate listener by id: ${func.id} in $func.")
                            }
                        ).toCollection(concurrentQueueOf())
                    }
                }
                typeQueue.asSequence()
            } else {
                typeSeq
            }
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
        val fastball = mainListenerFunctionMap[type] ?: cacheListenerFunctionMap[type]

        return if (fastball != null) {
            fastball.toList()
        } else {
            // 无法直接获取，则遍历类型并合并。
            // 获取类型对应函数列表
            val typeSeq: Sequence<ListenerFunction> = mainListenerFunctionMap.asSequence().flatMap { (k, v) ->
                if (k.isAssignableFrom(type)) {
                    v.asSequence()
                } else {
                    emptySequence()
                }
            }

            if (cache) {
                val typeQueue: Queue<ListenerFunction> = typeSeq.toCollection(concurrentQueueOf())
                // lock and cache it.
                synchronized(type) {
                    // merge.
                    cacheListenerFunctionMap.merge(type, typeQueue) { oldValue, value ->
                        (oldValue.asSequence() + value.asSequence()).distinctBy {
                            ListenerFunctionDistinction(it)
                        }.toCollection(concurrentQueueOf())
                    }
                }
                typeQueue.toList()
            } else {
                typeSeq.toList()
            }
        }


    }

}
