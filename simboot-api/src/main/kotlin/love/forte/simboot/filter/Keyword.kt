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

package love.forte.simboot.filter
/**
 * 匹配关键词.
 *
 */
public interface Keyword {

    /**
     * 匹配关键字解析出来的 [Regex] 实例。
     */
    public val regex: Regex

    /** 匹配关键字对应的原始文本。 */
    public val text: String

    /** 匹配关键字对应的参数提取器。 */
    public val matcherValue: MatcherValue
}




