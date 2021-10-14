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

package love.forte.simbot.kaiheila.event.system.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 * [Card消息中的Button点击事件](https://developer.kaiheila.cn/doc/event/user#Card%E6%B6%88%E6%81%AF%E4%B8%AD%E7%9A%84Button%E7%82%B9%E5%87%BB%E4%BA%8B%E4%BB%B6)
 *
 *
 * type: `message_btn_click`
 *
 */
@Serializable
public data class MessageBtnClickEventBody(
    @SerialName("msg_id")
    val msgId: String,
    @SerialName("user_id")
    val userId: String,
    val value: String,
    @SerialName("target_id")
    val targetId: String,
) : UserEventExtraBody


@Serializable
public data class MessageBtnClickEventExtra(override val body: MessageBtnClickEventBody) :
    UserEventExtra<MessageBtnClickEventBody> {
    override val type: String
        get() = "message_btn_click"
}
