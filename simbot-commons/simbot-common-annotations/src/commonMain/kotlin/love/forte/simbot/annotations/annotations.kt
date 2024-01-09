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

package love.forte.simbot.annotations

/**
 * 一个尚在试验阶段的API。试验阶段的API可能存在漏洞、缺陷，或实现不稳定，
 * 且有可能在未来被修改、删除，且没有兼容性保证。
 */
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    message = "一个尚在试验阶段的API。试验阶段的API可能存在漏洞、缺陷，或实现不稳定，且有可能在未来被修改、删除，且没有兼容性保证。",
    level = RequiresOptIn.Level.WARNING
)
@MustBeDocumented
public annotation class ExperimentalSimbotAPI

/**
 * 一个仅供 simbot 内部使用的API。
 * 它可能会随时变更、删除，且不保证兼容性。
 */
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    message = "一个仅供 simbot 内部使用的API。它可能会随时变更、删除，且不保证兼容性。",
    level = RequiresOptIn.Level.WARNING
)
@MustBeDocumented
public annotation class InternalSimbotAPI

/**
 * 一个设计为仅供 Java 用户使用的API
 */
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    message = "一个设计为仅供 Java 用户使用的API",
    level = RequiresOptIn.Level.WARNING
)
@MustBeDocumented
public annotation class Api4J

/**
 * 一个设计为仅供 JS 用户使用的API
 */
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    message = "一个设计为仅供 JS 用户使用的API",
    level = RequiresOptIn.Level.WARNING
)
@MustBeDocumented
public annotation class Api4Js
