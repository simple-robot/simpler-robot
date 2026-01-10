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
 * [`set_group_leave`-退出群组](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_group_leave-退出群组)
 *
 * @author ForteScarlet
 */
public class SetGroupLeaveApi private constructor(
    override val body: Any,
) : OneBotApi<Unit> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<Unit>>
        get() = OneBotApiResult.emptySerializer()

    public companion object Factory {
        private const val ACTION: String = "set_group_leave"

        /**
         * 构建一个 [SetGroupLeaveApi].
         *
         * @param groupId 群号
         * @param isDismiss 是否解散，如果登录号是群主，则仅在此项为 true 时能够解散
         */
        @JvmStatic
        @JvmOverloads
        public fun create(groupId: ID, isDismiss: Boolean? = null): SetGroupLeaveApi =
            SetGroupLeaveApi(Body(groupId, isDismiss))
    }

    /**
     * @property groupId 群号
     * @property isDismiss 是否解散，如果登录号是群主，则仅在此项为 true 时能够解散
     */
    @Serializable
    internal data class Body(
        @SerialName("group_id")
        internal val groupId: ID,
        @SerialName("is_dismiss")
        internal val isDismiss: Boolean? = null,
    )
}
