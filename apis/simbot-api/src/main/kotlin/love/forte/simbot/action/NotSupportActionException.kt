/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.action


/**
 *
 * 不支持的行为异常。
 * 对于组件，如果一个行为不被支持（例如 [MuteSupport] [DeleteSupport]）那么
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