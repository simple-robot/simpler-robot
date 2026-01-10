/*
 *     Copyright (c) 2023-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.kook.api.member

import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.api.ListDataResponse
import love.forte.simbot.kook.api.ListMeta
import love.forte.simbot.kook.objects.SimpleUser
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/*
 */

/**
 * [获取服务器中的用户列表](https://developer.kookapp.cn/doc/http/guild#%E8%8E%B7%E5%8F%96%E6%9C%8D%E5%8A%A1%E5%99%A8%E4%B8%AD%E7%9A%84%E7%94%A8%E6%88%B7%E5%88%97%E8%A1%A8)
 *
 * @author ForteScarlet
 */
public class GetGuildMemberListApi private constructor(
    private val guildId: String,
    private val channelId: String? = null,
    private val search: String? = null,
    private val roleId: Int? = null,
    private val mobileVerified: Int? = null,
    private val activeTime: Int? = null,
    private val joinedAt: Int? = null,
    private val page: Int? = null,
    private val pageSize: Int? = null,
    private val filterUserId: String? = null,
) : KookGetApi<GuildMemberList>() {
    public companion object Factory {
        private const val SORT_ASC = 0
        private const val SORT_DESC = 1
        private val PATH = ApiPath.create("guild", "user-list")

        /**
         * 构造 [GetGuildMemberListApi].
         *
         * @param guildId 服务器 id
         * @param channelId 频道 id
         * @param search 搜索关键字，在用户名或昵称中搜索
         * @param roleId 角色 ID，获取特定角色的用户列表
         * @param mobileVerified 只能为0或1，0是未认证，1是已认证
         * @param activeTime 根据活跃时间排序，0是顺序排列，1是倒序排列
         * @param joinedAt 根据加入时间排序，0是顺序排列，1是倒序排列
         * @param page 目标页数
         * @param pageSize 每页数据数量
         * @param filterUserId 获取指定 id 所属用户的信息
         *
         */
        @JvmStatic
        public fun create(
            guildId: String,
            channelId: String? = null,
            search: String? = null,
            roleId: Int? = null,
            mobileVerified: Int? = null,
            activeTime: Int? = null,
            joinedAt: Int? = null,
            page: Int? = null,
            pageSize: Int? = null,
            filterUserId: String? = null,
        ): GetGuildMemberListApi = GetGuildMemberListApi(
            guildId,
            channelId,
            search,
            roleId,
            mobileVerified,
            activeTime,
            joinedAt,
            page,
            pageSize,
            filterUserId
        )

        /**
         * 构造 [GetGuildMemberListApi].
         *
         * @param page 目标页数
         * @param pageSize 每页数据数量
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(guildId: String, page: Int? = null, pageSize: Int? = null): GetGuildMemberListApi {
            return GetGuildMemberListApi(guildId = guildId, page = page, pageSize = pageSize)
        }

        /**
         * 得到用于构建 [GetGuildMemberListApi] 的构建器 [Builder]。
         *
         * Kotlin 中也可以通过 [build] 使用 DSL 风格API。
         *
         */
        @JvmStatic
        public fun builder(guildId: String): Builder = Builder(guildId)

        /**
         * @suppress 为 [Builder] 染色
         */
        @Retention(AnnotationRetention.SOURCE)
        @DslMarker
        public annotation class BuilderDSLMarker

        /**
         * 用于构建 [GetGuildMemberListApi] 的构建器，通过 [builder] 获取。
         */
        public class Builder internal constructor(private val guildId: String) {

            /**
             * 频道 id
             */
            @BuilderDSLMarker
            public var channelId: String? = null

            /**
             * 搜索关键字，在用户名或昵称中搜索
             */
            @BuilderDSLMarker
            public var search: String? = null

            /**
             * 角色 ID，获取特定角色的用户列表
             */
            @BuilderDSLMarker
            public var roleId: Int? = null

            /**
             * 只能为0或1，0是未认证，1是已认证
             */
            @BuilderDSLMarker
            public var mobileVerified: Int? = null

            /**
             * 只能为0或1，0是未认证，1是已认证
             */
            @BuilderDSLMarker
            public var isMobileVerified: Boolean?
                get() = mobileVerified?.let { it == 1 }
                set(value) {
                    mobileVerified = if (value == true) 1 else 0
                }

            /**
             * 根据活跃时间排序，0是顺序排列，1是倒序排列
             */
            @BuilderDSLMarker
            public var activeTime: Int? = null

            /**
             * 根据活跃时间排序，顺序排列
             */
            @BuilderDSLMarker
            public fun sortByActiveTime(): Builder = apply {
                activeTime = SORT_ASC
            }

            /**
             * 根据活跃时间排序，倒序排列
             */
            @BuilderDSLMarker
            public fun sortByActiveTimeDesc(): Builder = apply {
                activeTime = SORT_DESC
            }

            /**
             * 根据加入时间排序，0是顺序排列，1是倒序排列
             */
            @BuilderDSLMarker
            public var joinedAt: Int? = null


            /**
             * 根据活跃时间排序，顺序排列
             */
            @BuilderDSLMarker
            public fun sortByJoinedAt(): Builder = apply {
                joinedAt = SORT_ASC
            }

            /**
             * 根据活跃时间排序，倒序排列
             */
            @BuilderDSLMarker
            public fun sortByJoinedAtDesc(): Builder = apply {
                joinedAt = SORT_DESC
            }

            /**
             * 目标页数
             */
            @BuilderDSLMarker
            public var page: Int? = null

            /**
             * 每页数据数量
             */
            @BuilderDSLMarker
            public var pageSize: Int? = null

            /**
             * 获取指定 id 所属用户的信息
             */
            @BuilderDSLMarker
            public var filterUserId: String? = null


            /**
             * @see channelId
             */
            public fun channelId(channelId: String?): Builder = apply {
                this.channelId = channelId
            }

            /**
             * @see search
             */
            public fun search(search: String?): Builder = apply {
                this.search = search
            }

            /**
             * @see roleId
             */
            public fun roleId(roleId: Int?): Builder = apply {
                this.roleId = roleId
            }

            /**
             * @see mobileVerified
             */
            public fun mobileVerified(mobileVerified: Int?): Builder = apply {
                this.mobileVerified = mobileVerified
            }

            /**
             * @see activeTime
             */
            public fun activeTime(activeTime: Int?): Builder = apply {
                this.activeTime = activeTime
            }

            /**
             * @see joinedAt
             */
            public fun joinedAt(joinedAt: Int?): Builder = apply {
                this.joinedAt = joinedAt
            }

            /**
             * @see page
             */
            public fun page(page: Int?): Builder = apply {
                this.page = page
            }

            /**
             * @see pageSize
             */
            public fun pageSize(pageSize: Int?): Builder = apply {
                this.pageSize = pageSize
            }

            /**
             * @see filterUserId
             */
            public fun filterUserId(filterUserId: String?): Builder = apply {
                this.filterUserId = filterUserId
            }


            /**
             * 构建 [GetGuildMemberListApi].
             *
             */
            public fun build(): GetGuildMemberListApi =
                create(
                    guildId,
                    channelId,
                    search,
                    roleId,
                    mobileVerified,
                    activeTime,
                    joinedAt,
                    page,
                    pageSize,
                    filterUserId
                )
        }

    }

    override val resultDeserializationStrategy: DeserializationStrategy<GuildMemberList> get() = GuildMemberList.serializer()

    override val apiPath: ApiPath get() = PATH

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("guild_id", guildId)
            channelId?.also { append("channel_id", it) }
            search?.also { append("search", it) }
            roleId?.also { append("role_id", it.toString()) }
            mobileVerified?.also { append("mobile_verified", it.toString()) }
            activeTime?.also { append("active_time", it.toString()) }
            joinedAt?.also { append("joined_at", it.toString()) }
            page?.also { append("page", it.toString()) }
            pageSize?.also { append("page_size", it.toString()) }
            filterUserId?.also { append("filter_user_id", it) }
        }
    }
}

