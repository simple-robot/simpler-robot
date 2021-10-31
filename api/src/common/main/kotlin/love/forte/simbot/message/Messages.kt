// @file:JvmMultifileClass
@file:JvmSynthetic //("MessageUtil")

package love.forte.simbot.message

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.*
import love.forte.simbot.Component
import love.forte.simbot.Simbot
import love.forte.simbot.SimbotComponent
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic
import kotlin.reflect.safeCast
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
     * 拼接元素。
     */
    // public fun plus(element: MsgElement<*>): Messages

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
}


/**
 * **仅** 允许一个单个元素的 [Messages]. 一般由其他的 [Message.Element] 实现，代表此消息只能独自存在。
 * 在追加其他任何元素的时候，会直接替换为后者。
 *
 */
@Serializable
public abstract class SingleOnlyMessage : Messages {
    /**
     * 对应的唯一消息。
     */
    public abstract val singleMessage: MsgElement<*>

    // List
    final override val size: Int get() = 1
    final override fun contains(element: MsgElement<*>): Boolean = element == singleMessage
    final override fun containsAll(elements: Collection<MsgElement<*>>): Boolean = elements.any { contains(it) }
    final override fun isEmpty(): Boolean = false
    final override fun iterator(): Iterator<MsgElement<*>> = iterator { singleMessage }
}


public fun messages(): Messages = EmptyMessages
public fun MsgElement<*>.toMessages(): Messages = MessagesImpl(component, listOf(this))
public fun messages(vararg messages: MsgElement<*>): Messages = messages.asList().toMessages()

public fun Iterable<MsgElement<*>>.toMessages(): Messages {
    when (this) {
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

    var list = toMutableList()
    val keySet = mutableSetOf<Message.Key<*>>()
    val component: Component = first().component

    forEachIndexed { i, message ->
        Simbot.check(message.component == component) { "All components in Messages must be consistent. The first was $component, but the element in $i was ${message.component} ." }
        if (i == 0) {
            list.add(message)
        } else {
            if (message.key.isReject(keySet)) {
                list = mutableListOf(message)
            } else {
                list.add(message)
            }

        }

    }

    return MessagesImpl(component, list)
}


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


}