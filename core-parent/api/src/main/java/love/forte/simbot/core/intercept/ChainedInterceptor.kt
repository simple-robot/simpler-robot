/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ChainedInterceptor.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.intercept

import love.forte.simbot.core.constant.PriorityConstant


/**
 * 链式拦截器。
 *
 * 链式拦截器不存在boolean类型的返回作为是否拦截的依据，
 * 而是以 [放行(pass)][InterceptChain.pass] 取代了 boolean类型的 [拦截][Interceptor.intercept]。
 *
 * 链式拦截与布尔拦截的区别在于，链式拦截可以在放行或拦截后执行更多的操作。
 *
 *
 * | A | ---|(pass)
 *   |      |--> | B | ---|(pass)
 *   |             |      |-->  | tail |
 *   |             |               |
 *   |             | <--------- | tail |
 *   | <-------- | B |
 * | A |
 *
 *
 *
 * @property T 拦截主体的类型，即拦截的时候所提供给拦截器的主要信息内容。
 * @property R 链式拦截器中放行 ([InterceptChain.pass]) 至下一条链所可以得到的结果值。
 * @property C 当前链式拦截器所对应的信息主体类型。
 */
public interface ChainedInterceptor<T, R, C : Context<T>> : Comparable<Interceptor<T, C>> {

    /**
     * 执行当前链式拦截器。
     */
    fun chainedIntercept(context: C, chain: InterceptChain<T, R>)


    /**
     * 排序值，默认即为最低值。一般情况下可以不用重写此方法。
     */
    @JvmDefault
    val priority: Int get() = PriorityConstant.LAST

    /**
     * 排序。
     */
    override fun compareTo(other: Interceptor<T, C>): Int = priority.compareTo(other.priority)
}



/**
 * 链式拦截器的链，用于pass到下一条链。
 */
public interface InterceptChain<T, R> {
    fun pass(context: Context<T>): R?
}



/**
 * [ChainedInterceptor] 作为链式最结尾的抽象类。
 * 链式拦截器的最终链部分，一般来讲，其正好对应了拦截对象的主体本身。
 */
public abstract class ChainedInterceptorTail<T, R, C : Context<T>> : ChainedInterceptor<T, R, C> {

    /**
     * 请不要实现方法。
     */
    override fun chainedIntercept(context: C, chain: InterceptChain<T, R>) {
        doPass(context)
    }


    /**
     * 执行此主体。
     */
    abstract fun doPass(context: C): R?
}

