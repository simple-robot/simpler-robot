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
 *
 * [修改服务器中用户的昵称](https://developer.kaiheila.cn/doc/http/guild#%E4%BF%AE%E6%94%B9%E6%9C%8D%E5%8A%A1%E5%99%A8%E4%B8%AD%E7%94%A8%E6%88%B7%E7%9A%84%E6%98%B5%E7%A7%B0)
 *
 * request method: POST
 *
 * @author ForteScarlet
 */
public class GuildNicknameReq(
    guildId: String,
    nickname: String? = null,
    userId: String? = null,
) : EmptyRespPostGuildApiReq {
    companion object Key : ApiData.Req.Key by key("/guild/nickname") {
        private val ROUTE = listOf("guild", "nickname")
        @JvmStatic
        public fun builder(): GuildNicknameReqBuilder = GuildNicknameReqBuilder()
    }

    override val key: ApiData.Req.Key
        get() = Key

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = ROUTE
    }

    override val body: Any = Body(guildId, nickname, userId)

    /** Request Body */
    @Serializable
    private data class Body(
        @SerialName("guild_id")
        val guildId: String,
        val nickname: String?,
        @SerialName("user_id")
        val userId: String?,
    ) {
        init {
            if (nickname != null) {
                require(nickname.length in 2..64) { "The length of 'nickname' must be between 2 and 64" }
            }
        }
    }

}


public inline fun guildNicknameReq(block: GuildNicknameReqBuilder.() -> Unit): GuildNicknameReq {
    return GuildNicknameReqBuilder().also(block).build()
}


public class GuildNicknameReqBuilder {
    var guildId: String? = null
    var nickname: String? = null
    var userId: String? = null

    fun build(): GuildNicknameReq = GuildNicknameReq(
        requireNotNull(guildId) { "Required guildId was null." },
        nickname, userId
    )
}
