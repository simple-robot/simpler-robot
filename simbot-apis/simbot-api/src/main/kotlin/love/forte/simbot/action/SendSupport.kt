/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.action

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.*
import love.forte.simbot.definition.Objective
import love.forte.simbot.event.Event
import love.forte.simbot.message.*
import love.forte.simbot.utils.runInBlocking


/**
 *
 * 一个允许发送消息的实例的行为。
 *
 * [SendSupport] 默的实现于 [love.forte.simbot.definition.Contact] 和 [love.forte.simbot.definition.ChatRoom] 上，
 * 因为作为 “联系人” 和 “聊天室”，他们理应能够发送消息。
 *
 *
 * @see love.forte.simbot.definition.Contact
 * @see love.forte.simbot.definition.ChatRoom
 * @see sendIfSupport
 *
 * @author ForteScarlet
 */
@JvmBlocking
@JvmAsync
public interface SendSupport {

    /**
     * 发送消息，并得到一个回执单。
     *
     * @throws MessageSendingException 如果信息发送这个过程本身出现异常
     * @throws SimbotIllegalArgumentException 如果提供参数中存在不合法参数等
     * @throws SimbotIllegalStateException 如果当前状态存在异常
     * @throws UnsupportedActionException 当不允许向成员发送消息时
     *
     */
    public suspend fun send(message: Message): MessageReceipt

    /**
     * 发送消息，并得到一个回执单。
     *
     * @see send
     * @throws MessageSendingException 如果信息发送这个过程本身出现异常
     * @throws SimbotIllegalArgumentException 如果提供参数中存在不合法参数等
     * @throws SimbotIllegalStateException 如果当前状态存在异常
     *
     */
    @JvmBlocking
    @JvmAsync
    public suspend fun send(message: MessageContent): MessageReceipt = send(message.messages)
    
    /**
     * 发送一段纯文本消息。
     * @see send
     */
    @JvmBlocking
    @JvmAsync
    public suspend fun send(text: String): MessageReceipt = send(Text.of(text))
    
}


/**
 * 通常标记在事件上，代表这是一个 **可回复消息** 的事件。
 *
 * [ReplySupport] 期望中是由一些 [事件][love.forte.simbot.event.Event] 进行实现，尤其是 [消息事件][love.forte.simbot.event.MessageEvent]，代表此事件可以 *回复消息* 。
 * 默认情况下 [ReplySupport] 不会实现于任何默认定义的事件类型（因为无法保证有哪些消息事件存在*可回复消息*这一行为），
 * 但是这不代表你所监听到的实际事件没有实现此类型（例如`tencent-guild`组件中的消息事件或`mirai`组件中的消息事件，便实际上的实现了 [ReplySupport] ）。
 *
 * *Note: 未来可能会默认实现于 [love.forte.simbot.event.MessageEvent]*
 *
 * 相比较于 [SendSupport], [ReplySupport] 更倾向于针对一次事件或者这次事件的发送者为目标的**回复**行为，而不是单纯的发送消息，例如 `tencent-guild` 组件中，
 * 公域机器人如果想要根据一个@消息回复一段消息，则**必须**引用这个消息的ID，因此在 `tencent-guild` 组件中，如果使用的是公域BOT，那么想要回复消息的最好的办法是使用 [ReplySupport.reply] 而不是 [SendSupport.send],
 * 如果要使用 `send`，你必须在消息中拼接一个 `ReplyTo` 来指定目标消息的ID。
 *
 *
 * 当你需要尝试使用回复时，假如你面对的是一个不知道是否真的实现了 [ReplySupport] 接口的消息事件, 那么你可以通过下面的方式来尝试发送：
 *
 * *示例中以 [love.forte.simbot.event.GroupMessageEvent] 为例, 以 `boot` 中的注解监听的形式*
 *
 * ### Kotlin:
 * ```kotlin
 *  suspend fun GroupMessageEvent.listener() {
 *    replyIfSupport { "Hello Simbot" }
 *  }
 * ```
 * Kotlin中提供了扩展函数 [replyIfSupport], 当当前事件 `event is SendSupport` 的时候进行回复，否则得到结果 `null`.
 *
 *
 * ### Java:
 * ```java
 *  public void listener(GroupMessageEvent event) {
 *      if (event instanceof ReplySupport) {
 *          ((ReplySupport) event).replyBlocking("Hello Simbot")
 *      }
 *  }
 * ```
 *
 * Java中可能会略显繁琐，你需要通过 `instanceof` 判断实现情况，在允许的情况下进行类型转化并进行回复。
 *
 * @see love.forte.simbot.event.MessageEvent
 * @see replyIfSupport
 *
 */
