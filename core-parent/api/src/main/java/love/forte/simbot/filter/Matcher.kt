/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Matcher.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.filter

/**
 * 匹配器。提供一个消息实例与一个关键词，判断是否通过。
 * msg参数不会是空字符串。
 */
public interface Matcher {
    /**
     * 通过一个[消息][msg]与当前filter的[关键词][keyword]判断此消息是否通过检测。
     */
    fun match(msg: String, keyword: Keyword): Boolean
}


/**
 * 多值匹配器，用于判定当存在多个匹配函数的时候则匹配规则。
 */
public interface MostMatcher {

    /**
     * 匹配多个可判断函数。
     */
    fun mostMatch(funcs: Iterable<() -> Boolean>): Boolean

}
