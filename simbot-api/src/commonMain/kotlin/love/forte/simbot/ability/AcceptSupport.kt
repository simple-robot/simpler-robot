/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.ability

import love.forte.simbot.event.RequestEvent
import love.forte.simbot.suspendrunner.ST


/**
 * 对“接受行为”的支持，由 [RequestEvent] 实现。
 *
 * @author ForteScarlet
 */
public interface AcceptSupport {
    /**
     * 接受此请求。
     *
     * @throws Throwable 任何可能产生的错误。
     */
    @ST
    public suspend fun accept()

    /**
     * 接受此请求。
     *
     * 对实现者：此函数具有默认实现以确保二进制兼容。
     *
     * @param options 用于当前接受行为的可选项。
     * 如果某选项实现不支持则会被忽略，支持的范围由实现者决定。
     * @since 4.0.0-RC3
     * @throws Throwable 任何可能产生的错误。
     */
    @ST
    public suspend fun accept(vararg options: AcceptOption) {
        accept()
    }
}

/**
 * [AcceptSupport.accept] 的可选项。
 * [AcceptOption] 可以自由扩展，且如果遇到不支持的实现则会将其忽略。
 *
 * @see AcceptSupport.accept
 */
public interface AcceptOption
