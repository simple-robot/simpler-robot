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
 *
 */

package love.forte.simbot.event.internal

import love.forte.simbot.Bot
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
 * @author ForteScarlet
 */
public abstract class BotRegisteredEvent : InternalEvent(), BotContainer {
    abstract override val bot: Bot

    public companion object Key : BaseInternalKey<BotRegisteredEvent>("api.internal.bot.registered", InternalBotEvent) {
        override fun safeCast(value: Any): BotRegisteredEvent? = doSafeCast(value)
    }
}

/**
 *
 * 某个Bot执行了 [Bot.start]
 *
 * @author ForteScarlet
 */
public abstract class BotStartedEvent : InternalEvent(), BotContainer {
    abstract override val bot: Bot

    public companion object Key : BaseInternalKey<BotStartedEvent>("api.internal.bot.started", InternalBotEvent) {
        override fun safeCast(value: Any): BotStartedEvent? = doSafeCast(value)
    }
}
