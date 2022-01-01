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

// @file:JvmMultifileClass
@file:JvmSynthetic //("MessageUtil")

package love.forte.simbot.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.*
import love.forte.simbot.Component
import love.forte.simbot.Simbot
import love.forte.simbot.SimbotComponent
import love.forte.simbot.message.Message.Element as MsgElement


/**
 * 针对于 [Message.Element] 的多态序列化的注册器。
 *
 * @see Messages
 */
public interface MessageElementPolymorphicRegistrar {
    public fun registrar(builderAction: PolymorphicModuleBuilder<MsgElement<*>>.() -> Unit)
}


/**
 * 消息列表，即 [MsgElement] 的列表，连接多条[消息元素][MsgElement]的链表。
 *
 * [Messages] 是不可变的，但是 [Messages] 中的元素并不一定。每次进行 [plus] 都应视为得到了一个新的 [Messages] 实例。
 *
 * [Messages] 中可以存在多个不同组件之间的 [MsgElement], 但是除了与 [SimbotComponent] 混用之外，不建议这么做。
 * 大多数情况下，组件对于 [Messages] 的解析很少会顾及到其他组件，而当遇到不支持的组件的时候，大概率会将其忽略或抛出异常。
 *
 * @see EmptyMessages
 * @see SingleOnlyMessage
 * @see MessageList
 */
public sealed interface Messages : List<MsgElement<*>>, RandomAccess, Message {


    /**
     * 拼接一个 [MsgElement].
     */
    public operator fun plus(element: MsgElement<*>): Messages


    /**
     * 拼接 [MsgElement] 列表.
     */
    public operator fun plus(messages: List<MsgElement<*>>): Messages


    /**
     * 拼接(替换为) [SingleOnlyMessage].
     */
    public operator fun plus(singleOnlyMessage: SingleOnlyMessage<*>): Messages = singleOnlyMessage

    /**
     */
    public companion object : MessageElementPolymorphicRegistrar {

        @JvmSynthetic
        @Suppress("ObjectPropertyName")
        private var _serializersModule = SerializersModule {
            polymorphic(MsgElement::class) {
                subclass(Text.serializer())
                subclass(At.serializer())
                subclass(AtAll.serializer())
                subclass(Emoji.serializer())
                subclass(Face.serializer())
            }
        }

        public val serializersModule: SerializersModule get() = _serializersModule


        public fun mergeSerializersModule(serializersModule: SerializersModule) {
            _serializersModule += serializersModule
        }

        public fun mergeSerializersModule(builderAction: SerializersModuleBuilder.() -> Unit) {
            mergeSerializersModule(SerializersModule(builderAction))
        }

        public override fun registrar(builderAction: PolymorphicModuleBuilder<MsgElement<*>>.() -> Unit) {
            registrarPolymorphic(builderAction)
        }

        public inline fun <reified M : MsgElement<*>> registrarPolymorphic(crossinline builderAction: PolymorphicModuleBuilder<M>.() -> Unit) {
            mergeSerializersModule {
                polymorphic(baseClass = M::class, builderAction = builderAction)
            }
        }

        internal object MessagesSerializer : KSerializer<Messages> {
            private val delegate = ListSerializer(PolymorphicSerializer(MsgElement::class))
            override fun deserialize(decoder: Decoder): Messages = delegate.deserialize(decoder).toMessages()
            override val descriptor: SerialDescriptor get() = delegate.descriptor
            override fun serialize(encoder: Encoder, value: Messages) {
                delegate.serialize(encoder, value)
            }
        }

        public val serializer: KSerializer<Messages> get() = MessagesSerializer

        /**
         * 得到一个空的消息列表。
         */
        @JvmStatic
        public fun getEmptyMessages(): Messages = emptyMessages()

        /**
         * 得到一个空的消息列表。
         */
        @JvmStatic
        public fun getMessages(): Messages = messages()

        /**
         * 将一个 [MsgElement] 作为一个 [Messages].
         */
        @JvmStatic
        public fun MsgElement<*>.elementToMessages(): Messages = toMessages()

        @JvmStatic
        public fun List<MsgElement<*>>.listToMessages(): Messages = toMessages()

        /**
         * 得到一个消息列表。
         */
        @JvmStatic
        public fun getMessages(vararg messages: MsgElement<*>): Messages = messages(*messages)
    }

}


/**
 * 得到一个空的消息列表。
 */
public fun emptyMessages(): Messages = EmptyMessages


/**
 * 没有任何元素的 [Messages]. 在追加列表时，总是会直接替换为后者。
 */
@Serializable
public object EmptyMessages : Messages, List<MsgElement<*>> by emptyList() {
    override fun plus(element: Message.Element<*>): Messages = element.toMessages()
    override fun plus(messages: List<Message.Element<*>>): Messages = messages.toMessages()
}


/**
 * **仅** 允许一个单个元素的 [Messages]. 一般配合 [Message.Element] 进行实现，代表此消息只能独自存在。
 * 在追加其他任何元素的时候，会直接替换为后者。
 *
 */
public abstract class SingleOnlyMessage<E : Message.Element<E>> : MsgElement<E>, Messages,
    AbstractList<MsgElement<*>>() {
    abstract override val component: Component
    abstract override val key: Message.Key<E>


    // List
    final override val size: Int get() = 1
    override fun get(index: Int): Message.Element<*> =
        if (index == 0) this else throw IndexOutOfBoundsException("Index in $index")


    /**
     * 拼接元素。
     */
    override fun plus(element: Message.Element<*>): Messages = element.toMessages()

    /**
     * 拼接元素。
     */
    override fun plus(messages: List<Message.Element<*>>): Messages =
        if (messages.isEmpty()) this else messages.toMessages()
}

