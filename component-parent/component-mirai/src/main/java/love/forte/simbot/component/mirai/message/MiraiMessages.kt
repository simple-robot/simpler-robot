/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiMessages.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

@file:JvmName("MiraiMessages")
package love.forte.simbot.component.mirai.message

import love.forte.simbot.core.api.message.assists.Permissions
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.MemberPermission


/**
 * member 转为 [Permissions]。
 */
public fun Member.toSimbotPermissions(): Permissions = when (this.permission) {
    MemberPermission.MEMBER -> Permissions.MEMBER
    MemberPermission.ADMINISTRATOR -> Permissions.ADMINISTRATOR
    MemberPermission.OWNER -> Permissions.OWNER
}
