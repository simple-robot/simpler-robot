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

package love.forte.simbot.component.onebot.v11.core.message

import love.forte.simbot.ability.ReplyOption
import love.forte.simbot.common.id.ID

/**
 * OneBot 回复消息的协议选项。
 *
 * @since 5.0
 */
public sealed class OneBotMessageReplyOption : ReplyOption {
    /**
     * 引用消息的 ID。同一次操作中至多指定一次。
     */
    public class Quote internal constructor(public val id: ID) : OneBotMessageReplyOption()

    companion {
        /**
         * 引用指定消息。
         */
        public fun quote(id: ID): Quote = Quote(id)
    }
}

internal fun List<ReplyOption>.oneBotReplyQuote(): ID? {
    val quotes = filterIsInstance<OneBotMessageReplyOption.Quote>()
    require(quotes.size <= 1) { "Duplicate OneBot reply option: Quote" }
    return quotes.singleOrNull()?.id
}
