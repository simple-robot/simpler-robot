/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiFriendInfo.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.message.result

import love.forte.simbot.component.mirai.message.MiraiFriendAccountInfo
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.results.FriendInfo
import net.mamoe.mirai.contact.Friend

/**
 *
 * mirai的 [FriendInfo] 实现。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class MiraiFriendInfo(friend: Friend) : FriendInfo {
    /** mirai不支持获取好友分组。(mirai 1.3.2) */
    override val grouping: String? = null
    override val originalData: String = "MiraiFriendInfo($friend)"
    override val accountInfo: AccountInfo = MiraiFriendAccountInfo(friend)
}