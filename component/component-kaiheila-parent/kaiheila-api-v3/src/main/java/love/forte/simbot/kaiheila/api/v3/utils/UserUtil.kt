@file:JvmName("UserUtil")

package love.forte.simbot.kaiheila.api.v3.utils

import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.GroupAccountInfo
import love.forte.simbot.api.message.results.*
import love.forte.simbot.kaiheila.objects.Guild
import love.forte.simbot.kaiheila.objects.PermissionType
import love.forte.simbot.kaiheila.objects.User


/** [User] as [FriendInfo] */
@JvmOverloads
public fun User.asFriendInfo(grouping: String? = null): FriendInfo =
    UserAsFriendInfo(this, grouping)

private class UserAsFriendInfo(private val user: User, override val grouping: String?) : FriendInfo,
    AccountInfo by user {
    override fun toString(): String = "FriendInfoByUser(user=${user.originalData})"
    override val originalData: String = toString()
}


/** [User] as [GroupMemberInfo] */
public fun User.asGroupMemberInfo(guild: Guild): GroupMemberInfo =
    UserAsGroupMemberInfo(this, guild)


private class UserAsGroupMemberInfo(
    private val user: User,
    override val groupInfo: Guild,
) : GroupMemberInfo, GroupAccountInfo by user {
    override val permission: Permissions

    init {
        val adminRoles = groupInfo.roles.asSequence().mapNotNull { role ->
            if (role.permissions.contains(PermissionType.ADMIN)) role.roleId else null
        }.toSet()

        permission = when {
            groupInfo.masterId == user.id -> Permissions.OWNER
            user.roles.any { userRole -> userRole in adminRoles } -> Permissions.ADMINISTRATOR
            else -> Permissions.MEMBER
        }
    }

    override fun toString(): String = "GroupMemberInfoByUser(user=${user.originalData})"
    override val originalData: String get() = toString()
}


public fun User.asOwner(): GroupOwner = UserAsOwner(this)

private class UserAsOwner(private val user: User) : GroupOwner, GroupAccountInfo by user {
    override val permission: Permissions
        get() = Permissions.OWNER
}

public fun User.asAdmin(): GroupAdmin = UserAsAdmin(this)

private class UserAsAdmin(private val user: User) : GroupAdmin, GroupAccountInfo by user {
    override val permission: Permissions
        get() = Permissions.ADMINISTRATOR
}



public fun User.asMuteInfo(lastTime: Long = -1): MuteInfo = UserAsMuteInfo(this, lastTime)

private class UserAsMuteInfo(private val user: User, override val lastTime: Long) : MuteInfo, GroupAccountInfo by user {
    override val originalData: String
        get() = toString()
}
