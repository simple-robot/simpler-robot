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
 * [更新频道聊天消息](https://developer.kaiheila.cn/doc/http/message#%E6%9B%B4%E6%96%B0%E9%A2%91%E9%81%93%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF)
 *
 * *无返回参数*
 */
public class MessageUpdateReq(
    /**
     * 消息 id
     */
    private val msgId: String,

    /**
     * 消息内容
     */
    private val content: String,

    /**
     * 回复某条消息的 msgId。如果为空，则代表删除回复，不传则无影响。
     */
    private val quote: String? = null,

    /**
     * 用户 id，针对特定用户临时更新消息，必须是正常消息才能更新。与发送临时消息概念不同，但同样不保存数据库。
     */
    private val tempTargetId: String? = null,
) : EmptyRespPostMessageApiReq, BaseApiDataReq.Empty(Key) {

    companion object Key : BaseApiDataKey("message", "update")

    protected override fun createBody(): Any = Body(msgId, content, quote, tempTargetId)


    @Serializable
    private data class Body(
        @SerialName("msg_id")
        val msgId: String,
        val content: String,
        val quote: String? = null,
        @SerialName("temp_target_id")
        val tempTargetId: String? = null,
    )

}




