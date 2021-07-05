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

@file:JvmName("MostMatchTypes")
package love.forte.simbot.filter


/**
 *
 * 过滤器的多匹配类型，当存在多个可匹配值（filter），
 * 则此函数指定一个多值的匹配规则。
 *
 * 同时，[MostMatchType] 也是一个字符串的[多元素匹配器][MostMatcher] ，以及一个 [多元素测试器][MostTester].
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
enum class MostMatchType(
    val filterMatcher: MostFilterMatcher,
    private val stringMatcher: MostMatcher<String>,
    private val mostTester: MostTester
) : MostMatcher<String> by stringMatcher, MostTester by mostTester {
    /** 任意匹配。 */
    ANY(::any, ::any, ::any),

    /** 全部匹配。 */
    ALL(::all, ::all, ::all),

    /** 任意不匹配。 */
    ANY_NO(::anyNo, ::anyNo, ::anyNo),

    /** 没有任何匹配。 */
    NONE(::none, ::none, ::none);


    /**
     * 匹配多个可判断函数。
     */
    fun mostMatch(filterData: FilterData, funcIter: Iterable<(FilterData) -> Boolean>): Boolean =
        filterMatcher(filterData, funcIter)
}


internal fun <T> any(value: T, matchers: Iterable<(T) -> Boolean>): Boolean = matchers.any { t -> t(value) }
internal fun <T> all(value: T, matchers: Iterable<(T) -> Boolean>): Boolean = matchers.all { t -> t(value) }
internal fun <T> anyNo(value: T, matchers: Iterable<(T) -> Boolean>): Boolean = matchers.any { t -> !t(value) }
internal fun <T> none(value: T, matchers: Iterable<(T) -> Boolean>): Boolean = matchers.all { t -> !t(value) }

internal fun any(testers: Iterable<() -> Boolean>): Boolean = testers.any { it() }
internal fun all(testers: Iterable<() -> Boolean>): Boolean = testers.all { it() }
internal fun anyNo(testers: Iterable<() -> Boolean>): Boolean = testers.any { !it() }
internal fun none(testers: Iterable<() -> Boolean>): Boolean = testers.all { !it() }
