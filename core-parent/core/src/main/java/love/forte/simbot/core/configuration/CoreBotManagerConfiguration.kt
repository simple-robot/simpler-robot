/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

package love.forte.simbot.core.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.api.sender.DefaultMsgSenderFactories
import love.forte.simbot.api.sender.MsgSenderFactories
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.BotVerifier
import love.forte.simbot.core.bot.CoreBotManager

/**
 *
 * 配置 [BotManager]。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans("coreBotManagerConfiguration")
public class CoreBotManagerConfiguration {

    /**
     * 配置一个 [BotManager] 实例。
     */
    @CoreBeans("coreBotManager")
    fun coreBotManager(
        verifier: BotVerifier,
        msgSenderFactories: MsgSenderFactories,
        defSenderFactories: DefaultMsgSenderFactories,
    ): BotManager =
        CoreBotManager(verifier, msgSenderFactories, defSenderFactories)

}