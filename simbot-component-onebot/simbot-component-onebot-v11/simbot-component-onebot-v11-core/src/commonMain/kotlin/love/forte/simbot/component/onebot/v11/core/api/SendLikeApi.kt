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
import love.forte.simbot.common.id.ID
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [`send_like`-发送好友赞](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#send_like-发送好友赞)
 *
 * @author ForteScarlet
 */
public class SendLikeApi private constructor(
    override val body: Any,
) : OneBotApi<Unit> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<Unit>>
        get() = OneBotApiResult.emptySerializer()

    public companion object Factory {
        private const val ACTION: String = "send_like"

        /**
         * 构建一个 [SendLikeApi].
         *
         * @param userId 对方 QQ 号
         * @param times 赞的次数，每个好友每天最多 10 次
         */
        @JvmStatic
        @JvmOverloads
        public fun create(userId: ID, times: Int? = null): SendLikeApi = SendLikeApi(
            Body(
                userId,
                times
            )
        )
    }

    /**
     * @property userId 对方 QQ 号
     * @property times 赞的次数，每个好友每天最多 10 次
     */
    @Serializable
    internal data class Body(
        @SerialName("user_id")
        internal val userId: ID,
        internal val times: Int? = null,
    )
}
