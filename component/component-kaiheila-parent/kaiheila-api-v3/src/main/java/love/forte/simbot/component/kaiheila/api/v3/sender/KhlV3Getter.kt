package love.forte.simbot.component.kaiheila.api.v3.sender

import love.forte.simbot.api.message.containers.*
import love.forte.simbot.api.message.results.*
import love.forte.simbot.api.sender.Getter
import love.forte.simbot.component.kaiheila.KhlBot
import love.forte.simbot.component.kaiheila.api.KhlSender
import love.forte.simbot.component.kaiheila.api.RespPageMeta
import love.forte.simbot.component.kaiheila.api.doRequestForData
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildListReq
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildView
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildViewReq
import love.forte.simbot.component.kaiheila.api.v3.guild.guildUserListReq
import love.forte.simbot.component.kaiheila.api.v3.user.UserViewReq
import love.forte.simbot.component.kaiheila.api.v3.utils.asAdmin
import love.forte.simbot.component.kaiheila.api.v3.utils.asFriendInfo
import love.forte.simbot.component.kaiheila.api.v3.utils.asGroupMemberInfo
import love.forte.simbot.component.kaiheila.api.v3.utils.asOwner
import love.forte.simbot.component.kaiheila.objects.PermissionType
import love.forte.simbot.component.kaiheila.objects.User
import sun.jvm.hotspot.debugger.Page

/**
 *
 * @author ForteScarlet
 */
public class KhlV3Getter(
    private val bot: KhlBot,
    private val def: Getter.Def,
) : KhlSender.Getter {
    override val authInfo: AuthInfo get() = emptyAuthInfo()
    override val botInfo: BotInfo get() = bot.botInfo

    private suspend fun friendInfo(guildId: String, code: String): User {
        return UserViewReq(guildId, code).doRequestForData(bot)!!
    }

    override suspend fun friendInfo(code: String): FriendInfo = def.getFriendInfo(code)

    override suspend fun friendInfo(code: AccountContainer): FriendInfo {
        if (code is GroupContainer) {
            return friendInfo(code.groupInfo.groupCode, code.accountInfo.accountCode).asFriendInfo()
        }
        if (code is GroupCodeContainer) {
            return friendInfo(code.groupCode, code.accountInfo.accountCode).asFriendInfo()
        }
        return def.getFriendInfo(code)
    }

    override suspend fun friendInfo(code: AccountCodeContainer): FriendInfo {
        if (code is GroupCodeContainer) {
            return friendInfo(code.groupCode, code.accountCode).asFriendInfo()
        }
        if (code is GroupContainer) {
            return friendInfo(code.groupInfo.groupCode, code.accountCode).asFriendInfo()
        }
        return super.getFriendInfo(code)
    }

    override suspend fun memberInfo(group: String, code: String): GroupMemberInfo {
        val view = UserViewReq(code, group).doRequestForData(bot)!!
        val groupInfo = groupInfo(group)

        return view.asGroupMemberInfo(groupInfo)
    }

    override suspend fun groupInfo(group: String): GuildView {
        val guildView: GuildView = GuildViewReq(group).doRequestForData(bot)!!
        // Init owner
        val owner = UserViewReq(guildView.masterId, guildView.id).doRequestForData(bot)!!
        guildView.owner = owner.asOwner()

        val adminRole = guildView.roles.find { r -> r.permissions.contains(PermissionType.ADMIN) }
        val total: Int

        if (adminRole != null) {
            // Init admin list
            val admins = guildUserListReq {
                this.guildId = group
                this.mobileVerified = 1
                this.roleId = adminRole.roleId
            }.doRequestForData(bot)!!
            // admins
            guildView.admins = admins.items.map(User::asAdmin)

            // init total
            total = admins.meta.total
        } else {
            val meta = guildUserListReq {
                this.guildId = group
                mobileVerified = 1
                pageSize = 1
            }.doRequestForData(bot)?.meta

            guildView.admins = emptyList()
            total = meta?.total ?: -1
        }


        guildView.total = total

        return guildView
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Not support.")
    override suspend fun friendList(cache: Boolean, limit: Int): FriendList =
        emptyFriendList()

    override suspend fun groupList(cache: Boolean, limit: Int): GroupList {
        val groups = GuildListReq.NoSort.doRequestForData(bot).items

        TODO("Not yet implemented")
    }

    override suspend fun groupMemberList(group: String, cache: Boolean, limit: Int): GroupMemberList {
        TODO("Not yet implemented")
    }

    override suspend fun banList(group: String, cache: Boolean, limit: Int): MuteList {
        TODO("Not yet implemented")
    }

    override suspend fun groupNoteList(group: String, cache: Boolean, limit: Int): GroupNoteList {
        TODO("Not yet implemented")
    }
}