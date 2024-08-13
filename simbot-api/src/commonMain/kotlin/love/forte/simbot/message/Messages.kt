/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

@file:JvmName("MessagesUtil")
@file:JvmMultifileClass

package love.forte.simbot.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import love.forte.simbot.message.MessagesBuilder.Companion.create
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * 一个 **消息链**。
 * 消息链 [Messages] 是一组 [Message.Element] 的表现。
 *
 * 消息链是**不可变的**。它通过 [plus] 与其他消息元素或消息链重新组合为新的消息链。
 *
 */
public sealed interface Messages : Message, Iterable<Message.Element> {
    /**
     * 得到当前消息链中的元素迭代器。
     */
    override fun iterator(): Iterator<Message.Element>

    /**
     * 获取当前消息链中的元素数量。
     */
    public val size: Int

    /**
     * 判断当前消息链是否为空。
     */
    public fun isEmpty(): Boolean

    /**
     * 判断 [Messages] 中是否包含元素 [element]。
     */
    public operator fun contains(element: Message.Element): Boolean

    /**
     * 以当前消息链为准构建一个 [List] 类型的瞬时副本。
     */
    public fun toList(): List<Message.Element>

    /**
     * 合并一个 [Message.Element] 并得到新的消息链。
     */
    public operator fun plus(element: Message.Element): Messages

    /**
     * 合并一个消息集并得到新的消息链。
     */
    public operator fun plus(messages: Iterable<Message.Element>): Messages

    public companion object {
        /**
         * 由标准API默认提供的消息类型的序列化信息。
         */
        @JvmStatic
        @get:JvmName("standardSerializersModule")
        public val standardSerializersModule: SerializersModule = SerializersModule {
            polymorphic(Message.Element::class) {
                includeMessageElementPolymorphic()
                resolvePlatformStandardSerializers()
            }
        }


        /**
         * 可用于 [Messages] 进行序列化的 [KSerializer].
         *
         * 会将 [Messages] 视为 [Message.Element] 列表进行序列化。
         */
        @JvmStatic
        @get:JvmName("serializer")
        public val serializer: KSerializer<Messages>
            get() = MessagesSerializer


        private object MessagesSerializer : KSerializer<Messages> {
            private val delegate = ListSerializer(PolymorphicSerializer(Message.Element::class))
            override val descriptor: SerialDescriptor get() = delegate.descriptor
            override fun deserialize(decoder: Decoder): Messages = delegate.deserialize(decoder).toMessages()
            override fun serialize(encoder: Encoder, value: Messages) {
                delegate.serialize(encoder, value.toList())
            }
        }

        /**
         * 为一个元素创建 [Messages] 对象。
         *
         * @param element 消息元素。
         * @return 包含该元素的 [Messages] 对象。
         */
        @JvmStatic
        public fun of(element: Message.Element): Messages = messagesOf(element)

        /**
         * 为一个或多个元素创建 [Messages] 对象。
         *
         * @param elements 可变数量的消息元素。
         * @return 包含这些元素的 [Messages] 对象。
         */
        @JvmStatic
        public fun of(vararg elements: Message.Element): Messages = messagesOf(elements = elements)

        /**
         * 创建一个空的 [Messages] 对象。
         *
         * @return 一个不包含任何元素的 [Messages] 对象。
         */
        @JvmStatic
        public fun empty(): Messages = emptyMessages()

        /**
         * 为一个可迭代对象创建 [Messages] 对象。
         *
         * @param iterable 可迭代的消息元素。
         * @return 包含这些元素的 [Messages] 对象。
         */
        @JvmStatic
        public fun of(iterable: Iterable<Message.Element>): Messages = iterable.toMessages()

        /**
         * 得到一个用于构建 [Messages] 的构建器 [MessagesBuilder]。
         *
         * @see MessagesBuilder.create
         */
        @JvmStatic
        @JvmOverloads
        public fun builder(container: MutableList<Message.Element> = mutableListOf()): MessagesBuilder =
            create(container)
    }
}

/**
 * 判断 [Messages] 中是否存在任何元素。
 */
public fun Messages.isNotEmpty(): Boolean = !isEmpty()

/**
 * 整合平台特别实现的序列化信息。
 */
internal expect fun PolymorphicModuleBuilder<Message.Element>.resolvePlatformStandardSerializers()


private object EmptyMessages : Messages {
    override fun iterator(): Iterator<Message.Element> = toList().iterator()
    override val size: Int get() = 0
    override fun isEmpty(): Boolean = true
    override fun contains(element: Message.Element): Boolean = false
    override fun toList(): List<Message.Element> = emptyList()

    override fun plus(element: Message.Element): Messages {
        return messagesOf(element)
    }

    override fun plus(messages: Iterable<Message.Element>): Messages {
        return when (messages) {
            is EmptyMessages -> EmptyMessages
            is Messages -> messages
            else -> messages.toMessages()
        }
    }

    override fun toString(): String = "EmptyMessages"

    override fun equals(other: Any?): Boolean {
        return other === EmptyMessages
    }

    override fun hashCode(): Int = 0
}

