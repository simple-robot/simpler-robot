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

@file:JvmName("MiraiBotEventRegistrars")

package love.forte.simbot.component.mirai.utils

import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.component.mirai.message.MiraiMessageCache
import love.forte.simbot.component.mirai.message.cacheId
import love.forte.simbot.component.mirai.message.event.*
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.core.configuration.ComponentBeans
import love.forte.simbot.listener.MsgGetProcessor
import love.forte.simbot.listener.onMsg
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.Stranger
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.utils.MiraiExperimentalApi
import net.mamoe.mirai.utils.MiraiLoggerWithSwitch
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.reflect.KClass


@ComponentBeans
// @AsConfig(prefix = "simbot.component.mirai")
public class MiraiBotEventRegistrar(private val cache: MiraiMessageCache) {

    @Volatile
    var started: Boolean = false
        private set

    public fun started() {
        started = true
    }


    private companion object : TypedCompLogger(MiraiBotEventRegistrar::class.java)


    @OptIn(MiraiExperimentalApi::class)
    fun registerSimbotEvents(bot: Bot, msgProcessor: MsgGetProcessor) = bot.run {

        //region 消息相关
        // private 消息
        // 如果允许私聊消息缓存，则直接实例化消息
        if (cache.privateMsgCacheable) {
            // 好友消息
            registerListenerAlways<FriendMessageEvent> {
                val msg = MiraiPrivateMsg(this).also {
                    cache.cachePrivateMsg(this@registerListenerAlways.cacheId, it)
                }
                msgProcessor.onMsg { msg }
            }
            // 临时会话消息
            registerListenerAlways<GroupTempMessageEvent> {
                val msg = MiraiTempMsg(this).also {
                    cache.cachePrivateMsg(this@registerListenerAlways.cacheId, it)
                }
                msgProcessor.onMsg { msg }
            }
        } else {
            // 否则懒触发事件
            // 好友消息
            registerListenerAlways<FriendMessageEvent> {
                msgProcessor.onMsg {
                    MiraiPrivateMsg(this).also {
                        cache.cachePrivateMsg(this@registerListenerAlways.cacheId, it)
                    }
                }
            }
            // 临时会话消息
            registerListenerAlways<GroupTempMessageEvent> {
                msgProcessor.onMsg {
                    MiraiTempMsg(this).also {
                        cache.cachePrivateMsg(this@registerListenerAlways.cacheId, it)
                    }
                }
            }
        }
        // 群消息
        // 如果允许缓存，则每次都直接实例化消息对象
        if (cache.groupMsgCacheable) {
            registerListenerAlways<GroupMessageEvent> {
                val msg = MiraiGroupMsg(this).also {
                    cache.cacheGroupMsg(this@registerListenerAlways.cacheId, it)
                }
                msgProcessor.onMsg { msg }
            }
        } else {
            // 否则事件懒触发
            registerListenerAlways<GroupMessageEvent> {
                msgProcessor.onMsg {
                    MiraiGroupMsg(this).also {
                        cache.cacheGroupMsg(this@registerListenerAlways.cacheId, it)
                    }
                }
            }
        }
        // 好友申请
        registerListenerAlways<NewFriendRequestEvent> {
            msgProcessor.onMsg { MiraiFriendRequest(this) }
        }

        // bot被戳事件
        registerListenerAlways<NudgeEvent> {
            // if (this.from.id != this.bot.id) {
            if (this.from.id != this.bot.id) {
                when (val sub = this.from) {
                    is Friend -> msgProcessor.onMsg { MiraiNudgedEvent.ByFriend(this, sub) }
                    is Member -> msgProcessor.onMsg { MiraiNudgedEvent.ByMember(this, sub) }
                    is Stranger -> msgProcessor.onMsg { MiraiNudgedEvent.ByStranger(this, sub) }
                }

            }
            // when (this) {
            //     is BotNudgedEvent.InGroup.ByMember -> msgProcessor.onMsg { MiraiNudgedEvent(this, from) }
            //     is BotNudgedEvent.InPrivateSession.ByFriend -> msgProcessor.onMsg {
            //         MiraiBotPrivateSessionNudgedByFriendMsg(this, from)
            //     }
            //     else -> {
            //     }
            // }
        }

        // 群里其他人被戳事件
        // registerListenerAlways<MemberNudgedEvent> {
        //     msgProcessor.onMsg { MiraiMemberNudgedMsg(this) }
        // }
        //endregion


        //region 申请相关
        // 入群申请
        registerListenerAlways<MemberJoinRequestEvent> {
            msgProcessor.onMsg { MiraiGroupMemberJoinRequest(this) }
        }

        // bot被邀请入群申请
        registerListenerAlways<BotInvitedJoinGroupRequestEvent> {
            msgProcessor.onMsg { MiraiBotInvitedJoinGroupRequest(this) }
        }
        //endregion

        //region 好友变动
        // 好友增加/减少
        registerListenerAlways<FriendAddEvent> {
            msgProcessor.onMsg { MiraiFriendAdded(this) }
        }
        registerListenerAlways<FriendDeleteEvent> {
            msgProcessor.onMsg { MiraiFriendDeleted(this) }
        }
        //endregion

        //region 群成员变动
        // 群成员增加 - member
        registerListenerAlways<MemberJoinEvent> {
            when (this) {
                // 主动入群
                is MemberJoinEvent.Active -> msgProcessor.onMsg { MiraiMemberJoined.Active(this) }
                // 被动入群
                is MemberJoinEvent.Invite -> msgProcessor.onMsg { MiraiMemberJoined.Invite(this) }
                //
                is MemberJoinEvent.Retrieve -> msgProcessor.onMsg { MiraiMemberJoined.Retrieve(this) }
            }
        }
        // 群成员增加 - bot
        // bot主动同意入群
        registerListenerAlways<BotJoinGroupEvent> {
            when (this) {
                is BotJoinGroupEvent.Active -> msgProcessor.onMsg { MiraiBotJoined.Active(this) }
                is BotJoinGroupEvent.Invite -> msgProcessor.onMsg { MiraiBotJoined.Invite(this) }
                is BotJoinGroupEvent.Retrieve -> msgProcessor.onMsg { MiraiBotJoined.Retrieve(this) }
            }
        }

        // 群成员减少
        // 群友减少
        registerListenerAlways<MemberLeaveEvent> {
            when (this) {
                is MemberLeaveEvent.Kick -> msgProcessor.onMsg { MiraiMemberLeaved.Kick(this) }
                is MemberLeaveEvent.Quit -> msgProcessor.onMsg { MiraiMemberLeaved.Quit(this) }
            }
        }
        // bot退群
        registerListenerAlways<BotLeaveEvent> {
            when (this) {
                is BotLeaveEvent.Kick -> msgProcessor.onMsg { MiraiBotLeaveEvent.Kick(this) }
                is BotLeaveEvent.Active -> msgProcessor.onMsg { MiraiBotLeaveEvent.Active(this) }
            }
        }
        //endregion

        //region 权限变动事件
        // 成员权限变动
        registerListenerAlways<MemberPermissionChangeEvent> {
            msgProcessor.onMsg { MiraiMemberPermissionChanged(this) }
        }
        // bot权限变动
        registerListenerAlways<BotGroupPermissionChangeEvent> {
            msgProcessor.onMsg { MiraiBotPermissionChanged(this) }
        }
        //endregion

        //region 消息撤回事件
        registerListenerAlways<MessageRecallEvent> {
            when (this) {
                //region 群消息撤回
                is MessageRecallEvent.GroupRecall -> {
                    msgProcessor.onMsg { MiraiMsgRecall.GroupRecall(this, cache) }
                }
                //endregion
                //region 好友消息撤回
                is MessageRecallEvent.FriendRecall -> {
                    msgProcessor.onMsg { MiraiMsgRecall.FriendRecall(this, cache) }
                }
                //endregion
            }
        }
        //endregion

        //region 禁言相关

        //region 群友被禁言
        registerListenerAlways<MemberMuteEvent> {
            msgProcessor.onMsg { MiraiMemberMuteMsg(this) }
        }
        //endregion
        //region 群友被解除禁言
        registerListenerAlways<MemberUnmuteEvent> {
            msgProcessor.onMsg { MiraiMemberUnmuteMsg(this) }
        }
        //endregion
        //region bot被禁言
        registerListenerAlways<BotMuteEvent> {
            msgProcessor.onMsg { MiraiBotMuteMsg(this) }
        }
        //endregion
        //region bot被取消禁言
        registerListenerAlways<BotUnmuteEvent> {
            msgProcessor.onMsg { MiraiBotUnmuteMsg(this) }
        }
        //endregion

        //region 全员禁言 GroupMuteAllEvent
        registerListenerAlways<GroupMuteAllEvent> {
            msgProcessor.onMsg { MiraiMuteAllMsg(this) }
        }
        //endregion

        //endregion


        //region 杂项事件
        //region 好友更换头像事件
        registerListenerAlways<FriendAvatarChangedEvent> {
            msgProcessor.onMsg { MiraiFriendAvatarChanged(this) }
        }
        //endregion


        //region 好友更换昵称事件
        registerListenerAlways<FriendNickChangedEvent> {
            msgProcessor.onMsg { MiraiFriendNickChanged(this) }
        }
        //endregion


        //region 好友输入状态变更事件
        registerListenerAlways<FriendInputStatusChangedEvent> {
            msgProcessor.onMsg { MiraiFriendInputStatusChanged(this) }
        }
        //endregion


        //region bot离线事件
        registerListenerAlways<BotOfflineEvent> {
            when (this) {
                is BotOfflineEvent.Active -> msgProcessor.onMsg { MiraiBotOffline.Active(this) }
                is BotOfflineEvent.Force -> msgProcessor.onMsg { MiraiBotOffline.Force(this) }
                is BotOfflineEvent.Dropped -> msgProcessor.onMsg { MiraiBotOffline.Dropped(this) }
                else -> msgProcessor.onMsg { MiraiBotOffline.Other(this) }
            }
        }
        //endregion

        //region bot重新登录事件
        registerListenerAlways<BotReloginEvent> {
            msgProcessor.onMsg { MiraiBotReLogin(this) }
        }
        //endregion


        //region 群名称变更事件
        registerListenerAlways<GroupNameChangeEvent> {
            msgProcessor.onMsg { MiraiGroupNameChanged(this) }
        }
        //endregion

        //region 群友群备注变更事件
        registerListenerAlways<MemberCardChangeEvent> {
            msgProcessor.onMsg { MiraiMemberCardChanged(this) }
        }
        //endregion


        //region 群友群头衔变更事件
        registerListenerAlways<MemberSpecialTitleChangeEvent> {
            msgProcessor.onMsg { MiraiMemberSpecialTitleChanged(this) }
        }
        //endregion
        //endregion

        //region 群成员荣耀变更事件
        registerListenerAlways<MemberHonorChangeEvent> {
            msgProcessor.onMsg { MiraiGroupHonorChangedImpl(this) }
        }
        //endregion

        // region 群龙王变动事件
        registerListenerAlways<GroupTalkativeChangeEvent> {
            msgProcessor.onMsg { MiraiGroupTalkativeChangedImpl(this) }
        }
        //endregion

        // region 其他客户端上线事件
        registerListenerAlways<OtherClientOnlineEvent> {
            msgProcessor.onMsg { MiraiOtherClientOnlineImpl(this) }
        }
        // endregion


        // region 其他客户端离线事件
        registerListenerAlways<OtherClientOfflineEvent> {
            msgProcessor.onMsg { MiraiOtherClientOfflineImpl(this) }
        }
        // endregion


        // region 其他客户端发送消息给Bot
        registerListenerAlways<OtherClientMessageEvent> {
            msgProcessor.onMsg { MiraiOtherClientMessageImpl(this) }
        }
        // endregion


        // region 其他客户端发送群消息时的同步
        registerListenerAlways<GroupMessageSyncEvent> {
            msgProcessor.onMsg { MiraiGroupMessageSyncImpl(this) }
        }
        // endregion

        // enable mirai log.
        logger.let { if (it is MiraiLoggerWithSwitch) it else null }?.enable()

        // region any else custom event
        lock.read {
            var t = 0
            customEventsInvokers.forEach { solver ->
                if (solver.filter(this)) {
                    registerListenerAlways(solver.eventType.kotlin) {
                        msgProcessor.onMsg(solver.msgGetType) {
                            solver.solver(this@run, this)
                        }
                        // msgProcessor.onMsg { MiraiGroupMessageSyncImpl(this) }
                    }
                    t++
                }
            }
            if (t > 0) {
                logger.info("Registered $t custom events for bot $id.")
            }
        }

        // endregion


    }


