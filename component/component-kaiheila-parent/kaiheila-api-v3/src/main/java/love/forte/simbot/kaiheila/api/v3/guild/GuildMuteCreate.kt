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

package love.forte.simbot.kaiheila.api.v3.guild

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.RouteInfoBuilder
import love.forte.simbot.component.kaiheila.api.key


/**
 * [添加服务器静音或闭麦](https://developer.kaiheila.cn/doc/http/guild#%E6%B7%BB%E5%8A%A0%E6%9C%8D%E5%8A%A1%E5%99%A8%E9%9D%99%E9%9F%B3%E6%88%96%E9%97%AD%E9%BA%A6)
 *
 * request method: POST
 *
 */
public class GuildMuteCreateReq(
    /** 服务器id */
    guildId: String,
    /** 用户id */
    userId: String,
    /** 1代表麦克风闭麦，2代表耳机静音 */
    type: Int
) : EmptyRespPostGuildApiReq {
    companion object Key : ApiData.Req.Key by key("/guild-mute/create") {
        private val ROUTE = listOf("guild-mute", "create")
    }

    override val key: ApiData.Req.Key get() = Key

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = ROUTE
    }

    override val body: Any = Body(guildId, userId, type)

    @Serializable
    private data class Body(

        /** 服务器id */
        @SerialName("guild_id")
        val guildId: String,

        /** 用户id */
        @SerialName("user_id")
        val userId: String,

        /** 1代表麦克风闭麦，2代表耳机静音 */
        val type: Int
    )
}
