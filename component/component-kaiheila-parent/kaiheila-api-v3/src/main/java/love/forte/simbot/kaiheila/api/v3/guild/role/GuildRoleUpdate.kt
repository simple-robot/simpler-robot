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
import love.forte.simbot.kaiheila.objects.Role


/**
 *
 * [更新服务器角色](https://developer.kaiheila.cn/doc/http/guild-role#更新服务器角色)
 *
 * method `POST`
 *
 * `/api/v3/guild-role/update`
 *
 */
public class GuildRoleUpdateReq(override val body: Body) :
    PostGuildRoleApiReq<ObjectResp<Role>>,
    BaseApiDataReq<ObjectResp<Role>>(Key) {

    constructor(
        /** 角色的id */
        roleId: Int,
        /** 角色的名称 */
        name: String,
        /** 角色的色值0x000000 - 0xFFFFFF */
        color: Int,
        /** 顺序，值越小载靠前 */
        position: Int,
        /** 只能为0或者1，是否把该角色的用户在用户列表排到前面 */
        hoist: Int,
        /** 只能为0或者1，该角色是否可以被提及 */
        mentionable: Int,
        /** 权限,参见 [权限说明](https://developer.kaiheila.cn/doc/http/guild-role#权限说明) */
        permissions: Int,
    ) : this(Body(roleId, name, color, position, hoist, mentionable, permissions))

    companion object Key : BaseApiDataKey("guild-role", "update")

    override val dataSerializer: DeserializationStrategy<ObjectResp<Role>>
        get() = Role.objectSerializer

    protected override fun createBody() = body


    @Serializable
    public data class Body(
        /** 角色的id */
        @SerialName("role_id")
        val roleId: Int,
        /** 角色的名称 */
        val name: String,
        /** 角色的色值0x000000 - 0xFFFFFF */
        val color: Int,
        /** 顺序，值越小载靠前 */
        val position: Int,
        /** 只能为0或者1，是否把该角色的用户在用户列表排到前面 */
        val hoist: Int,
        /** 只能为0或者1，该角色是否可以被提及 */
        val mentionable: Int,
        /** 权限,参见 [权限说明](https://developer.kaiheila.cn/doc/http/guild-role#权限说明) */
        val permissions: Int,
    )

}


