/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     DirectMessageCreate.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.component.kaiheila.api.v3.message.direct

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.BaseApiDataKey
import love.forte.simbot.component.kaiheila.api.BaseApiDataReq
import love.forte.simbot.component.kaiheila.api.ObjectResp
import love.forte.simbot.component.kaiheila.api.v3.message.MessageCreateResp
import love.forte.simbot.component.kaiheila.MessageType


/**
 * [发送私信聊天消息](https://developer.kaiheila.cn/doc/http/direct-message#%E5%8F%91%E9%80%81%E7%A7%81%E4%BF%A1%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF)
 *
 * @author ForteScarlet
 */
public class DirectMessageCreateReq internal constructor(
    /**
     * 消息类型, 不传默认为 1, 代表文本类型。2 图片消息，3 视频消息，4 文件消息，9 代表 kmarkdown 消息, 10 代表卡片消息。
     * 默认为 [MessageType.TEXT]
     * @see MessageType
     */
    private val type: Int, // = MessageType.TEXT.type,

    /**
     * 目标用户 id，后端会自动创建会话。有此参数之后可不传 chat_code参数
     */
    private val targetId: String?,

    /**
     * 目标会话 Code，chat_code 与 target_id 必须传一个
     */
    private val chatCode: String?,

    /**
     * 	消息内容
     */
    private val content: String,

    /**
     * 回复某条消息的 msgId
     */
    private val quote: String? = null,

    /**
     * nonce, 服务端不做处理, 原样返回
     */
    private val nonce: String? = null,
) : PostDirectMessageApiReq<ObjectResp<MessageCreateResp>>,
    BaseApiDataReq<ObjectResp<MessageCreateResp>>(Key) {
    init {
        if (chatCode == null && targetId == null) {
            throw IllegalStateException("Param 'chatCode' and 'targetId' cannot be null at the same time in direct-message-create request.")
        }
    }

    companion object Key : BaseApiDataKey("direct-message", "create") {
        @JvmStatic
        @JvmOverloads
        @JvmName("getInstanceByChatCode")
        fun byChatCode(
            type: MessageType = MessageType.TEXT,
            chatCode: String,
            content: String,
            quote: String? = null,
            nonce: String? = null,
        ): DirectMessageCreateReq = DirectMessageCreateReq(
            type = type.type,
            chatCode = chatCode,
            targetId = null,
            content = content,
            quote = quote,
            nonce = nonce
        )

        @JvmStatic
        @JvmOverloads
        @JvmName("getInstanceByTargetId")
        fun byTargetId(
            type: MessageType = MessageType.TEXT,
            targetId: String,
            content: String,
            quote: String? = null,
            nonce: String? = null,
        ): DirectMessageCreateReq = DirectMessageCreateReq(
            type = type.type,
            chatCode = null,
            targetId = targetId,
            content = content,
            quote = quote,
            nonce = nonce
        )
    }

    override val dataSerializer: DeserializationStrategy<ObjectResp<MessageCreateResp>>
        get() = MessageCreateResp.objectResp

    protected override fun createBody(): Any = Body(type, targetId, chatCode, content, quote, nonce)

    @Serializable
    private data class Body(
        private val type: Int,
        @SerialName("target_id")
        private val targetId: String?,
        @SerialName("chat_code")
        private val chatCode: String?,
        private val content: String,
        private val quote: String?,
        private val nonce: String?,
    )

}
