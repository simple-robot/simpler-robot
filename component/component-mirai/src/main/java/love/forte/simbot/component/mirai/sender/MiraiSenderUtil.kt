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
@file:JvmName("MiraiSenderUtil")
package love.forte.simbot.component.mirai.sender

import love.forte.simbot.component.mirai.message.EmptySingleMessage
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain

/**
 * 获取某个群的群员。
 */
@Suppress("NOTHING_TO_INLINE")
@Deprecated("use member(...)", ReplaceWith("member(groupCode, memberCode)"))
internal inline fun Bot.getGroupMember(groupCode: Long, memberCode: Long): Member = member(groupCode, memberCode)

/**
 * 获取某个群的群员。
 */
@Suppress("NOTHING_TO_INLINE")
@Deprecated("use memberOrNull(...)", ReplaceWith("memberOrNull(groupCode, memberCode)"))
internal inline fun Bot.getGroupMemberOrNull(groupCode: Long, memberCode: Long): Member? = memberOrNull(groupCode, memberCode)


/**
 * 判断 [Message] 为空。
 */
internal fun Message.isNotEmptyMsg(): Boolean =
            this !== EmptySingleMessage &&
            this !== EmptyMessageChain &&
            if (this is MessageChain) this.isNotEmpty() else true