@JvmBlocking
@JvmAsync
public interface ReplySupport {

    /**
     * 回复当前目标，并得到一个 [回复回执][MessageReceipt]
     */
    public suspend fun reply(message: Message): MessageReceipt

    /**
     * 回复当前目标，并得到一个 [回复回执][MessageReceipt]
     */
    public suspend fun reply(message: MessageContent): MessageReceipt = reply(message.messages)
    
    /**
     * 回复当前目标，并得到一个 [回复回执][MessageReceipt]
     */
    public suspend fun reply(text: String): MessageReceipt = reply(Text.of(text))
}


/**
 * “回复”回执。
 */
@Deprecated("No longer use", level = DeprecationLevel.ERROR)
public interface MessageReplyReceipt : MessageReceipt {
    override val id: ID
    override val isSuccess: Boolean

    /**
     * 是否作为 **回复** 发送成功。
     * 很多时候对于可回复事件来说，其只能 **回复一次**，因而在次数已经消耗的前提下，
     * 假若平台允许，**或许** 会继续尝试使用普通消息进行发送（需要当前目标实现 [SendSupport]）。
     *
     * 并不代表所有平台都会这么做，或者说大多数情况下，在回复次数耗尽后会抛出异常。
     *
     * @throws UnsupportedActionException 当此行为不被支持时
     */
    public val isReplySuccess: Boolean
}


/**
 * 消息回应支持。
 *
 * 此接口通常标记在一个 [消息事件][love.forte.simbot.event.MessageEvent] 上，代表这个消息能够被 *回应*。
 *
 * 很多允许频道的平台都会有这个功能，能够标记的内容也应当属于一个 [Message], 不过它们大多数的时候支持的类型有限，
 * 例如 emoji 或者一些自定义表情。
 *
 * [MessageReactSupport.react] 将不会对参数 `message` 进行类型限制，相对的，实现者需要对参数消息进行校验，并在存在不匹配的情况时抛出异常。
 *
 * [回应][MessageReactSupport.react] 与 [回复][ReplySupport.reply] 不同，
 * 回复常常类似于针对某个消息而回复一条消息，会产生一条新的消息；
 * 而回应则更多的是对于一个消息"作出回应"，通产情况下不会产生新的消息，一般会表现为标记一个表情。
 *
 */
@JvmBlocking
@JvmAsync
public interface MessageReactSupport {

    public suspend fun react(message: Message): MessageReceipt
}

/**
 * 标记回执。
 *
 * 对于标记回执的 [id][ReactReceipt.id]，有可能是这个回执所属ID，也有可能是被标记消息的ID。
 *
 */
@Deprecated("No longer use", level = DeprecationLevel.ERROR)
public interface ReactReceipt : MessageReceipt {
    override val id: ID
    override val isSuccess: Boolean
}


//region send
/**
 * 如果此目标允许发送消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun Objective.sendIfSupport(message: Message): MessageReceipt? =
    if (this is SendSupport) send(message) else null

/**
 * 如果此目标允许发送消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend inline fun Objective.sendIfSupport(message: () -> Message): MessageReceipt? =
    if (this is SendSupport) send(message()) else null

/**
 * 如果此事件允许发送消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun MessageContainer.sendIfSupport(message: Message): MessageReceipt? =
    if (this is SendSupport) send(message) else null

/**
 * 如果此事件允许发送消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend inline fun MessageContainer.sendIfSupport(message: () -> Message): MessageReceipt? =
    if (this is SendSupport) send(message()) else null

/**
 * 如果此目标允许发送消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun Objective.sendIfSupport(message: MessageContent): MessageReceipt? =
    if (this is SendSupport) send(message) else null

/**
 * 如果此事件允许发送消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun MessageContainer.sendIfSupport(message: MessageContent): MessageReceipt? =
    if (this is SendSupport) send(message) else null

/**
 * 如果此目标允许发送消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun Objective.sendIfSupport(message: String): MessageReceipt? =
    if (this is SendSupport) send(message) else null

/**
 * 如果此事件允许发送消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun MessageContainer.sendIfSupport(message: String): MessageReceipt? =
    if (this is SendSupport) send(message) else null
//endregion


//region reply
/**
 * 如果此事件允许回复消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun Event.replyIfSupport(message: Message): MessageReceipt? =
    if (this is ReplySupport) reply(message) else null

/**
 * 如果此事件允许回复消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun Event.replyIfSupport(message: () -> Message): MessageReceipt? =
    if (this is ReplySupport) reply(message()) else null

/**
 * 如果此事件允许回复消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun Event.replyIfSupport(message: MessageContent): MessageReceipt? =
    if (this is ReplySupport) reply(message) else null

/**
 * 如果此事件允许回复消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun Event.replyIfSupport(message: String): MessageReceipt? =
    if (this is ReplySupport) reply(message) else null

/**
 * 如果此目标允许回复消息，发送，否则得到null。
 */
