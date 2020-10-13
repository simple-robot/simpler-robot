/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiSenderUtil.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */
@file:JvmName("MiraiSenderUtil")
package love.forte.simbot.component.mirai.sender

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.getGroupOrNull

/**
 * 获取某个群的群员。
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun Bot.getGroupMember(groupCode: Long, memberCode: Long): Member = getGroup(groupCode)[memberCode]

/**
 * 获取某个群的群员。
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun Bot.getGroupMemberOrNull(groupCode: Long, memberCode: Long): Member? = getGroupOrNull(groupCode)?.getOrNull(memberCode)

