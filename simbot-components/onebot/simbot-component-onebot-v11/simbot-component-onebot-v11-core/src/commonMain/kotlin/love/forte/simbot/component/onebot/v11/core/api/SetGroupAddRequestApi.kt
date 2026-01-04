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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [`set_group_add_request`-处理加群请求／邀请](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_group_add_request-处理加群请求邀请)
 *
 * @author ForteScarlet
 */
public class SetGroupAddRequestApi private constructor(
    override val body: Any,
) : OneBotApi<Unit> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<Unit>>
        get() = OneBotApiResult.emptySerializer()

    public companion object Factory {
        private const val ACTION: String = "set_group_add_request"

        /**
         * 构建一个 [SetGroupAddRequestApi].
         *
         * @param flag 加群请求的 flag（需从上报的数据中获得）
         * @param subType `add` 或 `invite`，请求类型（需要和上报消息中的 `sub_type` 字段相符）
         * @param approve 是否同意请求／邀请
         * @param reason 拒绝理由（仅在拒绝时有效）
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            flag: String,
            subType: String,
            approve: Boolean? = null,
            reason: String? = null,
        ): SetGroupAddRequestApi = SetGroupAddRequestApi(Body(flag, subType, approve, reason))
    }

    /**
     * @property flag 加群请求的 flag（需从上报的数据中获得）
     * @property subType `add` 或 `invite`，请求类型（需要和上报消息中的 `sub_type` 字段相符）
     * @property approve 是否同意请求／邀请
     * @property reason 拒绝理由（仅在拒绝时有效）
     */
    @Serializable
    internal data class Body(
        internal val flag: String,
        @SerialName("sub_type")
        internal val subType: String,
        internal val approve: Boolean? = null,
        internal val reason: String? = null,
    )
}
