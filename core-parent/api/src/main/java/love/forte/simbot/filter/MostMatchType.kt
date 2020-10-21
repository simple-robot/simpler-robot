/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MostMatchType.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.filter


/**
 *
 * 多匹配类型，当存在多个可匹配值（例如codes等），
 * 则此函数指定一个多值的匹配规则。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
enum class MostMatchType(private val mostMatchFunc: (Iterable<() -> Boolean>) -> Boolean) : MostMatcher {
    /** 任意匹配。 */
    ANY({ it.any { t -> t() } }),

    /** 全部匹配。 */
    ALL({ it.all { t -> t() } }),

    /** 任意不匹配。 */
    ANY_NO({ it.any { t -> !t() } }),

    /** 没有任何匹配。 */
    NONE({ it.all { t -> !t() } });


    /**
     * 匹配多个可判断函数。
     */
    override fun mostMatch(funcs: Iterable<() -> Boolean>): Boolean =
        mostMatchFunc(funcs)
}