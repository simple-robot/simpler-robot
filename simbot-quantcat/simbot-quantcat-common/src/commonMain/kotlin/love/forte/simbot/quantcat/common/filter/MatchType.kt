/*
 *     Copyright (c) 2024-2025. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

package love.forte.simbot.quantcat.common.filter

import love.forte.simbot.quantcat.common.keyword.Keyword
import love.forte.simbot.quantcat.common.keyword.KeywordMatcher
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic


/**
 * 关键词匹配类型。
 *
 * @author ForteScarlet
 */
public enum class MatchType(
    private val matcher: KeywordMatcher,
    public val isPlainText: Boolean,
    /**
     * 是否为一种严格匹配模式。
     *
     * @since 4.15.0
     *
     * @see Keyword.isStrict
     */
    public val isStrict: Boolean = false
) {

    /**
     * 全等匹配
     *
     * @see String.equals
     */
    TEXT_EQUALS(
        matcher = { keyword, value -> keyword.text == value },
        isPlainText = true,
        isStrict = true
    ),

    /**
     * 忽略大小写的全等匹配
     *
     * @see String.equals
     *
     */
    TEXT_EQUALS_IGNORE_CASE(
        matcher = { keyword, value -> value.equals(keyword.text, ignoreCase = true) },
        isPlainText = true,
        isStrict = true,
    ),

    /**
     * 首部匹配
     *
     * @see String.startsWith
     */
    TEXT_STARTS_WITH(
        matcher = { keyword, value -> value.startsWith(keyword.text) },
        isPlainText = true,
        isStrict = false
    ),

    /**
     * 尾部匹配.
     *
     * @see String.endsWith
     */
    TEXT_ENDS_WITH(
        matcher = { keyword, value -> value.endsWith(keyword.text) },
        isPlainText = true,
        isStrict = false
    ),

    /**
     * 包含匹配.
     *
     * @see String.contains
     */
    TEXT_CONTAINS(
        matcher = { keyword, value -> keyword.text in value },
        isPlainText = true,
        isStrict = false
    ),

    /**
     * 正则完全匹配. `regex.matches(...)`
     *
     * @see Regex.matches
     */
    REGEX_MATCHES(
        matcher = { keyword, value -> keyword.regex.matches(value) },
        isPlainText = false,
        isStrict = true
    ),

    /**
     * 正则包含匹配. `regex.containsMatchIn(...)`
     *
     * @see Regex.containsMatchIn
     *
     */
    REGEX_CONTAINS(
        matcher = { keyword, value -> keyword.regex.containsMatchIn(value) },
        isPlainText = false,
        isStrict = false
    );

    /**
     * 提供一个匹配关键词 [keyword] 和匹配目标 [value], 对其进行匹配并返回匹配结果。
     *
     * @return [value] 是否与预期关键词 [keyword] 匹配
     */
    public fun match(keyword: Keyword, value: String): Boolean = matcher.test(keyword, value)

}


/**
 * 多值匹配，当可能存在多轮匹配时进行的取值策略。
 *
 * @author ForteScarlet
 *
 */
public enum class MultiFilterMatchType {
    /**
     * 任意匹配成功即可
     */
    ANY,

    /**
     * 需要全部匹配成功
     */
    ALL,

    /**
     * 需要无匹配内容
     */
    NONE;

    public companion object {
        /**
         * 得到一个默认的策略。
         */
        @get:JvmName("getDefault")
        @get:JvmStatic
        public val Default: MultiFilterMatchType get() = ANY
    }
}
