@file:JvmName("UserUtil")
package love.forte.simbot.component.kaiheila.api.v3.utils

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.results.FriendInfo
import love.forte.simbot.component.kaiheila.objects.User


@JvmOverloads
public fun User.asFriendInfo(grouping: String? = null): FriendInfo =
    UserAsFriendInfo(this, grouping)

private class UserAsFriendInfo(private val user: User, override val grouping: String?) : FriendInfo, AccountInfo by user {
    override fun toString(): String = "FriendInfoByUser(user=${user.originalData})"
    override val originalData: String = toString()
}
