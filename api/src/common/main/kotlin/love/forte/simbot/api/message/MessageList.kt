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
public sealed class MessageList : Collection<AbsoluteMessage>, RandomAccess, Message {


    public operator fun plus(message: Message): MessageList {
        TODO()
    }

}


/**
 * 得到一个空的消息列表。
 */
public fun emptyMessageList(): MessageList = EmptyMessageList

private object EmptyMessageList : MessageList() {
    override val size: Int get() = 0
    override fun contains(element: AbsoluteMessage): Boolean= false
    override fun containsAll(elements: Collection<AbsoluteMessage>): Boolean = false
    override fun isEmpty(): Boolean = true
    override fun iterator(): Iterator<AbsoluteMessage> = emptyList<AbsoluteMessage>().iterator()
}


