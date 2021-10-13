package love.forte.simbot.kaiheila.api.v3.guild.role

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kaiheila.api.BaseApiDataKey
import love.forte.simbot.kaiheila.api.BaseApiDataReq
import love.forte.simbot.kaiheila.api.ObjectResp


/**
 *
 * [赋予用户角色](https://developer.kaiheila.cn/doc/http/guild-role#赋予用户角色)
 *
 * method `POST`
 *
 * `/api/v3/guild-role/grant`
 */
public class GuildRoleGrantReq(
    /** 服务器id */
    private val guildId: String,
    /** 用户id */
    private val userId: String,
    /** 角色id */
    private val roleId: String,
) : PostGuildRoleApiReq<ObjectResp<UserRoleOperationResult>>,
    BaseApiDataReq<ObjectResp<UserRoleOperationResult>>(Key) {
    companion object Key : BaseApiDataKey("guild-role", "grant")


    override val dataSerializer: DeserializationStrategy<ObjectResp<UserRoleOperationResult>>
        get() = UserRoleOperationResult.objectSerializer

    protected override fun createBody(): Any = Body(guildId, userId, roleId)

    @Serializable
    private data class Body(
        @SerialName("guild_id") val guildId: String,
        @SerialName("user_id") val userId: String,
        @SerialName("role_id") val roleId: String,
    )
}

