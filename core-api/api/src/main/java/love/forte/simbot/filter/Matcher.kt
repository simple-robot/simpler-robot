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
@file:JvmName("Matchers")
package love.forte.simbot.filter

/**
 * 匹配器。提供一个消息实例与一个关键词，判断是否通过。
 * msg参数不会是空字符串。
 */
public fun interface Matcher {
    /**
     * 通过一个[消息][msg]与当前filter的[关键词][keyword]判断此消息是否通过检测。
     */
    fun match(msg: String, keyword: Keyword): Boolean
}


/**
 * 有指定目标的多值匹配规则.
 */
public fun interface MostMatcher<T> {
    /**
     * 提供一个 [当前匹配目标][value]和[元素匹配器列表][matchers], 返回最终的匹配结果。
     */
    fun mostMatch(value: T, matchers: Iterable<(T) -> Boolean>): Boolean
}


/**
 * 对多个测试器进行测试的测试器。
 */
public fun interface MostTester {
    /**
     * 提供多个 [测试器][testers] 并得到一个最终结果。
     */
    fun mostTest(testers: Iterable<() -> Boolean>): Boolean
}


/**
 * 多值过滤器匹配器，用于判定当存在多个匹配函数的时候则匹配规则。
 * 其规则类似 [MostMatcher], 但是为了适应多种情况, 并没有对其进行继承。
 */
public fun interface MostFilterMatcher : MostMatcher<FilterData> {
    /**
     * 匹配多个可判断函数。
     */
    override fun mostMatch(value: FilterData, matchers: Iterable<(FilterData) -> Boolean>): Boolean
}




/** invoke fun. */
public operator fun <T> MostMatcher<T>.invoke(value: T, matchers: Iterable<(T) -> Boolean>): Boolean = this.mostMatch(value, matchers)
