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
import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.GroupContainer
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.containers.PermissionContainer
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.component.mirai.message.*
import love.forte.simbot.component.mirai.message.result.MiraiGroupInfo
import net.mamoe.mirai.event.events.GroupMessageSyncEvent
import net.mamoe.mirai.message.data.source


/**
 * mirai 同步其他客户端发的群消息。
 * 暂时不将此类型归为 [群消息][love.forte.simbot.api.message.events.GroupMsg]，
 * 但是属于[消息事件][MessageGet]。
 */
public interface MiraiGroupMessageSync : MessageGet, GroupContainer, PermissionContainer


/**
 * 虽然接口定义不归类为群消息，但是实际上其本质仍为群消息。
 */
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


    override val groupInfo: GroupInfo = MiraiGroupInfo(event.group)

    override val permission: Permissions
        get() = event.sender.toSimbotPermissions()

    /**
     * 消息标识
     */
    override val flag: MiraiGroupMsgFlag = miraiGroupFlag { MiraiGroupFlagContent(message.source) }
}
