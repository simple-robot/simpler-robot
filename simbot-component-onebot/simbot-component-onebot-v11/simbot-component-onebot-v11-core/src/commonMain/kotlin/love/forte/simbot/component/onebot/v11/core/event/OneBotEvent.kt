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

package love.forte.simbot.component.onebot.v11.core.event

import love.forte.simbot.common.id.LongID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.onebot.common.annotations.OneBotInternalImplementationsOnly
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.core.event.stage.OneBotBotStageEvent
import love.forte.simbot.component.onebot.v11.core.utils.timestamp
import love.forte.simbot.event.BotEvent
import love.forte.simbot.event.Event
import love.forte.simbot.event.FuzzyEventTypeImplementation

/**
 * OneBot11原始事件结构体类型。
 */
public typealias OBSourceEvent = love.forte.simbot.component.onebot.v11.event.RawEvent

/**
 * 一个OneBot组件事件基类。
 * 是 [OneBotEvent] 的父类，
 * 也是一些与OneBot协议本身无关的事件类型
 * (例如 [OneBotBotStageEvent]) 的父类。
 */
@OneBotInternalImplementationsOnly
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface OneBotCommonEvent : Event


/**
 * OneBot11的[事件](https://github.com/botuniverse/onebot-11/tree/master/event)。
 *
 * 所有的事件类型接口都应当仅由内部实现，对外实现不稳定、不保证兼容性。
 *
 * @author ForteScarlet
 */
@OneBotInternalImplementationsOnly
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface OneBotEvent : OneBotCommonEvent {
    /**
     * 来自事件JSON的反序列化数据体。
     */
    public val sourceEvent: OBSourceEvent

    /**
     * 如果能够支持，则获取来自事件JSON的原始字符串。
     * 不支持、无法获取等情况下得到 `null`。
     */
    public val sourceEventRaw: String?

    /**
     * 事件发生的时间戳
     */
    override val time: Timestamp
        get() = sourceEvent.timestamp()

    /**
     * 收到事件的机器人 QQ 号
     */
    public val selfId: LongID
        get() = sourceEvent.selfId

    /**
     * 事件类型
     */
    public val postType: String
        get() = sourceEvent.postType
}

/**
 * OneBot组件事件中的 [BotEvent]。
 */
@OneBotInternalImplementationsOnly
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface OneBotBotEvent : OneBotEvent, BotEvent {
    override val bot: OneBotBot
}
