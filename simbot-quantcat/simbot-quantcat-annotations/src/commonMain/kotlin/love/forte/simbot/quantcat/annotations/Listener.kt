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

package love.forte.simbot.quantcat.annotations

import love.forte.simbot.common.PriorityConstant

/**
 * 标记一个函数为监听函数/事件处理器。
 *
 * 被 [Listener] 标记的函数在进行处理的时候会根据此函数的参数尝试自动分析其监听目标。
 * 一个事件处理器建议只有**一个** [Event][love.forte.simbot.event.Event] 类型的参数。
 *
 * ```kotlin
 * @Listener
 * suspend fun listenFoo(event: FooEvent) {
 *     // 此时监听的类型就是 FooEvent
 * }
 * ```
 *
 * 注：在 Kotlin 中，被标记的函数最好是可挂起函数（标记 `suspend`）。
 *
 *
 * @property id 此事件处理器的id。通常用于日志输出或调试用。默认会根据函数生成一个ID。
 * @property priority 此事件处理器的优先级
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
public annotation class Listener(
    val id: String = "",
    val priority: Int = PriorityConstant.DEFAULT,
)

