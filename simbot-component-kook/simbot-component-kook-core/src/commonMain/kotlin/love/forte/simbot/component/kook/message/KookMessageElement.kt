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

import love.forte.simbot.message.Message

/**
 * 此注解标记一个 [KookMessageElement] 的实现类型，用于标记其为一个**仅用于发送**的消息。
 *
 * 仅用于发送的消息通常情况下只能由程序构建并发送，不会在事件中收到此消息，且大概率**不支持**序列化。
 *
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
public annotation class KookSendOnlyMessage


/**
 * Kook 组件中对 [Message.Element] 消息实现的根类型。
 *
 * ## SendOnlyMessage
 * 对于一些**仅用于发送**的消息，它们会被标记上 [KookSendOnlyMessage] 注解，并大概率无法支持序列化。
 *
 * @see KookAssetMessage
 * @see KookAtAllHere
 * @see KookCardMessage
 * @see KookKMarkdownMessage
 *
 * @author ForteScarlet
 */
public interface KookMessageElement : Message.Element
