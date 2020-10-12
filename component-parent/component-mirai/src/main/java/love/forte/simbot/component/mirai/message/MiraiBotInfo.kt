/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiBotInfo.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.message

import love.forte.simbot.core.api.message.containers.BotInfo
import net.mamoe.mirai.Bot


/**
 * 基于mirai的 [Bot] 的 [BotInfo] 实现。
 */
public data class MiraiBotInfo(private val bot: Bot) : BotInfo {
    /** 当前的bot的账号 */
    override val botCode: String
        get() = bot.id.toString()

    /** 当前的bot的账号 */
    override val botCodeNumber: Long
        get() = bot.id

    /** 机器人的名称 */
    override val botName: String
        get() = bot.nick

    /** 机器人的头像 */
    override val botAvatar: String
        get() = "http://q1.qlogo.cn/g?b=qq&nk=${bot.id}&s=640"

}