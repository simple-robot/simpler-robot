/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.message


public fun <E : Message.Element<E>> Iterable<Message.Element<*>>.filter(key: Message.Key<E>): Sequence<E> {
    return asSequence().filter(key)
}


public fun <E : Message.Element<E>> Sequence<Message.Element<*>>.filter(key: Message.Key<E>): Sequence<E> {
    return mapNotNull { key.safeCast(it) }
}

