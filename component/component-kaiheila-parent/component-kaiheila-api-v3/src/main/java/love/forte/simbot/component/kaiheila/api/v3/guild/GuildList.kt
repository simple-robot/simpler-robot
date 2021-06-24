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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.*


/**
 * [获取当前用户加入的服务器列表](https://developer.kaiheila.cn/doc/http/guild#%E8%8E%B7%E5%8F%96%E5%BD%93%E5%89%8D%E7%94%A8%E6%88%B7%E5%8A%A0%E5%85%A5%E7%9A%84%E6%9C%8D%E5%8A%A1%E5%99%A8%E5%88%97%E8%A1%A8).
 *
 * request method: `GET`
 *
 * parameter or body: Empty.
 *
 * [GuildListReq] 几乎没有什么会变化的参数，请求方式：
 * ```
 * GuildListReq.NoSort.doRequest(V3, client, token)
 *
 * // or sort by id
 * // asc
 * GuildListReq.SortById.Asc.doRequest(...)
 * // desc
 * GuildListReq.SortById.Desc.doRequest(...)
 *
 * ```
 *
 *
 *
 *
 */
public sealed class GuildListReq<SORT> :
    GuildApiReq<ListResp<GuildListRespData, SORT>> {
    private companion object Key : ApiData.Req.Key by key("/guild/list") {
        private val ROUTE = listOf("guild", "list")
    }

    override val key: ApiData.Req.Key get() = Key


    /**
     * route build.
     */
    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = ROUTE
    }

    override val body: Any?
        get() = null

    /**
     * 无排序的 [Guild List请求实例][GuildListReq]
     */
    object NoSort : GuildListReq<ApiData.Resp.EmptySort>() {
        /**
         * data serializer.
         */
        override val dataSerializer: DeserializationStrategy<ListResp<GuildListRespData, ApiData.Resp.EmptySort>> =
            listResp(GuildListRespData.serializer(), ApiData.Resp.EmptySort.serializer())
    }

    /**
     * 根据ID排序
     */
    sealed class SortById(asc: Boolean = true) : GuildListReq<GuildApiRespSort>() {
        private val sortValue = if(asc) "id" else "-id"
        override fun route(builder: RouteInfoBuilder) {
            super.route(builder)
            builder.parametersBuilder.append("sort", sortValue)
        }

        /**
         * data serializer.
         */
        override val dataSerializer: DeserializationStrategy<ListResp<GuildListRespData, GuildApiRespSort>> =
            listResp(GuildListRespData.serializer(), GuildApiRespSort.serializer())

        object Asc : SortById()
        object Desc : SortById(false)
    }



}


/**
 *
 * [获取当前用户加入的服务器列表](https://developer.kaiheila.cn/doc/http/guild#%E8%8E%B7%E5%8F%96%E5%BD%93%E5%89%8D%E7%94%A8%E6%88%B7%E5%8A%A0%E5%85%A5%E7%9A%84%E6%9C%8D%E5%8A%A1%E5%99%A8%E5%88%97%E8%A1%A8)
 * api的返回值。
 *
 *
 *
 */
@Serializable
public data class GuildListRespData(
    /**
     * 服务器id
     */
    val id: String,
    /**
     * 服务器名称
     */
    val name: String,
    /**
     * 服务器主题
     */
    val topic: String,
    /**
     * 服务器主的id
     */
    @SerialName("master_id")
    val masterId: String,
    /**
     * 	服务器icon的地址
     */
    val icon: String,

    /**
     * 通知类型,
     * - `0` 代表默认使用服务器通知设置
     * - `1` 代表接收所有通知
     * - `2` 代表仅@被提及
     * - `3` 代表不接收通知
     */
    @SerialName("notify_type")
    val notifyType: Int,

    /**
     * 服务器默认使用语音区域
     */
    val region: String,
    /**
     * 是否为公开服务器
     */
    @SerialName("enable_open")
    // @Serializable(BooleanAsIntSerializer::class)
    val enableOpen: Boolean,
    /**
     * 公开服务器id
     */
    @SerialName("open_id")
    val openId: String,
    /**
     * 	默认频道id
     */
    @SerialName("default_channel_id")
    val defaultChannelId: String,
    /**
     * 欢迎频道id
     */
    @SerialName("welcome_channel_id")
    val welcomeChannelId: String,
) : GuildApiRespData


@Serializable
public data class GuildApiRespSort(val id: Int)

public inline val GuildApiRespSort.isAsc: Boolean get() = id == 1
public inline val GuildApiRespSort.isDesc: Boolean get() = !isAsc



