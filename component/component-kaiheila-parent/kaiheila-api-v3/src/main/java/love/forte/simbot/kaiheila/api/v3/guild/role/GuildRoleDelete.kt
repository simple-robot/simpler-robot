package love.forte.simbot.kaiheila.api.v3.guild.role

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kaiheila.api.BaseApiDataKey
import love.forte.simbot.kaiheila.api.BaseApiDataReq
import love.forte.simbot.kaiheila.api.EmptyResp


/**
 *
 * [删除服务器角色](https://developer.kaiheila.cn/doc/http/guild-role#删除服务器角色)
 *
 * method `POST`
 *
 * `/api/v3/guild-role/delete`
 *
 */
public class GuildRoleDeleteReq(
    /** 服务器id */
    private val guildId: String,
    /** 角色id */
    private val roleId: String,
) : EmptyRespPostGuildRoleApiReq,
    BaseApiDataReq<EmptyResp>(Key) {
    companion object Key : BaseApiDataKey("guild-role", "delete")

    protected override fun createBody(): Any = Body(guildId, roleId)

    @Serializable
    private data class Body(
        @SerialName("guild_id") val guildId: String,
        @SerialName("role_id") val roleId: String,
    )

}
