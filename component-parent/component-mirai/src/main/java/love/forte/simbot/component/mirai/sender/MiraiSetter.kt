/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiSetter.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.sender

import kotlinx.coroutines.runBlocking
import love.forte.common.utils.*
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.containers.AccountCodeContainer
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.GroupCodeContainer
import love.forte.simbot.api.message.events.FriendAddRequest
import love.forte.simbot.api.message.events.GroupAddRequest
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.sender.Setter
import love.forte.simbot.api.sender.SetterFactory
import love.forte.simbot.component.mirai.message.MiraiMessageFlag
import love.forte.simbot.component.mirai.message.event.MiraiBotInvitedJoinRequestFlagContent
import love.forte.simbot.component.mirai.message.event.MiraiFriendRequestFlagContent
import love.forte.simbot.component.mirai.message.event.MiraiGroupMemberJoinRequestFlagContent
import love.forte.simbot.core.CompLogger
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.mute
import java.util.concurrent.TimeUnit

public object MiraiSetterFactory : SetterFactory {
    override fun getOnMsgSetter(msg: MsgGet): Setter = MiraiSetter(Bot.getInstance(msg.botInfo.botCodeNumber))
    override fun getOnBotSetter(bot: BotContainer): Setter = MiraiSetter(Bot.getInstance(bot.botInfo.botCodeNumber))
}


