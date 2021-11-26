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

package love.forte.simbot.definition

import love.forte.simbot.Bot
import love.forte.simbot.action.MessageSendSupport
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReceipt


// 联系人
/**
 *
 * 一个联系目标。联系人是除 [Bot] 以外的可以进行信息交流的 [User], 例如好友, 一个群成员, 或者一个文字频道。
 *
 * 但是并不是 [Bot] 以外的所有人都可以进行信息交流，比如对于一个群成员，可能会受限于权限，或者受限于组织类型（例如一个订阅型组织，参考tg的频道）.
 *
 * @author ForteScarlet
 */
public interface Contact : User, MessageSendSupport, BotContainer {

    /**
     * 联系人所属的 [Bot].
     */
    override val bot: Bot


    /**
     * 向此联系目标发送消息。
     */
    override suspend fun send(message: Message): MessageReceipt

}


