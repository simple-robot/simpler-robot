@file:JvmMultifileClass
@file:JvmName("MessageUtil")

package love.forte.simbot.api.message

import love.forte.simbot.Simbot
import love.forte.simbot.api.Component
import love.forte.simbot.api.SimbotComponent
import love.forte.simbot.exception.SimbotRuntimeException
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName


/**
 * 消息列表，即 [AbsoluteMessage] 的列表，连接多条消息的链表。
 *
 * 在此链表所有唯一消息中，相同 [UniqueMessage.key] 的消息理应只存在一个。
 *
 * 当在追加元素的时候，出现了类型冲突，则会根据不同策略进行替换、合并或舍弃。
 *
 * 当然，舍弃指的是舍弃其他所有，留下追加元素。
 *
 *
 */
public sealed interface Messages : Collection<AbsoluteMessage>, RandomAccess, Message {

    /**
     * 这串消息所属组件。在消息链中，所有的消息都应属于同一个组件。
     */
    override val component: Component


    /**
     * 向后拼接一个 [AbsoluteMessage], 并得到一个新的实例。
     */
    public operator fun plus(absoluteMessage: AbsoluteMessage): Messages

    /**
     * 拼接一个 [messages], 并得到一个 **新的** 实例。
     */
    public operator fun plus(messages: Messages): Messages

    /**
     * 是否包含某个元素。
     */
    override fun contains(element: AbsoluteMessage): Boolean

    /**
     * 是否包含某个 [UniqueMessage].
     */
    public fun contains(key: Message.Key<*>): Boolean = this.any { it is UniqueMessage<*> && it.key == key }

    /**
     * 根据唯一 key 寻找对应的 [UniqueMessage] 实例。
     */
    @Suppress("UNCHECKED_CAST")
    public operator fun <M : AbsoluteMessage> get(key: Message.Key<M>): M? = this.find { it is UniqueMessage<*> && it.key == key } as? M
}


/**
 * 将一个 [AbsoluteMessage] 首部拼接一个 [Messages].
 */
public operator fun AbsoluteMessage.plus(messages: Messages): Messages {
    return when (messages) {
        // 目标是空的
        EmptyMessages -> toMessages()
        // 目标不可协同
        is SingleOnlyMessage -> messages
        // 合并
        else -> toMessages() + messages
    }
}

public operator fun AbsoluteMessage.plus(absoluteMessage: AbsoluteMessage): Messages = messages(this, absoluteMessage)



/**
 * 得到一个空的消息列表。
 */
public fun emptyMessages(): Messages = EmptyMessages


/**
 * 没有任何元素的 [Messages]. 在追加列表时，总是会直接替换为后者。
 */
public object EmptyMessages : Messages {
    override val component: Component = SimbotComponent
    override val size: Int get() = 0
    override fun contains(element: AbsoluteMessage): Boolean = false
    override fun containsAll(elements: Collection<AbsoluteMessage>): Boolean = false
    override fun isEmpty(): Boolean = true
    override fun iterator(): Iterator<AbsoluteMessage> = emptyList<AbsoluteMessage>().iterator()
    override fun plus(absoluteMessage: AbsoluteMessage): Messages = absoluteMessage.toMessages()
    override fun plus(messages: Messages): Messages = messages
    override fun contains(key: Message.Key<*>): Boolean = false
    override fun <M : AbsoluteMessage> get(key: Message.Key<M>): M? = null
}


/**
 * **仅** 允许一个单个元素的 [Messages]. 一般由其他的 [AbsoluteMessage] 实现，代表此消息只能独自存在。
 * 在追加其他任何元素的时候，会直接替换为后者。
 *
 */
public abstract class SingleOnlyMessage : Messages {
    /**
     * 对应的唯一消息。
     */
    public abstract val singleMessage: UniqueMessage<*>

    // List
    final override val size: Int get() = 1
    final override fun contains(element: AbsoluteMessage): Boolean = element == singleMessage
    final override fun containsAll(elements: Collection<AbsoluteMessage>): Boolean = elements.any { contains(it) }
    final override fun isEmpty(): Boolean = false
    final override fun iterator(): Iterator<AbsoluteMessage> = iterator { singleMessage }
    final override fun plus(absoluteMessage: AbsoluteMessage): Messages = messages(absoluteMessage)
    final override fun plus(messages: Messages): Messages = messages
    override fun contains(key: Message.Key<*>): Boolean = singleMessage.key == key
    @Suppress("UNCHECKED_CAST")
    override fun <M : AbsoluteMessage> get(key: Message.Key<M>): M? = if (contains(key)) singleMessage as M else null
}


public fun messages(): Messages = EmptyMessages
public fun AbsoluteMessage.toMessages(): Messages = MessagesImpl(component, listOf(this))
public fun messages(vararg messages: AbsoluteMessage): Messages = messages.asList().toMessages()

public fun List<AbsoluteMessage>.toMessages(): Messages {
    if (this.isEmpty()) return emptyMessages()
    if (size == 1) return first().toMessages()

    val list = toMutableList()
    val component: Component = first().component

    forEachIndexed { i, message ->
        Simbot.check(message.component == component) { "All components in Messages must be consistent. The first was $component, but the element in $i was ${message.component} ." }
        if (i == 0) {
            list.add(message)
            return@forEachIndexed
        }





    }

    // TODO solve


    TODO()
    // return MessagesImpl(list)
}


/**
 * [Messages] 基础实现。
 *
 */
internal class MessagesImpl
/*
 * delegate 的内容不进行验证，通过顶层函数进行solve. 基本上, delegate 不允许为空。
 */
internal constructor(override val component: Component, private val delegate: List<AbsoluteMessage>) : Messages, Collection<AbsoluteMessage> {
    init {
        Simbot.check(delegate.isEmpty()) { "Messages init message list cannot be empty." }
    }

    override val size: Int get() = delegate.size

    override fun contains(element: AbsoluteMessage): Boolean {
        TODO("Not yet implemented")
    }

    override fun containsAll(elements: Collection<AbsoluteMessage>): Boolean {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean = delegate.isEmpty()
    override fun iterator(): Iterator<AbsoluteMessage> = delegate.iterator()


    override fun plus(absoluteMessage: AbsoluteMessage): Messages {
        TODO("Not yet implemented")
    }

    override fun plus(messages: Messages): Messages {
        TODO("Not yet implemented")
    }

}