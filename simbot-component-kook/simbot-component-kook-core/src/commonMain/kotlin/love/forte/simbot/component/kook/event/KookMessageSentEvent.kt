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

package love.forte.simbot.component.kook.event

import love.forte.simbot.component.kook.message.KookMessageReceipt
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.event.MessageSentEvent


/**
 * Kook 消息通过 API 发生真实的消息发送后推送的通知事件。
 *
 * @author ForteScarlet
 * @since 5.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class KookMessageSentEvent : KookEvent(), MessageSentEvent {
    abstract override val receipt: KookMessageReceipt
}
