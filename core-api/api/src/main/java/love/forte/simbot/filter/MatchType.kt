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
 * 对于一个消息的匹配类型。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
enum class MatchType(private val matchFunc : (String, Keyword) -> Boolean) : Matcher {
    /** 相同匹配。 */
    EQUALS({ msg, kw -> msg == kw.text }),
    /** 包含匹配。 */
    CONTAINS({ msg, kw -> msg.contains(kw.text) }),
    /** 开头匹配。 */
    STARTS_WITH({ msg, kw -> msg.startsWith(kw.text) }),
    /** 结尾匹配。 */
    ENDS_WITH({ msg, kw -> msg.endsWith(kw.text) }),
    /** 正则全文匹配。 */
    REGEX_MATCHES({ msg, kw -> kw.regex.matches(msg) }),
    /** 正则查找匹配。 */
    REGEX_FIND({ msg, kw -> kw.regex.find(msg, 0) != null }) ;

    /**
     * 匹配
     */
    override fun match(msg: String, keyword: Keyword): Boolean = matchFunc(msg, keyword)

}