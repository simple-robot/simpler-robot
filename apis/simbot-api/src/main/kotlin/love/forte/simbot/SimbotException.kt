/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
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



