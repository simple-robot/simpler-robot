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
@file:JvmName("ListenerFunctions")
package love.forte.simbot.listener

import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.sender.MsgSender
import love.forte.simbot.bot.Bot
import love.forte.simbot.filter.AtDetection
import love.forte.simbot.filter.ListenerFilter
import java.lang.reflect.Type


/**
 * [ListenerFunction] 的 [排序规则][Comparator] 实例。
 */
@get:JvmName("listenerFunctionComparable")
public val ListenerFunctionComparable: Comparator<ListenerFunction> = Comparator { f1, f2 -> f1.priority.compareTo(f2.priority) }



/**
 * 监听函数。
 *
 * 监听函数不应去关心过滤逻辑与拦截逻辑，仅需通过 [love.forte.simbot.filter.FilterManager] 或其他渠道得到于其对应的一个 [filter] 即可。
 *
 * @see love.forte.simbot.annotation.Listen
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface ListenerFunction {

    /**
     * 此监听函数的唯一编号，用于防止出现重复冲突。
     * 在判断重复性前，会优先判断此id，再使用equals。
     * 如果id相同，则认为其相同，则不再使用equals判断。
     */
    val id: String

    /**
     * 监听函数的名称。
     */
    val name: String

    /**
     * 此函数是否属于一个 **备用函数**.
     *
     * @see love.forte.simbot.annotation.SpareListen
     */
    val spare: Boolean

    /**
     * 监听函数的优先级。
     */
    val priority: Int


    /**
     * 获取此监听函数上可以得到的注解。
     */
    fun <A : Annotation> getAnnotation(type: Class<out A>) : A?


    /**
     * 监听的类型们。
     */
    val listenTypes: Set<Class<out MsgGet>>


    /**
     * 此监听函数对应的监听过滤器。
     */
    val filter: ListenerFilter?


    /**
     * 判断当前监听函数是否可以触发当前类型的监听.
     */
    
    fun <T: MsgGet> canListen(onType: Class<T>): Boolean {
        return listenTypes.contains(onType) || listenTypes.any { it.isAssignableFrom(onType) }
    }


    /**
     * 当前监听函数的载体。例如一个 Class。
     * 例如如果是个method，那么此即为 method所在的 [Class]。
     */
    val type: Type


    /**
     * 一个监听函数可能会被分为一个或多个组。
     * @since 2.1.0
     */
    @SimbotExperimentalApi
    val groups: List<ListenerGroup>


    /**
     * 执行监听函数并返回一个执行后的响应结果。
     *
     * @throws Throwable 执行可能会存在任何可能发生的以外异常。而 [ListenResult] 中包含的一般仅仅是方法执行时候出现的异常。
     */
    suspend operator fun invoke(data: ListenerFunctionInvokeData): ListenResult<*>


    /**
     * 是否异步执行。如果是，则当前监听函数的任务将会为异步执行。
     * 对于“异步”的具体行为，由 [ListenerManager] 进行具体实现。一般来讲是通过线程或线程池进行异步执行。
     *
     * @since 2.3.0
     */
    val async: Boolean



    /**
     * 得到当前监听函数的 [开关][Switch].
     */
    @SimbotExperimentalApi
    val switch: Switch


    /**
     * 监听函数所对应的 [开关][Switch]. 此开关可以操作其对应的监听函数的开关状态。
     *
     * 如果希望开关支持注册 **监听器**，用于监听执行了 [enable] 或 [disable] 后的操作，则参考 [onSwitch].
     *
     *
     * 监听函数的[开关][Switch] 将会直接作用于 [监听函数][ListenerFunction] 自身，当监听函数被关闭，则 [ListenerFunction.invoke] 将会永远得到 [ListenResult.Default].
     *
     */
    @SimbotExperimentalApi
    public interface Switch {
        public companion object : Switch {
            val DISABLE_FUNCTION_INVOKER: suspend (ListenerFunctionInvokeData) -> ListenResult<*> = { ListenResult }

            @SimbotExperimentalApi
            override fun enable() {
            }

            @SimbotExperimentalApi
            override fun disable() {
            }
            override val isEnable: Boolean
                get() = false
        }


        /**
         * 启用当前监听函数。正常来讲，监听函数在默认情况下总是启动的。
         * 当监听函数被启用后，此监听函数便会开始收到并处理监听事件。
         *
         * @since 2.2.0
         */
        @SimbotExperimentalApi
        fun enable()

        /**
         * 停用当前监听函数。
         * 当监听函数被停用后，在启用前将不会再接收新的监听函数事件。
         *
         * @since 2.2.0
         */
        @SimbotExperimentalApi
        fun disable()


        /**
         * 判断当前监听函数是否已经启用。
         *
         * @since 2.2.0
         */
        val isEnable: Boolean


        /**
         * 开关的监听器。当执行了 [enable] 或者 [disable] （之后）便会触发 [onSwitch], 其代表执行的动作所代表的开关状态。enable即true,disable即false。
         */
        @SimbotExperimentalApi
        public fun interface Listener {

            @SimbotExperimentalApi
            fun onSwitch(switch: Boolean)
        }

    }

}



public abstract class BlockingListenerFunction : ListenerFunction {
    abstract fun invokeBlocking(data: ListenerFunctionInvokeData): ListenResult<*>
    final override suspend fun invoke(data: ListenerFunctionInvokeData): ListenResult<*> = invokeBlocking(data)
}






/**
 * 监听函数执行所需要的数据。
 */
public interface ListenerFunctionInvokeData {
    /** 监听到的消息。 */
    val msgGet: MsgGet
    /** 监听函数上下文。 */
    @OptIn(SimbotExperimentalApi::class)
    val context: ListenerContext
    /** at检测器。 */
    val atDetection: AtDetection
    /** 当前监听到消息的bot。 */
    val bot: Bot
    /** 当前动态送信器。 */
    val msgSender: MsgSender
    /**
     *  监听函数拦截器。
     *  如果是空的可以使用 `love.forte.simbot.core.intercept.EmptyListenerInterceptorChain`
     */
    val listenerInterceptorChain: ListenerInterceptorChain
    /**
     * 根据类型获取一个实例。
     * 有可能存在一些额外参数。
     */
    operator fun get(type: Class<*>): Any?
}





/**
 * 监听器已经存在异常。
 */
public class ListenerAlreadyExistsException : IllegalStateException {
    constructor() : super()
    constructor(s: String?) : super(s)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}


/**
 * 监听器不存在异常。
 */
public class ListenerNotExistsException : IllegalStateException {
    constructor() : super()
    constructor(s: String?) : super(s)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}



/**
 * 判断监听函数是否相等。
 */
internal fun ListenerFunction.funcEquals(other: Any?): Boolean {
    if(this === other) return true

    if(other !is ListenerFunction) return false

    if(this.id == other.id) return true
    return this == other
}

/**
 * 用于distinct的实例。
 */
public class ListenerFunctionDistinction(private val func: ListenerFunction) {
    override operator fun equals(other: Any?): Boolean = func.funcEquals(other)
    override fun hashCode(): Int {
        return func.hashCode()
    }
}