    /**
     * register event.
     */
    private inline fun <reified E : Event> Bot.registerListenerAlways(crossinline handler: suspend E.(E) -> Unit): Listener<E> =
        this.eventChannel.subscribeAlways {
            handler(it)
        }

    /**
     * register event.
     */
    private fun <E : Event> Bot.registerListenerAlways(
        eventType: KClass<E>,
        handler: suspend E.(E) -> Unit,
    ): Listener<E> =
        this.eventChannel.subscribeAlways(eventType) {
            handler(it)
        }
}

private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()

private val customEventsInvokers: MutableList<EventsSolver<Event>> = mutableListOf()

public data class EventsSolver<out E : Event>(
    val eventType: Class<@UnsafeVariance E>,
    val msgGetType: Class<out MsgGet>,
    val filter: Bot.() -> Boolean,
    val solver: Bot.(@UnsafeVariance E) -> MsgGet?,
)

public fun <E : Event> registerEventSolver(solver: EventsSolver<E>) = lock.write {
    customEventsInvokers.add(solver)
}

/**
 * 注册一个事件，以及这个事件最终得到的 [MsgGet] 实例。
 * 当 [MsgGet] 得到了一个null，则视为放弃此事件。
 */
public inline fun <reified E : Event, reified MG : MsgGet> registerEventSolver(
    noinline filter: Bot.() -> Boolean,
    noinline solver: Bot.(E) -> MG?,
) = registerEventSolver(EventsSolver(E::class.java, MG::class.java, filter, solver))

