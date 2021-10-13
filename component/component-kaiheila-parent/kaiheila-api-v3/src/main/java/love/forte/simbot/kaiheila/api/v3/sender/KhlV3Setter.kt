package love.forte.simbot.kaiheila.api.v3.sender

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import love.forte.common.utils.Carrier
import love.forte.common.utils.toCarrier
import love.forte.simbot.SimbotIllegalArgumentException
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.events.*
import love.forte.simbot.api.sender.Setter
import love.forte.simbot.component.kaiheila.KhlBot
import love.forte.simbot.component.kaiheila.api.KhlSender
import love.forte.simbot.component.kaiheila.api.doRequest
import love.forte.simbot.component.kaiheila.api.isSuccess
import love.forte.simbot.component.kaiheila.api.v3.guild.*
import love.forte.simbot.component.kaiheila.api.v3.message.MessageDeleteReq
import love.forte.simbot.component.kaiheila.api.v3.message.direct.DirectMessageDeleteReq
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
    override suspend fun friendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean,
        blackList: Boolean,
    ) = def.setFriendAddRequest(flag, friendRemark, agree, blackList)

    override suspend fun groupAddRequest(
        flag: Flag<GroupAddRequest.FlagContent>,
        agree: Boolean,
        blackList: Boolean,
        why: String?,
    ) = def.setGroupAddRequest(flag, agree, blackList, why)


    override suspend fun groupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun groupAnonymous(group: String, agree: Boolean): Carrier<Boolean> =
        def.setGroupAnonymous(group, agree)

    private val muteDeleteJobs = ConcurrentHashMap<String, Job>()

    override suspend fun groupBan(
        groupCode: String,
        memberCode: String,
        time: Long,
        timeUnit: TimeUnit,
    ): Carrier<Boolean> {
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

        val resp = req.doRequest(bot).also { deleteJob?.start() }
        return resp.isSuccess.toCarrier()
    }

    override suspend fun groupWholeBan(groupCode: String, mute: Boolean) = def.groupWholeBan(groupCode, mute)

    override suspend fun groupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<String> {
        val req = GuildNicknameReq(groupCode, nickname = remark, userId = memberCode)

        return if (req.doRequest(bot).isSuccess) {
            remark?.toCarrier() ?: Carrier.empty()
        } else {
            Carrier.empty()
        }

    }

    override suspend fun groupQuit(groupCode: String, forcibly: Boolean): Carrier<Boolean> {

        val req = GuildLeaveReq(groupCode)

        val resp = req.doRequest(bot)

        return resp.isSuccess.toCarrier()
    }

    override suspend fun groupMemberKick(
        groupCode: String,
        memberCode: String,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> {

        val req = GuildKickoutReq(guildId = groupCode, targetId = memberCode)

        val resp = req.doRequest(bot)

        return resp.isSuccess.toCarrier()
    }

    override suspend fun groupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?) =
        def.groupMemberSpecialTitle(groupCode, memberCode, title)

    override suspend fun msgRecall(flag: MessageGet.MessageFlag<MessageGet.MessageFlagContent>): Carrier<Boolean> {
        val f = flag.flag
        val id = f.id

        val req = when (f) {
            is PrivateMsg.FlagContent -> DirectMessageDeleteReq(id)
            is GroupMsg.FlagContent -> MessageDeleteReq(id)
            else -> throw SimbotIllegalArgumentException("Flag is not channel message or direct message.")
        }


        val resp = req.doRequest(bot)

        return resp.isSuccess.toCarrier()
    }

    override suspend fun groupName(groupCode: String, name: String) =
        def.groupName(groupCode, name)

    override suspend fun friendDelete(friend: String) =
        def.setFriendDelete(friend)
}