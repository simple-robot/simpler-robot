/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
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

package love.forte.simbot.message

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.bot.Bot
import love.forte.simbot.message.Image.Key.toImage
import love.forte.simbot.resources.Resource

/**
 * 用于构建 [Messages] 的构建器，提供了针对于 [标准消息][StandardMessage] 的快捷api，
 * 并支持追加其他额外消息。
 *
 * 在Java中，你可以直接实例化并使用此构建器：
 * ```java
 * final MessagesBuilder builder = new MessagesBuilder();
 *
 * final Messages messages = builder.at(Identifies.ID(123))
 *         .face(Identifies.ID("hi"))
 *         .atAll()
 *         .text("Hello ")
 *         .append(Text.of("World"))
 *         .build();
 * ```
 *
 * 在kotlin中，你可以参考使用 [buildMessages] 来得到更佳的使用体验:
 *
 * ```kotlin
 * buildMessages {
 *  + "Hello "
 *  + "World".toText()
 *  + Face(2.ID)
 *  at(567.ID)
 * }
 * ```
 *
 * *此构建器不是线程安全的。*
 *
 * @author ForteScarlet
 *
 * @see buildMessages
 * @see Messages
 */
public class MessagesBuilder @JvmOverloads constructor(collection: MutableCollection<Message.Element<*>>? = null) {
    public constructor(messages: Messages) : this(messages.toList().toMutableList())
    
    private val collection: MutableCollection<Message.Element<*>> = collection ?: mutableListOf()
    
    private fun appendElement(messageElement: Message.Element<*>): MessagesBuilder = also {
        collection.add(messageElement)
    }
    
    private fun appendElements(messageElements: Iterable<Message.Element<*>>): MessagesBuilder = also {
        collection.addAll(messageElements)
    }
    
    
    // region 整合方法
    /**
     * 拼接一个字符串文本。
     * @see Text
     */
    public fun text(text: String): MessagesBuilder = appendElement(text.toText())
    
    /**
     * 拼接一个 [at][At]。
     *
     * @see At
     */
    @JvmOverloads
    public fun at(target: ID, atType: String = "user", originContent: String = "@$target"): MessagesBuilder =
        appendElement(At(target, atType, originContent))
    
    /**
     * 拼接一个 [atAll][AtAll]。
     */
    public fun atAll(): MessagesBuilder = appendElement(AtAll)
    
    /**
     * 拼接一个[表情][Face]。
     *
     * @see Face
     */
    public fun face(id: ID): MessagesBuilder = appendElement(Face(id))
    
    /**
     * 拼接一个[emoji][Emoji]。
     *
     * @see Emoji
     */
    public fun emoji(id: ID): MessagesBuilder = appendElement(Emoji(id))
    
    /**
     * 通过 [Bot.uploadImage] 上传并拼接一个 [Image] 消息到当前消息中。
     *
     * @see Bot.uploadImage
     */
    @Suppress("UNUSED_PARAMETER", "RedundantSuspendModifier")
    @JvmSynthetic
    @Deprecated("Just use image(resource, id)", ReplaceWith("image(resource)"), level = DeprecationLevel.ERROR)
    public suspend fun image(bot: Bot, resource: Resource): MessagesBuilder = image(resource)
    
    /**
     * 通过 [ResourceImage] 拼接一个 [Image] 消息到当前消息中。
     *
     * @see Image.toImage
     */
    @JvmOverloads
    public fun image(resource: Resource, id: ID = resource.name.ID): MessagesBuilder =
        appendElement(resource.toImage(id))
    
    /**
     * 通过 [Bot.resolveImage] 获取并拼接一个 [Image] 消息到当前消息中。
     *
     * @see Bot.resolveImage
     */
    @JvmSynthetic
    public suspend fun image(bot: Bot, id: ID): MessagesBuilder = appendElement(bot.resolveImage(id))
    
    /**
     * 通过 [Bot.uploadImageBlocking] 上传并拼接一个 [Image] 消息到当前消息中。
     *
     * @see Bot.uploadImageBlocking
     */
    @Api4J
    @JvmName("image")
    @Suppress("UNUSED_PARAMETER")
    @Deprecated("Just use image(resource, id)", ReplaceWith("image(resource)"), level = DeprecationLevel.ERROR)
    public fun image4J(bot: Bot, resource: Resource): MessagesBuilder = image(resource)
    
    
    /**
     * 通过 [Bot.resolveImageBlocking] 获取并拼接一个 [Image] 消息到当前消息中。
     *
     * @see Bot.resolveImageBlocking
     */
    @Api4J
    @JvmName("image")
    public fun image4J(bot: Bot, id: ID): MessagesBuilder = runBlocking { image(bot, id) }
    // endregion
    
    
    /**
     * 拼接一个字符串文本。
     */
    public fun append(text: String): MessagesBuilder = text(text)
    
    /**
     * 拼接一个任意消息。
     */
    public fun append(element: Message.Element<*>): MessagesBuilder = appendElement(element)
    
    
    /**
     * 拼接多个任意消息。
     */
    public fun append(vararg elements: Message.Element<*>): MessagesBuilder {
        if (elements.isNotEmpty()) {
            appendElements(elements.toList())
        }
        return this
    }
    
    /**
     * 拼接多个任意消息。
     */
    public fun append(elements: Iterable<Message.Element<*>>): MessagesBuilder = appendElements(elements)
    
    
    /**
     * 拼接一个字符串文本。
     */
    @JvmSynthetic
    public operator fun String.unaryPlus(): MessagesBuilder = append(this)
    
    /**
     * 拼接一个任意消息。
     */
    @JvmSynthetic
    public operator fun Message.Element<*>.unaryPlus(): MessagesBuilder = appendElement(this)
    
    
    /**
     * 拼接多个任意消息。
     */
    @JvmSynthetic
    public operator fun Iterable<Message.Element<*>>.unaryPlus(): MessagesBuilder = appendElements(this)
    
    
    /**
     * 根据前构建器中的内容构建一个 [Messages] 实例。
     */
    public fun build(): Messages = collection.toMessages()
}


/**
 * 通过 [MessagesBuilder] 构建 [Messages].
 *
 * ```kotlin
 * buildMessages {
 *  + "Hello "
 *  + "World".toText()
 *  + Face(2.ID)
 *  at(567.ID)
 * }
 * ```
 *
 */
public inline fun buildMessages(
    initial: MutableCollection<Message.Element<*>>? = null,
    block: MessagesBuilder.() -> Unit,
): Messages {
    return MessagesBuilder(initial).also(block).build()
}



