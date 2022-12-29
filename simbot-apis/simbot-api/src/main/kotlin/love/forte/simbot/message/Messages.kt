/*
 * Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
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
     *
     * ```kotlin
     * val imageList = messages[Image]
     * ```
     *
     */
    public operator fun <E : MsgElement<E>> get(key: Message.Key<E>): List<E>
    
    /**
     * 根据 [MsgElement] 来获取当前消息链中的**第一个**匹配类型的消息。
     * 当未寻得指定类型的消息时抛出 [NoSuchElementException].
     *
     * ```kotlin
     * val firstImage: Image = messages.getFirst(Image)
     * ```
     *
     * @throws NoSuchElementException 当不存在此类型的元素时
     */
    public fun <E : MsgElement<E>> getFirst(key: Message.Key<E>): E
    
    
    /**
     * 根据 [MsgElement] 来获取当前消息链中的**第一个**匹配类型的消息。
     * 当未寻得指定类型的消息时得到null。
     *
     * ```kotlin
     * val firstImage: Image? = messages.getFirstOrNull(Image)
     * ```
     */
    public fun <E : MsgElement<E>> getFirstOrNull(key: Message.Key<E>): E?
    
    
    /**
     * 通过索引访问目标位置的元素。
     * @throws IndexOutOfBoundsException 索引越界时
     */
    public operator fun get(index: Int): MsgElement<*>
    
    /**
     * 拼接一个 [MsgElement]，得到一个新的 [Messages]。
     */
    public operator fun plus(element: MsgElement<*>): Messages
    
    /**
     * 拼接 [MsgElement] 列表，得到一个新的 [Messages]。
     */
    public operator fun plus(messages: Iterable<MsgElement<*>>): Messages
    
    /**
     * 将当前 [Messages] 转化为不可变的消息元素列表。
     */
    public fun toList(): List<MsgElement<*>>
    
    @Suppress("DEPRECATION_ERROR")
    public companion object {
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
         * 当前 [Messages] 可用于序列化的 [SerializersModule]。其中包含 [StandardMessage] 各实现类型的序列化信息。
         *
         * @see StandardMessage
         */
        @get:JvmStatic
        public val serializersModule: SerializersModule get() = _serializersModule
        
        internal object MessagesSerializer : KSerializer<Messages> {
            private val delegate = ListSerializer(PolymorphicSerializer(MsgElement::class))
            override val descriptor: SerialDescriptor get() = delegate.descriptor
            override fun deserialize(decoder: Decoder): Messages = delegate.deserialize(decoder).toMessages()
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
        public fun emptyMessages(): Messages = EmptyMessages
        
        /**
         * 得到一个空的消息列表。
         */
        @JvmStatic
        public fun messages(): Messages = emptyMessages()
        
        /**
         * 将一个 [MsgElement] 作为一个 [Messages].
         */
        @JvmStatic
        public fun MsgElement<*>.elementToMessages(): Messages = toMessages()
        
        /**
         * 将一个 [Iterable] 转为 [Messages].
         */
        @JvmStatic
        public fun Iterable<MsgElement<*>>.listToMessages(): Messages = toMessages()
        
        /**
         * 得到一个消息列表。
         */
        @JvmStatic
        public fun toMessages(vararg messages: MsgElement<*>): Messages = messages.asList().toMessages()
        
        /**
         * 判断两个 [Messages] 是否在元素内容上相同。[contentEquals] 会依次比较各个元素，
         * 当元素数量、内容、顺序都完全相同时得到 `true`。
         */
        @JvmStatic
        public fun Messages.contentEquals(other: Messages): Boolean {
            when {
                this === other -> return true
                this.javaClass == other.javaClass -> return this == other
                this.size != other.size -> return false
                // both empty
                this.isEmpty() -> return true
                else -> {
                    val size = size
                    for (index in 0 until size) {
                        if (this[index] != other[index]) {
                            return false
                        }
                    }
                    return true
                }
            }
        }
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
    override fun <E : Message.Element<E>> getFirst(key: Message.Key<E>): E {
        throw NoSuchElementException("No such element in empty messages")
    }
    
    override fun <E : Message.Element<E>> getFirstOrNull(key: Message.Key<E>): E? = null
    
    override fun get(index: Int): Message.Element<*> {
        throw IndexOutOfBoundsException("'EmptyMessages' has no element.")
    }
    
    override fun plus(element: MsgElement<*>): Messages = element.toMessages()
    override fun plus(messages: Iterable<MsgElement<*>>): Messages = messages.toMessages()
    override fun toString(): String = "EmptyMessages"
}


/**
 * @suppress [SingleOnlyMessage] 已弃用并会在未来被移除，其特性不再有效。
 */
@Deprecated("Deprecated and will be removing in future", level = DeprecationLevel.ERROR)
public abstract class SingleOnlyMessage<E : MsgElement<E>> : MsgElement<E>, Messages, IndexAccessView<MsgElement<*>> {
    abstract override val key: Message.Key<E>
    
    /**
     * 用作 [toString] 展示信息的消息字符串结果。
     */
    protected abstract fun messageString(): String
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
public fun MsgElement<*>.toMessages(): Messages = SingleValueMessageList(this)

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
    var initSize = -1
    
    when (this) {
        is Messages -> return this
        is Collection -> when {
            isEmpty() -> return emptyMessages()
            size == 1 -> return first().toMessages()
            else -> {
                initSize = size
            }
        }
        
        is View -> when {
            isEmpty() -> return emptyMessages()
            else -> {
                val s = size
                if (s == 1) {
                    return first().toMessages()
                }
                initSize = s
            }
        }
    }
    
    val list = buildList(if (initSize > 0) initSize else 8) {
        addAll(this@toMessages)
    }
    
    return if (list.size == 1) SingleValueMessageList(list.first())
    else MessageListImpl(list)
}


/**
 * [MsgElement] 与另外一个 [MsgElement] 进行拼接并组合为 [Messages].
 */
public operator fun MsgElement<*>.plus(other: MsgElement<*>): Messages =
    MessageListImpl(buildList(2) {
        add(this@plus)
        add(other)
    })


/**
 * [MsgElement] 与另外一个 [Messages] 进行拼接并组合为 [Messages].
 *
 * 作为 `receiver` 的 [MsgElement] 会尝试置于首位。
 */
public operator fun MsgElement<*>.plus(other: Messages): Messages =
    when {
        other.isEmpty() -> this.toMessages()
        other is SingleValueMessageList -> MessageListImpl(buildList(2) {
            add(this@plus)
            add(other.value)
        })
        
        other is MessageListImpl -> MessageListImpl(buildList(other.size + 1) {
            add(this@plus)
            addAll(other.list)
        })
        
        else -> CrossMessages(this, other)
    }

/**
 * [Messages] 基础实现, 是元素数量**不应为空**的消息列表。
 */
public sealed class MessageList : Messages, IndexAccessView<MsgElement<*>>

/**
 * 仅存在独立元素内容的 [MessageList] 实现。
 */
private class SingleValueMessageList(val value: MsgElement<*>) : MessageList() {
    override val size: Int get() = 1
    override fun contains(element: MsgElement<*>): Boolean = value == element
    override fun get(index: Int): MsgElement<*> {
        if (index == 0) return value else throw IndexOutOfBoundsException("Index $index of size 1")
    }
    
    override fun <E : Message.Element<E>> getFirst(key: Message.Key<E>): E =
        getFirstOrNull(key) ?: throw NoSuchElementException(key.toString())
    
    override fun <E : Message.Element<E>> getFirstOrNull(key: Message.Key<E>): E? = key.safeCast(value)
    
    override fun <E : MsgElement<E>> get(key: Message.Key<E>): List<E> {
        return key.safeCast(value)?.let { listOf(it) } ?: emptyList()
    }
    
    override fun isEmpty(): Boolean = false
    override fun iterator(): Iterator<MsgElement<*>> = iterator { yield(value) }
    
    override fun plus(element: MsgElement<*>): Messages {
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
            if (messages is MessageListImpl) {
                addAll(messages.list)
            } else {
                addAll(messages)
            }
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

/**
 * 直接使用一个 [List] 对消息链进行描述的 [MessageList] 实现。
 */
private class MessageListImpl(val list: List<MsgElement<*>>) : MessageList() {
    init {
        Simbot.require(list.isNotEmpty()) { "Messages init message list cannot be empty." }
    }
    
    override fun iterator(): Iterator<MsgElement<*>> = list.iterator()
    
    override val size: Int
        get() = list.size
    
    override fun isEmpty(): Boolean = list.isEmpty()
    
    override fun contains(element: MsgElement<*>): Boolean = element in list
    
    override fun get(index: Int): MsgElement<*> = list[index]
    
    override fun <E : Message.Element<E>> getFirst(key: Message.Key<E>): E =
        getFirstOrNull(key) ?: throw NoSuchElementException(key.toString())
    
    override fun <E : Message.Element<E>> getFirstOrNull(key: Message.Key<E>): E? {
        for (element in list) {
            val cast = key.safeCast(element)
            if (cast != null) {
                return cast
            }
        }
        
        return null
    }
    
    override fun <E : MsgElement<E>> get(key: Message.Key<E>): List<E> {
        return mapNotNull { key.safeCast(it) }
    }
    
    /**
     * 拼接元素。
     */
    override fun plus(element: MsgElement<*>): Messages {
        return MessageListImpl(list.toMutableList().apply { add(element) })
    }
    
    /**
     * 拼接元素。
     */
    override fun plus(messages: Iterable<MsgElement<*>>): Messages {
        when {
            messages is Collection && messages.isEmpty() -> return this
            messages is View && messages.isEmpty() -> return this
            messages is SingleValueMessageList -> return plus(messages.value)
            messages is MessageListImpl -> return MessageListImpl(list + messages.list)
            messages is Messages -> CrossMessages(this, messages)
        }
        
        return MessageListImpl(list.toMutableList().apply { addAll(messages) })
    }
    
    override fun toList(): List<MsgElement<*>> = list.toList()
    
    override fun toString(): String = "Messages($list)"
    
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is MessageListImpl) return false
        
        return list == other.list
    }
    
    override fun hashCode(): Int = list.hashCode()
    
}


// TODO
private class CrossMessages(
    private val left: Message,
    private val content: Message,
) : MessageList() {
    override fun <E : Message.Element<E>> get(key: Message.Key<E>): List<E> {
        return buildList {
            fun process(msg: Message) {
                if (msg is Messages) {
                    addAll(msg[key])
                } else {
                    key.safeCast(msg)?.also(::add)
                }
            }
            
            process(left)
            process(content)
        }
    }
    
    private val leftSize = if (left is Messages) left.size else 1
    private val contentSize = if (content is Messages) content.size else 1
    
    override val size: Int = leftSize + contentSize
    
    override fun get(index: Int): MsgElement<*> {
        if (index !in 0 until size) {
            throw IndexOutOfBoundsException("Index $index of size $size")
        }
        
        fun getByIndex(index: Int, msg: Message): MsgElement<*> {
            if (msg is MsgElement<*>) {
                return msg
            }
            return (msg as Messages)[index]
        }
        
        if (index in 0 until leftSize) {
            return getByIndex(index, left)
        }
        return getByIndex(index - leftSize, content)
    }
    
    override fun <E : Message.Element<E>> getFirst(key: Message.Key<E>): E =
        getFirstOrNull(key) ?: throw NoSuchElementException(key.toString())
    
    override fun <E : Message.Element<E>> getFirstOrNull(key: Message.Key<E>): E? {
        for (element in this) {
            val cast = key.safeCast(element)
            if (cast != null) {
                return cast
            }
        }
        
        return null
    }
    
    override fun plus(element: MsgElement<*>): Messages {
        return CrossMessages(this, element)
    }
    
    override fun plus(messages: Iterable<MsgElement<*>>): Messages {
        return CrossMessages(this, messages.toMessages())
    }
    
    override fun toList(): List<MsgElement<*>> {
        return buildList {
            fun process(msg: Message) {
                when (msg) {
                    is MsgElement<*> -> {
                        add(msg)
                    }
                    
                    is Messages -> {
                        addAll(msg)
                    }
                }
            }
            process(left)
            process(content)
        }
    }
    
    override fun iterator(): Iterator<MsgElement<*>> {
        return Itr(left, content)
    }
    
    private class Itr(left: Message, private val content: Message) : Iterator<MsgElement<*>> {
        private var mark: Int = 0
        private var current: Any? = parse(left)
        
        private fun parse(msg: Message): Any = when (msg) {
            is Messages -> msg.iterator()
            is MsgElement<*> -> msg
        }
        
        override fun hasNext(): Boolean {
            val c = current
            
            if (c == null) {
                if (mark == 0) {
                    mark++
                    current = parse(content)
                    return hasNext()
                }
                
                return false
            }
            
            if (c is Iterator<*>) {
                if (!c.hasNext()) {
                    current = null
                    return hasNext()
                }
            }
            
            return true
        }
        
        override fun next(): MsgElement<*> {
            if (current == null) throw NoSuchElementException()
            
            return when (val c = current) {
                is Iterator<*> -> c.next() as MsgElement<*>
                else -> {
                    (c as MsgElement<*>).also {
                        current = null
                    }
                }
            }
        }
    }
    
    override fun isEmpty(): Boolean = false
    
    override fun contains(element: MsgElement<*>): Boolean {
        fun ct(msg: Message): Boolean = if (msg is Messages) msg.contains(element) else element == msg
        
        return ct(left) || ct(content)
    }
    
    override fun toString(): String {
        return "CrossMessages($left, $content)"
    }
}
