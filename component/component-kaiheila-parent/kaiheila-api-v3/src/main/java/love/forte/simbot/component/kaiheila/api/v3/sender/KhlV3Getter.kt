package love.forte.simbot.component.kaiheila.api.v3.sender

import kotlinx.coroutines.runBlocking
import love.forte.simbot.api.message.containers.*
import love.forte.simbot.api.message.results.*
import love.forte.simbot.api.sender.Getter
import love.forte.simbot.component.kaiheila.KhlBot
import love.forte.simbot.component.kaiheila.api.KhlSender
import love.forte.simbot.component.kaiheila.api.doRequest
import love.forte.simbot.component.kaiheila.api.doRequestForData
import love.forte.simbot.component.kaiheila.api.v3.user.UserViewReq
import love.forte.simbot.component.kaiheila.api.v3.utils.asFriendInfo
import love.forte.simbot.component.kaiheila.objects.User

/**
 *
 * @author ForteScarlet
 */
public class KhlV3Getter(
    private val bot: KhlBot,
    private val def: Getter.Def
) : KhlSender.Getter {
    override val authInfo: AuthInfo get() = emptyAuthInfo()
    override val botInfo: BotInfo get() = bot.botInfo

    private suspend fun friendInfo(guildId: String, code: String): User {
        return UserViewReq(guildId, code).doRequestForData(bot)!!
    }

    override fun getFriendInfo(code: String): FriendInfo = def.getFriendInfo(code)

    override fun getFriendInfo(code: AccountContainer): FriendInfo {
        if (code is GroupContainer) {
            return runBlocking { friendInfo(code.groupInfo.groupCode, code.accountInfo.accountCode).asFriendInfo() }
        }
        return def.getFriendInfo(code)
    }

    override fun getFriendInfo(code: AccountCodeContainer): FriendInfo {
        if (code is GroupCodeContainer) {
            // return runBlocking { friendInfo() }
            TODO()
        }
        return super.getFriendInfo(code)
    }

    override fun getMemberInfo(group: String, code: String): GroupMemberInfo {
        TODO("Not yet implemented")
    }

    override fun getGroupInfo(group: String): GroupFullInfo {
        TODO("Not yet implemented")
    }

    override fun getFriendList(cache: Boolean, limit: Int): FriendList {
        TODO("Not yet implemented")
    }

    override fun getGroupList(cache: Boolean, limit: Int): GroupList {
        TODO("Not yet implemented")
    }

    override fun getGroupMemberList(group: String, cache: Boolean, limit: Int): GroupMemberList {
        TODO("Not yet implemented")
    }

    override fun getBanList(group: String, cache: Boolean, limit: Int): MuteList {
        TODO("Not yet implemented")
    }

    override fun getGroupNoteList(group: String, cache: Boolean, limit: Int): GroupNoteList {
        TODO("Not yet implemented")
    }
}