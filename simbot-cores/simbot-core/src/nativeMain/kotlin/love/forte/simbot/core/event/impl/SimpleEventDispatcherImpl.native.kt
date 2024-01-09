/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.core.event.impl

import love.forte.simbot.common.collection.PriorityConcurrentQueue
import love.forte.simbot.event.EventListenerRegistrationHandle
import kotlin.concurrent.Volatile
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.WeakReference

internal actual fun <T : Any> createQueueRegistrationHandle(
    priority: Int,
    queue: PriorityConcurrentQueue<T>,
    target: T
): EventListenerRegistrationHandle =
    WeakEventListenerRegistrationHandle(priority, queue, target)


@OptIn(ExperimentalNativeApi::class)
private class WeakEventListenerRegistrationHandle<T : Any>(
    private val priority: Int,
    queue: PriorityConcurrentQueue<T>,
    target: T
) : EventListenerRegistrationHandle {
    @Volatile
    private var queueRef: WeakReference<PriorityConcurrentQueue<T>>? = WeakReference(queue)

    @Volatile
    private var targetRef: WeakReference<T>? = WeakReference(target)

    override fun dispose() {
        val qr = queueRef
        val tr = targetRef
        if (qr == null || tr == null) {
            return
        }

        val queue = qr.value
        val target = tr.value

        if (queue != null && target != null) {
            queue.remove(priority, target)
        }

        // clear all
        queueRef = null
        targetRef = null
    }

}
