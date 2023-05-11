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

package love.forte.simbot.utils.item

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import love.forte.simbot.Api4J
import java.util.stream.Stream
import kotlin.streams.asStream


/**
 * 使用 [Sequence] 实现 [Items] 的基本约定。
 *
 * @author ForteScarlet
 */
public class SequenceItems<T>(private val sequenceFactory: (Items.PreprocessingProperties) -> Sequence<T>) : BaseItems<T, SequenceItems<T>>() {
    override val self: SequenceItems<T>
        get() = this
    
    private val sequence: Sequence<T> get() = sequenceFactory(preprocessingProperties)
    
    override suspend fun collect(collector: suspend (T) -> Unit) {
        val sequence = sequence
        sequence.forEach { collector(it) }
    }
    
    override fun asFlow(): Flow<T> {
        return sequence.asFlow()
    }
    
    override fun asSequence(): Sequence<T> {
        return sequence
    }
    
    @Api4J
    override fun asStream(): Stream<out T> {
        return sequence.asStream()
    }
}
