/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmStatic


/**
 *
 * [删除频道](https://developer.kookapp.cn/doc/http/channel#%E5%88%A0%E9%99%A4%E9%A2%91%E9%81%93)
 *
 *
 * @author ForteScarlet
 */
public class DeleteChannelApi private constructor(private val channelId: String) : KookPostApi<Unit>() {
    public companion object Factory {
        private val PATH = ApiPath.create("channel", "delete")

        /**
         * 构建 [DeleteChannelApi].
         * @param channelId 要删除的频道的ID
         */
        @JvmStatic
        public fun create(channelId: String): DeleteChannelApi =
            DeleteChannelApi(channelId)

    }

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val apiPath: ApiPath
        get() = PATH


    override fun createBody(): Any = Body(channelId)

    @Serializable
    private data class Body(
        @SerialName("channel_id")
        val channelId: String
    )

}
