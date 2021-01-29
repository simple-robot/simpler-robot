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

@file:JvmName("MostMatchTypes")
package love.forte.simbot.filter


/**
 *
 * 过滤器的多匹配类型，当存在多个可匹配值（filter），
 * 则此函数指定一个多值的匹配规则。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
enum class MostMatchType(
    val filterMatcher: MostFilterMatcher,
    private val stringMatcher: MostMatcher<String>
) : MostMatcher<String> by stringMatcher {
    /** 任意匹配。 */
    ANY(::any, ::any),

    /** 全部匹配。 */
    ALL(::all, ::all),

    /** 任意不匹配。 */
    ANY_NO(::anyNo, ::anyNo),

    /** 没有任何匹配。 */
    NONE(::none, ::none);


    /**
     * 匹配多个可判断函数。
     */
    fun mostMatch(filterData: FilterData, funcs: Iterable<(FilterData) -> Boolean>): Boolean =
        filterMatcher(filterData, funcs)

}


internal fun <T> any(value: T, matchers: Iterable<(T) -> Boolean>): Boolean = matchers.any { t -> t(value) }
internal fun <T> all(value: T, matchers: Iterable<(T) -> Boolean>): Boolean = matchers.all { t -> t(value) }
internal fun <T> anyNo(value: T, matchers: Iterable<(T) -> Boolean>): Boolean = matchers.any { t -> !t(value) }
internal fun <T> none(value: T, matchers: Iterable<(T) -> Boolean>): Boolean = matchers.all { t -> !t(value) }
