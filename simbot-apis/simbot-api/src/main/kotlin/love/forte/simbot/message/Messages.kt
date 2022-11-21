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

package love.forte.simbot.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.*
import love.forte.simbot.Simbot
import love.forte.simbot.utils.view.IndexAccessView
import love.forte.simbot.utils.view.View
import love.forte.simbot.utils.view.emptyView
import love.forte.simbot.message.Message.Element as MsgElement

/**
 * 消息列表，代表为可能多条的 [MsgElement] 信息。
 *
 * ## 不可变
 * [Messages] 是不可变的。每次进行 [plus] 都应视为得到了一个新的 [Messages] 实例。
 *
 * ## 序列化
 * 当你需要对 [Messages] 进行序列化的时候，你所使用的 [KSerializer] 必须为 [Messages.serializer].
 *
 * ## 构建器
 * 除了直接使用拼接的方式，你也可以参考 [MessagesBuilder] 通过构建器来构建 [Messages] 实例。
 *
 * @see EmptyMessages
 * @see SingleOnlyMessage
 * @see MessageList
 * @see MessagesBuilder
 */
public sealed interface Messages : View<MsgElement<*>>, RandomAccess, Message {
    
    /**
     * 根据 [MsgElement] 来获取当前消息链中的所有匹配消息。
     */
    public operator fun <E : MsgElement<E>> get(key: Message.Key<E>): List<E>
    
    
    /**
     * 拼接一个 [MsgElement].
     */
    public operator fun plus(element: MsgElement<*>): Messages
    
    
    /**
     * 拼接 [MsgElement] 列表.
     */
    public operator fun plus(messages: Iterable<MsgElement<*>>): Messages
    
    
    /**
     * 拼接 [MsgElement] 列表.
     */
    public operator fun plus(messages: Messages): Messages
    
    
    /**
     * 拼接(替换为) [SingleOnlyMessage].
     */
    public operator fun plus(singleOnlyMessage: SingleOnlyMessage<*>): Messages = singleOnlyMessage
    
    /**
     * 将当前 [Messages] 转化为不可变的消息链列表。
     */
    public fun toList(): List<MsgElement<*>>
    