/**
 * 注册一个事件，以及这个事件最终得到的 [MsgGet] 实例。
 * 当 [MsgGet] 得到了一个null，则视为放弃此事件。
 */
public inline fun <reified E : Event, reified MG : MsgGet> registerEventSolver(
    noinline solver: Bot.(E) -> MG?,
) = registerEventSolver(EventsSolver(E::class.java, MG::class.java, { true }, solver))


/**
 * 注册一个事件，以及这个事件最终得到的 [MsgGet] 实例。
 * 当 [MsgGet] 得到了一个null，则视为放弃此事件。
 */
@JvmSynthetic
public fun <E : Event, MG : MsgGet> registerEventSolver(
    eventType: KClass<E>,
    msgGetType: KClass<out MsgGet>,
    filter: Bot.() -> Boolean = { true },
    solver: Bot.(E) -> MG?,
) = registerEventSolver(EventsSolver(eventType.java, msgGetType.java, filter, solver))


@JvmOverloads
public fun <E : Event, MG : MsgGet> registerEventSolver(
    eventType: Class<E>,
    msgGetType: Class<out MsgGet>,
    filter: Bot.() -> Boolean = { true },
    solver: (Bot, E) -> MG?,
) = registerEventSolver(EventsSolver(eventType, msgGetType, filter, solver))
