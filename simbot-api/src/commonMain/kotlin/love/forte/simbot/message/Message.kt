/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.message

import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic


/**
 *
 * 一个 **消息**。
 *
 * @see Message.Element
 * @see Messages
 *
 * @author ForteScarlet
 */
public sealed interface Message {

    /**
     * 一个 **消息元素**，是消息链中的最小单位。
     * 消息元素本身也是消息。
     */
    public interface Element : Message
}

/**
 * Configure polymorphic for [Message.Element].
 */
public inline fun SerializersModuleBuilder.messageElementPolymorphic(
    block: PolymorphicModuleBuilder<Message.Element>.() -> Unit
) {
    polymorphic(Message.Element::class) {
        block(this)
    }
}
