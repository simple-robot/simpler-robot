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

package love.forte.simbot.component.kaiheila.event.message

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.component.kaiheila.event.Event


/**
 * [消息相关事件列表](https://developer.kaiheila.cn/doc/event/message)
 *
 * 由于消息相关事件中没有系统事件，因此 [Event.extra] 全部都是 [Event.Extra.Text] 类型。
 *
 *
 * TODO: [#75](https://github.com/kaiheila/api-docs/issues/75)
 */
public interface MessageEventExtra : Event.Extra.Text


/**
 * 消息相关事件接口
 *
 */
public interface MessageEvent<E : MessageEventExtra> : Event<E>, MessageGet {
    override val accountInfo: AccountInfo
        get() = extra.author

    override val botInfo: BotInfo
        get() = TODO("Not yet implemented")

    override val id: String
        get() = msgId

    override val time: Long
        get() = msgTimestamp

}
