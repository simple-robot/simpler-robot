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

package love.forte.simbot.filter


/**
 *
 * 对于一个消息的匹配类型。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
enum class MatchType(
    private val matchFunc: (String, Keyword) -> Boolean,
    private val stringMatchFunc: (String, String) -> Boolean,
) : KeywordMatcher, StringMatcher {
    /** 相同匹配。 */
    EQUALS(
        { msg, kw -> msg == kw.text },
        { m1, m2 -> m1 == m2 }
    ),

    /** 包含匹配。 */
    CONTAINS(
        { msg, kw -> kw.text in msg },
        { m1, m2 -> m2 in m1 }
    ),

    /** 开头匹配。 */
    STARTS_WITH(
        { msg, kw -> msg.startsWith(kw.text) },
        { m1, m2 -> m1.startsWith(m2) }
    ),

    /** 结尾匹配。 */
    ENDS_WITH(
        { msg, kw -> msg.endsWith(kw.text) },
        { m1, m2 -> m1.endsWith(m2) }
    ),

    /** 正则全文匹配。 */
    REGEX_MATCHES(
        { msg, kw -> kw.regex.matches(msg) },
        { m1, m2 -> Regex(m2).matches(m1) }
    ),

    /** 正则查找匹配。 */
    REGEX_FIND(
        { msg, kw -> kw.regex.find(msg, 0) != null },
        { m1, m2 -> Regex(m2).find(m1, 0) != null }
    );

    /**
     * 匹配
     */
    override fun match(msg: String, keyword: Keyword): Boolean = matchFunc(msg, keyword)
    override fun match(msg1: String, matched: String): Boolean = stringMatchFunc(msg1, matched)
}