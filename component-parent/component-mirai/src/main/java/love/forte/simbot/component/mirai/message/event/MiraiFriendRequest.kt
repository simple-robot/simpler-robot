/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiFriendRequest.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.message.event

import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.assists.flag
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.events.FriendAddRequest
import love.forte.simbot.component.mirai.utils.userAvatar
import net.mamoe.mirai.event.events.NewFriendRequestEvent


/**
 * 好友添加请求的标识主体。
 */
public data class MiraiFriendRequestFlagContent(val event: NewFriendRequestEvent) :
    FriendAddRequest.FlagContent {
    override val id: String
        get() = event.eventId.toString()
}

/**
 * 好友申请事件对应的 [AccountInfo] 数据。
 */
private data class MiraiFriendRequester(private val event: NewFriendRequestEvent) : AccountInfo {
    override val accountCode: String
        get() = event.fromId.toString()
    override val accountCodeNumber: Long
        get() = event.fromId

    override val accountNickname: String?
        get() = event.fromNick
    override val accountRemark: String? = null
    override val accountAvatar: String?
        get() = userAvatar(event.fromId)
}


/**
 *
 * mirai 好友添加请求。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class MiraiFriendRequest(event: NewFriendRequestEvent) :
    AbstractMiraiMsgGet<NewFriendRequestEvent>(event), FriendAddRequest {
    override val id: String = event.eventId.toString()

    /** 请求者。 */
    override val accountInfo: AccountInfo = MiraiFriendRequester(event)

    /** 好友申请的消息。如果消息为空，则返回null。 */
    override val msg: String? = event.message.takeIf { it.isNotBlank() }

    override val flag: Flag<MiraiFriendRequestFlagContent> = flag { MiraiFriendRequestFlagContent(event) }
}