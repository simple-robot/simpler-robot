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
 * 字符串之间的匹配器。
 */
public fun interface StringMatcher : Matcher<String, String>


/**
 * 常见的字符串匹配器。
 */
public enum class StringMatchers(private val matcher: StringMatcher) :
    StringMatcher by matcher {

    /**
     * 全等匹配
     */
    EQUALS({ t, r -> t == r }),

    /**
     * 忽略大小写的全等匹配
     */
    EQUALS_IGNORE_CASE({ t, r -> t.equals(r, true) }),


    /**
     * 首部匹配
     */
    STARTS_WITH({ t, r -> t.startsWith(r) }),

    /**
     * 尾部匹配.
     *
     */
    ENDS_WITH({ t, r -> t.endsWith(r) }),

    /**
     * 包含匹配.
     *
     */
    CONTAINS({ target, rule -> rule in target }),

}