/**
 * 得到元素为空的 [Messages] 实例。
 */
public fun messages(): Messages = EmptyMessages

/**
 * 得到元素数量为1的[Messages]实例。如果当前消息元素为 [SingleOnlyMessage] 类型，则会直接返回其自身。
 */
public fun MsgElement<*>.toMessages(): Messages =
    if (this is SingleOnlyMessage<*>) this else SingleValueMessageList(this)


public fun messages(vararg messages: MsgElement<*>): Messages = messages.asList().toMessages()

/**
 * 将 元素为 [MsgElement] 的 [Iterable] 转化为 [Messages] 实例。
 *
 * 当自身本身就是 [Messages] 时会直接返回自身。
 *
 */
public fun Iterable<MsgElement<*>>.toMessages(): Messages {
    when (this) {
        is Messages -> return this
        is Collection -> when {
            isEmpty() -> return emptyMessages()
            size == 1 -> return first().toMessages()
        }
        else -> {
            val iter = this.iterator()
            if (!iter.hasNext()) return emptyMessages()
            val next = iter.next()
            if (!iter.hasNext()) return next.toMessages()
        }
    }

    val iter = iterator()
    val list = mutableListOf<Message.Element<*>>()
    while (iter.hasNext()) {
        val next = iter.next()
        if (iter.hasNext()) {
            if (next is SingleOnlyMessage<*>) {
                list.clear()
                continue
            }
            list.add(next)
        } else {
            if (next is SingleOnlyMessage<*>) return next else list.add(next)
        }
    }

    return if (list.size == 1) SingleValueMessageList(list.first())
    else MessageListImpl(list)
}


public operator fun Message.Element<*>.plus(other: Message.Element<*>): Messages = messages(this, other)
public operator fun Message.Element<*>.plus(other: Messages): Messages = this.toMessages() + other
public operator fun Message.Element<*>.plus(other: SingleOnlyMessage<*>): Messages = other


/**
 * [Messages] 基础实现, 是元素数量不应为空的消息列表。
 *
 * [MessageList] 是不可变的，每次变更都**可能**会得到一个新的实例。
 *
 */
public sealed class MessageList : Messages, Collection<MsgElement<*>>


internal class SingleValueMessageList(private val value: MsgElement<*>) : MessageList() {
    override val size: Int get() = 1
    override fun contains(element: Message.Element<*>): Boolean = value == element
    override fun containsAll(elements: Collection<Message.Element<*>>): Boolean = elements.all(::contains)
    override fun get(index: Int): Message.Element<*> {
        if (index == 0) return value else throw IndexOutOfBoundsException("Index $index out of last index: 0")
    }

    override fun indexOf(element: Message.Element<*>): Int = if (element == value) 0 else -1
    override fun lastIndexOf(element: Message.Element<*>): Int = indexOf(element)
    override fun isEmpty(): Boolean = false
    override fun iterator(): Iterator<Message.Element<*>> = iterator { yield(value) }
    override fun listIterator(): ListIterator<Message.Element<*>> = SingleValueListIterator(value)
    override fun listIterator(index: Int): ListIterator<Message.Element<*>> = SingleValueListIterator(get(index))
    override fun subList(fromIndex: Int, toIndex: Int): List<Message.Element<*>> {
        if (fromIndex == 0) {
            if (toIndex == 0) return emptyList()
            if (toIndex == 1) return this
        }

        throw IndexOutOfBoundsException("fromIndex: $fromIndex, toIndex: $toIndex, but lastIndex: 0")
    }
    override fun plus(element: Message.Element<*>): Messages {
        if (element is SingleOnlyMessage<*>) return element
        return MessageListImpl(listOf(value, element))
    }

    override fun plus(messages: List<Message.Element<*>>): Messages {
        if (messages.isEmpty()) return this
        if (messages.size == 1) return plus(messages.first())

        val list = buildList {
            add(value)
            addAll(messages)
        }
        return list.toMessages()
    }

    private class SingleValueListIterator(private val value: MsgElement<*>) : ListIterator<MsgElement<*>> {
        private var next = false
        override fun hasNext(): Boolean = !next
        override fun hasPrevious(): Boolean = next
        override fun next(): Message.Element<*> {
            if (next) throw NoSuchElementException()
            else return value.also {
                next = true
            }
        }
        override fun previous(): Message.Element<*> {
            if (next) return value.also { next = false }
            else throw NoSuchElementException()
        }

        override fun nextIndex(): Int = if (next) 1 else 0
        override fun previousIndex(): Int = if (next) 0 else -1
    }
}


internal class MessageListImpl
internal constructor(private val delegate: List<MsgElement<*>>) : MessageList(), List<MsgElement<*>> by delegate {
    init {
        Simbot.check(delegate.isNotEmpty()) { "Messages init message list cannot be empty." }
    }

    /**
     * 拼接元素。
     */
    override fun plus(element: Message.Element<*>): Messages {
        if (element is SingleOnlyMessage<*>) return element

        return MessageListImpl(delegate + element)
    }

    /**
     * 拼接元素。
     */
    override fun plus(messages: List<Message.Element<*>>): Messages {
        if (messages.isEmpty()) return this
        if (messages.size == 1) {
            val element = messages.first()
            if (element is SingleOnlyMessage<*>) return element
        }

        val newList = delegate.toMutableList()
        newList.addAll(messages)

        return newList.toMessages()
    }
}