    @Suppress("DEPRECATION_ERROR")
    public companion object {
        
        @JvmSynthetic
        @Suppress("ObjectPropertyName")
        private var _serializersModule = SerializersModule {
            polymorphic(MsgElement::class) {
                subclass(Text.serializer())
                subclass(At.serializer())
                subclass(AtAll.serializer())
                subclass(Emoji.serializer())
                subclass(Face.serializer())
                subclass(ResourceImage.serializer())
            }
        }
        
        /**
         * 当前 [Messages] 可用于序列化的 [SerializersModule]. 在组件加载完毕后，其中应包含了所有组件下注册的额外消息类型的多态信息。
         *
         */
        public val serializersModule: SerializersModule get() = _serializersModule
        
        internal object MessagesSerializer : KSerializer<Messages> {
            private val delegate = ListSerializer(PolymorphicSerializer(MsgElement::class))
            override fun deserialize(decoder: Decoder): Messages = delegate.deserialize(decoder).toMessages()
            override val descriptor: SerialDescriptor get() = delegate.descriptor
            override fun serialize(encoder: Encoder, value: Messages) {
                delegate.serialize(encoder, value.toList())
            }
        }
        
        /**
         * 可用于 [Messages] 进行序列化的 [KSerializer].
         */
        @JvmStatic
        public val serializer: KSerializer<Messages>
            get() = MessagesSerializer
        
        /**
         * 得到一个空的消息列表。
         */
        @JvmStatic
        public fun emptyMessages(): Messages = love.forte.simbot.message.emptyMessages()
        
        /**
         * 得到一个空的消息列表。
         */
        @JvmStatic
        public fun messages(): Messages = love.forte.simbot.message.messages()
        
        /**
         * 将一个 [MsgElement] 作为一个 [Messages].
         */
        @JvmStatic
        public fun MsgElement<*>.elementToMessages(): Messages = toMessages()
        
        /**
         * 将一个list转为 [Messages].
         */
        @JvmStatic
        public fun List<MsgElement<*>>.listToMessages(): Messages = toMessages()
        
        /**
         * 得到一个消息列表。
         */
        @JvmStatic
        public fun toMessages(vararg messages: MsgElement<*>): Messages = messages.asList().toMessages()
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
public object EmptyMessages : Messages, View<MsgElement<*>> by emptyView() {
    override fun toList(): List<MsgElement<*>> = emptyList()
    override fun <E : MsgElement<E>> get(key: Message.Key<E>): List<E> = emptyList()
    override fun plus(element: MsgElement<*>): Messages = element.toMessages()
    override fun plus(messages: Iterable<MsgElement<*>>): Messages = messages.toMessages()
    override fun plus(messages: Messages): Messages = messages
    override fun toString(): String = "EmptyMessages"
}


/**
 * **仅** 允许一个单个元素的 [Messages]. 一般配合 [MsgElement] 进行实现，代表此消息只能独自存在。
 * 在追加其他任何元素的时候，会直接替换为后者。
 *
 */
public abstract class SingleOnlyMessage<E : MsgElement<E>> : MsgElement<E>, Messages,
    AbstractList<MsgElement<*>>() {
    abstract override val key: Message.Key<E>
    
    /**
     * 用作 [toString] 展示信息的消息字符串结果。
     */
    protected abstract fun messageString(): String
    
    // List
    final override val size: Int get() = 1
    override fun get(index: Int): MsgElement<*> =
        if (index == 0) this else throw IndexOutOfBoundsException("Index $index of size 1")
    
    
    /**
     * 拼接元素。
     */
    override fun plus(element: MsgElement<*>): Messages = element.toMessages()
    
    /**
     * 拼接元素。
     */
    override fun plus(messages: Iterable<MsgElement<*>>): Messages {
        if (messages is Collection && messages.isEmpty()) {
            return this
        }
        
        val newMessages = messages.toMessages()
        return if (newMessages.isEmpty()) this else newMessages
    }
    
    
    final override fun toString(): String {
        return "S@Messages([$this])"
    }
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

/**
 * 将提供的消息元素组合为 [Messages].
 */
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
    val list = mutableListOf<MsgElement<*>>()
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


/**
 * [MsgElement] 与另外一个 [MsgElement] 进行拼接并组合为 [Messages].
 */
public operator fun MsgElement<*>.plus(other: MsgElement<*>): Messages =
    when {
        // 当前为single only或者目标为single only, 都将导致直接保留后者.
        this is SingleOnlyMessage || other is SingleOnlyMessage -> other.toMessages()
        else -> messages(this, other)
    }

/**
 * [MsgElement] 与另外一个 [Messages] 进行拼接并组合为 [Messages].
 *
 * 作为 `receiver` 的 [MsgElement] 会尝试置于首位, 但如果 [other] 是 [SingleOnlyMessage], 则 `receiver` 将会被舍弃。
 */
public operator fun MsgElement<*>.plus(other: Messages): Messages =
    // if (other is SingleOnlyMessage<*>) other else this.toMessages() + other
    when {
        other.isEmpty() -> this.toMessages()
        // 当前为single only或者目标为single only, 都将导致直接保留后者.
        this is SingleOnlyMessage || other is SingleOnlyMessage<*> -> other
        else -> this.toMessages() + other
    }

/**
 * 与一个 [SingleOnlyMessage] 进行拼接并得到 [Messages]. 将会直接舍弃 `receiver`。
 */
public operator fun MsgElement<*>.plus(other: SingleOnlyMessage<*>): Messages = other


/**
 * [Messages] 基础实现, 是元素数量**不应为空**的消息列表。代表为空消息的对象为 [EmptyMessages].
 *
 * [MessageList] 是不可变的。
 *
 */
public sealed class MessageList : Messages, IndexAccessView<MsgElement<*>>


internal class SingleValueMessageList(private val value: MsgElement<*>) : MessageList() {
    override val size: Int get() = 1
    override fun contains(element: MsgElement<*>): Boolean = value == element
    override fun get(index: Int): MsgElement<*> {
        if (index == 0) return value else throw IndexOutOfBoundsException("Index $index out of last index: 0")
    }
    
    override fun <E : MsgElement<E>> get(key: Message.Key<E>): List<E> {
        return key.safeCast(value)?.let { listOf(it) } ?: emptyList()
    }
    
    override fun isEmpty(): Boolean = false
    override fun iterator(): Iterator<MsgElement<*>> = iterator { yield(value) }
    
    override fun plus(element: MsgElement<*>): Messages {
        if (element is SingleOnlyMessage<*>) return element
        return MessageListImpl(listOf(value, element))
    }
    
    override fun plus(messages: Iterable<MsgElement<*>>): Messages {
        if (messages is Collection) {
            if (messages.isEmpty()) return this
            if (messages.size == 1) return plus(messages.first())
        }
        
        if (messages is View) {
            if (messages.isEmpty()) return this
            if (messages.size == 1) return plus(messages.first())
        }
        
        return buildList {
            add(value)
            addAll(messages)
        }.toMessages()
    }
    
    override fun plus(messages: Messages): Messages {
        if (messages.isEmpty()) return this
        if (messages.size == 1) return plus(messages.first())
        
        return buildList {
            add(value)
            addAll(messages)
        }.toMessages()
    }
    
    override fun toList(): List<MsgElement<*>> = listOf(value)
    
    override fun toString(): String {
        return "SingleValueMessages(value=$value)"
    }
    
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is MessageList) return false
        if (other.size != 1) return false
        return value == other.first()
    }
    
    override fun hashCode(): Int {
        return value.hashCode()
    }
}


internal class MessageListImpl(private val delegate: List<MsgElement<*>>) : MessageList() {
    init {
        Simbot.require(delegate.isNotEmpty()) { "Messages init message list cannot be empty." }
    }
    
