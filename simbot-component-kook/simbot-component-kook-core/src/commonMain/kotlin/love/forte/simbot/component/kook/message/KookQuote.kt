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

package love.forte.simbot.component.kook.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.kook.objects.Quote
import love.forte.simbot.message.MessageIdReference
import love.forte.simbot.message.MessageReference
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic


/**
 * 一个通过 [KookMessageContent.reference]
 * 查询得到的消息引用信息。
 *
 * 也可用于发送，效果等同于使用 [MessageIdReference]。
 *
 * @author ForteScarlet
 */
@SerialName("kook.quote")
@Serializable
@ConsistentCopyVisibility
public data class KookQuote internal constructor(public val quote: Quote) :
    MessageReference {
    override val id: ID
        get() = quote.id.ID

    public companion object {
        /**
         * 将 [Quote] 作为 [KookQuote]。
         */
        @JvmStatic
        @JvmName("create")
        public fun Quote.asMessage(): KookQuote = KookQuote(this)
    }

}
