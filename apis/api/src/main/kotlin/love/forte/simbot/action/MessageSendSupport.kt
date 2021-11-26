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

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.SimbotIllegalArgumentException
import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.definition.Objectives
import love.forte.simbot.event.Event
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReceipt


/**
 *
 * 一个允许发送消息的实例的行为。
 *
 * @author ForteScarlet
 */
public interface MessageSendSupport {

    /**
     * 发送消息，并得到一个回执单。
     *
     * @throws MessageSendingException 如果信息发送这个过程本身出现异常
     * @throws SimbotIllegalArgumentException 如果提供参数中存在不合法参数等
     * @throws SimbotIllegalStateException 如果当前状态存在异常
     *
     */
    @JvmSynthetic
    public suspend fun send(message: Message): MessageReceipt


    @Api4J
    public fun sendBlocking(message: Message): MessageReceipt = runBlocking { send(message) }

}

/**
 * 通常标记在事件上，代表这是一个 **可回复的** 事件。
 *
 *
 */
public interface MessageReplySupport {

    /**
     * 回复当前目标，并得到一个 [回复回执][MessageReplyReceipt]
     */
    @JvmSynthetic
    public suspend fun reply(message: Message): MessageReplyReceipt

    @Api4J
    public fun replyBlocking(message: Message): MessageReplyReceipt = runBlocking { reply(message) }
}


/**
 * “回复”回执。
 */
public interface MessageReplyReceipt : MessageReceipt {
    override val id: ID
    override val isSuccess: Boolean

    /**
     * 是否作为 **回复** 发送成功。
     * 很多时候对于可回复事件来说，其只能 **回复一次**，因而在次数已经消耗的前提下，
     * 假若平台允许，**或许** 会继续尝试使用普通消息进行发送（需要当前目标实现 [MessageSendSupport]）。
     *
     * 并不代表所有平台都会这么做，或者说大多数情况下，在回复次数耗尽后会抛出异常。
     *
     */
    public val isReplySuccess: Boolean
}


/**
 * 消息标记支持。
 *
 * 此接口通常标记在一个 [消息事件][love.forte.simbot.event.MessageEvent] 上，代表这个消息能够被 *标记*。
 *
 * 很多允许频道的平台都会有这个功能，能够标记的内容也应当属于一个 [Message], 不过它们大多数的时候支持的类型有限，
 * 例如 emoji 或者一些自定义表情。
 *
 * [MessageMarkSupport.mark] 将不会对参数 `message` 进行类型限制，相对的，实现者需要对参数消息进行校验，并在存在不匹配的情况时抛出异常。
 *
 */
public interface MessageMarkSupport {

    @JvmSynthetic
    public suspend fun mark(message: Message): MessageMarkReceipt

    @Api4J
    public fun markBlocking(message: Message): MessageMarkReceipt = runBlocking { mark(message) }

}

/**
 * 标记回执。
 *
 * 对于标记回执的 [id][MessageMarkReceipt.id]，有可能是这个回执所属ID，也有可能是被标记消息的ID。
 *
 */
public interface MessageMarkReceipt : MessageReceipt {
    override val id: ID
    override val isSuccess: Boolean
}


/**
 * 如果此目标允许发送消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun Objectives.trySend(message: Message): MessageReceipt? =
    if (this is MessageSendSupport) send(message) else null

/**
 * 如果此事件允许发送消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun Event.trySend(message: Message): MessageReceipt? =
    if (this is MessageSendSupport) send(message) else null


/**
 * 如果此目标允许回复消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun Objectives.tryReply(message: Message): MessageReplyReceipt? =
    if (this is MessageReplySupport) reply(message) else null


/**
 * 如果此组织允许回复消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun Event.tryReply(message: Message): MessageReplyReceipt? =
    if (this is MessageReplySupport) reply(message) else null


/**
 * 如果此目标允许回复标记消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun Objectives.tryMark(message: Message): MessageMarkReceipt? =
    if (this is MessageMarkSupport) mark(message) else null

/**
 * 如果此事件允许回复标记消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun Event.tryMark(message: Message): MessageMarkReceipt? =
    if (this is MessageMarkSupport) mark(message) else null


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