@JvmSynthetic
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("ReplySupport通常由Event类型实现", level = DeprecationLevel.ERROR)
public suspend fun Objective.replyIfSupport(message: Message): MessageReceipt? =
    if (this is ReplySupport) reply(message) else null


/**
 * 如果此组织允许回复消息，发送，否则得到null。
 */
@JvmSynthetic
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("ReplySupport通常由Event类型实现", level = DeprecationLevel.ERROR)
public suspend fun MessageContainer.replyIfSupport(message: Message): MessageReceipt? =
    if (this is ReplySupport) reply(message) else null

/**
 * 如果此目标允许回复消息，发送，否则得到null。
 */
@JvmSynthetic
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("ReplySupport通常由Event类型实现", level = DeprecationLevel.ERROR)
public suspend inline fun Objective.replyIfSupport(message: () -> Message): MessageReceipt? =
    if (this is ReplySupport) reply(message()) else null


/**
 * 如果此组织允许回复消息，发送，否则得到null。
 */
@JvmSynthetic
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("ReplySupport通常由Event类型实现", level = DeprecationLevel.ERROR)
public suspend inline fun MessageContainer.replyIfSupport(message: () -> Message): MessageReceipt? =
    if (this is ReplySupport) reply(message()) else null


/**
 * 如果此目标允许回复消息，发送，否则得到null。
 */
@JvmSynthetic
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("ReplySupport通常由Event类型实现", level = DeprecationLevel.ERROR)
public suspend fun Objective.replyIfSupport(message: MessageContent): MessageReceipt? =
    if (this is ReplySupport) reply(message) else null


/**
 * 如果此组织允许回复消息，发送，否则得到null。
 */
@JvmSynthetic
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("ReplySupport通常由Event类型实现", level = DeprecationLevel.ERROR)
public suspend fun MessageContainer.replyIfSupport(message: MessageContent): MessageReceipt? =
    if (this is ReplySupport) reply(message) else null


/**
 * 如果此目标允许回复消息，发送，否则得到null。
 */
@JvmSynthetic
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("ReplySupport通常由Event类型实现", level = DeprecationLevel.ERROR)
public suspend fun Objective.replyIfSupport(message: String): MessageReceipt? =
    if (this is ReplySupport) reply(message) else null


/**
 * 如果此组织允许回复消息，发送，否则得到null。
 */
@JvmSynthetic
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("ReplySupport通常由Event类型实现", level = DeprecationLevel.ERROR)
public suspend fun MessageContainer.replyIfSupport(message: String): MessageReceipt? =
    if (this is ReplySupport) reply(message) else null
//endregion


//region react
/**
 * 如果此目标允许回复标记消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun Objective.reactIfSupport(message: Message): MessageReceipt? =
    if (this is MessageReactSupport) react(message) else null

/**
 * 如果此事件允许回复标记消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend fun MessageContainer.reactIfSupport(message: Message): MessageReceipt? =
    if (this is MessageReactSupport) react(message) else null

/**
 * 如果此目标允许回复标记消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend inline fun Objective.reactIfSupport(message: () -> Message): MessageReceipt? =
    if (this is MessageReactSupport) react(message()) else null

/**
 * 如果此事件允许回复标记消息，发送，否则得到null。
 */
@JvmSynthetic
public suspend inline fun MessageContainer.reactIfSupport(message: () -> Message): MessageReceipt? =
    if (this is MessageReactSupport) react(message()) else null
//endregion


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
