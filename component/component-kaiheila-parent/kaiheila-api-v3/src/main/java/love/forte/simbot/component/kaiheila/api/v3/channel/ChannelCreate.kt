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

package love.forte.simbot.component.kaiheila.api.v3.channel

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.*


/**
 *
 * [创建频道](https://developer.kaiheila.cn/doc/http/channel#%E5%88%9B%E5%BB%BA%E9%A2%91%E9%81%93)
 *
 * request method: POST
 *
 * @author ForteScarlet
 */
public class ChannelCreateReq(
    /** 是 服务器id */
    guildId: String,
    /** 是 频道名称 */
    name: String,
    /** 否 频道类型，1 文字，2 语音，默认为文字 */
    type: Int = 1,
    /** 否 父分组id */
    parentId: String? = null,
    /** 否 语音频道人数限制，最大99 */
    limitAmount: Int = 99,
    /** 否 语音音质，默认为2。1流畅，2正常，3高质量 */
    voiceQuality: Int = 2,
) : ChannelApiReq<ObjectResp<ChannelView>> {
    companion object Key : ApiData.Req.Key by key("/channel/create") {
        private val ROUTE = listOf("channel", "create")
        private val DATA_SERIALIZER: DeserializationStrategy<ObjectResp<ChannelView>> =
            objectResp(ChannelView.serializer())
    }

    override val method: HttpMethod
        get() = HttpMethod.Post

    override val key: ApiData.Req.Key get() = Key

    override val dataSerializer: DeserializationStrategy<ObjectResp<ChannelView>>
        get() = DATA_SERIALIZER

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = ROUTE
    }

    override val body: Any = Body(guildId, name, type, parentId, limitAmount, voiceQuality)

    @Serializable
    private data class Body(
        /** 是 服务器id */
        @SerialName("guild_id")
        val guildId: String,

        /** 是 频道名称 */
        val name: String,

        /** 否 频道类型，1 文字，2 语音，默认为文字 */
        val type: Int = 1,

        /** 否 父分组id */
        @SerialName("parent_id")
        val parentId: String? = null,

        /** 否 语音频道人数限制，最大99 */
        @SerialName("limit_amount")
        val limitAmount: Int = 99,

        /** 否 语音音质，默认为2。1流畅，2正常，3高质量 */
        @SerialName("voice_quality")
        val voiceQuality: Int = 2,
    ) {
        init {
            require(type in 1..2) { "The type must be 1(Text) or 2(Voice), but $type" }
            require(limitAmount in 1..99) { "The limitAmount must between 1 and 99, but $limitAmount" }
            require(voiceQuality in 1..3) { "The voiceQuality must between 1 and 3, but $voiceQuality" }
        }
    }
}

public class ChannelCreateBuilder {
    /** 是 服务器id */
    var guildId: String? = null

    /** 是 频道名称 */
    var name: String? = null

    /** 否 频道类型，1 文字，2 语音，默认为文字 */
    var type: Int = 1

    /** 否 父分组id */
    var parentId: String? = null

    /** 否 语音频道人数限制，最大99 */
    var limitAmount: Int = 99

    /** 否 语音音质，默认为2。1流畅，2正常，3高质量 */
    var voiceQuality: Int = 2

    fun build(): ChannelCreateReq = ChannelCreateReq(
        requireNotNull(guildId) { "Required guildId was null." },
        requireNotNull(name) { "Required name was null." },
        type, parentId, limitAmount, voiceQuality
    )
}

public inline fun channelCreateReq(block: ChannelCreateBuilder.() -> Unit) : ChannelCreateReq {
    return ChannelCreateBuilder().also(block).build()
}