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

package love.forte.simbot.component.kaiheila.api.v3.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.BaseApiDataKey
import love.forte.simbot.component.kaiheila.api.BaseApiDataReq


/**
 *
 * [给某个消息添加回应](https://developer.kaiheila.cn/doc/http/message#%E7%BB%99%E6%9F%90%E4%B8%AA%E6%B6%88%E6%81%AF%E6%B7%BB%E5%8A%A0%E5%9B%9E%E5%BA%94)
 *
 */
public class MessageAddReactionReq(
    /**
     * 	频道消息的id
     */
    private val msgId: String,
    /**
     *	emoji的id, 可以为 `GuilEmoji` 或者 `Emoji`
     */
    private val emoji: String,
) : EmptyRespPostMessageApiReq, BaseApiDataReq.Empty(Key) {
    companion object Key : BaseApiDataKey("message", "add-reaction")

    override fun createBody(): Any = Body(msgId, emoji)

    @Serializable
    private data class Body(@SerialName("msg_id") val msgId: String, val emoji: String)
}
