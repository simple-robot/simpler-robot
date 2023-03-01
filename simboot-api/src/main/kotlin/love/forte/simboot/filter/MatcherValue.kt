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
 * 匹配器动态参数.
 * 动态参数的规则为 `{{name[,regex]}}`, 或者原生的正则分组 `(?<name>regex)`
 *
 * @author forte
</name> */
public interface MatcherValue {
    /**
     * 获取原始字符串
     *
     * @return 原始字符串
     */
    public val original: String

    /**
     * 获取用于匹配的正则
     *
     * @return 匹配正则
     */
    public val regex: Regex

    /**
     * 是否匹配. 使用的完全正则匹配: `regex.matches(text)`
     *
     * @param text text
     * @return matches.
     */
    public fun matches(text: String): Boolean

    /**
     * 根据变量名称获取一个动态参数。
     * 此文本需要符合正则表达式。
     *
     * @param name 变量名
     * @param text 文本
     * @return 得到的参数
     */
    public fun getParam(name: String, text: String): String?

    /**
     * 从一段匹配的文本中提取出需要的参数。
     *
     *
     * 此文本需要符合正则表达式, 否则得到null。
     *
     * @param text 匹配文本
     * @return 参数提取器。
     */
    public fun getParameters(text: String?): MatchParameters
}
