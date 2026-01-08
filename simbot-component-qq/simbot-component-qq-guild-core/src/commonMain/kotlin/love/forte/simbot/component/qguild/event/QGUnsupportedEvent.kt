/*
 * Copyright (c) 2023-2025. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.event

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.annotations.FragileSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.qguild.event.Signal


/**
 * 尚未支持的QQ频道事件类型。
 *
 * 用于表示、推送所有标准库中已经存在、但是组件实现中暂无对应的具体事件类型。
 *
 * ### 仅限临时应用
 *
 * [QGUnsupportedEvent] 用于对尚未支持的**组件事件**提供一个"捡漏"的方案，
 * 但是使用 [QGUnsupportedEvent] 应当只是临时的。当某个事件在某个更新中被支持后，
 * 将不会再出现在 [QGUnsupportedEvent] 中。
 *
 * 这很好理解。举个例子，在 `v1.0` 版本中，[QGUnsupportedEvent] 可以接收到 `事件A` 和 `事件B` ——
 * 因为它们都没有自己独立的 _组件事件实现_ 。但是到了 `v1.1` 版本，
 * `事件A` 得到了独立事件类型的支持，而这个变化会直接导致使用 [QGUnsupportedEvent]
 * 永远无法再接收到 [sourceEventEntity] 类型为 `事件A` 的事件了。
 *
 * @author ForteScarlet
 */
@FragileSimbotAPI
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGUnsupportedEvent : QGBotEvent<Signal.Dispatch>() {
    /**
     * 事件ID。一个随机ID。
     */
    override val id: ID = UUID.random()

    /**
     * 事件构建时间，即此对象被构建的时间
     */
    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()

    /**
     * 原始的标准库事件对象。
     */
    abstract override val sourceEventEntity: Signal.Dispatch
}
