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
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [`set_friend_add_request`-处理加好友请求](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_friend_add_request-处理加好友请求)
 *
 * @author ForteScarlet
 */
public class SetFriendAddRequestApi private constructor(
    override val body: Any,
) : OneBotApi<Unit> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<Unit>>
        get() = OneBotApiResult.emptySerializer()

    public companion object Factory {
        private const val ACTION: String = "set_friend_add_request"

        /**
         * 构建一个 [SetFriendAddRequestApi].
         *
         * @param flag 加好友请求的 flag（需从上报的数据中获得）
         * @param approve 是否同意请求
         * @param remark 添加后的好友备注（仅在同意时有效）
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            flag: String,
            approve: Boolean? = null,
            remark: String? = null,
        ): SetFriendAddRequestApi = SetFriendAddRequestApi(Body(flag, approve, remark))
    }

    /**
     * @property flag 加好友请求的 flag（需从上报的数据中获得）
     * @property approve 是否同意请求
     * @property remark 添加后的好友备注（仅在同意时有效）
     */
    @Serializable
    internal data class Body(
        internal val flag: String,
        internal val approve: Boolean? = null,
        internal val remark: String? = null,
    )
}
