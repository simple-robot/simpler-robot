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

package love.forte.simbot.kaiheila.api.v3.guild.role

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kaiheila.api.ApiData
import love.forte.simbot.kaiheila.api.EmptyResp
import love.forte.simbot.kaiheila.api.objectResp


/**
 * [服务器角色权限相关接口](https://developer.kaiheila.cn/doc/http/guild-role) 请求实例接口。
 *
 *
 */
public interface GuildRoleApiReq<RESP : ApiData.Resp<*>> : ApiData.Req<RESP>
public interface GetGuildRoleApiReq<RESP : ApiData.Resp<*>> : GuildRoleApiReq<RESP>, ApiData.Req.Get<RESP>
public interface PostGuildRoleApiReq<RESP : ApiData.Resp<*>> : GuildRoleApiReq<RESP>, ApiData.Req.Post<RESP>

public interface EmptyRespGuildRoleApiReq : GuildRoleApiReq<EmptyResp>, ApiData.Req.Empty
public interface EmptyRespGetGuildRoleApiReq : EmptyRespGuildRoleApiReq, GetGuildRoleApiReq<EmptyResp>
public interface EmptyRespPostGuildRoleApiReq : EmptyRespGuildRoleApiReq, PostGuildRoleApiReq<EmptyResp>


/**
 * [服务器角色权限相关接口](https://developer.kaiheila.cn/doc/http/guild-role) 响应实例接口。
 */
public abstract class GuildRoleApiRespData : love.forte.simbot.kaiheila.api.v3.BaseV3RespData()


/**
 *
 * 对用户角色的操作结果响应体。
 *
 * @see GuildRoleGrantReq
 * @see GuildRoleRevokeReq
 *
 *
 */
@Serializable
public data class UserRoleOperationResult(
    @SerialName("user_id") val userId: String,
    @SerialName("guild_id") val guildId: String,
    val roles: List<Int>,
) : love.forte.simbot.kaiheila.api.v3.BaseV3RespData() {
    companion object {
        val objectSerializer = objectResp<UserRoleOperationResult>()
    }
}









