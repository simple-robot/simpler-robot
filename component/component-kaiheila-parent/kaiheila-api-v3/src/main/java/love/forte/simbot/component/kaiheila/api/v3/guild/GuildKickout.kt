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

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.EmptyResp
import love.forte.simbot.component.kaiheila.api.RouteInfoBuilder
import love.forte.simbot.component.kaiheila.api.emptyResp
import love.forte.simbot.component.kaiheila.logger.NamedKeyLogger


/**
 * [踢出服务器](https://developer.kaiheila.cn/doc/http/guild#%E8%B8%A2%E5%87%BA%E6%9C%8D%E5%8A%A1%E5%99%A8)
 *
 * request method: POST
 *
 */
public class GuildKickoutReq(
    guildId: String,
    targetId: String,
) : GuildApiReq<EmptyResp> {
    companion object Key : NamedKeyLogger("/guild/kickout", "api.guild.kickout") {
        private val ROUTE = listOf("guild", "kickout")
    }

    override val key: ApiData.Req.Key
        get() = Key

    override val dataSerializer: DeserializationStrategy<EmptyResp> = emptyResp()

    override val method: HttpMethod get() = HttpMethod.Post

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = ROUTE
    }

    override val body: Any = Body(guildId, targetId)

    @Serializable
    private data class Body(
        @SerialName("guild_id") val guildId: String,
        @SerialName("target_id") val targetId: String,
    )

}