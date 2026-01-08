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

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.bot.QQGuildBotManager
import love.forte.simbot.event.BotRegisteredEvent
import love.forte.simbot.event.BotStartedEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation

/**
 *
 * QQ组件中对内部 bot 事件的实现。
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public sealed interface QGInternalBotEvent : QGInternalEvent

/**
 * qq频道组件中，每当 [QQGuildBotManager] 通过任意 [QQGuildBotManager.register] 注册并得到Bot实例后触发的事件。
 *
 * 此事件会在注册完成后**异步**触发.
 *
 * @see QQGuildBotManager
 * @see QQGuildBotManager.register
 */
public abstract class QGBotRegisteredEvent : QGInternalBotEvent, BotRegisteredEvent {
    abstract override val bot: QGBot

    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()
    override val id: ID = UUID.random()
}

/**
 * qq频道组件中，每当 [QGBot.start] 被执行的时候会被推送的事件。
 * 当事件被推送的时候代表此bot实际上已经完成的 `start` 的逻辑，但是[QGBot.start]会直到事件处理流程完成后才会最终返回。
 *
 * @see QGBot
 * @see QGBot.start
 */
public abstract class QGBotStartedEvent : QGInternalBotEvent, BotStartedEvent {
    abstract override val bot: QGBot

    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()
    override val id: ID = UUID.random()
}
