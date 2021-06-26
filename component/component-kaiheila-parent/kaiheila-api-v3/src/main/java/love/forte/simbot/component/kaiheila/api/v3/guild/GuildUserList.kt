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
    val guildId: String,
    /**	否 服务器中频道的 ID */
    val channelId: String? = null,
    /**	否 搜索关键字，在用户名或昵称中搜索 */
    val search: String? = null,
    /**	否 角色 ID，获取特定角色的用户列表 */
    val roleId: Int? = null,
    /**	否 只能为0或1，0是未认证，1是已认证 */
    val mobileVerified: Int? = null,
    /**	否 根据活跃时间排序，0是顺序排列，1是倒序排列 */
    val activeTime: Int? = null,
    /**	否 根据加入时间排序，0是顺序排列，1是倒序排列 */
    val joinedAt: Int? = null,
    /**	否 目标页 */
    val page: Int? = null,
    /**	否 每页数据数量 */
    val pageSize: Int? = null,
) : GuildApiReq<ObjectResp<GuildUserList>> {

    companion object Key : ApiData.Req.Key by key("/guild/user-list") {
        private val ROUTE = listOf("guild", "user-list")
        private val DATA_SERIALIZER: DeserializationStrategy<ObjectResp<GuildUserList>> =
            objectResp(GuildUserList.serializer())

        @JvmStatic
        fun builder(): GuildUserListReqBuilder = GuildUserListReqBuilder()
    }

    override val method: HttpMethod
        get() = HttpMethod.Get

    override val key: ApiData.Req.Key
        get() = Key

    override val dataSerializer: DeserializationStrategy<ObjectResp<GuildUserList>>
        get() = DATA_SERIALIZER

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = ROUTE
        builder.parameters {
            append("guild_id", guildId)
            appendIfNotnull("channelId", channelId) { it }
            appendIfNotnull("search", search) { it }
            appendIfNotnull("roleId", roleId)
            appendIfNotnull("mobileVerified", mobileVerified)
            appendIfNotnull("activeTime", activeTime)
            appendIfNotnull("joinedAt", joinedAt)
            appendIfNotnull("page", page)
            appendIfNotnull("pageSize", pageSize)
        }
    }

    override val body: Any?
        get() = null
}


public inline fun guildUserListReq(block: GuildUserListReqBuilder.() -> Unit): GuildUserListReq {
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
    val items: List<GuildUser>,
) : GuildApiRespData


/**
 * Guild User from [GuildUserListReq]
 *
 * ```json
 *  {
 *  "id": "444",
 *  "username": "***",
 *  "avatar": "https:// **.jpg",
 *  "online": true,
 *  "nickname": "***",
 *  "joined_at": 1611743334000,
 *  "active_time": 1612691445583,
 *  "roles": [],
 *  "is_master": true,
 *  "abbr": "***",
 *  }
 * ```
 */
@Serializable
public data class GuildUser(
    override val id: String,
    override val username: String,
    override val nickname: String,
    override val online: Boolean,
    override val status: Int = 0,
    override val avatar: String,
    override val bot: Boolean = false,
    @SerialName("joined_at")
    val joinedAt: Long,
    @SerialName("active_time")
    val activeTime: Long,
    @SerialName("is_master")
    val master: Boolean,
    @SerialName("mobile_verified")
    override val mobileVerified: Boolean = false,
    override val system: Boolean = false,
    @SerialName("invited_count")
    override val invitedCount: Int = 0,
    @SerialName("identify_num")
    override val identifyNum: String = username.split("#", limit = 2).let { if (it.size < 2) it[1] else "" },
    override val roles: List<Int> = emptyList(),
) : User {

    override val mobilePrefix: String? get() = null
    override val mobile: String? get() = null
    override val originalData: String
        get() = this.toString()
}
