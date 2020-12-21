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

@file:JvmName("MiraiContactUtil")
@file:Suppress("NOTHING_TO_INLINE")

package love.forte.simbot.component.mirai.sender

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member


internal inline fun Bot.member(group: Long, code: Long): Member = this.getGroupOrFail(group).getOrFail(code)
internal inline fun Bot.group(group: Long): Group = this.getGroupOrFail(group)
internal inline fun Bot.friend(friend: Long): Friend = this.getFriendOrFail(friend)

internal inline fun Bot.memberOrNull(group: Long, code: Long): Member? = this.getGroup(group)?.get(code)
internal inline fun Bot.groupOrNull(group: Long): Group? = this.getGroup(group)
internal inline fun Bot.friendOrNull(friend: Long): Friend? = this.getFriend(friend)