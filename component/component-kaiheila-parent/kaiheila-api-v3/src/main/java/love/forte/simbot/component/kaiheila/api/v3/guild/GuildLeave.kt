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

package love.forte.simbot.component.kaiheila.api.v3.guild

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.RouteInfoBuilder
import love.forte.simbot.component.kaiheila.api.key


/**
 * [离开服务器](https://developer.kaiheila.cn/doc/http/guild#%E7%A6%BB%E5%BC%80%E6%9C%8D%E5%8A%A1%E5%99%A8)
 *
 * request method: POST
 *
 */
public class GuildLeaveReq(guildId: String) : EmptyRespPostGuildApiReq {
    companion object Key : ApiData.Req.Key by key("/guild/leave") {
        private val ROUTE = listOf("guild", "leave")
    }

    override val key: ApiData.Req.Key get() = Key

    override val body: Any = Body(guildId)

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = ROUTE
    }

    @Serializable
    private data class Body(@SerialName("guild_id") val guildId: String)

}







