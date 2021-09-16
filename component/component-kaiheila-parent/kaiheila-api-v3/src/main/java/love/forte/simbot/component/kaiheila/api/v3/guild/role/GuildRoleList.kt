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

package love.forte.simbot.component.kaiheila.api.v3.guild.role

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.component.kaiheila.api.*
import love.forte.simbot.component.kaiheila.api.v3.guild.GetGuildApiReq
import love.forte.simbot.component.kaiheila.objects.Role


/**
 *
 * [获取服务器角色列表](https://developer.kaiheila.cn/doc/http/guild-role#获取服务器角色列表)
 *
 * method: `GET`
 *
 * `/api/v3/guild-role/list`
 *
 * @param guildId 服务器的id
 */
public class GuildRoleListReq(val guildId: String) :
    GetGuildApiReq<ListResp<Role, ApiData.Resp.EmptySort>>,
    BaseApiDataReq<ListResp<Role, ApiData.Resp.EmptySort>>(Key) {
    companion object Key : BaseApiDataKey("guild-role", "list") {

    }

    override val dataSerializer: DeserializationStrategy<ListResp<Role, ApiData.Resp.EmptySort>>
        get() = Role.emptySortSerializer

    protected override fun createBody(): Any? = null

    override fun RouteInfoBuilder.doRoute() {
        parameters { append("guild_id", guildId) }
    }

}


