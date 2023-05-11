/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.filter

/**
 *
 * @author ForteScarlet
 */
public enum class MatchType(
    /**
     * Keyword匹配器。
     */
    private val matcher: KeywordMatcher,

    /**
     * 代表此匹配模式是否为纯文本匹配而并非通过正则的匹配。
     */
    public val isPlainText: Boolean
) : KeywordMatcher by matcher {
    /**
     * 全等匹配
     *
     * @see StringMatchers.EQUALS
     */
    TEXT_EQUALS(StringMatchers.EQUALS, true),

    /**
     * 忽略大小写的全等匹配
     *
     * @see StringMatchers.EQUALS_IGNORE_CASE
     */
    TEXT_EQUALS_IGNORE_CASE(StringMatchers.EQUALS_IGNORE_CASE, true),


    /**
     * 首部匹配
     *
     * @see StringMatchers.STARTS_WITH
     */
    TEXT_STARTS_WITH(StringMatchers.STARTS_WITH, true),

    /**
     * 尾部匹配.
     *
     * @see StringMatchers.ENDS_WITH
     */
    TEXT_ENDS_WITH(StringMatchers.ENDS_WITH, true),

    /**
     * 包含匹配.
     *
     * @see StringMatchers.CONTAINS
     */
    TEXT_CONTAINS(StringMatchers.CONTAINS, true),

    /**
     * 正则完全匹配. `regex.matches(...)`
     *
     * @see KeywordRegexMatchers.MATCHES
     */
    REGEX_MATCHES(KeywordRegexMatchers.MATCHES, false),


    /**
     * 正则包含匹配. `regex.find(...)`
     *
     * @see KeywordRegexMatchers.CONTAINS
     */
    REGEX_CONTAINS(KeywordRegexMatchers.CONTAINS, false),

    ;

    constructor(matcher: StringMatcher, isPlainText: Boolean) : this(matcher.toKeywordMatcher(), isPlainText)

}
