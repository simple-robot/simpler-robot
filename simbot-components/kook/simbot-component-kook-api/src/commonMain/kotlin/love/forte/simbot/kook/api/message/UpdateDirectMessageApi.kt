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
 * [更新私信聊天消息](https://developer.kookapp.cn/doc/http/direct-message#%E6%9B%B4%E6%96%B0%E7%A7%81%E4%BF%A1%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF)
 *
 * > 目前支持消息 type为 `9`、`10` 的修改，即 `KMarkdown` 和 `CardMessage`
 *
 * @author ForteScarlet
 */
public class UpdateDirectMessageApi private constructor(
    private val msgId: String? = null,
    private val content: String,
    private val quote: String? = null,
) : KookPostApi<Unit>() {
    public companion object Factory {
        private val PATH = ApiPath.create("direct-message", "update")

        /**
         * 构建 [UpdateDirectMessageApi].
         *
         * @param msgId 消息 id
         * @param content 消息内容
         * @param quote 回复某条消息的msgId。如果为空，则代表删除回复，不传则无影响。
         */
        @JvmStatic
        @JvmOverloads
        public fun create(msgId: String? = null, content: String, quote: String? = null): UpdateDirectMessageApi =
            UpdateDirectMessageApi(msgId, content, quote)

    }

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override fun createBody(): Any = Body(msgId, content, quote)

    @Serializable
    private data class Body(
        @SerialName("msg_id") private val msgId: String? = null,
        private val content: String,
        private val quote: String? = null,
    )
}
