/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.qguild.utils

import kotlinx.coroutines.CancellationException
import love.forte.simbot.qguild.QGInternalApi
import love.forte.simbot.common.utils.runCatchingCancellable as commonRunCatchingCancellable

/**
 * 类似于[kotlin.runCatching]，但不会捕获 [CancellationException]。
 *
 * [CancellationException] 是协程的控制信号，而不是常规操作失败，它必须继续传播。
 *
 * 此实现已迁移至 [love.forte.simbot.common.utils.runCatchingCancellable]；此函数仅为保持
 * QQ Guild 4.7.0 的二进制兼容而保留，并已废弃。请改用 common-core 中的实现。
 *
 * @since 4.7.0
 */
@QGInternalApi
@Deprecated(
    "已迁移至 love.forte.simbot.common.utils.runCatchingCancellable；仅为保持二进制兼容而保留",
    ReplaceWith(
        "runCatchingCancellable(block)",
        "love.forte.simbot.common.utils.runCatchingCancellable"
    )
)
public inline fun <R> runCatchingCancellable(block: () -> R): Result<R> =
    commonRunCatchingCancellable(block)

/**
 * 类似于[kotlin.runCatching]，但不会捕获 [CancellationException]。
 *
 * [CancellationException] 是协程的控制信号，而不是常规操作失败，它必须继续传播。
 *
 * 此实现已迁移至 [love.forte.simbot.common.utils.runCatchingCancellable]；此函数仅为保持
 * QQ Guild 4.7.0 的二进制兼容而保留，并已废弃。请改用 common-core 中的实现。
 *
 * @since 4.7.0
 */
@QGInternalApi
@Deprecated(
    "已迁移至 love.forte.simbot.common.utils.runCatchingCancellable；仅为保持二进制兼容而保留",
    ReplaceWith(
        "this.runCatchingCancellable(block)",
        "love.forte.simbot.common.utils.runCatchingCancellable"
    )
)
public inline fun <T, R> T.runCatchingCancellable(block: T.() -> R): Result<R> =
    this.commonRunCatchingCancellable(block)
