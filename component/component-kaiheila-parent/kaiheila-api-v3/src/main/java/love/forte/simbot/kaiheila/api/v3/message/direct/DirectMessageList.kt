/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     DirectMessageList.kt
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

package love.forte.simbot.kaiheila.api.v3.message.direct

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.component.kaiheila.api.*
import love.forte.simbot.component.kaiheila.api.v3.message.DirectMessageDetails
import love.forte.simbot.component.kaiheila.api.v3.message.MessageReqFlag


/**
 * [获取私信聊天消息列表](https://developer.kaiheila.cn/doc/http/direct-message#%E8%8E%B7%E5%8F%96%E7%A7%81%E4%BF%A1%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF%E5%88%97%E8%A1%A8)
 *
 * method: GET
 */
public class DirectMessageListReq internal constructor(
    /**
     * 私信会话 Code。chat_code 与 target_id 必须传一个
     */
    private val chatCode: String?,
    /**
     * 目标用户 id，后端会自动创建会话。有此参数之后可不传 chat_code参数
     */
    private val targetId: String?,
    /**
     * 参考消息 id，不传则默认为最新的消息 id
     */
    private val msgId: String? = null,
    /**
     * 查询模式，有三种模式可以选择。不传则默认查询最新的消息
     */
    private val flag: String? = null,
) : GetDirectMessageApiReq<ListResp<DirectMessageDetails, ApiData.Resp.EmptySort>>,
    BaseApiDataReq<ListResp<DirectMessageDetails, ApiData.Resp.EmptySort>>(Key) {

    init {
        if (chatCode == null && targetId == null) {
            throw IllegalStateException("Param 'chatCode' and 'targetId' cannot be null at the same time in direct-message-list request.")
        }
    }


    companion object Key : BaseApiDataKey("direct-message", "list") {
        private val DATA_SERIALIZER = emptySortListResp<DirectMessageDetails>()

        @JvmStatic
        @JvmOverloads
        @JvmName("getInstanceByChatCode")
        fun byChatCode(
            chatCode: String,
            msgId: String? = null,
            flag: MessageReqFlag? = null,
        ): DirectMessageListReq {
            return DirectMessageListReq(
                chatCode = chatCode,
                targetId = null,
                msgId = msgId,
                flag = flag?.flag
            )
        }

        @JvmStatic
        @JvmOverloads
        @JvmName("getInstanceByTargetId")
        fun byTargetId(
            targetId: String,
            msgId: String? = null,
            flag: MessageReqFlag? = null,
        ): DirectMessageListReq {
            return DirectMessageListReq(
                chatCode = null,
                targetId = targetId,
                msgId = msgId,
                flag = flag?.flag
            )
        }

    }

    override val dataSerializer: DeserializationStrategy<ListResp<DirectMessageDetails, ApiData.Resp.EmptySort>>
        get() = DATA_SERIALIZER

    protected override fun createBody(): Any? = null

    override fun RouteInfoBuilder.doRoute() {
        parameters {
            appendIfNotnull("chat_code", chatCode)
            appendIfNotnull("target_id", targetId)
            appendIfNotnull("msg_id", msgId)
            appendIfNotnull("flag", flag)
        }
    }
}

