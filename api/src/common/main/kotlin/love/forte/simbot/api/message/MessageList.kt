@file:JvmMultifileClass
@file:JvmName("MessageUtil")

package love.forte.simbot.api.message

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName


/**
 * 消息列表，即 [AbsoluteMessage] 的列表，连接多条消息的链表。
 *
 * 在此链表中，相同 [Key][AbsoluteMessage.key] 的消息理应只存在一个。
 *
 * 当在追加元素的时候，出现了类型冲突，则会根据不同策略进行替换、合并或舍弃。
 *
 * 当然，舍弃指的是舍弃其他所有，留下追加元素。
 *
 *
 */
public interface MessageList : Collection<AbsoluteMessage>, RandomAccess, Message {


    public operator fun plus(absoluteMessage: AbsoluteMessage): MessageList {
        TODO()
    }

    public operator fun plus(messageList: MessageList): MessageList {
        TODO()
    }

}


/**
 * 得到一个空的消息列表。
 */
public fun emptyMessageList(): MessageList = EmptyMessageList

/**
 * 没有任何元素的 [MessageList]. 在追加列表时，总是会直接替换为后者。
 */
internal object EmptyMessageList : MessageList {
    override val size: Int get() = 0
    override fun contains(element: AbsoluteMessage): Boolean = false
    override fun containsAll(elements: Collection<AbsoluteMessage>): Boolean = false
    override fun isEmpty(): Boolean = true
    override fun iterator(): Iterator<AbsoluteMessage> = emptyList<AbsoluteMessage>().iterator()
}


/**
 * **仅** 允许一个单个元素的 [MessageList]. 一般由其他的 [AbsoluteMessage] 实现，代表此消息只能独自存在。
 * 在追加其他任何元素的时候，会直接替换为后者。
 */
public abstract class SingleOnlyMessage : MessageList {
    /**
     * 对应的唯一消息。
     */
    public abstract val singleMessage: AbsoluteMessage

    // List
    final override val size: Int get() = 1
    final override fun contains(element: AbsoluteMessage): Boolean = element == singleMessage
    final override fun containsAll(elements: Collection<AbsoluteMessage>): Boolean = elements.any { contains(it) }
    final override fun isEmpty(): Boolean = false
    final override fun iterator(): Iterator<AbsoluteMessage> = iterator { singleMessage }
}