private class SingleElementMessages(val element: Message.Element) : Messages {
    override fun iterator(): Iterator<Message.Element> = toList().iterator()

    override val size: Int
        get() = 1

    override fun isEmpty(): Boolean = false

    override fun contains(element: Message.Element): Boolean = element == this.element

    override fun toList(): List<Message.Element> = listOf(element)

    override fun plus(element: Message.Element): Messages {
        return ListMessages(listOf(this.element, element))
    }

    override fun plus(messages: Iterable<Message.Element>): Messages {
        if (messages is EmptyMessages) return this
        if (messages is Collection) {
            if (messages.isEmpty()) {
                return this
            }

            return ListMessages(
                buildList(messages.size + 1) {
                    add(this@SingleElementMessages.element)
                    addAll(messages)
                }
            )
        }

        return ListMessages(
            buildList {
                add(this@SingleElementMessages.element)
                addAll(messages)
            }
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is SingleElementMessages) return false

        return element == other.element
    }

    override fun hashCode(): Int = element.hashCode()

    override fun toString(): String = "Messages([$element])"
}

/**
 * 基于 [List] 的 [Messages] 实现。[list] 中至少存在2个元素，不会为空。
 *
 */
private class ListMessages(val list: List<Message.Element>) : Messages {
    override fun iterator(): Iterator<Message.Element> = list.iterator()

    override val size: Int
        get() = list.size

    override fun isEmpty(): Boolean = list.isEmpty() // always true

    override fun contains(element: Message.Element): Boolean = list.contains(element)

    override fun toList(): List<Message.Element> = list.toList()

    override fun plus(element: Message.Element): Messages {
        return ListMessages(list + element)
    }

    override fun plus(messages: Iterable<Message.Element>): Messages {
        if (messages is EmptyMessages) return this
        if (messages is Collection && messages.isEmpty()) return this

        return ListMessages(list + messages)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is ListMessages) return false

        return list == other.list
    }

    override fun hashCode(): Int = list.hashCode()

    override fun toString(): String = "Messages($list)"

}

/**
 * 合并两个 [Message] 为 [Messages]。
 *
 */
public operator fun Message.plus(other: Message): Messages {
    if (this is EmptyMessages || other is EmptyMessages) {
        val valid = if (this is EmptyMessages) other else this
        return when (valid) {
            is Messages -> valid
            is Message.Element -> messagesOf(valid)
        }
    }

    // all valid

    return when {
        this is Message.Element && other is Message.Element -> messagesOf(this, other)
        this is Messages -> when (other) {
            is Messages -> this.plus(other)
            is Message.Element -> this.plus(other)
        }

        else -> {
            other as Messages
            when (this) {
                is Messages -> other.plus(this)
                is Message.Element -> other.plus(this)
            }
        }
    }
}

/**
 * 返回一个空的 [Messages] 对象
 *
 * @return 空的Messages对象
 */
public fun emptyMessages(): Messages = EmptyMessages

/**
 * 创建一个单元素的 [Messages] 对象
 *
 * @param element 单个消息元素
 * @return 单元素的 [Messages] 对象
 */
public fun messagesOf(element: Message.Element): Messages = SingleElementMessages(element)

/**
 * 根据输入的消息元素数组创建 [Messages] 对象
 *
 * @param elements 消息元素数组
 * @return 根据输入创建的 [Messages] 对象
 */
public fun messagesOf(vararg elements: Message.Element): Messages {
    return when (elements.size) {
        0 -> emptyMessages()
        1 -> messagesOf(elements[0])
        else -> ListMessages(elements.toList())
    }
}

/**
 * 将元素集转换为 [Messages] 对象
 *
 * @return 根据输入的元素列表创建的 [Messages] 对象
 */
public fun Iterable<Message.Element>.toMessages(): Messages {
    return when (this) {
        is Collection -> when {
            isEmpty() -> emptyMessages()
            size == 1 -> messagesOf(first())
            else -> ListMessages(this.toList())
        }

        else -> {
            val iterator = iterator()
            val hasValue = iterator.hasNext()
            if (!hasValue) {
                return emptyMessages()
            }

            val firstValue = iterator.next()
            val hasSecondValue = iterator.hasNext()
            if (!hasSecondValue) {
                return messagesOf(firstValue)
            }

            val list = buildList {
                add(firstValue)
                for (element in iterator) {
                    add(element)
                }
            }

            ListMessages(list)
        }
    }
}

/**
 * Encodes the given [messages] object to a String representation using the StringFormat.
 *
 * @receiver [StringFormat], e.g. [Json]
 * @param messages The Messages object that needs to be encoded.
 * @return The encoded String representation of the Messages object.
 */
public fun StringFormat.encodeMessagesToString(messages: Messages): String =
    encodeToString(Messages.serializer, messages)

/**
 * Decodes a string representation of Messages using the provided StringFormat.
 *
 * @receiver [StringFormat], e.g. [Json]
 * @param string The string representation of Messages to decode.
 * @return The deserialized Messages object.
 */
public fun StringFormat.decodeMessagesFromString(string: String): Messages =
    decodeFromString(Messages.serializer, string)

