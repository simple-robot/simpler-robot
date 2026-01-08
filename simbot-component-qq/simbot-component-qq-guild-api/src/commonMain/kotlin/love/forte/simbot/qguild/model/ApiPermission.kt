/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.api.ApiDescription
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * [接口权限对象](https://bot.q.qq.com/wiki/develop/api/openapi/api_permissions/model.html)
 *
 */
@ApiModel
@Serializable
public data class ApiPermission(
    /**
     * API 接口名，例如 `/guilds/{guild_id}/members/{user_id}`
     */
    val path: String,
    /**
     * 请求方法，例如 `GET`
     */
    val method: String,
    /**
     * API 接口名称，例如 `获取频道信`
     */
    val desc: String,
    /**
     * 授权状态，`auth_stats` 为 1 时已授权
     */
    @SerialName("auth_status")
    val authStatus: Int
) {
    public companion object {
        /**
         * 授权状态，auth_stats 为 `1` 时已授权
         */
        public const val AUTH_STATUS_AUTHORIZED: Int = 1
    }
}

/**
 * 判断 [ApiPermission.authStatus] 的值是否为 [`1`][ApiPermission.AUTH_STATUS_AUTHORIZED]
 */
public inline val ApiPermission.isAuthorized: Boolean
    get() = authStatus == 1


/**
 * [接口权限需求对象（APIPermissionDemand）](https://bot.q.qq.com/wiki/develop/api/openapi/api_permissions/model.html#%E6%8E%A5%E5%8F%A3%E6%9D%83%E9%99%90%E9%9C%80%E6%B1%82%E5%AF%B9%E8%B1%A1-apipermissiondemand)
 */
@ApiModel
@Serializable
public data class ApiPermissionDemand(
    /**
     * 申请接口权限的频道 id
     */
    @SerialName("guild_id") val guildId: String,
    /**
     * 接口权限需求授权链接发送的子频道 id
     */
    @SerialName("channel_id") val channelId: String,
    /**
     * 权限接口唯一标识
     */
    @SerialName("api_identify") val apiIdentify: ApiPermissionDemandIdentify,

    /**
     * 接口权限链接中的接口权限描述信息
     */
    val title: String,
    /**
     * 接口权限链接中的机器人可使用功能的描述信息
     */
    val desc: String,
)

/**
 * [接口权限需求标识对象（APIPermissionDemandIdentify）](https://bot.q.qq.com/wiki/develop/api/openapi/api_permissions/model.html#%E6%8E%A5%E5%8F%A3%E6%9D%83%E9%99%90%E9%9C%80%E6%B1%82%E6%A0%87%E8%AF%86%E5%AF%B9%E8%B1%A1-apipermissiondemandidentify)
 */
@Serializable
public data class ApiPermissionDemandIdentify(
    /**
     * API 接口名，例如 `/guilds/{guild_id}/members/{user_id}`
     */
    val path: String,

    /**
     * 请求方法，例如 `GET`
     */
    val method: String,
) {
    public companion object {
        /**
         * 通过 [ApiPermission] 构建一个 [ApiPermissionDemandIdentify].
         */
        @JvmStatic
        @JvmName("of")
        public fun ApiDescription.toIdentify(): ApiPermissionDemandIdentify =
            ApiPermissionDemandIdentify(path, method)
    }
}
