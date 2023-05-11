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

package love.forte.simboot.annotation

/**
 * 指定一个参数，此参数为通过 [love.forte.simboot.filter.Keyword]
 * 解析而得到的动态参数提取器中的内容。
 *
 * @param value 所需动态参数的key。
 * @param required 对于参数绑定器来讲其是否为必须的。
 *  如果不是必须的，则在无法获取参数后传递null作为结果，否则将会抛出异常并交由后续绑定器处理。
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
public annotation class FilterValue(val value: String, val required: Boolean = true)
