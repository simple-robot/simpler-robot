/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.event.internal

import love.forte.simbot.bot.Bot
import love.forte.simbot.ID
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
 * 由于 [love.forte.simbot.BotManager.register] 并非可挂起函数，
 * 因此通常情况下BotManager在完成bot注册逻辑后会立即返回，而对于 [BotRegisteredEvent] 事件的推送会异步的进行.
 *
 * 在对 [BotRegisteredEvent] 的处理时，请注意你处理的bot是 有概率已经启动、关闭甚至被清除了的，
 * 且尽量避免在此事件中对bot进行 `start` 等相关操作。
 *
 * @see love.forte.simbot.BotManager
 * @see love.forte.simbot.BotManager.register
 *
 * @author ForteScarlet
 */
public abstract class BotRegisteredEvent : InternalEvent(), BotContainer {
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
 * 因为 [Bot.start] 本质上是可挂起的，因此通常情况下bot执行`start`后，
 * 会推送并等待针对事件[BotStartedEvent]的整个处理流程完成后才会结束挂起。
 *
 * 因此对于 [BotStartedEvent] 的处理中，需要尽量避免过长时间的挂起或阻塞，且尽量避免嵌套执行start。
 *
 *
 * @see Bot.start
 *
 * @author ForteScarlet
 */
public abstract class BotStartedEvent : InternalEvent(), BotContainer {
    abstract override val id: ID
    abstract override val bot: Bot

    public companion object Key : BaseInternalKey<BotStartedEvent>("api.internal.bot.started", InternalBotEvent) {
        override fun safeCast(value: Any): BotStartedEvent? = doSafeCast(value)
    }
}
