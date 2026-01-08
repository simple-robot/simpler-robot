/*
 * Copyright (c) 2022-2025. ForteScarlet.
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

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.event.BotEvent
import love.forte.simbot.event.Event
import love.forte.simbot.event.FuzzyEventTypeImplementation

/**
 *
 * QQ频道bot的事件总类。
 *
 * @param T 此类型代表其真正事件所得到的结果。
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public abstract class QGEvent<T : Any> : Event {
    /**
     * 事件ID。
     */
    abstract override val id: ID

    /**
     * 接收到事件的时间。
     */
    abstract override val time: Timestamp

    /**
     * 真正的原始事件所得到的事件实体。
     */
    public abstract val sourceEventEntity: T

    /**
     * 接收到的事件的原始JSON字符串
     */
    public abstract val sourceEventRaw: String
}

/**
 * 与 [QGBot] 相关的 QQ频道 [BotEvent] 子类型。
 *
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public abstract class QGBotEvent<T : Any> : QGEvent<T>(), BotEvent {
    abstract override val bot: QGBot
}
