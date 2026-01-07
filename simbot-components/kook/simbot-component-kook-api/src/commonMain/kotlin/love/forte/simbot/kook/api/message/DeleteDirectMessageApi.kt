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

package love.forte.simbot.kook.api.message

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * [删除私信聊天消息](https://developer.kookapp.cn/doc/http/direct-message#%E5%88%A0%E9%99%A4%E7%A7%81%E4%BF%A1%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF)
 *
 * > 只能删除自己的消息。
 *
 * @author ForteScarlet
 */
public class DeleteDirectMessageApi private constructor(
    private val msgId: String? = null,
) : KookPostApi<Unit>() {
    public companion object Factory {
        private val PATH = ApiPath.create("message", "delete")

        /**
         * 构建 [DeleteDirectMessageApi]
         *
         * @param msgId 消息 id
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(msgId: String? = null): DeleteDirectMessageApi = DeleteDirectMessageApi(msgId)

    }

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override fun createBody(): Any = Body(msgId)

    @Serializable
    private data class Body(@SerialName("msg_id") val msgId: String?)
}
