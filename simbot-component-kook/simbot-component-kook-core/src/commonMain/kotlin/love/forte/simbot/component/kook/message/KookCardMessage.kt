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
import love.forte.simbot.component.kook.message.KookCardMessage.Companion.asMessage
import love.forte.simbot.kook.objects.card.Card
import love.forte.simbot.kook.objects.card.CardMessageBuilder
import love.forte.simbot.kook.objects.card.buildCardMessage
import kotlin.jvm.JvmStatic
import love.forte.simbot.kook.objects.card.CardMessage as KkCardMessage


/**
 * 将 [Card] 作为消息使用。
 * @author ForteScarlet
 */
@ExperimentalSimbotAPI
@SerialName("kook.card")
@Serializable
public data class KookCardMessage(public val cards: KkCardMessage) : KookMessageElement {

    public companion object {
        /**
         * 将 [Card] 作为 [KookCardMessage] 使用。
         */
        @JvmStatic
        @ExperimentalSimbotAPI
        public fun KkCardMessage.asMessage(): KookCardMessage = KookCardMessage(this)
    }
}

/**
 * 通过 [buildCardMessage] 构建 [CardMessage][KkCardMessage] 并包装为 [KookCardMessage]。
 *
 * @see KookCardMessage
 * @see buildCardMessage
 */
@ExperimentalSimbotAPI
public inline fun kookCard(action: CardMessageBuilder.() -> Unit): KookCardMessage {
    return buildCardMessage(action).asMessage()
}
