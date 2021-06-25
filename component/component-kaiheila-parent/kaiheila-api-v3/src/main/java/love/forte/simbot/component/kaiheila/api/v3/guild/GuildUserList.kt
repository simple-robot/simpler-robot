/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     GuildUserList.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.component.kaiheila.api.v3.guild

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.`object`.User
import love.forte.simbot.component.kaiheila.api.*


/**
 * [获取服务器中的用户列表](https://developer.kaiheila.cn/doc/http/guild#%E8%8E%B7%E5%8F%96%E6%9C%8D%E5%8A%A1%E5%99%A8%E4%B8%AD%E7%9A%84%E7%94%A8%E6%88%B7%E5%88%97%E8%A1%A8)
 *
 *
 * request method: GET
 *
 * @see guildUserListReq
 * @see GuildUserListReq.builder
 */
public class GuildUserListReq(
    /**	是 服务器的 ID */
    @SerialName("guild_id")
    val guildId: String,
    /**	否 服务器中频道的 ID */
    @SerialName("channel_id")
    val channelId: String? = null,
    /**	否 搜索关键字，在用户名或昵称中搜索 */
    val search: String? = null,
    /**	否 角色 ID，获取特定角色的用户列表 */
    @SerialName("role_id")
    val roleId: Int? = null,
    /**	否 只能为0或1，0是未认证，1是已认证 */
    @SerialName("mobile_verified")
    val mobileVerified: Int? = null,
    /**	否 根据活跃时间排序，0是顺序排列，1是倒序排列 */
    @SerialName("active_time")
    val activeTime: Int? = null,
    /**	否 根据加入时间排序，0是顺序排列，1是倒序排列 */
    @SerialName("joined_at")
    val joinedAt: Int? = null,
    /**	否 目标页 */
    val page: Int? = null,
    /**	否 每页数据数量 */
    @SerialName("page_size")
    val pageSize: Int? = null,
) : GuildApiReq<ObjectResp<GuildUserList>> {

    private companion object Key : ApiData.Req.Key by key("/guild/user-list") {
        private val ROUTE = listOf("guild", "user-list")
        private val DATA_SERIALIZER: DeserializationStrategy<ObjectResp<GuildUserList>> =
            objectResp(GuildUserList.serializer())

        @JvmStatic
        fun builder(): GuildUserListReqBuilder = GuildUserListReqBuilder()
    }

    override val key: ApiData.Req.Key
        get() = Key

    override val dataSerializer: DeserializationStrategy<ObjectResp<GuildUserList>>
        get() = DATA_SERIALIZER

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = ROUTE
    }

    override val body: Any?
        get() = null
}



public inline fun guildUserListReq(block: GuildUserListReqBuilder.() -> Unit) : GuildUserListReq {
    return GuildUserListReqBuilder().also(block).build()
}



public class GuildUserListReqBuilder {
    /**	是 服务器的 ID */
    var guildId: String? = null
    /**	否 服务器中频道的 ID */
    var channelId: String? = null
    /**	否 搜索关键字，在用户名或昵称中搜索 */
    var search: String? = null
    /**	否 角色 ID，获取特定角色的用户列表 */
    var roleId: Int? = null
    /**	否 只能为0或1，0是未认证，1是已认证 */
    var mobileVerified: Int? = null
    /**	否 根据活跃时间排序，0是顺序排列，1是倒序排列 */
    var activeTime: Int? = null
    /**	否 根据加入时间排序，0是顺序排列，1是倒序排列 */
    var joinedAt: Int? = null
    /**	否 目标页 */
    var page: Int? = null
    /**	否 每页数据数量 */
    var pageSize: Int? = null

    fun build(): GuildUserListReq = GuildUserListReq(
        requireNotNull(guildId) { "Required parameter guildId, but null." },
        channelId, search, roleId, mobileVerified, activeTime, joinedAt, page, pageSize
    )
}


/**
 * Guild list user 响应数据。
 */
@Serializable
public data class GuildUserList(
    /**
     * 用户数量
     */
    @SerialName("user_count")
    val userCount: Int,
    /**
     * 在线用户数量
     */
    @SerialName("online_count")
    val onlineCount: Int,
    /**
     * 离线用户数量
     */
    @SerialName("offline_count")
    val offlineCount: Int,
    /**
     * 用户列表
     */
    val items: List<User>,
) : GuildApiRespData
