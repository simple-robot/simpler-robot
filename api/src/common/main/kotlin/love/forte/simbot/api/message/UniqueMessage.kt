package love.forte.simbot.api.message



// UniqueMessage

/**
 * 唯一的 [Message], 代表在 [MessageList] 中, 如果
 *
 */
public interface UniqueMessage : AbsoluteMessage {
    public val key: Message.Key<*>

}








/**
 * 一个可合并的消息。[MergeableMessage] 在 消息列表中的时候，如果添加了相互冲突的消息，则会将其进行合并。
 *
 * `| M1 | M2 | M3 | + M2.1 -> | M1 | (M2 + M2.1) | M3`
 *
 * 合并有多种可能，一是真正的合并，其他的则有可能为直接替换为后来者等等。
 *
 */
public interface MergeableMessage : UniqueMessage {
    override val key: Message.Key<out AbsoluteMessage>
}