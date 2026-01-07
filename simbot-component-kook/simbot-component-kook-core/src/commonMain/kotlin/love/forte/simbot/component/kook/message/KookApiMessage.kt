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

package love.forte.simbot.component.kook.message

import love.forte.simbot.kook.api.KookApi
import love.forte.simbot.kook.api.message.SendChannelMessageApi
import kotlin.jvm.JvmStatic


/**
 * 提供一个 [KookApi] 作为原始的消息发送请求（例如 [SendChannelMessageApi]）。
 *
 * 此消息会在发送时直接通过 [api] 发起一个请求，但是不会处理它的响应。
 *
 * 默认情况下此 API 请求过程中产生的异常会直接抛出。这可能会影响并中断消息发送的流程。
 *
 * 这是一个**仅用于发送**的消息，且**不支持**序列化。
 *
 * @see KookApi
 *
 * @author ForteScarlet
 */
@KookSendOnlyMessage
public data class KookApiMessage(public val api: KookApi<*>) : KookMessageElement {

    public companion object {
        /**
         * 通过 [KookApi] 构建 [KookApiMessage].
         */
        @JvmStatic
        public fun KookApi<*>.toRequest(): KookApiMessage =
            KookApiMessage(this)
    }
}

