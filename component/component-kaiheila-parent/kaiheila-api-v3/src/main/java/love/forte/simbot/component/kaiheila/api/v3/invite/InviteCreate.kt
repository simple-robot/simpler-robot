package love.forte.simbot.component.kaiheila.api.v3.invite

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.BaseApiDataKey
import love.forte.simbot.component.kaiheila.api.BaseApiDataReq
import love.forte.simbot.component.kaiheila.api.ObjectResp
import love.forte.simbot.component.kaiheila.api.objectResp


/**
 *
 * [创建邀请链接](https://developer.kaiheila.cn/doc/http/invite#创建邀请链接)
 *
 * `/api/v3/invite/create`
 *
 * method POST
 *
 */
public class InviteCreateReq(
    private val guildId: String,
    private val channelId: String,
) : PostInviteApiReq<ObjectResp<InviteCreateResp>>,
    BaseApiDataReq<ObjectResp<InviteCreateResp>>(Key) {
    companion object Key : BaseApiDataKey("invite", "create")

    override val dataSerializer: DeserializationStrategy<ObjectResp<InviteCreateResp>>
        get() = InviteCreateResp.objectSerializer

    override fun createBody(): Any = Body(guildId, channelId)

    @Serializable
    private data class Body(
        @SerialName("guild_id") val guildId: String,
        @SerialName("channel_id") val channelId: String,
    )
}


@Serializable
public data class InviteCreateResp(val url: String) : InviteApiRespData {
    companion object Serializer {
        val objectSerializer = objectResp<InviteCreateResp>()
    }
}