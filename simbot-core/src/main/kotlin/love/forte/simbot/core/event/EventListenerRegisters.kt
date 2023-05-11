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

package love.forte.simbot.core.event

import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.InternalSimbotApi
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListenerHandle
import love.forte.simbot.event.EventListenerRegistrar

/**
 * 向 [EventListenerRegistrar] 中注册多个监听函数。
 */
@OptIn(InternalSimbotApi::class, ExperimentalSimbotApi::class)
public inline fun EventListenerRegistrar.listeners(block: EventListenerRegistrationDescriptionsGenerator.() -> Unit) {
    val listeners = EventListenerRegistrationDescriptionsGenerator().also(block).build()
    listeners.forEach { register(it) }
}

/**
 * 向 [EventListenerRegistrar] 中注册一个监听函数。
 */
@OptIn(ExperimentalSimbotApi::class)
public inline fun <E : Event> EventListenerRegistrar.listen(
    key: Event.Key<E>,
    block: SimpleListenerRegistrationDescriptionBuilder<E>.() -> Unit,
): EventListenerHandle {
    val listener = SimpleListenerRegistrationDescriptionBuilder(key).also(block).build()
    return register(listener)
}
