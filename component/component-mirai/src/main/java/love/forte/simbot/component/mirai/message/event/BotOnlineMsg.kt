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

package love.forte.simbot.component.mirai.message.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.events.EventGet
import love.forte.simbot.component.mirai.message.MiraiBotAccountInfo
import net.mamoe.mirai.event.events.BotOfflineEvent
import net.mamoe.mirai.event.events.BotReloginEvent
import net.mamoe.mirai.utils.MiraiExperimentalApi


/**
 * 可登录的。
 */
public interface LoginAble {
    /** 同步登录。 */
    fun loginBlocking()

    /** 异步登录。 */
    fun loginAsync()
}


/**
 * mirai bot离线事件。
 *
 * @see BotOfflineEvent
 */
public sealed class MiraiBotOffline<E : BotOfflineEvent>(event: E) :
    AbstractMiraiMsgGet<E>(event),
    EventGet,
    MiraiSpecialEvent<E>,
    LoginAble {
    public override val id: String = "MBOffline-${event.hashCode()}"
    public override val accountInfo: AccountInfo = MiraiBotAccountInfo(event.bot)

    /** 阻塞登录。 */
    public override fun loginBlocking() {
        runBlocking { event.bot.login() }
    }


    /** 异步登录。 */
    public override fun loginAsync() {
        CoroutineScope(Dispatchers.Default).launch {
            event.bot.login()
        }
        // GlobalScope.launch { event.bot.login() }
    }

    /**
     * 本次下线的异常原因。可能为null。
     */
    public abstract val cause: Throwable?

    /**
     * 本次下线所出现的原因与消息。可能为null。
     */
    public abstract val message: String?

    /**
     * 主动下线。
     */
    public class Active(event: BotOfflineEvent.Active) : MiraiBotOffline<BotOfflineEvent.Active>(event) {
        override val cause: Throwable?
            get() = event.cause
        override val message: String?
            get() = event.cause?.localizedMessage
    }

    /**
     * 被挤下线。
     */
    public class Force(event: BotOfflineEvent.Force) : MiraiBotOffline<BotOfflineEvent.Force>(event) {
        override val cause: Throwable?
            get() = null
        override val message: String = "${event.title}: ${event.message}"
    }

    /**
     * 网络原因掉线。
     */
    public class Dropped(event: BotOfflineEvent.Dropped) : MiraiBotOffline<BotOfflineEvent.Dropped>(event) {
        override val cause: Throwable?
            get() = event.cause
        override val message: String?
            get() = cause?.localizedMessage
    }

    /**
     * 其他掉线原因。
     */
    public class Other(event: BotOfflineEvent) : MiraiBotOffline<BotOfflineEvent>(event) {

        @OptIn(MiraiExperimentalApi::class)
        override val cause: Throwable? = if (event is BotOfflineEvent.CauseAware) event.cause else null
        override val message: String?
            get() = cause?.localizedMessage
    }
}


/**
 * bot重新登录事件。
 */
public class MiraiBotReLogin(event: BotReloginEvent) : AbstractMiraiMsgGet<BotReloginEvent>(event), EventGet,
    LoginAble {
    override val id: String = "MBRLogin-${event.hashCode()}"
    override val accountInfo: AccountInfo = MiraiBotAccountInfo(event.bot)
    val cause: Throwable? get() = event.cause

    /** 阻塞登录。 */
    public override fun loginBlocking() {
        runBlocking { event.bot.login() }
    }


    /** 异步登录。 */
    public override fun loginAsync() {
        event.bot.launch {
            event.bot.login()
        }
    }
}
