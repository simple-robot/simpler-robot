/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

package love.forte.simbot.component.mirai.sender

import kotlinx.coroutines.CoroutineScope
import love.forte.common.utils.Carrier
import love.forte.common.utils.toCarrier
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.containers.AccountCodeContainer
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.GroupCodeContainer
import love.forte.simbot.api.message.events.FriendAddRequest
import love.forte.simbot.api.message.events.GroupAddRequest
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.message.results.Result
import love.forte.simbot.api.sender.AdditionalApi
import love.forte.simbot.api.sender.Setter
import love.forte.simbot.api.sender.SetterFactory
import love.forte.simbot.component.mirai.additional.MiraiSetterAdditionalApi
import love.forte.simbot.component.mirai.additional.SetterInfo
import love.forte.simbot.component.mirai.message.MiraiMessageFlag
import love.forte.simbot.component.mirai.message.event.MiraiBotInvitedJoinRequestFlagContent
import love.forte.simbot.component.mirai.message.event.MiraiFriendRequestFlagContent
import love.forte.simbot.component.mirai.message.event.MiraiGroupMemberJoinRequestFlagContent
import love.forte.simbot.component.mirai.message.messageSource
import love.forte.simbot.core.TypedCompLogger
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.PermissionDeniedException
import net.mamoe.mirai.message.data.MessageSource
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import java.util.concurrent.TimeUnit

public object MiraiSetterFactory : SetterFactory {
    override fun getOnMsgSetter(msg: MsgGet, def: Setter.Def): Setter =
        MiraiSetter(Bot.getInstance(msg.botInfo.botCodeNumber), def)

    override fun getOnBotSetter(bot: BotContainer, def: Setter.Def): Setter =
        MiraiSetter(Bot.getInstance(bot.botInfo.botCodeNumber), def)
}