    override fun iterator(): Iterator<MsgElement<*>> = delegate.iterator()
    
    override val size: Int
        get() = delegate.size
    
    override fun isEmpty(): Boolean = delegate.isEmpty()
    
    override fun contains(element: MsgElement<*>): Boolean = element in delegate
    
    override fun get(index: Int): MsgElement<*> = delegate[index]
    
    override fun <E : MsgElement<E>> get(key: Message.Key<E>): List<E> {
        return mapNotNull { key.safeCast(it) }
    }
    
    /**
     * 拼接元素。
     */
    override fun plus(element: MsgElement<*>): Messages {
        if (element is SingleOnlyMessage<*>) return element
        
        return MessageListImpl(delegate + element)
    }
    
    /**
     * 拼接元素。
     */
    override fun plus(messages: Iterable<MsgElement<*>>): Messages {
        if (messages is Collection) {
            if (messages.isEmpty()) return this
            if (messages.size == 1) {
                val element = messages.first()
                if (element is SingleOnlyMessage<*>) return element
            }
        }
        
        if (messages is View) {
            if (messages.isEmpty()) return this
            if (messages.size == 1) {
                val element = messages.first()
                if (element is SingleOnlyMessage<*>) return element
            }
        }
        
        val newList = delegate.toMutableList()
        newList.addAll(messages)
        
        return newList.toMessages()
    }
    
    override fun plus(messages: Messages): Messages {
        if (messages.isEmpty()) return this
        if (messages.size == 1) {
            val element = messages.first()
            if (element is SingleOnlyMessage<*>) return element
        }
        
        val newList = delegate.toMutableList()
        newList.addAll(messages)
        
        return newList.toMessages()
    }
    
    override fun toList(): List<MsgElement<*>> = delegate.toList()
    
    
    override fun toString(): String = "Messages($delegate)"
    
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is MessageList) return false
        
        if (other is MessageListImpl) {
            return delegate == other.delegate
        }
        
        if (other.size != size) return false
        val otherIter = other.iterator()
        for (element in this) {
            if (!otherIter.hasNext()) return false
            if (otherIter.next() != element) return false
        }
        
        return true
    }
    
    override fun hashCode(): Int = delegate.hashCode()
    
}