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

package love.forte.simbot.intercept

import love.forte.simbot.Context
import love.forte.simbot.constant.PriorityConstant
import love.forte.simbot.intercept.InterceptionType.BLOCK
import love.forte.simbot.intercept.InterceptionType.INTERCEPT


/**
 * 布尔拦截器，提供一个拦截方法，方法的返回值代表是否进行 **拦截**，
 * 即如果为true，则拦截，否则放行。
 */
public interface Interceptor<T, C: Context<T>> : Comparable<Interceptor<T, C>> {


    /**
     * 判断是否需要进行拦截。
     *
     * @see InterceptionType
     *
     * @return 拦截结果类型。枚举类型中，参数为 **true** 则代表 **被拦截**，反之同理。
     * 例如你想要终止接下来的执行，则返回与 **true** 相关的值，例如 [InterceptionType.INTERCEPT]。
     */
    fun intercept(context: C): InterceptionType


    /**
     * 排序值，默认即为最低值。一般情况下可以不用重写此方法。
     */
    @JvmDefault
    val priority: Int get() = PriorityConstant.LAST

    /**
     * 排序。
     */
    @JvmDefault
    override fun compareTo(other: Interceptor<T, C>): Int = priority.compareTo(other.priority)


}




/**
 * 拦截器的拦截结果类型。
 *
 * 其中，参数 [isPrevent] 相同的值含义一致，只不过提供了几个比较好理解的类型以供个人习惯使用。
 *
 * 例如 [INTERCEPT] 与 [BLOCK] 含义相同，都代表拦截接下来的行动。
 *
 */
public enum class InterceptionType(val isPrevent: Boolean) {
    /** 拦截。 */
    INTERCEPT(true),
    /** 拦截。 */
    BLOCK(true),
    /** 拦截（阻止）。 */
    PREVENT(true),
    /** 拦截。 */
    HOLD_UP(true),
    /** 拦截。 */
    HEAD_OFF(true),



    //********************************//

    /** 放行。 */
    PASS(false),
    /** 放行。 */
    RELEASE(false),
    /** 放行。 */
    GREEN_LIGHT(false),
    /** 放行。 */
    ALLOW(false),

    // Easter eggs
    @Suppress("EnumEntryName")
    `(x)`(INTERCEPT.isPrevent),
    @Suppress("EnumEntryName")
    `(v)`(ALLOW.isPrevent),
    ;

    companion object {
        /**
         * 根据是否拦截获取一个实例。
         */
        @JvmStatic
        fun getTypeByPrevent(isPrevent: Boolean): InterceptionType = if (isPrevent) INTERCEPT else PASS
    }


}
