/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.common.api

import kotlinx.serialization.Serializable
import love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor
import love.forte.simbot.component.onebot.common.annotations.SourceEventConstructor


/**
 * 用于心跳事件中 `status` 属性和
 * `get_status` API 中响应数据的状态结果。
 *
 * [`get_status`-获取运行状态](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_status-获取运行状态)
 *
 * status 在定义中存在 “OneBot 实现自行添加的其它内容” 的扩展字段。
 * 这些内容可以考虑从事件的原始字符串或API响应的原始字符串中自行解析，
 * 它的不确定性导致其难以直接通过 Kotlin Serialization 对其进行定义。
 *
 * @property online 当前 QQ 在线，`null` 表示无法查询到在线状态
 * @property good 状态符合预期，意味着各模块正常运行、功能正常，且 QQ 在线
 *
 * @author ForteScarlet
 */
@Serializable
public data class StatusResult
@ApiResultConstructor
@SourceEventConstructor
internal constructor(
    public val online: Boolean? = null,
    public val good: Boolean = false,
)
