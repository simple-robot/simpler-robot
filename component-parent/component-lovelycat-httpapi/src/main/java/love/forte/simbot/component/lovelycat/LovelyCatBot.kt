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

package love.forte.simbot.component.lovelycat

import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.sender.BotSender
import love.forte.simbot.bot.Bot
import love.forte.simbot.component.lovelycat.message.event.lovelyCatBotInfo


/**
 * 可爱猫bot信息，应当为一个账号（可选）和一个上报地址（必须）
 *
 * @author ForteScarlet
 */
public class LovelyCatBot(
    val code: String,
    val api: LovelyCatApiTemplate,
    override val sender: BotSender
) : Bot {

    /**
     * bot信息
     */
    override val botInfo: BotInfo
        get() = lovelyCatBotInfo(code, api)


    override fun close() {
        // close nothing.
    }
}