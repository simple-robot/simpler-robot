/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

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
