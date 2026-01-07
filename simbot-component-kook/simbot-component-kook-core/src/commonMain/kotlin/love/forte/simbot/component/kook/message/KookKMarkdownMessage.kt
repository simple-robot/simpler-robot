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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.component.kook.message.KookKMarkdownMessage.Companion.asMessage
import love.forte.simbot.kook.ExperimentalKookApi
import love.forte.simbot.kook.objects.kmd.KMarkdown
import love.forte.simbot.kook.objects.kmd.KMarkdownBuilder
import love.forte.simbot.kook.objects.kmd.buildKMarkdown
import kotlin.jvm.JvmStatic


/**
 * 将 [KMarkdown] 作为消息使用。
 * @author ForteScarlet
 */
@OptIn(ExperimentalKookApi::class)
@SerialName("kook.kmd")
@Serializable
@ExperimentalSimbotAPI
public data class KookKMarkdownMessage(public val kMarkdown: KMarkdown) : KookMessageElement {

    public companion object {

        /**
         * 将 [KMarkdown] 作为 [KookKMarkdownMessage] 使用。
         */
        @JvmStatic
        public fun KMarkdown.asMessage(): KookKMarkdownMessage = KookKMarkdownMessage(this)
    }
}

/**
 * 通过 [buildKMarkdown] 构建 [KMarkdown] 并包装为 [KookKMarkdownMessage]。
 *
 * @see buildKMarkdown
 * @see KookKMarkdownMessage
 */
@OptIn(ExperimentalKookApi::class)
@ExperimentalSimbotAPI
public inline fun kookKMarkdown(block: KMarkdownBuilder.() -> Unit): KookKMarkdownMessage {
    return buildKMarkdown(block).asMessage()
}
