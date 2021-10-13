package love.forte.simbot.kaiheila.api.v3.invite

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.BaseApiDataKey
import love.forte.simbot.component.kaiheila.api.BaseApiDataReq


/**
 * [删除邀请链接](https://developer.kaiheila.cn/doc/http/invite#删除邀请链接)
 *
 * `/api/v3/invite/delete`
 *
 * method POST
 */
public class InviteDeleteReq(
    private val guildId: String,
    private val channelId: String,
    private val urlCode: String,
) : EmptyRespPostInviteApiReq,
    BaseApiDataReq.Empty(Key) {
    companion object Key : BaseApiDataKey("invite", "delete")

    override fun createBody(): Any = Body(guildId, channelId, urlCode)

    @Serializable
    private data class Body(@SerialName("guild_id") val guildId: String,
                            @SerialName("channel_id") val channelId: String,
                            @SerialName("url_code") val urlCode: String)
}

data class User(val age: Int)
