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
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.InternalKookApi
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.objects.Channel
import love.forte.simbot.kook.objects.PermissionOverwrite
import love.forte.simbot.kook.objects.PermissionUser
import love.forte.simbot.kook.util.BooleanToIntSerializer
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 *
 * [获取频道详情](https://developer.kookapp.cn/doc/http/channel#%E8%8E%B7%E5%8F%96%E9%A2%91%E9%81%93%E8%AF%A6%E6%83%85)
 *
 * @author ForteScarlet
 */
public class GetChannelViewApi private constructor(private val targetId: String, private val needChildren: Boolean?) :
    KookGetApi<ChannelView>() {

    public companion object Factory {
        private val PATH = ApiPath.create("channel", "view")

        /**
         * 构建 [GetChannelViewApi]
         * @param targetId 目标频道ID
         */
        @JvmStatic
        @JvmOverloads
        public fun create(targetId: String, needChildren: Boolean? = null): GetChannelViewApi =
            GetChannelViewApi(targetId, needChildren)
    }

    override val resultDeserializationStrategy: DeserializationStrategy<ChannelView>
        get() = ChannelView.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("target_id", targetId)
            needChildren?.also { append("need_children", it.toString()) }
        }
    }
}


/**
 * [GetChannelViewApi] 的响应体。
 *
 * @see GetChannelViewApi
 */
@Serializable
public data class ChannelView @ApiResultType constructor(
    /** 频道id */
    public val id: String,
    /** 服务器id */
    @SerialName("guild_id")
    public val guildId: String,
    /** 频道创建者id */
    @SerialName("user_id")
    public val userId: String,
    /** 父分组频道id */
    @SerialName("parent_id")
    public val parentId: String,
    /** 频道名称 */
    public val name: String,
    /** 频道简介 */
    public val topic: String,

    /**
     * 频道类型，`0` 分组，`1` 文字，`2` 语音
     */
    public val type: Int,
    /** 频道排序 */
    public val level: Int,
    /** 慢速限制，单位秒。用户发送消息之后再次发送消息的等待时间。 */
    @SerialName("slow_mode")
    public val slowMode: Int,

    /**
     * 是否已设置密码
     */
    @SerialName("has_password")
    public val hasPassword: Boolean = false,

    /** 人数限制 */
    @SerialName("limit_amount")
    public val maximumMember: Int,

    /** 是否为分组类型 */
    @SerialName("is_category")
    @Serializable(BooleanToIntSerializer::class)
    public val isCategory: Boolean = false,

    /** 语音服务器地址，HOST:PORT的格式 */
    @SerialName("server_url")
    val serverUrl: String,

    /**
     * 针对角色的频道权限覆盖
     */
    @SerialName("permission_overwrites")
    public val permissionOverwrites: List<PermissionOverwrite> = emptyList(),

    /**
     * 针对用户的频道权限覆盖
     */
    @SerialName("permission_users")
    public val permissionUsers: List<String> = emptyList(),
    // TODO String?

    /**
     * 是否与分组频道同步权限
     */
    @SerialName("permission_sync")
    public val permissionSync: Int = 0,

    /**
     * 子频道的id列表
     *
     * 受 [GetChannelViewApi] 的 `need_children` 属性影响可能得到 `null` 。
     */
    public val children: List<String>? = null
)

/**
 * 将 [ChannelView] 转化为 [Channel] 类型，
 * 并可选的提供一些缺失字段的默认值。
 *
 * @since 4.3.0
 */
@InternalKookApi
public fun ChannelView.toChannel(
    permissionUsers: List<PermissionUser> = emptyList(),
): Channel = ChannelViewChannel(
    this,
    permissionUsers = permissionUsers,
)

private class ChannelViewChannel(
    private val channelView: ChannelView,
    override val permissionUsers: List<PermissionUser>,
) : Channel {
    override val id: String
        get() = channelView.id
    override val name: String
        get() = channelView.name
    override val userId: String
        get() = channelView.userId
    override val guildId: String
        get() = channelView.guildId
    override val topic: String
        get() = channelView.topic
    override val isCategory: Boolean
        get() = channelView.isCategory
    override val parentId: String
        get() = channelView.parentId
    override val level: Int
        get() = channelView.level
    override val slowMode: Int
        get() = channelView.slowMode
    override val type: Int
        get() = channelView.type
    override val permissionSync: Int
        get() = channelView.permissionSync
    override val hasPassword: Boolean
        get() = channelView.hasPassword
    override val permissionOverwrites: List<PermissionOverwrite>
        get() = channelView.permissionOverwrites

    override fun toString(): String {
        return "ChannelViewChannel(channelView=$channelView, permissionOverwrites=$permissionOverwrites, " +
            "permissionUsers=$permissionUsers)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChannelViewChannel) return false

        if (channelView != other.channelView) return false
        if (permissionOverwrites != other.permissionOverwrites) return false
        if (permissionUsers != other.permissionUsers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channelView.hashCode()
        result = 31 * result + permissionOverwrites.hashCode()
        result = 31 * result + permissionUsers.hashCode()
        return result
    }
}
