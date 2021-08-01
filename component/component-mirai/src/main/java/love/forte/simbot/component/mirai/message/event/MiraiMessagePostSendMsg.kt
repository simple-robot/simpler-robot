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

import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.component.mirai.message.cacheId
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessagePostSendEvent

/**
 * Event for [MessagePostSendEvent]
 *
 *
 */
public sealed class MiraiMessagePostSendMsg<C : Contact>(event: MessagePostSendEvent<C>) :
    AbstractMiraiMsgGet<MessagePostSendEvent<C>>(event), MessageGet {
    override val id: String = event.receipt?.source?.cacheId ?: ("H:" + event.hashCode())
    override val time: Long = System.currentTimeMillis()


}
