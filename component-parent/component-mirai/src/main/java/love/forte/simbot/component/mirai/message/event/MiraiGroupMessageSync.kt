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

package love.forte.simbot.component.mirai.message.event

import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.component.mirai.message.MiraiBotAccountInfo
import love.forte.simbot.component.mirai.message.MiraiMessageChainContent
import love.forte.simbot.component.mirai.message.miraiMessageFlag
import net.mamoe.mirai.event.events.GroupMessageSyncEvent
import net.mamoe.mirai.message.data.source


/**
 * mirai 同步其他客户端发的群消息。
 * 暂时不将此类型归为 [群消息][love.forte.simbot.api.message.events.GroupMsg]，
 * 但是属于[消息事件][MessageGet]。
 */
public interface MiraiGroupMessageSync : MessageGet




public class MiraiGroupMessageSyncImpl(event: GroupMessageSyncEvent) : MiraiMessageMsgGet<GroupMessageSyncEvent>(event),
    MiraiGroupMessageSync {
    /**
     * 账号的信息。即bot自己。
     */
    override val accountInfo: AccountInfo = MiraiBotAccountInfo(event.bot)

    /**
     *  消息事件的消息正文文本。
     */
    override val msgContent: MessageContent = MiraiMessageChainContent(message)

    /**
     * 消息标识
     */
    override val flag: Flag<MessageGet.MessageFlagContent> = miraiMessageFlag(MiraiGroupFlagContent(message.source))
}
