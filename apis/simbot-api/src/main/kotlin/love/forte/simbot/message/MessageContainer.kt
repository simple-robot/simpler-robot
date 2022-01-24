/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

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