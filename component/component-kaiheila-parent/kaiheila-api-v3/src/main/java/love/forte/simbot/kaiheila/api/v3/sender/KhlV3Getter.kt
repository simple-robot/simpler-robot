package love.forte.simbot.kaiheila.api.v3.sender

import kotlinx.coroutines.async
import love.forte.simbot.api.message.containers.*
import love.forte.simbot.api.message.results.*
import love.forte.simbot.api.sender.Getter
import love.forte.simbot.component.kaiheila.KhlBot
import love.forte.simbot.component.kaiheila.api.KhlSender
import love.forte.simbot.component.kaiheila.api.doRequestForData
import love.forte.simbot.component.kaiheila.api.v3.channel.ChannelView
import love.forte.simbot.component.kaiheila.api.v3.channel.ChannelViewReq
import love.forte.simbot.component.kaiheila.api.v3.guild.*
import love.forte.simbot.component.kaiheila.api.v3.user.UserViewReq
import love.forte.simbot.component.kaiheila.api.v3.utils.*
import love.forte.simbot.component.kaiheila.objects.PermissionType
import love.forte.simbot.component.kaiheila.objects.User

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
        val groupInfo = guildInfo(group)

        return view.asGroupMemberInfo(groupInfo)
    }


    override suspend fun groupInfo(group: String): GroupFullInfo {
        return when {
            group.startsWith("guild:") -> guildInfo(group.substring(6))
            group.startsWith("channel:") -> channelInfo(group.substring(8))
            else -> guildInfo(group)
        }
    }


    private suspend fun channelInfo(channelId: String): ChannelView {
        val channelView = ChannelViewReq(channelId).doRequestForData(bot)!!
        val owner = UserViewReq(channelView.userId, channelView.guildId).doRequestForData(bot)!!
        channelView.owner = owner.asOwner()

        val meta = guildUserListReq {
            this.guildId = channelView.guildId
            mobileVerified = 1
            pageSize = 1
        }.doRequestForData(bot)?.meta

        channelView.admins = emptyList()
        val total = meta?.total ?: -1


        channelView.total = total

        return channelView
    }


    private suspend fun guildInfo(group: String): GuildView {
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
        val groups: List<GuildListRespData> = GuildListReq.NoSort.doRequestForData(bot).items
        return groups.asGroupList(limit)
    }

    override suspend fun groupMemberList(group: String, cache: Boolean, limit: Int): GroupMemberList {
        val userList = guildUserListReq {
            guildId = group
        }.doRequestForData(bot)!!

        val groupInfo = guildInfo(group)
        userList.groupInfo = groupInfo

        return userList
    }

    override suspend fun banList(group: String, cache: Boolean, limit: Int): MuteList {
        val muteList = GuildMuteListReq.Detail(group).doRequestForData(bot)!!

        // init results.
        var allIds = muteList.mic.userIds.asSequence() + muteList.headset.userIds.asSequence()
        if (limit > 0) {
            allIds = allIds.take(limit)
        }

        val results = allIds.map { id ->
            bot.async {
                UserViewReq(id, group).doRequestForData(bot)!!.asMuteInfo()
            }
        }.toList().map { job -> job.await() }
        muteList.results = results

        return muteList
    }


    override suspend fun groupNoteList(group: String, cache: Boolean, limit: Int) =
        def.groupNoteList(group, cache, limit)
}