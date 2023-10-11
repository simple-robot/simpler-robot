/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

@file:JvmName("Collectables")
package love.forte.simbot.collection

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlin.jvm.JvmName

/**
 * 得到一个没有元素的 [Collectable]。
 */
public fun <T> emptyCollectable(): Collectable<T> = EmptyCollectable

private data object EmptyCollectable : Collectable<Nothing> {
    override fun asFlow(): Flow<Nothing> = emptyFlow()
    override fun asSequence(): Sequence<Nothing> = emptySequence()
}

/**
 * 将一个 [Flow] 转化为 [Collectable]。
 *
 */
public fun <T> Flow<T>.asCollectable(): Collectable<T> = FlowCollectable(this)

// TODO Expect

private class FlowCollectable<T>(private val flow: Flow<T>) : Collectable<T> {
    override fun asFlow(): Flow<T> = flow
    override fun asSequence(): Sequence<T> {
        TODO("Not yet implemented")
    }
}

// TODO

public fun <T> Iterable<T>.asCollectable(): Collectable<T> = IterableCollectable(this)


private class IterableCollectable<T>(private val collection: Iterable<T>) : Collectable<T> {
    override fun asFlow(): Flow<T> = collection.asFlow()
    override fun asSequence(): Sequence<T> = collection.asSequence()
}

// TODO

public fun <T> Sequence<T>.asCollectable(): Collectable<T> = SequenceCollectable(this)


private class SequenceCollectable<T>(private val collection: Sequence<T>) : Collectable<T> {
    override fun asFlow(): Flow<T> = collection.asFlow()
    override fun asSequence(): Sequence<T> = collection
}
