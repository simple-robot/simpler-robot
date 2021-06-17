/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.api.sender

import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.events.MsgGet


/**
 * 警告送信器工厂
 */
object WarnFactories : DefaultMsgSenderFactories {
    override val defaultSenderFactory: DefaultSenderFactory
        get() = WarnGetterFactory
    override val defaultSetterFactory: DefaultSetterFactory
        get() = WarnGetterFactory
    override val defaultGetterFactory: DefaultGetterFactory
        get() = WarnGetterFactory
}

public object WarnGetterFactory : DefaultSenderFactory, DefaultSetterFactory, DefaultGetterFactory {
    override fun getOnMsgGetter(msg: MsgGet): Getter.Def = WarnGetter
    override fun getOnBotGetter(bot: BotContainer): Getter.Def = WarnGetter

    override fun getOnMsgSetter(msg: MsgGet): Setter.Def = WarnSetter
    override fun getOnBotSetter(bot: BotContainer): Setter.Def = WarnSetter

    override fun getOnMsgSender(msg: MsgGet): Sender.Def = WarnSender
    override fun getOnBotSender(bot: BotContainer): Sender.Def = WarnSender
}
