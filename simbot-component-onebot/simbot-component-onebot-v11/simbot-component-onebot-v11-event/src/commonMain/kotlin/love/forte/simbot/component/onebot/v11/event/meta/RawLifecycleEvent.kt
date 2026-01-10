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

package love.forte.simbot.component.onebot.v11.event.meta

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.v11.event.ExpectEventType

/**
 * [生命周期](https://github.com/botuniverse/onebot-11/blob/master/event/meta.md#生命周期)
 *
 *
 * @property subType 事件子类型，分别表示 OneBot 启用、停用、WebSocket 连接成功.
 * 可能的值: `enable`、`disable`、`connect`.
 * 注意，目前生命周期元事件中，只有 HTTP POST 的情况下可以收到 `enable` 和 `disable`，
 * 只有正向 WebSocket 和反向 WebSocket 可以收到 `connect`。
 */
@Serializable
@ExpectEventType(postType = RawMetaEvent.POST_TYPE, subType = "lifecycle")
public data class RawLifecycleEvent(
    override val time: Long,
    @SerialName("self_id")
    override val selfId: LongID,
    @SerialName("post_type")
    override val postType: String,
    @SerialName("meta_event_type")
    override val metaEventType: String,
    @SerialName("sub_type")
    public val subType: String,
) : RawMetaEvent