/**
 * @suppress 仅针对v4.0.0-dev16及以下的版本的JVM二进制兼容
 */
@Suppress("FunctionName")
@Deprecated("仅供临时针对v4.0.0-dev16及以下的版本的JVM二进制兼容", level = DeprecationLevel.HIDDEN)
public object MessagesKt {
    @JvmStatic
    public fun StringFormat.encodeMessagesToString(messages: Messages): String =
        encodeToString(Messages.serializer, messages)

    @JvmStatic
    public fun StringFormat.decodeMessagesFromString(string: String): Messages =
        decodeFromString(Messages.serializer, string)

    @JvmStatic
    @JvmName("emptyMessages")
    public fun emptyMessages_compatible(): Messages = emptyMessages()

    @JvmStatic
    @JvmName("messagesOf")
    public fun messagesOf_compatible(element: Message.Element): Messages = messagesOf(element)

    @JvmStatic
    @JvmName("messagesOf")
    public fun messagesOf_compatible(vararg elements: Message.Element): Messages = messagesOf(*elements)

    @JvmStatic
    @JvmName("toMessages")
    public fun Iterable<Message.Element>.toMessages_compatible(): Messages = toMessages()

    @JvmStatic
    @JvmName("plus")
    public fun Message.plus_compatible(other: Message): Messages = plus(other)

    @JvmStatic
    @JvmName("isNotEmpty")
    public fun Messages.isNotEmpty_compatible(): Boolean = isNotEmpty()
}

/**
 * [MessagesBuilder]'s dsl marker.
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
public annotation class MessagesBuilderDsl

/**
 * 一个可以追加 [Message.Element] 的类型接口。
 *
 * @since 4.4.0
 *
 * @see MessagesBuilder
 */
public interface MessagesAddable<T : MessagesAddable<T>> {
    /**
     * Add an element to the [MessagesBuilder] container.
     *
     * @param element the element to be added
     * @return the updated [MessagesAddable][T] instance
     */
    public fun add(element: Message.Element): T

    /**
     * Adds the given text as [Text]
     * to the [MessagesBuilder] container.
     *
     * @param text the text to add to the container
     * @return the updated [MessagesAddable][T] instance
     */
    public fun add(text: String): T = add(text.toText())

    /**
     * Add the given messages to this [MessagesAddable].
     *
     * @param messages 要添加的消息元素集
     * @return the updated [MessagesAddable][T] object
     */
    public fun addAll(messages: Iterable<Message.Element>): T

    /**
     * Add an element to this [MessagesAddable].
     *
     * @see add
     */
    @MessagesBuilderDsl
    public operator fun Message.Element.unaryPlus(): T = add(this)

    /**
     * Add a text as [Text] to this [MessagesAddable].
     *
     * @see add
     */
    @MessagesBuilderDsl
    public operator fun String.unaryPlus(): T = add(this)

    /**
     * Add messages to this [MessagesAddable].
     *
     * @see add
     */
    @MessagesBuilderDsl
    public operator fun Iterable<Message.Element>.unaryPlus(): T = addAll(this)

    /**
     * Build [Messages].
     *
     * This method constructs and returns a [Messages] object using the container.
     *
     * @return The constructed [Messages] object.
     */
    public fun build(): Messages
}


/**
 * 一个用于动态构建 [Messages] 的构建器。
 * 使用 [create] 构建。
 *
 * Kotlin 中也可以使用 [buildMessages] 以 DSL 的方式构建 [Messages]。
 *
 * @see buildMessages
 */
public class MessagesBuilder
private constructor(private val container: MutableList<Message.Element>) :
    MessagesAddable<MessagesBuilder> {
    public companion object {
        /**
         * Creates a new instance of MessagesBuilder.
         *
         * @param container The list of Message.Element objects. Defaults to an empty mutable list if not specified.
         * @return An instance of MessagesBuilder.
         */
        @JvmStatic
        @JvmOverloads
        public fun create(container: MutableList<Message.Element> = mutableListOf()): MessagesBuilder =
            MessagesBuilder(container)
    }

    /**
     * Add an element to the [MessagesBuilder] container.
     *
     * @param element the element to be added
     * @return the updated MessagesBuilder instance
     */
    override fun add(element: Message.Element): MessagesBuilder = apply { container.add(element) }

    /**
     * Add the given messages to the [MessagesBuilder] container.
     *
     * @param messages 要添加的消息元素集
     * @return the updated MessagesBuilder object
     */
    override fun addAll(messages: Iterable<Message.Element>): MessagesBuilder = apply {
        if (messages is Messages) {
            when (messages) {
                EmptyMessages -> {
                    // do nothing.
                }

                is ListMessages -> {
                    container.addAll(messages.list)
                }

                is SingleElementMessages -> {
                    container.add(messages.element)
                }
            }
        } else {
            container.addAll(messages)
        }
    }

    /**
     * Build [Messages].
     *
     * This method constructs and returns a [Messages] object using the container.
     *
     * @return The constructed [Messages] object.
     */
    override fun build(): Messages = container.toMessages()
}
