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

package love.forte.simbot.qguild.api.apipermission

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.ApiDescription
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.model.ApiPermissionDemand
import love.forte.simbot.qguild.model.ApiPermissionDemandIdentify
import love.forte.simbot.qguild.model.ApiPermissionDemandIdentify.Companion.toIdentify
import kotlin.jvm.JvmStatic

/**
 *
 * [创建频道 API 接口权限授权链接](https://bot.q.qq.com/wiki/develop/api/openapi/api_permissions/post_api_permission_demand.html)
 *
 * 用于创建 API 接口权限授权链接，该链接指向 `guild_id` 对应的频道 。
 *
 * 需要注意，私信场景中，当需要查询私信来源频道的权限时，应使用 `src_guild_id` ，即 [message](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#message)
 * 中的 `src_guild_id`
 *
 * 每天只能在一个频道内发 `3` 条（默认值）频道权限授权链接。
 *
 * @author ForteScarlet
 */
public class DemandApiPermissionApi private constructor(
    guildId: String, override val body: Body
) : PostQQGuildApi<ApiPermissionDemand>() {

    public companion object Factory : SimplePostApiDescription(
        "/guilds/{guild_id}/api_permission/demand"
    ) {

        /**
         * 构造 [DemandApiPermissionApi].
         */
        @JvmStatic
        public fun create(guildId: String, body: Body): DemandApiPermissionApi = DemandApiPermissionApi(guildId, body)

        /**
         * 构造 [DemandApiPermissionApi].
         */
        @JvmStatic
        public fun create(
            guildId: String,
            channelId: String,
            apiIdentify: ApiPermissionDemandIdentify,
            desc: String,
        ): DemandApiPermissionApi = DemandApiPermissionApi(guildId, Body.of(channelId, apiIdentify, desc))

        /**
         * 构造 [DemandApiPermissionApi].
         */
        @JvmStatic
        public fun create(
            guildId: String,
            channelId: String,
            apiDescription: ApiDescription,
            desc: String,
        ): DemandApiPermissionApi =
            DemandApiPermissionApi(guildId, Body(channelId, apiDescription.toIdentify(), desc))

    }

    override val path: Array<String> = arrayOf("guilds", guildId, "api_permission", "demand")

    override fun createBody(): Any? = null

    override val resultDeserializationStrategy: DeserializationStrategy<ApiPermissionDemand>
        get() = ApiPermissionDemand.serializer()


    /**
     * 用于在 [DemandApiPermissionApi] 中进行请求的
     *
     * @property channelId 授权链接发送的子频道 id
     * @property apiIdentify api 权限需求标识对象
     * @property desc 机器人申请对应的 API 接口权限后可以使用功能的描述
     */
    @Serializable
    public class Body @ApiModelConstructor public constructor(
        @SerialName("channel_id") public val channelId: String,
        @SerialName("api_identify") public val apiIdentify: ApiPermissionDemandIdentify,
        public val desc: String,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Body) return false
            if (channelId != other.channelId) return false
            if (apiIdentify != other.apiIdentify) return false
            if (desc != other.desc) return false
            return true
        }

        override fun hashCode(): Int {
            var result = channelId.hashCode()
            result = 31 * result + apiIdentify.hashCode()
            result = 31 * result + desc.hashCode()
            return result
        }

        override fun toString(): String = "Body(channelId=$channelId, apiIdentify=$apiIdentify, desc=$desc)"

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelId"))
        public operator fun component1(): String = channelId

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("apiIdentify"))
        public operator fun component2(): ApiPermissionDemandIdentify = apiIdentify

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("desc"))
        public operator fun component3(): String = desc

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            channelId: String = this.channelId,
            apiIdentify: ApiPermissionDemandIdentify = this.apiIdentify,
            desc: String = this.desc,
        ): Body = Body(channelId, apiIdentify, desc)

        public companion object {
            /**
             * 构造一个 [Body]。
             *
             * @since 5.0
             */
            @JvmStatic
            public fun of(
                channelId: String,
                apiIdentify: ApiPermissionDemandIdentify,
                desc: String,
            ): Body = Body(
                channelId = channelId,
                apiIdentify = apiIdentify,
                desc = desc
            )
        }
    }
}
