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

package love.forte.simbot.component.mirai.message.result

import love.forte.simbot.api.message.results.FriendInfo
import love.forte.simbot.api.message.results.FriendList
import net.mamoe.mirai.Bot


/**
 * [FriendList] 实现。
 */
public class MiraiFriendList(bot: Bot, limit: Int = -1) : FriendList {
    override val results: List<FriendInfo> by lazy(LazyThreadSafetyMode.PUBLICATION) {
        if (limit > 0) {
            bot.friends.asSequence().take(limit).map { MiraiFriendInfo(it) }.toList()
        } else {
            bot.friends.map { MiraiFriendInfo(it) }
        }
    }
    override val originalData: String = "MiraiFriendList(bot=$bot)"
}