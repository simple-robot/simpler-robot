/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot


/**
 * Simbot中的部分特殊异常的标记接口。
 * @author ForteScarlet
 *
 * @see Simbot.check
 * @see Simbot.require
 *
 * @see SimbotException
 * @see SimbotRuntimeException
 * @see SimbotIllegalArgumentException
 * @see SimbotIllegalStateException
 */
public interface SimbotError {
    public val message: String?
    public val cause: Throwable?
}


/**
 * 基础的simbot受检异常。
 * @author ForteScarlet
 */
public open class SimbotException : Exception, SimbotError {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * 基础的simbot的运行时异常。
 * @author ForteScarlet
 */
public open class SimbotRuntimeException : RuntimeException, SimbotError {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * simbot中的参数异常。
 * @author ForteScarlet
 *
 * @see IllegalArgumentException
 */
public open class SimbotIllegalArgumentException : SimbotError, IllegalArgumentException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * simbot中的状态异常
 * @author ForteScarlet
 *
 * @see IllegalArgumentException
 */
public open class SimbotIllegalStateException : SimbotError, IllegalArgumentException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}



