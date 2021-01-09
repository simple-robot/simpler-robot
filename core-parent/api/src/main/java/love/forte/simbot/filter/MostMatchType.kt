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

package love.forte.simbot.filter


/**
 *
 * 过滤器的多匹配类型，当存在多个可匹配值（例如codes等），
 * 则此函数指定一个多值的匹配规则。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
enum class MostMatchType(private val mostMatchFunc: (FilterData, Iterable<(FilterData) -> Boolean>) -> Boolean) : MostMatcher {
    /** 任意匹配。 */
    ANY({ d, it ->  it.any { t -> t(d) } }),

    /** 全部匹配。 */
    ALL({ d, it -> it.all { t -> t(d) } }),

    /** 任意不匹配。 */
    ANY_NO({ d, it ->  it.any { t -> !t(d) } }),

    /** 没有任何匹配。 */
    NONE({ d, it ->  it.all { t -> !t(d) } });


    /**
     * 匹配多个可判断函数。
     */
    override fun mostMatch(filterData: FilterData, funcs: Iterable<(FilterData) -> Boolean>): Boolean =
        mostMatchFunc(filterData, funcs)
}