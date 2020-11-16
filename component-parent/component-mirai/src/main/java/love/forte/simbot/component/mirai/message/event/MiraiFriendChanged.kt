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

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.events.FriendIncrease
import love.forte.simbot.api.message.events.FriendReduce
import love.forte.simbot.component.mirai.message.MiraiFriendAccountInfo
import net.mamoe.mirai.event.events.FriendAddEvent
import net.mamoe.mirai.event.events.FriendDeleteEvent


/**
 * Mirai 好友增加事件。
 * id由事件的 hashcode 来作为唯一值。
 */
public class MiraiFriendAdded(event: FriendAddEvent) : AbstractMiraiMsgGet<FriendAddEvent>(event), FriendIncrease {
    override val id: String = "MFInc-${event.hashCode()}"
    override val accountInfo: AccountInfo = MiraiFriendAccountInfo(event.friend)
}




/**
 * Mirai 好友减少事件。
 * id由事件的 hashcode 来作为唯一值。
 */
public class MiraiFriendDeleted(event: FriendDeleteEvent) : AbstractMiraiMsgGet<FriendDeleteEvent>(event), FriendReduce {
    override val id: String = "MFRed-${event.hashCode()}"
    override val accountInfo: AccountInfo = MiraiFriendAccountInfo(event.friend)
}