/**
 * 构建 [GetGuildMemberListApi.Builder]。
 *
 * ```kotlin
 * val api = GetGuildMemberListApi.build(guildId = "xxx") {
 *      // ...
 * }
 * ```
 *
 * @see GetGuildMemberListApi.builder
 *
 */
@Suppress("RedundantCompanionReference")
public inline fun GetGuildMemberListApi.Factory.build(
    guildId: String,
    block: GetGuildMemberListApi.Factory.Builder.() -> Unit
): GetGuildMemberListApi =
    GetGuildMemberListApi.Factory.builder(guildId).also(block).build()


/**
 * [GetGuildMemberListApi] 响应结果。
 *
 */
@Serializable
public data class GuildMemberList @ApiResultType constructor(
    /**
     * 用户列表
     */
    override val items: List<SimpleUser>,
    override val meta: ListMeta,
    override val sort: Map<String, Int> = emptyMap(),
    /**
     * 用户数量
     */
    @SerialName("user_count") val userCount: Int,
    /**
     * 在线用户数量
     */
    @SerialName("online_count") val onlineCount: Int,
    /**
     * 离线用户数量
     */
    @SerialName("offline_count") val offlineCount: Int,
) : ListDataResponse<SimpleUser, Map<String, Int>>()


/**
 * 批次量的通过 [GetGuildMemberListApi] 查询所有结果直至最后一次响应的 [ListMeta.page] >= [ListMeta.pageTotal]。
 *
 * @param block 通过一个页码参数来通过 [GetGuildMemberListApi] 发起一次请求
 */
public inline fun GetGuildMemberListApi.Factory.createFlow(
    crossinline block: suspend GetGuildMemberListApi.Factory.(page: Int) -> GuildMemberList
): Flow<GuildMemberList> = flow {
    var page = 1
    do {
        val userList = block(page)
        emit(userList)
        page = userList.meta.page + 1
    } while (userList.items.isNotEmpty() && userList.meta.page < userList.meta.pageTotal)
}

/**
 * 批次量的通过 [GetGuildMemberListApi] 查询所有结果直至最后一次响应的 [ListMeta.page] >= [ListMeta.pageTotal]。
 *
 * @param block 通过一个页码参数来通过 [GetGuildMemberListApi] 发起一次请求
 */
public inline fun GetGuildMemberListApi.Factory.createItemFlow(
    crossinline block: suspend GetGuildMemberListApi.Factory.(page: Int) -> GuildMemberList
): Flow<SimpleUser> = flow {
    var page = 1
    do {
        val userList = block(page)
        userList.items.forEach { emit(it) }
        page = userList.meta.page + 1
    } while (userList.items.isNotEmpty() && userList.meta.page < userList.meta.pageTotal)
}
