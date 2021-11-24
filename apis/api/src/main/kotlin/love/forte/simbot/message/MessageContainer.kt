package love.forte.simbot.message


/**
 * 一个消息容器，代表了一个承载着消息的内容。
 *
 * 常见有消息事件，和可以查询的历史消息。
 *
 * @author ForteScarlet
 */
public interface MessageContainer {

    /**
     * 消息内容。
     */
    public val messageContent: MessageContent

}

//
public interface RemoteMessageContainer : MessageContainer {
    override val messageContent: RemoteMessageContent
}