/**
 *
 * mirai [Setter] 实现。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class MiraiSetter(
    private val bot: Bot,
    private val defSetter: Setter,
) : Setter, CoroutineScope by bot {
    private companion object : TypedCompLogger(MiraiSetter::class.java);
    
    private lateinit var _setterInfo: SetterInfo
    private val setterInfo: SetterInfo
        get() {
            if (!::_setterInfo.isInitialized) {
                _setterInfo = SetterInfo(bot)
                // synchronized(this) {
                //     if (!::_setterInfo.isInitialized) {
                //         _setterInfo = SetterInfo(bot)
                //     }
                // }
            }
            return _setterInfo
        }


    /**
     * 设置好友申请。
     */
    override suspend fun friendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean,
        blackList: Boolean,
    ): Carrier<Boolean> {
        val f = flag.flag
        return if (f is MiraiFriendRequestFlagContent) {
            val event = f.event
            if (agree) {
                event.accept()
            } else {
                event.reject(blackList)
            }
            true.toCarrier()
        } else {
            throw IllegalArgumentException("flag content $f is not Mirai's flag content and cannot be used by mirai component.")
        }
    }


    /**
     * 通过群申请。
     */
    override suspend fun groupAddRequest(
        flag: Flag<GroupAddRequest.FlagContent>,
        agree: Boolean,
        blackList: Boolean,
        why: String?,
    ): Carrier<Boolean> {

        return when (val f = flag.flag) {
            // member join.
            is MiraiGroupMemberJoinRequestFlagContent -> {
                val event = f.event
                if (agree) {
                    event.accept()
                } else {
                    event.reject(blackList, why ?: "")
                }
                true.toCarrier()
            }
            // bot invited.
            is MiraiBotInvitedJoinRequestFlagContent -> {
                val event = f.event
                if (agree) {
                    event.accept()
                } else {
                    // only ignore, no reject.
                    event.ignore()
                }
                true.toCarrier()
            }
            else -> throw IllegalArgumentException("flag content $f is not Mirai's flag content and cannot be used by mirai component.")
        }
    }


    override suspend fun groupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Carrier<Boolean> {
        bot.group(groupCode.toLong()).member(memberCode.toLong()).modifyAdmin(promotion)
        return true.toCarrier()
    }
    // defSetter.setGroupAdmin(groupCode, memberCode, promotion)

    override suspend fun groupAdmin(groupCode: Long, memberCode: Long, promotion: Boolean): Carrier<Boolean> {
        bot.group(groupCode).member(memberCode).modifyAdmin(promotion)
        return true.toCarrier()
    }


    /**
     * 设置群匿名是否开启。
     * 不支持修改匿名聊天状态，仅支持返回当前状态。
     * @return 设置操作的回执，代表当前状态。
     */
    private fun setGroupAnonymous0(group: Long, agree: Boolean): Carrier<Boolean> {
        bot.group(group).settings.isAnonymousChatEnabled = agree
        return agree.toCarrier()
    }

    override suspend fun groupAnonymous(group: String, agree: Boolean): Carrier<Boolean> =
        setGroupAnonymous0(group.toLong(), agree)

    override suspend fun groupAnonymous(group: Long, agree: Boolean): Carrier<Boolean> =
        setGroupAnonymous0(group, agree)

    override suspend fun groupAnonymous(group: GroupCodeContainer, agree: Boolean): Carrier<Boolean> =
        setGroupAnonymous0(group.groupCodeNumber, agree)

    /**
     * 禁言/解除禁言
     */
    private suspend fun groupBan0(
        groupCode: Long,
        memberCode: Long,
        time: Long,
        timeUnit: TimeUnit,
    ): Carrier<Boolean> {
        bot.member(groupCode, memberCode).apply {
            if (time <= 0) {
                unmute()
            } else {
                mute(timeUnit.toSeconds(time).toInt())
            }
        }
        return true.toCarrier()
    }

    override suspend fun groupBan(
        groupCode: String,
        memberCode: String,
        time: Long,
        timeUnit: TimeUnit,
    ): Carrier<Boolean> =
        groupBan0(groupCode.toLong(), memberCode.toLong(), time, timeUnit)

    override suspend fun groupBan(groupCode: Long, memberCode: Long, time: Long, timeUnit: TimeUnit): Carrier<Boolean> =
        groupBan0(groupCode, memberCode, time, timeUnit)

    override suspend fun groupBan(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        time: Long,
    ): Carrier<Boolean> =
        groupBan(group.groupCodeNumber, member.accountCodeNumber, time)

    override suspend fun groupBan(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        time: Long,
        timeUnit: TimeUnit,
    ): Carrier<Boolean> = groupBan(group.groupCodeNumber, member.accountCodeNumber, time, timeUnit)


    /**
     * 设置全体禁言。
     */
    private fun groupWholeBan0(groupCode: Long, ban: Boolean): Carrier<Boolean> {
        return bot.group(groupCode).settings.apply {
            isMuteAll = ban
        }.isMuteAll.toCarrier()
    }

    override suspend fun groupWholeBan(groupCode: String, mute: Boolean): Carrier<Boolean> =
        groupWholeBan0(groupCode.toLong(), mute)

    override suspend fun groupWholeBan(groupCode: Long, mute: Boolean): Carrier<Boolean> =
        groupWholeBan0(groupCode, mute)

    override suspend fun groupWholeBan(groupCode: GroupCodeContainer, mute: Boolean): Carrier<Boolean> =
        groupWholeBan0(groupCode.groupCodeNumber, mute)

    /**
     * 设置群员的群名片。需要有对应权限。
     */
    private fun groupRemark0(groupCode: Long, memberCode: Long, remark: String?): Carrier<String> {
        return bot.member(groupCode, memberCode).run {
            remark?.also { nameCard = it }
        }.toCarrier()
    }

    override suspend fun groupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<String> =
        groupRemark0(groupCode.toLong(), memberCode.toLong(), remark)

    override suspend fun groupRemark(groupCode: Long, memberCode: Long, remark: String?): Carrier<String> =
        groupRemark0(groupCode, memberCode, remark)

    override suspend fun groupRemark(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        remark: String?,
    ): Carrier<String> = setGroupRemark(group.groupCodeNumber, member.accountCodeNumber, remark)

    /**
     * 退出群或解散群。
     *
     * ※ mirai尚不支持解散群。（mirai 2.4.0）
     */
    private suspend fun setGroupQuit0(groupCode: Long): Carrier<Boolean> {
        return bot.group(groupCode).quit().toCarrier()
    }

    override suspend fun groupQuit(groupCode: String, forcibly: Boolean): Carrier<Boolean> =
        setGroupQuit0(groupCode.toLong())

    override suspend fun groupQuit(groupCode: Long, forcibly: Boolean): Carrier<Boolean> =
        setGroupQuit0(groupCode)

    override suspend fun groupQuit(group: GroupCodeContainer, forcibly: Boolean): Carrier<Boolean> =
        groupQuit(group.groupCodeNumber, forcibly)

    /**
     * 踢出群员。
     */
    private suspend fun groupMemberKick0(
        groupCode: Long,
        memberCode: Long,
        why: String?,
    ): Carrier<Boolean> {
        bot.member(groupCode, memberCode).kick(why ?: "")
        return true.toCarrier()
    }

    override suspend fun groupMemberKick(
        groupCode: String,
        memberCode: String,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> =
        groupMemberKick0(groupCode.toLong(), memberCode.toLong(), why)

    override suspend fun groupMemberKick(
        groupCode: Long,
        memberCode: Long,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> =
        groupMemberKick0(groupCode, memberCode, why)

    override suspend fun groupMemberKick(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> = groupMemberKick(group.groupCodeNumber, member.accountCodeNumber, why, blackList)

    /**
     * 设置群员专属头衔。
     */
    private fun groupMemberSpecialTitle0(groupCode: Long, memberCode: Long, title: String?): Carrier<String> {
        return bot.member(groupCode, memberCode).run {
            title?.also { specialTitle = it }
            specialTitle
        }.toCarrier()
    }

    override suspend fun groupMemberSpecialTitle(
        groupCode: String,
        memberCode: String,
        title: String?,
    ): Carrier<String> =
        groupMemberSpecialTitle0(groupCode.toLong(), memberCode.toLong(), title)

    override suspend fun groupMemberSpecialTitle(groupCode: Long, memberCode: Long, title: String?): Carrier<String> =
        groupMemberSpecialTitle0(groupCode, memberCode, title)

    override suspend fun groupMemberSpecialTitle(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        title: String?,
    ): Carrier<String> = groupMemberSpecialTitle(group.groupCodeNumber, member.accountCodeNumber, title)

    /**
     * 撤回消息。需要填入一个 [Flag] 实例，
     * 而这个 [flag] 实例必须是 mirai组件所实现的 [MiraiMessageFlag] 类型。
     *
     * @throws IllegalArgumentException 当 [flag] 不是 [MiraiMessageFlag] 类型实例的时候。
     * @throws PermissionDeniedException 无权操作的时候
     */
    override suspend fun msgRecall(flag: MessageGet.MessageFlag<MessageGet.MessageFlagContent>): Carrier<Boolean> {
        val source: MessageSource = flag.messageSource(bot.id)

        return try {
            source.recall()
            true
        } catch (e: PermissionDeniedException) {
            logger.warn("Recall msg failed: Permission denied.", e)
            false
        } catch (e: IllegalStateException) {
            // MiraiImpl.recallMessage: end-check
            // // 1001: No message meets the requirements (实际上是没权限, 管理员在尝试撤回群主的消息)
            // // 154: timeout
            // // 3: <no message>

            // if IllegalStateException, recall false.
            val localizedMessage = e.localizedMessage

            val warnMsgAlso = when {
                localizedMessage.contains("result=1001") -> "没有权限或权限不足"
                localizedMessage.contains("result=154") -> "timeout"
                else -> null
            }

            warnMsgAlso?.let { w ->
                logger.warn("Recall msg failed: $w", e)
            } ?: run {
                logger.warn("Recall msg failed.", e)
            }
            false
        }.toCarrier()

    }


    /**
     * 设置群名称。
     * 群名称是移步上传并设置的，这里直接返回的 [name] 的值，但是不保证 [name] 被成功的设置。
     */
    private fun groupName0(groupCode: Long, name: String): Carrier<String> {
        return name.apply { bot.group(groupCode).name = this }.toCarrier()
    }

    override suspend fun groupName(groupCode: String, name: String): Carrier<String> =
        groupName0(groupCode.toLong(), name)

    override suspend fun groupName(groupCode: Long, name: String): Carrier<String> =
        groupName0(groupCode, name)

    override suspend fun groupName(group: GroupCodeContainer, name: String): Carrier<String> =
        groupName(group.groupCodeNumber, name)

    /**
     * 删除好友。
     */
    private suspend fun friendDelete0(code: Long): Carrier<Boolean> {
        bot.friend(code).delete()
        return true.toCarrier()
    }

    override suspend fun friendDelete(friend: String): Carrier<Boolean> =
        friendDelete0(friend.toLong())

    override suspend fun friendDelete(friend: Long): Carrier<Boolean> =
        friendDelete0(friend)

    override suspend fun friendDelete(friend: AccountCodeContainer): Carrier<Boolean> =
        friendDelete0(friend.accountCodeNumber)


    override suspend fun <R : Result> execute(additionalApi: AdditionalApi<R>): R {
        if (additionalApi is MiraiSetterAdditionalApi) {
            return additionalApi.execute(setterInfo)
        }
        return super.additionalExecute(additionalApi)
    }

}