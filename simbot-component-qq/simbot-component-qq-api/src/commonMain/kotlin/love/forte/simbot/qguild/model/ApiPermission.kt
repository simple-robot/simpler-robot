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

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.ApiDescription
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * [接口权限对象](https://bot.q.qq.com/wiki/develop/api/openapi/api_permissions/model.html)
 *
 * @property path API 接口名，例如 `/guilds/{guild_id}/members/{user_id}`
 * @property method 请求方法，例如 `GET`
 * @property desc API 接口名称，例如 `获取频道信`
 * @property authStatus 授权状态，`auth_stats` 为 1 时已授权
 */
@ApiModel
@Serializable
public class ApiPermission @ApiModelConstructor constructor(
    public val path: String,
    public val method: String,
    public val desc: String,

    @SerialName("auth_status")
    public val authStatus: Int
) {
    public companion object {
        /**
         * 授权状态，auth_stats 为 `1` 时已授权
         */
        public const val AUTH_STATUS_AUTHORIZED: Int = 1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ApiPermission) return false

        if (path != other.path) return false
        if (method != other.method) return false
        if (desc != other.desc) return false
        if (authStatus != other.authStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + method.hashCode()
        result = 31 * result + desc.hashCode()
        result = 31 * result + authStatus
        return result
    }

    override fun toString(): String {
        return "ApiPermission(path='$path', method='$method', desc='$desc', authStatus=$authStatus)"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("path"))
    public operator fun component1(): String = path

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("method"))
    public operator fun component2(): String = method

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("desc"))
    public operator fun component3(): String = desc

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("authStatus"))
    public operator fun component4(): Int = authStatus

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        path: String = this.path,
        method: String = this.method,
        desc: String = this.desc,
        authStatus: Int = this.authStatus,
    ): ApiPermission = ApiPermission(path, method, desc, authStatus)
    //endregion
}

/**
 * 判断 [ApiPermission.authStatus] 的值是否为 [`1`][ApiPermission.AUTH_STATUS_AUTHORIZED]
 */
public inline val ApiPermission.isAuthorized: Boolean
    get() = authStatus == ApiPermission.AUTH_STATUS_AUTHORIZED


/**
 * [接口权限需求对象（APIPermissionDemand）](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/api_permissions/model.html#%E6%8E%A5%E5%8F%A3%E6%9D%83%E9%99%90%E9%9C%80%E6%B1%82%E5%AF%B9%E8%B1%A1-apipermissiondemand)
 *
 * @property guildId 申请接口权限的频道 id
 * @property channelId 接口权限需求授权链接发送的子频道 id
 * @property apiIdentify 权限接口唯一标识
 * @property title 接口权限链接中的接口权限描述信息
 * @property desc 接口权限链接中的机器人可使用功能的描述信息
 */
@ApiModel
@Serializable
public class ApiPermissionDemand @ApiModelConstructor constructor(
    @SerialName("guild_id") public val guildId: String,
    @SerialName("channel_id") public val channelId: String,
    @SerialName("api_identify") public val apiIdentify: ApiPermissionDemandIdentify,
    public val title: String,
    public val desc: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ApiPermissionDemand) return false

        if (guildId != other.guildId) return false
        if (channelId != other.channelId) return false
        if (apiIdentify != other.apiIdentify) return false
        if (title != other.title) return false
        if (desc != other.desc) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guildId.hashCode()
        result = 31 * result + channelId.hashCode()
        result = 31 * result + apiIdentify.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + desc.hashCode()
        return result
    }

    override fun toString(): String {
        return "ApiPermissionDemand(" +
            "guildId='$guildId', " +
            "channelId='$channelId', " +
            "apiIdentify=$apiIdentify, " +
            "title='$title', " +
            "desc='$desc')"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildId"))
    public operator fun component1(): String = guildId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelId"))
    public operator fun component2(): String = channelId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("apiIdentify"))
    public operator fun component3(): ApiPermissionDemandIdentify = apiIdentify

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("title"))
    public operator fun component4(): String = title

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("desc"))
    public operator fun component5(): String = desc

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        guildId: String = this.guildId,
        channelId: String = this.channelId,
        apiIdentify: ApiPermissionDemandIdentify = this.apiIdentify,
        title: String = this.title,
        desc: String = this.desc,
    ): ApiPermissionDemand = ApiPermissionDemand(guildId, channelId, apiIdentify, title, desc)
    //endregion
}

/**
 * [接口权限需求标识对象（APIPermissionDemandIdentify）](https://bot.q.qq.com/wiki/develop/api/openapi/api_permissions/model.html#%E6%8E%A5%E5%8F%A3%E6%9D%83%E9%99%90%E9%9C%80%E6%B1%82%E6%A0%87%E8%AF%86%E5%AF%B9%E8%B1%A1-apipermissiondemandidentify)
 *
 * @property path API 接口名，例如 `/guilds/{guild_id}/members/{user_id}`
 * @property method 请求方法，例如 `GET`
 */
@Serializable
public class ApiPermissionDemandIdentify @ApiModelConstructor constructor(
    public val path: String,
    public val method: String,
) {
    public companion object {
        /**
         * 构建 [ApiPermissionDemandIdentify]。
         *
         * @since 5.0
         */
        @JvmStatic
        public fun of(path: String, method: String): ApiPermissionDemandIdentify =
            ApiPermissionDemandIdentify(path, method)

        /**
         * 通过 [ApiPermission] 构建一个 [ApiPermissionDemandIdentify].
         */
        @JvmStatic
        @JvmName("of")
        public fun ApiDescription.toIdentify(): ApiPermissionDemandIdentify =
            ApiPermissionDemandIdentify(path, method)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ApiPermissionDemandIdentify) return false

        if (path != other.path) return false
        if (method != other.method) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + method.hashCode()
        return result
    }

    override fun toString(): String {
        return "ApiPermissionDemandIdentify(path='$path', method='$method')"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("path"))
    public operator fun component1(): String = path

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("method"))
    public operator fun component2(): String = method

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        path: String = this.path,
        method: String = this.method,
    ): ApiPermissionDemandIdentify = ApiPermissionDemandIdentify(path, method)
    //endregion
}
