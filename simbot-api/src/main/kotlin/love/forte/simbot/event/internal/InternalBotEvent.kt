/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.event.internal

import love.forte.simbot.ID
import love.forte.simbot.bot.Bot
import love.forte.simbot.definition.BotContainer
import love.forte.simbot.message.doSafeCast


/**
 * 与 [Bot] 相关的内部流转事件。
 *
 * @author ForteScarlet
 */
public abstract class InternalBotEvent : InternalEvent() {
    public companion object Key : BaseInternalKey<InternalBotEvent>("api.internal.bot", InternalEvent) {
        override fun safeCast(value: Any): InternalBotEvent? = doSafeCast(value)
    }
}


/**
 * 一个被注册的bot。只要被注册就应触发。
 *
 * 由于 [love.forte.simbot.bot.BotManager.register] 并非可挂起函数，
 * 因此通常情况下BotManager在完成bot注册逻辑后会立即返回，而对于 [BotRegisteredEvent] 事件的推送会异步的进行.
 *
 * 在对 [BotRegisteredEvent] 的处理时，请注意你处理的bot是 有概率已经启动、关闭甚至被清除了的，
 * 且尽量避免在此事件中对bot进行 `start` 等相关操作。
 *
 * @see love.forte.simbot.bot.BotManager
 * @see love.forte.simbot.bot.BotManager.register
 *
 * @author ForteScarlet
 */
public abstract class BotRegisteredEvent : InternalBotEvent(), BotContainer {
    abstract override val id: ID
    abstract override val bot: Bot

    public companion object Key : BaseInternalKey<BotRegisteredEvent>("api.internal.bot.registered", InternalBotEvent) {
        override fun safeCast(value: Any): BotRegisteredEvent? = doSafeCast(value)
    }
}

/**
 *
 * 某个Bot执行了 [Bot.start].
 *
 * [BotStartedEvent] 事件的推送应当是**异步**的，不应影响到bot正常的启动流程。
 *
 *
 * @see Bot.start
 *
 * @author ForteScarlet
 */
public abstract class BotStartedEvent : InternalBotEvent(), BotContainer {
    abstract override val id: ID
    abstract override val bot: Bot

    public companion object Key : BaseInternalKey<BotStartedEvent>("api.internal.bot.started", InternalBotEvent) {
        override fun safeCast(value: Any): BotStartedEvent? = doSafeCast(value)
    }
}
