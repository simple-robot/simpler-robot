/*
 *     Copyright (c) 2021-2026. ForteScarlet.
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

package love.forte.simbot.kook.api.channel

import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.api.ListData
import love.forte.simbot.kook.api.ListMeta
import love.forte.simbot.kook.objects.Channel
import love.forte.simbot.kook.objects.PermissionOverwrite
import love.forte.simbot.kook.objects.PermissionUser
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * [获取频道列表](https://developer.kookapp.cn/doc/http/channel#%E8%8E%B7%E5%8F%96%E9%A2%91%E9%81%93%E5%88%97%E8%A1%A8)
 *
 * @author ForteScarlet
 */
public class GetChannelListApi private constructor(
    private val guildId: String,
    private val page: Int? = null,
    private val pageSize: Int? = null,
    private val type: Int? = null,
) : KookGetApi<ListData<ChannelInfo>>() {
    public companion object Factory {
        private val PATH = ApiPath.create("channel", "list")

        private val serializer = ListData.serializer(ChannelInfo.serializer())

        /**
         * 构建 [GetChannelListApi]
         * @param guildId 频道服务器ID
         * @param page 目标页数
         * @param pageSize 每页数据数量
         * @param type 频道类型, `1`为文字，`2`为语音, 默认为`1`.
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            guildId: String,
            page: Int? = null,
            pageSize: Int? = null,
            type: Int? = null,
        ): GetChannelListApi = GetChannelListApi(guildId, page, pageSize, type)
    }

    override val resultDeserializationStrategy: DeserializationStrategy<ListData<ChannelInfo>>
        get() = serializer

    override val apiPath: ApiPath
        get() = PATH

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("guild_id", guildId)
            page?.also { append("page", it.toString()) }
            pageSize?.also { append("page_size", it.toString()) }
            type?.also { append("type", it.toString()) }
        }
    }
}

/**
 * Api [GetChannelListApi] 的响应体。
 */
@Serializable
public data class ChannelInfo @ApiResultType constructor(
    /**
     * 频道id
     */
    public val id: String,
    /**
     * 频道名称
     */
    public val name: String,
    /**
     * 是否为分组类型
     */
    @SerialName("is_category") public val isCategory: Boolean,
    /**
     * 频道创建者id
     */
    @SerialName("user_id") public val userId: String,
    /**
     * 父分组频道id
     */
    @SerialName("parent_id") public val parentId: String,
    /**
     * 频道排序
     */
    public val level: Int,
    /**
     * 频道类型
     */
    public val type: Int,
    /**
     * 人数限制
     */
    @SerialName("limit_amount")
    public val limitAmount: Int,

    /**
     * @since 4.3.0
     */
    @SerialName("guild_id")
    public val guildId: String = "",

    /**
     * 主题
     * @since 4.3.0
     */
    public val topic: String = "",

    /**
     * 慢速模式。单位为秒。
     * @since 4.3.0
     */
    @SerialName("slow_mode")
    public val slowMode: Int = 0,

    /**
     * @since 4.3.0
     */
    @SerialName("permission_overwrites")
    public val permissionOverwrites: List<PermissionOverwrite> = emptyList(),

    /**
     * @since 4.3.0
     */
    @SerialName("permission_sync")
    public val permissionSync: Int = 0,

    /**
     * @since 4.3.0
     */
    @SerialName("has_password")
    public val hasPassword: Boolean = false,
)

/**
 * 将 [ChannelInfo] 转化为 [Channel] 类型，
 * 并可选的提供一些缺失字段的默认值。
 */
public fun ChannelInfo.toChannel(
    guildId: String = this.guildId,
    topic: String = this.topic,
    slowMode: Int = this.slowMode,
    permissionOverwrites: List<PermissionOverwrite> = this.permissionOverwrites,
    permissionUsers: List<PermissionUser> = emptyList(),
    permissionSync: Int = this.permissionSync,
    hasPassword: Boolean = this.hasPassword,
): Channel = ChannelInfoChannel(
    this,
    guildId = guildId,
    topic = topic,
    slowMode = slowMode,
    permissionOverwrites = permissionOverwrites,
    permissionUsers = permissionUsers,
    permissionSync = permissionSync,
    hasPassword = hasPassword
)


private class ChannelInfoChannel(
    private val channelInfo: ChannelInfo,
    override val guildId: String,
    override val topic: String,
    override val slowMode: Int,
    override val permissionOverwrites: List<PermissionOverwrite>,
    override val permissionUsers: List<PermissionUser>,
    override val permissionSync: Int,
    override val hasPassword: Boolean,
) : Channel {
    override val id: String
        get() = channelInfo.id
    override val name: String
        get() = channelInfo.name
    override val userId: String
        get() = channelInfo.userId
    override val isCategory: Boolean
        get() = channelInfo.isCategory
    override val parentId: String
        get() = channelInfo.parentId
    override val level: Int
        get() = channelInfo.level
    override val type: Int
        get() = channelInfo.type

    override fun toString(): String {
        return "ChannelInfoChannel(channelInfo=$channelInfo, guildId='$guildId', topic='$topic', slowMode=$slowMode, " +
            "permissionOverwrites=$permissionOverwrites, " +
            "permissionUsers=$permissionUsers, permissionSync=$permissionSync, " +
            "hasPassword=$hasPassword)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChannelInfoChannel) return false

        if (channelInfo != other.channelInfo) return false
        if (guildId != other.guildId) return false
        if (topic != other.topic) return false
        if (slowMode != other.slowMode) return false
        if (permissionOverwrites != other.permissionOverwrites) return false
        if (permissionUsers != other.permissionUsers) return false
        if (permissionSync != other.permissionSync) return false
        if (hasPassword != other.hasPassword) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channelInfo.hashCode()
        result = 31 * result + guildId.hashCode()
        result = 31 * result + topic.hashCode()
        result = 31 * result + slowMode
        result = 31 * result + permissionOverwrites.hashCode()
        result = 31 * result + permissionUsers.hashCode()
        result = 31 * result + permissionSync
        result = 31 * result + hasPassword.hashCode()
        return result
    }
}


/**
 * 批次量的通过 [GetChannelListApi] 查询所有结果直至最后一次响应的 [ListMeta.page] >= [ListMeta.pageTotal]。
 *
 * @param block 通过一个页码参数来通过 [GetChannelListApi] 发起一次请求
 */
public inline fun GetChannelListApi.Factory.createFlow(
    crossinline block: suspend GetChannelListApi.Factory.(page: Int) -> ListData<ChannelInfo>
): Flow<ListData<ChannelInfo>> = flow {
    var page = 1
    do {
        val listData = block(page)
        emit(listData)
        page = listData.meta.page + 1
    } while (listData.items.isNotEmpty() && listData.meta.page < listData.meta.pageTotal)
}

/**
 * 批次量的通过 [GetChannelListApi] 查询所有结果直至最后一次响应的 [ListMeta.page] >= [ListMeta.pageTotal]。
 *
 * @param block 通过一个页码参数来通过 [GetChannelListApi] 发起一次请求
 */
public inline fun GetChannelListApi.Factory.createItemFlow(
    crossinline block: suspend GetChannelListApi.Factory.(page: Int) -> ListData<ChannelInfo>
): Flow<ChannelInfo> = flow {
    var page = 1
    do {
        val listData = block(page)
        listData.items.forEach { emit(it) }
        page = listData.meta.page + 1
    } while (listData.items.isNotEmpty() && listData.meta.page < listData.meta.pageTotal)
}
