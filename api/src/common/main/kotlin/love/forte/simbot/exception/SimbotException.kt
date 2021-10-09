package love.forte.simbot.exception

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


/**
 *
 * @author ForteScarlet
 */
public interface SimbotError {
    public val message: String?
    public val cause: Throwable?
}


/**
 *
 * @author ForteScarlet
 */
public open class SimbotException : Exception, SimbotError {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 *
 * @author ForteScarlet
 */
public open class SimbotRuntimeException : RuntimeException, SimbotError {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 *
 * @author ForteScarlet
 */
public open class SimbotIllegalArgumentException : SimbotError, IllegalArgumentException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 *
 * @author ForteScarlet
 */
public open class SimbotIllegalStateException : SimbotError, IllegalArgumentException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}



