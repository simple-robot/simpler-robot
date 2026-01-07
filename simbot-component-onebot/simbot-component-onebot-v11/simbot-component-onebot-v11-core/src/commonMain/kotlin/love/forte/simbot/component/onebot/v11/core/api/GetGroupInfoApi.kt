/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.api

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [`get_group_info`-获取群信息](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_group_info-获取群信息)
 *
 * @author ForteScarlet
 */
public class GetGroupInfoApi private constructor(
    override val body: Any,
) : OneBotApi<GetGroupInfoResult> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<GetGroupInfoResult>
        get() = GetGroupInfoResult.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<GetGroupInfoResult>>
        get() = RES_SER

    public companion object Factory {
        private const val ACTION: String = "get_group_info"

        private val RES_SER: KSerializer<OneBotApiResult<GetGroupInfoResult>> =
            OneBotApiResult.serializer(GetGroupInfoResult.serializer())

        /**
         * 构建一个 [GetGroupInfoApi].
         *
         * @param groupId 群号
         * @param noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
         */
        @JvmStatic
        @JvmOverloads
        public fun create(groupId: ID, noCache: Boolean? = null): GetGroupInfoApi =
            GetGroupInfoApi(Body(groupId, noCache))
    }

    /**
     * @property groupId 群号
     * @property noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     */
    @Serializable
    internal data class Body(
        @SerialName("group_id")
        internal val groupId: ID,
        @SerialName("no_cache")
        internal val noCache: Boolean? = null,
    )
}

/**
 * [GetGroupInfoApi] 的响应体。
 *
 * @property groupId 群号
 * @property groupName 群名称
 * @property memberCount 成员数
 * @property maxMemberCount 最大成员数（群容量）
 */
@Serializable
public data class GetGroupInfoResult @ApiResultConstructor constructor(
    @SerialName("group_id")
    public val groupId: LongID,
    @SerialName("group_name")
    public val groupName: String,
    @SerialName("member_count")
    public val memberCount: Int,
    @SerialName("max_member_count")
    public val maxMemberCount: Int,
)
