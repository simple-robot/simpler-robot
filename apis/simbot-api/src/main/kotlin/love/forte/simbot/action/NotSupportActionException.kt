package love.forte.simbot.action


/**
 *
 * 不支持的行为异常。
 * 对于组件，如果一个行为不被支持（例如 [MuteAction] [DeleteSupport]）那么
 *
 * @author ForteScarlet
 */
public class NotSupportActionException : ActionException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

public inline fun actionNotSupportBecause(block: () -> String): Nothing = throw NotSupportActionException(block())