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

package love.forte.simbot.kaiheila.api.v3.message

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kaiheila.api.*


/**
 * [获取频道消息某回应的用户列表](https://developer.kaiheila.cn/doc/http/message#%E8%8E%B7%E5%8F%96%E9%A2%91%E9%81%93%E6%B6%88%E6%81%AF%E6%9F%90%E5%9B%9E%E5%BA%94%E7%9A%84%E7%94%A8%E6%88%B7%E5%88%97%E8%A1%A8)
 *
 *
 */
public class MessageReactionListReq(
    /** 频道消息的id */
    private val msgId: String,
    /** emoji的id, 可以为GuilEmoji或者Emoji, 注意：在get中，应该进行urlencode */
    private val emoji: String,
) : GetMessageApiReq<ObjectResp<MessageReactionListResp>>, BaseApiDataReq<ObjectResp<MessageReactionListResp>>(Key) {

    companion object Key : BaseApiDataKey("message", "reaction-list") {
        private val DATA_SERIALIZER: DeserializationStrategy<ObjectResp<MessageReactionListResp>> =
            objectResp(MessageReactionListResp.serializer())
    }

    override val dataSerializer: DeserializationStrategy<ObjectResp<MessageReactionListResp>> get() = DATA_SERIALIZER

    protected override fun createBody(): Any = Body(msgId, emoji)

    @Serializable
    private data class Body(@SerialName("msg_id") val msgId: String, val emoji: String)



}


@Serializable
public class MessageReactionListResp(

    /**
     * 用户的id
     */
    val id: String,

    /**
     * 用户的名称
     */
    val username: String,

    /**
     * 用户在服务器内的呢称
     */
    val nickname: String,

    /**
     * 用户名的认证数字，用户名正常为：user_name#identify_num
     */
    @SerialName("identify_num")
    val identifyNum: String,
    /**
     * 当前是否在线
     */
    val online: Boolean,
    /**
     * 用户的状态, 0代表正常，10代表被封禁
     */
    val status: Int,

    /**
     * 用户的头像的url地址
     */
    val avatar: String,

    /**
     * 	用户是否为机器人
     */
    @SerialName("bot")
    val isBot: Boolean,
    /**
     * 用户点击reaction的毫秒时间戳
     */
    @SerialName("reaction_time")
    val reactionTime: Long,
) : BaseRespData() {
    companion object {
        const val STATUS_NORMAL = 0
        const val STATUS_BAN = 10
    }

}


public fun MessageReactionListResp.isNormal(): Boolean = status == MessageReactionListResp.STATUS_NORMAL
public fun MessageReactionListResp.isBan(): Boolean = status == MessageReactionListResp.STATUS_BAN
