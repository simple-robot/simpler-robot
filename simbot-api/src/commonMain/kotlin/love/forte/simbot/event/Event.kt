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

package love.forte.simbot.event

import love.forte.simbot.bot.Bot
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.IDContainer
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.Component
import love.forte.simbot.suspendrunner.STP

/**
 * 一个 **事件**。
 *
 * @author ForteScarlet
 */
public interface Event : IDContainer {
    /**
     * 事件的ID。
     * 如果平台事件中不存在可用ID，则此值可能为一个随机值。
     */
    public override val id: ID

    /**
     * 事件发生的时间或此事件被接收到的时间。
     *
     * 如果原始事件内支持此类时间属性则直接使用，
     * 否则 [time] 有可能是 [Event] 对象被构建或原始事件被接收到的时间。
     */
    public val time: Timestamp
}

/**
 * 一个由某个组件所发起的事件。
 *
 * @author ForteScarlet
 */
public interface ComponentEvent : Event {
    /**
     * 所属组件
     */
    public val component: Component
}

/**
 * 与 [Bot] 相关的事件。
 *
 * @author ForteScarlet
 */
public interface BotEvent : ComponentEvent {
    /**
     * 相关的 [Bot]。
     */
    public val bot: Bot

    /**
     * [component] 默认由 [bot.component][Bot.component] 提供。
     *
     */
    override val component: Component
        get() = bot.component
}

/**
 * 存在一个 [主要事件中心][content] 的事件类型。
 */
@STP
public interface ContentEvent : Event {
    /**
     * 这个事件的主要事件中心值。
     */
    public suspend fun content(): Any?
}

/**
 * 存在一个 [源头][source] 的事件类型。
 */
@STP
public interface SourceEvent : Event {
    /**
     * 这个事件的“源头”。
     */
    public suspend fun source(): Any?
}

/**
 * 发生了某种变化的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ChangeEvent : ContentEvent {
    /**
     * 发生了变化的主体。
     */
    override suspend fun content(): Any?
}
