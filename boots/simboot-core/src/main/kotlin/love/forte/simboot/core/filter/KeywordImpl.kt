/*
 *  Copyright (c) 2020-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */
@file:JvmName("CoreKeywords")

package love.forte.simboot.core.filter

import love.forte.simboot.filter.Keyword
import love.forte.simboot.filter.MatcherValue
import love.forte.simboot.filter.RegexMatcherValue
import love.forte.simbot.Attribute
import love.forte.simbot.attribute

/**
 * 装载 [Keyword] 列表的属性key。
 */
public val KeywordsAttribute: Attribute<MutableList<Keyword>> = attribute("\$listener.keywords")


/**
 * 一个普通的value值构建为 [Keyword] 实例。
 */
internal class KeywordImpl(override val text: String) : Keyword {
    override val matcherValue: MatcherValue
    override val regex: Regex

    init {
        val regexParameterMatcher = RegexMatcherValue(text)
        matcherValue = regexParameterMatcher
        regex = regexParameterMatcher.regex
    }
}



