/*
 * Copyright (c) 2020-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */
@file:JvmName("CoreKeywords")

package love.forte.simboot.core.filter

import love.forte.simboot.filter.Keyword
import love.forte.simboot.filter.MatchType
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
internal class KeywordImpl(override val text: String, matchType: MatchType) : Keyword {
    override val matcherValue: MatcherValue
    override val regex: Regex

    init {
        val regexParameterMatcher = RegexMatcherValue(text, matchType.isPlainText)
        matcherValue = regexParameterMatcher
        regex = regexParameterMatcher.regex
    }
}



