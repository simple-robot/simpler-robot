package love.forte.simbot.kaiheila.api.v3.guild.role

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kaiheila.api.BaseApiDataKey
import love.forte.simbot.kaiheila.api.BaseApiDataReq
import love.forte.simbot.kaiheila.api.ObjectResp


/**
 *
 * [删除用户角色](https://developer.kaiheila.cn/doc/http/guild-role#删除用户角色)
 *
 * method `POST`
 *
 * `/api/v3/guild-role/revoke`
 *
 */
public class GuildRoleRevokeReq(
    /** 服务器ID */
    private val guildId: String,
    /** 角色ID */
    private val roleId: String,
    /** 用户ID */
    private val userId: String,
) : PostGuildRoleApiReq<ObjectResp<UserRoleOperationResult>>,
    BaseApiDataReq<ObjectResp<UserRoleOperationResult>>(Key) {
    companion object Key : BaseApiDataKey("guild-role", "revoke")

    override val dataSerializer: DeserializationStrategy<ObjectResp<UserRoleOperationResult>>
        get() = UserRoleOperationResult.objectSerializer

    protected override fun createBody(): Any = Body(guildId, roleId, userId)

    @Serializable
    private data class Body(
        @SerialName("guild_id") val guildId: String,
        @SerialName("role_id") val roleId: String,
        @SerialName("user_id") val userId: String
    )
}




