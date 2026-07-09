/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.qguild.api.interaction

import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PutQQGuildApi
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.SimplePutApiDescription
import org.jetbrains.annotations.Range
import kotlin.jvm.JvmStatic

/**
 * [回应互动事件](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/msg-btn.html#点击回调按钮)
 *
 * @author ForteScarlet
 * @since 4.4.0
 */
public class InteractionResponseApi private constructor(
    interactionId: String,
    @Suppress("ConstructorParameterNaming")
    private val _body: InteractionResponseBody,
) : PutQQGuildApi<Unit>(), QQGuildApiWithoutResult {
    public companion object Factory : SimplePutApiDescription(
        "/interactions/{interaction_id}"
    ) {
        public const val CODE_SUCCESS: Int = 0
        public const val CODE_FAILED: Int = 1
        public const val CODE_FREQUENT: Int = 2
        public const val CODE_DUPLICATE: Int = 3
        public const val CODE_NO_PERMISSION: Int = 4
        public const val CODE_ADMIN_ONLY: Int = 5

        /**
         * Create an [InteractionResponseApi].
         */
        @JvmStatic
        public fun create(interactionId: String, body: InteractionResponseBody): InteractionResponseApi =
            InteractionResponseApi(interactionId, body)

        /**
         * Create an [InteractionResponseApi].
         *
         * @param code `0` 成功, `1` 操作失败, `2` 操作频繁, `3` 重复操作, `4` 没有权限, `5` 仅管理员操作
         */
        @JvmStatic
        public fun create(interactionId: String, code: Int): InteractionResponseApi =
            create(interactionId, InteractionResponseBody(code))
    }

    override val path: Array<String> = arrayOf("interactions", interactionId)

    override fun createBody(): Any = _body
}

/**
 * The body of [InteractionResponseApi].
 *
 * @property code `0` 成功, `1` 操作失败, `2` 操作频繁, `3` 重复操作, `4` 没有权限, `5` 仅管理员操作
 *
 * @since 4.4.0
 */
@Serializable
public data class InteractionResponseBody(val code: Int)
