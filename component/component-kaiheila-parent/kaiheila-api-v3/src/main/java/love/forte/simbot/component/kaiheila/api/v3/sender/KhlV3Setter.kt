package love.forte.simbot.component.kaiheila.api.v3.sender

import kotlinx.coroutines.*
import love.forte.common.utils.Carrier
import love.forte.common.utils.toCarrier
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.events.FriendAddRequest
import love.forte.simbot.api.message.events.GroupAddRequest
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.api.sender.Setter
import love.forte.simbot.component.kaiheila.KhlBot
import love.forte.simbot.component.kaiheila.api.KhlSender
import love.forte.simbot.component.kaiheila.api.doRequest
import love.forte.simbot.component.kaiheila.api.isSuccess
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildLeaveReq
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildMuteCreateReq
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildMuteDeleteReq
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildNicknameReq
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 *
 * @author ForteScarlet
 */
public class KhlV3Setter(
    private val bot: KhlBot,
    private val def: Setter.Def,
    private val scope: CoroutineScope,
) : KhlSender.Setter {
    override fun setFriendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean,
        blackList: Boolean,
    ) = def.setFriendAddRequest(flag, friendRemark, agree, blackList)

    override fun setGroupAddRequest(
        flag: Flag<GroupAddRequest.FlagContent>,
        agree: Boolean,
        blackList: Boolean,
        why: String?,
    ) = def.setGroupAddRequest(flag, agree, blackList, why)


    override fun setGroupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupAnonymous(group: String, agree: Boolean): Carrier<Boolean> =
        def.setGroupAnonymous(group, agree)

    private val muteDeleteJobs = ConcurrentHashMap<String, Job>()

    override fun setGroupBan(groupCode: String, memberCode: String, time: Long, timeUnit: TimeUnit): Carrier<Boolean> {
        var deleteJob: Job? = null
        val req = when {
            time > 0 -> {
                // 有时间，定时删除
                GuildMuteCreateReq(guildId = groupCode, userId = memberCode, 1).also {
                    muteDeleteJobs.compute("${bot.clientId}:$groupCode:$memberCode") { _, old ->
                        old?.cancel()
                        deleteJob = scope.launch(start = CoroutineStart.LAZY) {
                            GuildMuteDeleteReq(guildId = groupCode, userId = memberCode, 1).doRequest(bot)
                        }
                        deleteJob
                    }
                }
            }
            time == 0L -> {
                GuildMuteDeleteReq(guildId = groupCode, userId = memberCode, 1).also {
                    muteDeleteJobs.remove("${bot.clientId}:$groupCode:$memberCode")?.cancel()
                }
            }
            else -> {
                // 小于0，则视为无限期。
                GuildMuteCreateReq(guildId = groupCode, userId = memberCode, 1)
            }
        }

        val resp = runBlocking {
            req.doRequest(bot).also { deleteJob?.start() }
        }

        return resp.isSuccess.toCarrier()
    }

    override fun setGroupWholeBan(groupCode: String, mute: Boolean): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<String> {
        val req = GuildNicknameReq(groupCode, nickname = remark, userId = memberCode)

        runBlocking { req.doRequest(bot) }

        return remark?.toCarrier() ?: Carrier.empty()
    }

    override fun setGroupQuit(groupCode: String, forcibly: Boolean): Carrier<Boolean> {

        val req = GuildLeaveReq(groupCode)

        val resp = runBlocking { req.doRequest(bot) }

        return resp.isSuccess.toCarrier()
    }

    override fun setGroupMemberKick(
        groupCode: String,
        memberCode: String,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?): Carrier<String> {
        TODO("Not yet implemented")
    }

    override fun setMsgRecall(flag: MessageGet.MessageFlag<MessageGet.MessageFlagContent>): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupName(groupCode: String, name: String): Carrier<String> {
        TODO("Not yet implemented")
    }

    override fun setFriendDelete(friend: String): Carrier<Boolean> {
        TODO("Not yet implemented")
    }
}