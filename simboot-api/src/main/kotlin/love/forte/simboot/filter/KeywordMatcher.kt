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

import love.forte.simboot.Matcher

/**
 * 使用 [Keyword] 作为匹配规则的匹配器。
 * @author ForteScarlet
 */
public fun interface KeywordMatcher : Matcher<String, Keyword>


/**
 * 使用 keyword 进行得正则匹配.
 *
 * 如果你想使用 [StringMatcher] 作为 [KeywordMatcher] 使用，参考 [toKeywordMatcher].
 *
 */
public enum class KeywordRegexMatchers(private val matcher: KeywordMatcher) :
    KeywordMatcher by matcher {
    /**
     * 完整正则匹配
     */
    MATCHES({ t, r -> r.regex.matches(t) }),

    /**
     * 包含正则匹配
     */
    CONTAINS({ t, r -> r.regex.containsMatchIn(t) }),

}


public fun StringMatcher.toKeywordMatcher(): KeywordMatcher = KeywordMatcher { t: String, r: Keyword -> match(t, r.text) }
