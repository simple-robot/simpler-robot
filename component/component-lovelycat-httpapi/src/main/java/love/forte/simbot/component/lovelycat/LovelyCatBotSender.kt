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
import love.forte.simbot.api.sender.Getter
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.api.sender.Setter


public class LovelyCatBotSender(
    sender: Sender,
    setter: Setter,
    getter: Getter,
    override val botInfo: BotInfo
): BotSender(sender, setter, getter, botInfo)


