package love.forte.simbot.common.api.messages.results

import love.forte.simbot.common.api.messages.containers.AccountContainer
import love.forte.simbot.common.api.messages.containers.GroupContainer
import love.forte.simbot.common.api.messages.containers.PermissionContainer


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