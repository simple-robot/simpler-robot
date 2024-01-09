/*
 *     Copyright (c) 2021-2024. ForteScarlet.
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

package love.forte.simbot.quantcat.annotations

import love.forte.simbot.quantcat.common.filter.FilterValueProperties

/**
 * 指定一个参数，此参数为通过 [love.forte.simbot.quantcat.annotations.Filter]
 * 解析而得到的动态参数提取器中的内容。
 *
 * 参数提取格式基于正则匹配模式，参考 [Filter.value] 中的相关说明。
 *
 * @param value 所需动态参数的key。
 * @param required 对于参数绑定器来讲其是否为必须的。
 *  如果不是必须的，则在无法获取参数后传递null作为结果，否则将会抛出异常并交由后续绑定器处理。
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
public annotation class FilterValue(val value: String, val required: Boolean = true)

/**
 * [FilterValue] to [FilterValueProperties].
 */
public fun FilterValue.toProperties(): FilterValueProperties =
    FilterValueProperties(
        value = value,
        required = required
    )
