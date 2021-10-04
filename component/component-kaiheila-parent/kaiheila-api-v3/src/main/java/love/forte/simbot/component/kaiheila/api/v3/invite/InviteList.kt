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

package love.forte.simbot.component.kaiheila.api.v3.invite

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.*
import love.forte.simbot.component.kaiheila.objects.User


/**
 *
 * [获取邀请列表](https://developer.kaiheila.cn/doc/http/invite#获取邀请列表)
 *
 * `/api/v3/invite/list`
 *
 * method: GET
 *
 */
public class InviteListReq(
    private val guildId: String,
    private val channelId: String,
) : GetInviteApiReq<ListResp<InviteListResp, ApiData.Resp.EmptySort>>,
    BaseApiDataReq<ListResp<InviteListResp, ApiData.Resp.EmptySort>>(Key) {
    companion object Key : BaseApiDataKey("invite", "list")

    override val dataSerializer: DeserializationStrategy<ListResp<InviteListResp, ApiData.Resp.EmptySort>>
        get() = InviteListResp.listSerializer

    override fun RouteInfoBuilder.doRoute() {
        parameters {
            append("guild_id", guildId)
            append("channel_id", channelId)
        }
    }

    override fun createBody(): Any? = null
}


@Serializable
public data class InviteListResp(
    /**
     * 服务器id
     *
     */
    @SerialName("guild_id")
    val guildId: String,
    /**
     * 频道id
     *
     */
    @SerialName("channel_id")
    val channelId: String,
    /**
     * url code
     *
     */
    @SerialName("url_code")
    val urlCode: String,
    /**
     * 地址
     *
     */
    val url: String,
    /**
     * 用户
     *
     */
    val user: User,
) : InviteApiRespData() {
    companion object Serializer {
        val listSerializer = listResp<InviteListResp, ApiData.Resp.EmptySort>()
    }
}