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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.api.message.results.MuteInfo
import love.forte.simbot.api.message.results.MuteList
import love.forte.simbot.component.kaiheila.api.*


/**
 *
 * [服务器静音闭麦列表](https://developer.kaiheila.cn/doc/http/guild#%E6%9C%8D%E5%8A%A1%E5%99%A8%E9%9D%99%E9%9F%B3%E9%97%AD%E9%BA%A6%E5%88%97%E8%A1%A8)
 *
 * request method: GET
 *
 */
public sealed class GuildMuteListReq<T : GuildMuteListResult>(
    private val guildId: String,
    detailResultType: Boolean
) : GetGuildApiReq<ObjectResp<T>> {
    companion object Key : ApiData.Req.Key by key("/guild-mute/list") {
        private val ROUTE = listOf("guild-mute", "list")
        private val DETAIL_SERIALIZER: DeserializationStrategy<ObjectResp<GuildMuteListByDetail>> = objectResp(GuildMuteListByDetail.serializer())
        private val SIMPLE_SERIALIZER: DeserializationStrategy<ObjectResp<GuildMuteListBySimple>> = objectResp(GuildMuteListBySimple.serializer())
    }
    private val resultType: String? = if (detailResultType) "detail" else null
    override val key: ApiData.Req.Key get() = Key
    override val body: Any? get() = null

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = ROUTE
        builder.parameters {
            append("guild_id", guildId)
            appendIfNotnull("return_type", resultType) { it }
        }
    }

    class Detail(guildId: String) : GuildMuteListReq<GuildMuteListByDetail>(guildId, true) {
        override val dataSerializer: DeserializationStrategy<ObjectResp<GuildMuteListByDetail>>
            get() = DETAIL_SERIALIZER
    }

    @Deprecated("官方推荐使用 'Detail' 进行查询", ReplaceWith("GuildMuteListReq.Detail(guildId)"))
    class Simple(guildId: String) : GuildMuteListReq<GuildMuteListBySimple>(guildId, false) {
        override val dataSerializer: DeserializationStrategy<ObjectResp<GuildMuteListBySimple>>
            get() = SIMPLE_SERIALIZER
    }


}


/**
 * mute list result.
 */
public sealed class GuildMuteListResult : GuildApiRespData(), ApiData.Resp.Data, MuteList {
    /**
     * 根据类型获取用户列表。
     */
    abstract fun getUserIds(type: Int): List<String>

    override val originalData: String
        get() = toString()

    override lateinit var results: List<MuteInfo>
}


/**
 *
 * 通过 [服务器静音列表](https://developer.kaiheila.cn/doc/http/guild#%E6%9C%8D%E5%8A%A1%E5%99%A8%E9%9D%99%E9%9F%B3%E9%97%AD%E9%BA%A6%E5%88%97%E8%A1%A8)
 * 的 `return_type = 'detail'` 得到的结果.
 *
 */
@Serializable
public data class GuildMuteListByDetail(
    val mic: Mic,
    val headset: Headset,
) : GuildMuteListResult() {

    @Serializable
    data class Mic(
        override val type: Int = 1,
        @SerialName("user_ids")
        override val userIds: List<String> = emptyList(),
    ) : GuildMuteResult

    @Serializable
    data class Headset(
        override val type: Int = 2,
        @SerialName("user_ids")
        override val userIds: List<String> = emptyList(),
    ) : GuildMuteResult

    override fun getUserIds(type: Int): List<String> {
        return when (type) {
            mic.type -> mic.userIds
            headset.type -> headset.userIds
            else -> emptyList()
        }
    }
}


@Serializable
public data class GuildMuteListBySimple(
    @SerialName("1") val mic: List<String>,
    @SerialName("2") val headset: List<String>,
) : GuildMuteListResult() {
    override fun getUserIds(type: Int): List<String> = when (type) {
        1 -> mic
        2 -> headset
        else -> emptyList()
    }
}


/**
 * Mute响应值里有两种属性：禁言类型、对应用户列表。
 */
public interface GuildMuteResult {
    /**
     * `1`代表麦克风闭麦，`2`代表耳机静音。
     */
    val type: Int

    /**
     * 对应用户列表
     */
    val userIds: List<String>
}


// public


public inline val GuildMuteListResult.micMuteUsers: List<String> get() = getUserIds(1)
public inline val GuildMuteListResult.headsetMuteUsers: List<String> get() = getUserIds(2)


