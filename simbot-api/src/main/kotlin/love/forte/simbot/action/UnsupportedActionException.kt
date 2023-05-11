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

package love.forte.simbot.action


/**
 *
 * 不支持的行为异常。
 * 对于组件，如果一个行为不被支持（例如 [SendSupport]）那么可能会抛出此异常。
 *
 * @author ForteScarlet
 */
public class UnsupportedActionException : ActionException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * 抛出 [UnsupportedActionException].
 *
 * e.g.
 * ```kotlin
 * actionUnsupported(cause) { "Send message" }
 * ```
 */
public inline fun actionUnsupported(cause: Throwable? = null, block: () -> String): Nothing = throw UnsupportedActionException(block(), cause)
