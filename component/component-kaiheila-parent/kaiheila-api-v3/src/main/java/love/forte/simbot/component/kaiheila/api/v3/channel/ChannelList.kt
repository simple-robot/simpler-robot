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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import love.forte.simbot.component.kaiheila.`object`.Channel
import love.forte.simbot.component.kaiheila.`object`.ChannelPermissionOverwrites
import love.forte.simbot.component.kaiheila.api.*


/**
 * [获取频道列表](https://developer.kaiheila.cn/doc/http/channel#%E8%8E%B7%E5%8F%96%E9%A2%91%E9%81%93%E5%88%97%E8%A1%A8)
 *
 * request method: GET
 *
 */
public class ChannelListReq(private val guildId: String) :
    GetChannelApiReq<ListResp<ChannelInfo, ApiData.Resp.EmptySort>> {
    companion object Key : ApiData.Req.Key by key("/channel/list") {
        private val ROUTE = listOf("channel", "list")
        private val DATA_SERIALIZER: DeserializationStrategy<ListResp<ChannelInfo, ApiData.Resp.EmptySort>> =
            listResp(ChannelInfo.serializer())
    }

    override val key: ApiData.Req.Key get() = Key

    override val dataSerializer: DeserializationStrategy<ListResp<ChannelInfo, ApiData.Resp.EmptySort>>
        get() = DATA_SERIALIZER

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = ROUTE
        builder.parameters {
            append("guild_id", guildId)
        }
    }

    override val body: Any?
        get() = null

    override fun post(resp: ListResp<ChannelInfo, ApiData.Resp.EmptySort>) {
        resp.data.items.forEach { it.guildIdLate = guildId }
    }
}


@Serializable()
public data class ChannelInfo(
    /**
     * 频道id
     */
    override val id: String,
    /**
     *	频道名称
     */
    override val name: String,
    /**
     * 是否为分组类型
     */
    @SerialName("is_category")
    override val isCategory: Boolean,
    /**
     *	频道创建者id
     */
    @SerialName("user_id")
    override val userId: String,
    /**
     *	父分组频道id
     */
    @SerialName("parent_id")
    override val parentId: String,
    /**
     * 频道排序
     */
    override val level: Int,
    /**
     * 频道类型
     */
    override val type: Int,

    @SerialName("limit_amount")
    val limitAmount: Int,


    override val topic: String = "",
    @SerialName("slow_mode")
    override val slowMode: Int = 0,
    @SerialName("permission_overwrites")
    override val permissionOverwrites: List<ChannelPermissionOverwrites> = emptyList(),
    @SerialName("permission_users")
    override val permissionUsers: List<String> = emptyList(),
    @SerialName("permission_sync")
    override val permissionSync: Int = 0,
) : Channel {

    @Transient
    internal lateinit var guildIdLate: String
    override val guildId: String get() = guildIdLate
    override val originalData: String
        get() = toString()

}





