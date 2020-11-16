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

package love.forte.simbot.api.message.results

import love.forte.simbot.api.message.containers.AccountContainer
import love.forte.simbot.api.message.containers.GroupContainer
import love.forte.simbot.api.message.containers.PermissionContainer


/**
 *
 * 群成员的信息。其中包含了[群信息][GroupContainer] 与 这个人的 [账号信息][AccountContainer] 与 [权限信息][PermissionContainer]
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface GroupMemberInfo : Result, GroupContainer, AccountContainer, PermissionContainer


/**
 * 群成员列表。
 */
public interface GroupMemberList : MultipleResults<GroupMemberInfo>, GroupContainer