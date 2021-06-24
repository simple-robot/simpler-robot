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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.*


/**
 * [发送频道聊天消息](https://developer.kaiheila.cn/doc/http/message#%E5%8F%91%E9%80%81%E9%A2%91%E9%81%93%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF)
 *
 */
public class MessageCreateReq(
    /**
     * 消息类型, 不传默认为 1, 代表文本类型。2 图片消息，3 视频消息，4 文件消息，9 代表 kmarkdown 消息, 10 代表卡片消息。
     * 默认为 [MessageType.TEXT]
     * @see MessageType
     */
    type: Int = MessageType.TEXT.type,

    /**
     * 	目标频道 id
     */
    targetId: String,

    /**
     * 	消息内容
     */
    content: String,

    /**
     * 回复某条消息的 msgId
     */
    quote: String? = null,

    /**
     * nonce, 服务端不做处理, 原样返回
     */
    nonce: String? = null,

    /**
     * 用户id,如果传了，代表该消息是临时消息，该消息不会存数据库，但是会在频道内只给该用户推送临时消息。用于在频道内针对用户的操作进行单独的回应通知等。
     */
    tempTargetId: String? = null,
) : MessageApiReq<ObjectResp<MessageCreateRespData>> {

    constructor(
        type: MessageType = MessageType.TEXT,
        targetId: String,
        content: String,
        quote: String? = null,
        nonce: String? = null,
        tempTargetId: String? = null,
    ) : this(type.type, targetId, content, quote, nonce, tempTargetId)

    constructor(
        targetId: String,
        content: String,
        quote: String? = null,
        nonce: String? = null,
        tempTargetId: String? = null,
    ) : this(MessageType.TEXT.type, targetId, content, quote, nonce, tempTargetId)


    private val _body = Body(type, targetId, content, quote, nonce, tempTargetId)

    override val body: Any
        get() = _body

    override val dataSerializer: DeserializationStrategy<ObjectResp<MessageCreateRespData>>
        get() = DATA_SERIALIZER

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = ROUTE
    }

    override val key: ApiData.Req.Key
        get() = Key

    companion object Key : ApiData.Req.Key by key("/message/create") {
        private val ROUTE = listOf("message", "create")
        private val DATA_SERIALIZER: DeserializationStrategy<ObjectResp<MessageCreateRespData>> =
            objectResp(MessageCreateRespData.serializer())
    }

    @Serializable
    private data class Body(
        val type: Int,
        @SerialName("target_id")
        val targetId: String,
        val content: String,
        val quote: String? = null,
        val nonce: String? = null,
        @SerialName("temp_target_id")
        val tempTargetId: String? = null,
    )
}


/**
 * [发送频道聊天消息](https://developer.kaiheila.cn/doc/http/message#%E5%8F%91%E9%80%81%E9%A2%91%E9%81%93%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF)
 *
 * request method: post
 *
 * @author ForteScarlet
 */
@Serializable
public data class MessageCreateRespData(
    /**
     * 服务端生成的消息 id
     */
    @SerialName("msg_id")
    val msgId: String,

    /**
     * 消息发送时间(服务器时间戳)
     */
    @SerialName("msg_timestamp")
    val msgTimestamp: Long,

    /**
     * 随机字符串，见参数列表
     */
    val nonce: String? = null,
) : MessageApiRespData




