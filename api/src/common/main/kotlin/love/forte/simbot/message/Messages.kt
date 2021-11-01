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
import love.forte.simbot.like
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic
import love.forte.simbot.message.Message.Element as MsgElement


/**
 * 消息列表，即 [MsgElement] 的列表，连接多条消息的链表。
 *
 * [Messages] 是不可变的，但是 [Messages] 中的元素并不一定。每次进行 [plus] 都应视为得到了一个新的 [Messages] 实例。
 *
 */
public sealed interface Messages : List<MsgElement<*>>, RandomAccess, Message {
    /**
     * 这串消息所属组件。在消息链中，所有的消息都应属于同一个组件。
     *
     * 当目前消息中不存在任何元素的时候，得到 [SimbotComponent].
     */
    override val component: Component get() = firstOrNull()?.component ?: SimbotComponent

    /**
     * plus single [MsgElement].
     */
    public operator fun plus(element: MsgElement<*>): Messages


    /**
     * plus [MsgElement] List.
     */
    public operator fun plus(messages: List<MsgElement<*>>): Messages


    /**
     * plus [SingleOnlyMessage].
     */
    public operator fun plus(singleOnlyMessage: SingleOnlyMessage<*>): Messages = singleOnlyMessage

    /**
     */
    public companion object {
        @JvmSynthetic
        @Suppress("ObjectPropertyName")
        private var _serializersModule = SerializersModule {
            polymorphic(MsgElement::class) {
                val c = StandardMessage::class
                c.qualifiedName
                subclass(Text.serializer())

            }
        }
        public val serializersModule: SerializersModule get() = _serializersModule

        public fun addPolymorphic(builderAction: PolymorphicModuleBuilder<MsgElement<*>>.() -> Unit = {}) {
            _serializersModule += SerializersModule {
                polymorphic(baseClass = MsgElement::class, builderAction = builderAction)
            }
        }

        private object MessagesSerializer : KSerializer<Messages> {
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
    override val component: Component = SimbotComponent
    override fun plus(element: Message.Element<*>): Messages = element.toMessages()
    override fun plus(messages: List<Message.Element<*>>): Messages = messages.toMessages()
}


/**
 * **仅** 允许一个单个元素的 [Messages]. 一般配合 [Message.Element] 进行实现，代表此消息只能独自存在。
 * 在追加其他任何元素的时候，会直接替换为后者。
 *
 */
public abstract class SingleOnlyMessage<E : Message.Element<E>> : MsgElement<E>, Messages, AbstractList<MsgElement<*>>() {
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


public fun messages(): Messages = EmptyMessages
public fun MsgElement<*>.toMessages(): Messages =
    if (this is SingleOnlyMessage<*>) this else MessagesImpl(component, listOf(this))

public fun messages(vararg messages: MsgElement<*>): Messages = messages.asList().toMessages()

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

    val component: Component = first().component

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

    return MessagesImpl(component, list)
}


public operator fun Message.Element<*>.plus(other: Message.Element<*>): Messages = messages(this, other)
public operator fun Message.Element<*>.plus(other: Messages): Messages = this.toMessages() + other
public operator fun Message.Element<*>.plus(other: SingleOnlyMessage<*>): Messages = other


/**
 * [Messages] 基础实现。
 *
 */
// @Serializable
internal class MessagesImpl
/*
 * delegate 的内容不进行验证，通过顶层函数进行solve. 原则上delegate 不允许为空。
 */
internal constructor(override val component: Component, private val delegate: List<MsgElement<*>>) : Messages,
    List<MsgElement<*>>, AbstractList<MsgElement<*>>() {
    init {
        Simbot.check(delegate.isNotEmpty()) { "Messages init message list cannot be empty." }
    }

    override val size: Int get() = delegate.size
    override fun iterator(): Iterator<MsgElement<*>> = delegate.iterator()
    override fun get(index: Int): Message.Element<*> = delegate[index]

    /**
     * 拼接元素。
     */
    override fun plus(element: Message.Element<*>): Messages {
        Simbot.check(element.component like component) { "Message's component is $component, cannot append with message element with component ${element.component}" }

        if (element is SingleOnlyMessage<*>) return element

        return MessagesImpl(component, delegate + element)
    }

    /**
     * 拼接元素。
     */
    override fun plus(messages: List<Message.Element<*>>): Messages {
        if (messages.isEmpty()) return this
        if (messages.first() is SingleOnlyMessage<*>) return messages.first().toMessages()

        val newList = delegate.toMutableList()
        if (messages is Messages) {
            Simbot.check(messages.component like component) { "Message's component is $component, cannot append with message element with component ${messages.component}" }
            newList.addAll(messages)
        } else {
            for (message in messages) {
                Simbot.check(message.component like component) { "Message's component is $component, cannot append with message element with component ${message.component}" }
                newList.add(message)
            }

        }


        return MessagesImpl(component, newList)
    }
}