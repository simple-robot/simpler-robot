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


/**
 * 基于关键词 [Keyword] 的字符串匹配器接口。
 *
 * @author ForteScarlet
 */
public fun interface KeywordMatcher {
    /**
     * 提供一个匹配关键词 [keyword] 和匹配目标 [value], 对其进行匹配并返回匹配结果。
     *
     * @return [value] 是否与预期关键词 [keyword] 匹配
     */
    public fun test(keyword: Keyword, value: String): Boolean

}

/**
 * 一组用于匹配的关键词。
 *
 * @see KeywordMatcher
 * @author ForteScarlet
 */
public interface Keyword {
    /**
     * 关键词的字符串文本。
     */
    public val text: String

    /**
     * 关键词文本 [text] 转化后的正则类型。
     */
    public val regex: Regex

    /**
     * 基于正则 [regex] 的匹配与参数提取器。
     */
    public val regexValueMatcher: ValueMatcher
}
