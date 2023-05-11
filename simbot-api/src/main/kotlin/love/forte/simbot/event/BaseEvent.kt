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

package love.forte.simbot.event

/**
 * 此注解标记一个 [Event] 类型并将其标记为一个 **基础事件类型**。
 *
 * ## 基础事件
 * 属于 **基础事件** 的所有事件类型, 它们的主要作用是为其他子事件类型提供语义、类型或属性支撑，
 * 而其本身没有很强的可监听性。
 *
 * 同时一般被标记为 [BaseEvent] 的事件可能会存在多个子事件类型，因此他们涉及的事件范围会很大。
 *
 * 因此被标记为 [BaseEvent] 的事件类型通常**不建议**直接进行监听，而是应该选择它们之下语义更加明确的事件类型。
 *
 *
 * 此注解目前仅做标记使用。
 *
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
public annotation class BaseEvent
