/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.action

import love.forte.simbot.SimbotIllegalArgumentException
import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.definition.Organization
import love.forte.simbot.definition.User
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReceipt

/**
 * 一个 **可交流的** 行为。
 *
 * 交流分为两种交流：主动交流，被动交流。
 *
 */
public interface CommunicableAction : Action




public enum class CommunicationType



/**
 *
 * 一个允许发送消息的实例的行为。
 *
 * @author ForteScarlet
 */
public interface MessageSendSupport : Action {

    /**
     * 发送消息，并得到一个回执单。
     *
     * @throws MessageSendingException 如果信息发送这个过程本身出现异常
     * @throws SimbotIllegalArgumentException 如果提供参数中存在不合法参数等
     * @throws SimbotIllegalStateException 如果当前状态存在异常
     *
     */
    public suspend fun send(message: Message): MessageReceipt

}


/**
 * 如果此用户允许发送消息，发送，否则得到null。
 */
public suspend fun User.sendIfSupport(message: Message): MessageReceipt? = if (this is MessageSendSupport) send(message) else null

/**
 * 如果此组织允许发送消息，发送，否则得到null。
 */
public suspend fun Organization.sendIfSupport(message: Message): MessageReceipt? = if (this is MessageSendSupport) send(message) else null





/**
 * 信息发送可能会出现异常。这是信息发送过程本身出现异常，比如网络异常或者目标接收者返回了错误等。
 *
 * 不包含诸如 [参数错误][SimbotIllegalArgumentException]、[状态错误][SimbotIllegalStateException] 等。
 */
public open class MessageSendingException : ActionException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}
