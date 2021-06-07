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

package love.forte.simbot.component.mirai.sender

import kotlinx.coroutines.runBlocking
import love.forte.common.utils.*
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.containers.AccountCodeContainer
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.GroupCodeContainer
import love.forte.simbot.api.message.events.*
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
) : Setter {
    private companion object : TypedCompLogger(MiraiSetter::class.java) {
        private val setGroupAnonymous0Logger: Int by lazy(LazyThreadSafetyMode.NONE) {
            logger.warn("It is not supported to modify the anonymous chat status, only to return to the current status. This warning will only appear once.")
            0
        }
    }

    private lateinit var _setterInfo: SetterInfo
    private val setterInfo: SetterInfo
        get() {
        if (!::_setterInfo.isInitialized) {
            kotlinx.atomicfu.locks.synchronized(this) {
                if (!::_setterInfo.isInitialized) {
                    _setterInfo = SetterInfo(bot)
                }
            }
        }
        return _setterInfo
    }


    /**
     * 设置好友申请。
     */
    override fun setFriendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean,
        blackList: Boolean,
    ): Carrier<Boolean> {
        val f = flag.flag
        return if (f is MiraiFriendRequestFlagContent) {
            runBlocking {
                val event = f.event
                if (agree) {
                    event.accept()
                } else {
                    event.reject(blackList)
                }
            }
            true.toCarrier()
        } else {
            throw IllegalArgumentException("flag content $f is not Mirai's flag content and cannot be used by mirai component.")
        }
    }


    /**
     * 通过群申请。
     */
    override fun setGroupAddRequest(
        flag: Flag<GroupAddRequest.FlagContent>,
        agree: Boolean,
        blackList: Boolean,
        why: String?,
    ): Carrier<Boolean> {

        return runBlocking {
            when (val f = flag.flag) {
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
    }


    override fun setGroupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Carrier<Boolean> =
        defSetter.setGroupAdmin(groupCode, memberCode, promotion)

    override fun setGroupAdmin(groupCode: Long, memberCode: Long, promotion: Boolean): Carrier<Boolean> =
        defSetter.setGroupAdmin(groupCode, memberCode, promotion)


    /**
     * 设置群匿名是否开启。
     * 不支持修改匿名聊天状态，仅支持返回当前状态。
     * @return 设置操作的回执，代表当前状态。
     */
    private fun setGroupAnonymous0(group: Long): Carrier<Boolean> {
        setGroupAnonymous0Logger
        return bot.group(group).settings.isAnonymousChatEnabled.toCarrier()
    }

    override fun setGroupAnonymous(group: String, agree: Boolean): Carrier<Boolean> =
        setGroupAnonymous0(group.toLong())

    override fun setGroupAnonymous(group: Long, agree: Boolean): Carrier<Boolean> =
        setGroupAnonymous0(group)

    override fun setGroupAnonymous(group: GroupCodeContainer, agree: Boolean): Carrier<Boolean> =
        setGroupAnonymous0(group.groupCodeNumber)

    /**
     * 禁言/解除禁言
     */
    private fun setGroupBan0(groupCode: Long, memberCode: Long, time: Long, timeUnit: TimeUnit): Carrier<Boolean> {
        bot.member(groupCode, memberCode).apply {
            runBlocking {
                time.takeIf { time > 0 }?.let { t ->
                    val muteTime: Long = t timeBy timeUnit timeAs Seconds
                    this@apply.mute(muteTime.toInt())
                } ?: this@apply.unmute()
            }
        }
        return true.toCarrier()
    }

    override fun setGroupBan(groupCode: String, memberCode: String, time: Long, timeUnit: TimeUnit): Carrier<Boolean> =
        setGroupBan0(groupCode.toLong(), memberCode.toLong(), time, timeUnit)

    override fun setGroupBan(groupCode: Long, memberCode: Long, time: Long, timeUnit: TimeUnit): Carrier<Boolean> =
        setGroupBan0(groupCode, memberCode, time, timeUnit)

    override fun setGroupBan(group: GroupCodeContainer, member: AccountCodeContainer, time: Long): Carrier<Boolean> =
        setGroupBan(group.groupCodeNumber, member.accountCodeNumber, time)

    override fun setGroupBan(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        time: Long,
        timeUnit: TimeUnit,
    ): Carrier<Boolean> = setGroupBan(group.groupCodeNumber, member.accountCodeNumber, time, timeUnit)


    /**
     * 设置全体禁言。
     */
    private fun setGroupWholeBan0(groupCode: Long, ban: Boolean): Carrier<Boolean> {
        return bot.group(groupCode).settings.apply {
            isMuteAll = ban
        }.isMuteAll.toCarrier()
    }

    override fun setGroupWholeBan(groupCode: String, mute: Boolean): Carrier<Boolean> =
        setGroupWholeBan0(groupCode.toLong(), mute)

    override fun setGroupWholeBan(groupCode: Long, mute: Boolean): Carrier<Boolean> =
        setGroupWholeBan0(groupCode, mute)

    override fun setGroupWholeBan(groupCode: GroupCodeContainer, mute: Boolean): Carrier<Boolean> =
        setGroupWholeBan0(groupCode.groupCodeNumber, mute)

    /**
     * 设置群员的群名片。需要有对应权限。
     */
    private fun setGroupRemark0(groupCode: Long, memberCode: Long, remark: String?): Carrier<String> {
        return bot.member(groupCode, memberCode).run {
            remark?.also { nameCard = it }
        }.toCarrier()
    }

    override fun setGroupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<String> =
        setGroupRemark0(groupCode.toLong(), memberCode.toLong(), remark)

    override fun setGroupRemark(groupCode: Long, memberCode: Long, remark: String?): Carrier<String> =
        setGroupRemark0(groupCode, memberCode, remark)

    override fun setGroupRemark(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        remark: String?,
    ): Carrier<String> = setGroupRemark(group.groupCodeNumber, member.accountCodeNumber, remark)

    /**
     * 退出群或解散群。
     *
     * ※ mirai尚不支持解散群。（mirai 2.4.0）
     */
    private fun setGroupQuit0(groupCode: Long): Carrier<Boolean> {
        return runBlocking { bot.group(groupCode).quit() }.toCarrier()
    }

    override fun setGroupQuit(groupCode: String, forcibly: Boolean): Carrier<Boolean> =
        setGroupQuit0(groupCode.toLong())

    override fun setGroupQuit(groupCode: Long, forcibly: Boolean): Carrier<Boolean> =
        setGroupQuit0(groupCode)

    override fun setGroupQuit(group: GroupCodeContainer, forcibly: Boolean): Carrier<Boolean> =
        setGroupQuit(group.groupCodeNumber, forcibly)

    /**
     * 踢出群员。
     */
    private fun setGroupMemberKick0(
        groupCode: Long,
        memberCode: Long,
        why: String?,
    ): Carrier<Boolean> {
        runBlocking {
            bot.member(groupCode, memberCode).kick(why ?: "")
        }
        return true.toCarrier()
    }

    override fun setGroupMemberKick(
        groupCode: String,
        memberCode: String,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> =
        setGroupMemberKick0(groupCode.toLong(), memberCode.toLong(), why)

    override fun setGroupMemberKick(
        groupCode: Long,
        memberCode: Long,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> =
        setGroupMemberKick0(groupCode, memberCode, why)

    override fun setGroupMemberKick(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> = setGroupMemberKick(group.groupCodeNumber, member.accountCodeNumber, why, blackList)

    /**
     * 设置群员专属头衔。
     */
    private fun setGroupMemberSpecialTitle0(groupCode: Long, memberCode: Long, title: String?): Carrier<String> {
        return bot.member(groupCode, memberCode).run {
            title?.also { specialTitle = it }
            specialTitle
        }.toCarrier()
    }

    override fun setGroupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?): Carrier<String> =
        setGroupMemberSpecialTitle0(groupCode.toLong(), memberCode.toLong(), title)

    override fun setGroupMemberSpecialTitle(groupCode: Long, memberCode: Long, title: String?): Carrier<String> =
        setGroupMemberSpecialTitle0(groupCode, memberCode, title)

    override fun setGroupMemberSpecialTitle(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        title: String?,
    ): Carrier<String> = setGroupMemberSpecialTitle(group.groupCodeNumber, member.accountCodeNumber, title)

    /**
     * 撤回消息。需要填入一个 [Flag] 实例，
     * 而这个 [flag] 实例必须是 mirai组件所实现的 [MiraiMessageFlag] 类型。
     *
     * @throws IllegalArgumentException 当 [flag] 不是 [MiraiMessageFlag] 类型实例的时候。
     * @throws PermissionDeniedException 无权操作的时候
     */
    override fun setMsgRecall(flag: MessageGet.MessageFlag<MessageGet.MessageFlagContent>): Carrier<Boolean> {
        val source: MessageSource = flag.messageSource(bot.id)

        return runBlocking {
            try {
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
            }
        }.toCarrier()

    }


    /**
     * 设置群名称。
     * 群名称是移步上传并设置的，这里直接返回的 [name] 的值，但是不保证 [name] 被成功的设置。
     */
    private fun setGroupName0(groupCode: Long, name: String): Carrier<String> {
        return name.apply { bot.group(groupCode).name = this }.toCarrier()
    }

    override fun setGroupName(groupCode: String, name: String): Carrier<String> =
        setGroupName0(groupCode.toLong(), name)

    override fun setGroupName(groupCode: Long, name: String): Carrier<String> =
        setGroupName0(groupCode, name)

    override fun setGroupName(group: GroupCodeContainer, name: String): Carrier<String> =
        setGroupName(group.groupCodeNumber, name)

    /**
     * 删除好友。
     */
    private fun setFriendDelete0(code: Long): Carrier<Boolean> {
        runBlocking { bot.friend(code).delete() }
        return true.toCarrier()
    }

    override fun setFriendDelete(friend: String): Carrier<Boolean> =
        setFriendDelete0(friend.toLong())

    override fun setFriendDelete(friend: Long): Carrier<Boolean> =
        setFriendDelete0(friend)

    override fun setFriendDelete(friend: AccountCodeContainer): Carrier<Boolean> =
        setFriendDelete0(friend.accountCodeNumber)


    /**
     * 设置群精华消息。
     *
     * 请通过 [additionalExecute] 配合 [love.forte.simbot.component.mirai.additional.MiraiEssenceMessageApi] 使用。
     */
    @Deprecated("Use additionalExecute by MiraiEssenceMessageApi")
    fun setGroupEssenceMessage(group: Long, msgFlag: Flag<GroupMsg.FlagContent>): Carrier<Boolean> {
        if (msgFlag !is MiraiMessageFlag<*>) {
            throw IllegalArgumentException("Mirai only supports setting the essence message through the group Msg.flag under mirai, but type(${msgFlag::class.java})")
        }
        runBlocking {
            msgFlag.flagSource.source?.let { s ->
                bot.getGroupOrFail(group).setEssenceMessage(s).toCarrier()
            } ?: throw IllegalArgumentException("Mirai message source is empty.")
        }


        return true.toCarrier()
    }


    override fun <R : Result> additionalExecute(additionalApi: AdditionalApi<R>): R {
        if (additionalApi is MiraiSetterAdditionalApi) {
            return additionalApi.execute(setterInfo)
        }
        return super.additionalExecute(additionalApi)
    }

}