/**
 *
 * mirai [Setter] 实现。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class MiraiSetter(private val bot: Bot) : Setter {
    private companion object : CompLogger("MiraiSetter") {
        private val setGroupAnonymous0Logger: Int by lazy(LazyThreadSafetyMode.NONE) {
            logger.warn("It is not supported to modify the anonymous chat status, only to return to the current status. This warning will only appear once.")
            0
        }
    }

    /**
     * 设置好友申请。
     */
    override fun setFriendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean,
        blackList: Boolean
    ): Carrier<Boolean> {
        val f = flag.flag
        return if (f is MiraiFriendRequestFlagContent) {
            val event = f.event
            if (agree) {
                runBlocking { event.accept() }
            } else {
                runBlocking { event.reject(blackList) }
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
        why: String?
    ): Carrier<Boolean> {
        return when (val f = flag.flag) {
            // member join.
            is MiraiGroupMemberJoinRequestFlagContent -> {
                val event = f.event
                if (agree) {
                    runBlocking { event.accept() }
                } else {
                    runBlocking { event.reject(blackList, why ?: "") }
                }
                true.toCarrier()
            }
            // bot invited.
            is MiraiBotInvitedJoinRequestFlagContent -> {
                val event = f.event
                if (agree) {
                    runBlocking { event.accept() }
                } else {
                    // only ignore, no reject.
                    runBlocking { event.ignore() }
                }
                true.toCarrier()
            }
            else -> throw IllegalArgumentException("flag content $f is not Mirai's flag content and cannot be used by mirai component.")
        }
    }


    /**
     * 设置群管理。
     */
    @Deprecated("Api not supported by mirai: changeGroupAdmin")
    private fun changeGroupAdmin0(groupCode: Long, memberCode: Long, promotion: Boolean): Carrier<Boolean> {
        throw IllegalStateException("Api not supported by mirai: changeGroupAdmin")
    }

    @Deprecated("Api not supported by mirai: changeGroupAdmin")
    override fun setGroupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Carrier<Boolean> =
        changeGroupAdmin0(groupCode.toLong(), memberCode.toLong(), promotion)

    @Deprecated("Api not supported by mirai: changeGroupAdmin")
    override fun setGroupAdmin(groupCode: Long, memberCode: Long, promotion: Boolean): Carrier<Boolean> =
        changeGroupAdmin0(groupCode, memberCode, promotion)


    /**
     * 设置群匿名是否开启。
     * 不支持修改匿名聊天状态，仅支持返回当前状态。
     * @return 设置操作的回执，代表当前状态。
     */
    private fun setGroupAnonymous0(group: Long, agree: Boolean): Carrier<Boolean> {
        setGroupAnonymous0Logger
        return bot.getGroup(group).settings.isAnonymousChatEnabled.toCarrier()
    }

    override fun setGroupAnonymous(group: String, agree: Boolean): Carrier<Boolean> =
        setGroupAnonymous0(group.toLong(), agree)

    override fun setGroupAnonymous(group: Long, agree: Boolean): Carrier<Boolean> =
        setGroupAnonymous0(group, agree)

    override fun setGroupAnonymous(group: GroupCodeContainer, agree: Boolean): Carrier<Boolean> =
        setGroupAnonymous0(group.groupCodeNumber, agree)

    /**
     * 禁言/解除禁言
     */
    private fun setGroupBan0(groupCode: Long, memberCode: Long, time: Long, timeUnit: TimeUnit): Carrier<Boolean> {
        bot.getGroupMember(groupCode, memberCode).apply {
            time.takeIf { time > 0 }?.let { t ->
                val muteTime: Long = t timeBy timeUnit timeAs Seconds
                runBlocking {
                    this@apply.mute(muteTime)
                }
            } ?: runBlocking {
                this@apply.unmute()
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
        timeUnit: TimeUnit
    ): Carrier<Boolean> = setGroupBan(group.groupCodeNumber, member.accountCodeNumber, time, timeUnit)


    /**
     * 设置全体禁言。
     */
    private fun setGroupWholeBan0(groupCode: Long, ban: Boolean): Carrier<Boolean> {
        return bot.getGroup(groupCode).settings.apply {
            isMuteAll = ban
        }.isMuteAll.toCarrier()
    }

    override fun setGroupWholeBan(groupCode: String, ban: Boolean): Carrier<Boolean> =
        setGroupWholeBan0(groupCode.toLong(), ban)

    override fun setGroupWholeBan(groupCode: Long, ban: Boolean): Carrier<Boolean> =
        setGroupWholeBan0(groupCode, ban)

    override fun setGroupWholeBan(groupCode: GroupCodeContainer, ban: Boolean): Carrier<Boolean> =
        setGroupWholeBan0(groupCode.groupCodeNumber, ban)

    /**
     * 设置群员的群名片。需要有对应权限。
     */
    private fun setGroupRemark0(groupCode: Long, memberCode: Long, remark: String?): Carrier<String> {
        return bot.getGroupMember(groupCode, memberCode).run {
            remark?.also { nameCard = it }
            nameCard
        }.toCarrier()
    }

    override fun setGroupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<String> =
        setGroupRemark0(groupCode.toLong(), memberCode.toLong(), remark)

    override fun setGroupRemark(groupCode: Long, memberCode: Long, remark: String?): Carrier<String> =
        setGroupRemark0(groupCode, memberCode, remark)

    override fun setGroupRemark(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        remark: String?
    ): Carrier<String> = setGroupRemark(group.groupCodeNumber, member.accountCodeNumber, remark)

    /**
     * 退出群或解散群。
     * mirai尚不支持解散群。（mirai 1.3.2）
     */
    private fun setGroupQuit0(groupCode: Long, forcibly: Boolean): Carrier<Boolean> {
        return runBlocking { bot.getGroup(groupCode).quit() }.toCarrier()
    }

    override fun setGroupQuit(groupCode: String, forcibly: Boolean): Carrier<Boolean> =
        setGroupQuit0(groupCode.toLong(), forcibly)

    override fun setGroupQuit(groupCode: Long, forcibly: Boolean): Carrier<Boolean> =
        setGroupQuit0(groupCode, forcibly)

    override fun setGroupQuit(group: GroupCodeContainer, forcibly: Boolean): Carrier<Boolean> =
        setGroupQuit(group.groupCodeNumber, forcibly)

    /**
     * 踢出群员。
     * [blackList] 参数无效。
     */
    private fun setGroupMemberKick0(
        groupCode: Long,
        memberCode: Long,
        why: String?,
        blackList: Boolean
    ): Carrier<Boolean> {
        runBlocking {
            bot.getGroupMember(groupCode, memberCode).kick(why ?: "")
        }
        return true.toCarrier()
    }

    override fun setGroupMemberKick(
        groupCode: String,
        memberCode: String,
        why: String?,
        blackList: Boolean
    ): Carrier<Boolean> =
        setGroupMemberKick0(groupCode.toLong(), memberCode.toLong(), why, blackList)

    override fun setGroupMemberKick(
        groupCode: Long,
        memberCode: Long,
        why: String?,
        blackList: Boolean
    ): Carrier<Boolean> =
        setGroupMemberKick0(groupCode, memberCode, why, blackList)

    override fun setGroupMemberKick(
        group: GroupCodeContainer,
        member: AccountCodeContainer,
        why: String?,
        blackList: Boolean
    ): Carrier<Boolean> = setGroupMemberKick(group.groupCodeNumber, member.accountCodeNumber, why, blackList)

    /**
     * 设置群员专属头衔。
     */
    private fun setGroupMemberSpecialTitle0(groupCode: Long, memberCode: Long, title: String?): Carrier<String> {
        return bot.getGroupMember(groupCode, memberCode).run {
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
        title: String?
    ): Carrier<String> = setGroupMemberSpecialTitle(group.groupCodeNumber, member.accountCodeNumber, title)

    /**
     * 撤回消息。需要填入一个 [Flag] 实例，
     * 而这个 [flag] 实例必须是 mirai组件所实现的 [MiraiMessageFlag] 类型。
     *
     * @throws IllegalArgumentException 当 [flag] 不是 [MiraiMessageFlag] 类型实例的时候。
     */
    override fun setMsgRecall(flag: Flag<MessageGet.MessageFlagContent>): Carrier<Boolean> {
        return if (flag is MiraiMessageFlag<*>) {
                flag.flag.source?.let { source ->
                    runBlocking {
                        try {
                            bot.recall(source)
                            true
                        } catch (e: IllegalStateException) {
                            // if IllegalStateException, recall false.
                            false
                        }
                    }
                } ?: false
        } else {
            throw IllegalArgumentException("The 'flag($flag)' is not a 'MiraiMessageFlag' instance, cannot be recall by MiraiSetter.")
        }.toCarrier()
    }


    /**
     * 设置群名称。
     * 群名称是移步上传并设置的，这里直接返回的 [name] 的值，但是不保证 [name] 被成功的设置。
     */
    private fun setGroupName0(groupCode: Long, name: String): Carrier<String> {
        return name.apply { bot.getGroup(groupCode).name = this }.toCarrier()
    }

    override fun setGroupName(groupCode: String, name: String): Carrier<String> =
        setGroupName0(groupCode.toLong(), name)

    override fun setGroupName(groupCode: Long, name: String): Carrier<String> =
        setGroupName0(groupCode, name)

    override fun setGroupName(group: GroupCodeContainer, name: String): Carrier<String> =
        setGroupName(group.groupCodeNumber, name)
}