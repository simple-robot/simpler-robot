/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.quantcat.common.keyword

import love.forte.simbot.common.attribute.Attribute
import love.forte.simbot.common.attribute.attribute

/**
 * 装载 [Keyword] 列表的属性key。
 */
public val KeywordListAttribute: Attribute<MutableList<Keyword>> = attribute("\$listener.keywordList")

/**
 * 一个普通的value值构建为 [Keyword] 实例。
 */
public class SimpleKeyword(override val text: String, isPlainText: Boolean = false) : Keyword {
    override val regexValueMatcher: ValueMatcher
    override val regex: Regex

    init {
        val regexParameterMatcher = RegexValueMatcher(text, isPlainText)
        regexValueMatcher = regexParameterMatcher
        regex = regexParameterMatcher.regex
    }
}

