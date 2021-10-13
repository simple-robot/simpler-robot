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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kaiheila.api.BaseApiDataKey
import love.forte.simbot.kaiheila.api.BaseApiDataReq
import love.forte.simbot.kaiheila.api.ObjectResp
import love.forte.simbot.kaiheila.api.objectResp
import love.forte.simbot.kaiheila.api.v3.guild.GuildApiRespData
import love.forte.simbot.kaiheila.objects.Permissions

/**
 *
 * [创建服务器角色](https://developer.kaiheila.cn/doc/http/guild-role#创建服务器角色)
 *
 * guild-role/create
 *
 * @author ForteScarlet
 */
class GuildRoleCreateReq(
    val guildId: String,
    val name: String? = null,
) : BaseApiDataReq<ObjectResp<GuildRoleCreateResp>>(Key),
    PostGuildRoleApiReq<ObjectResp<GuildRoleCreateResp>>  {
    companion object Key : BaseApiDataKey("guild-role", "create")

    override val dataSerializer: DeserializationStrategy<ObjectResp<GuildRoleCreateResp>>
        get() = GuildRoleCreateResp.objectResp

    override fun createBody(): Any = Body(name, guildId)

    @Serializable
    private data class Body(
        val name: String?,
        @SerialName("guild_id")
        val guildId: String
    )
}


/**
 * 响应值
 */
@Suppress("MemberVisibilityCanBePrivate")
@Serializable
public data class GuildRoleCreateResp(
    /**
     * 角色的id
     */
    @SerialName("role_id")
    val roleId: Int,
    /**
     * 角色的名称
     */
    val name: String,
    /**
     * 角色的色值0x000000 - 0xFFFFFF
     */
    val color: Int,
    /**
     * 顺序，值越小载靠前
     */
    val position: Int,
    /**
     * 只能为0或者1，是否把该角色的用户在用户列表排到前面
     */
    @SerialName("hoist")
    val hoist: Int,
    /**
     * 只能为0或者1，该角色是否可以被提及
     */
    val mentionable: Int,
    @SerialName("permissions")
    val permissions: Permissions,
) : GuildApiRespData() {
    companion object {
        val objectResp = objectResp<GuildRoleCreateResp>()
    }

    val permissionsValue get() = permissions.perm.toInt()



}