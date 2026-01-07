/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.kook

/**
 * 尚在实验阶段的API，可能会随时修改、删除。
 */
@RequiresOptIn("尚在实验阶段的API，可能会随时修改、删除。")
@Retention(AnnotationRetention.BINARY)
public annotation class ExperimentalKookApi

/**
 * KOOK 组件内部使用的API，可能会随时修改、删除。
 */
@RequiresOptIn("KOOK 组件内部使用的API，可能会随时修改、删除。")
@Retention(AnnotationRetention.BINARY)
public annotation class InternalKookApi

/**
 * 一个具有较为复杂的条件或规则、
 * 有较为明显的副作用、
 * 或应用场景有限的API。使用它们需要谨慎，并应仔细阅读相关说明与文档。
 */
@RequiresOptIn("一个具有较为复杂的条件或规则、有较为明显的副作用、或应用场景有限的API。使用它们需要谨慎，并应仔细阅读相关说明与文档。")
@Retention(AnnotationRetention.BINARY)
public annotation class DiscreetKookApi
