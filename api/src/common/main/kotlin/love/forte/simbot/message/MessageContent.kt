package love.forte.simbot.message

/**
 * 一个消息内容，其中存在一个 [消息元数据][Message.Metadata] 和一个[消息链][Messages]。
 *
 */
public sealed class MessageContent {

    /**
     * 接收到的消息的 [元数据][Message.Metadata].
     */
    public abstract val metadata: Message.Metadata

    /**
     * 接收到的消息链
     */
    public abstract val messages: Messages

}


/**
 * 一个接收到的 [MessageContent], 即事件中的 [MessageContent].
 */
public abstract class ReceivedMessageContent : MessageContent()
