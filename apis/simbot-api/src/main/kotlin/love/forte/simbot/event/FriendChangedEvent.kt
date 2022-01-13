/*
 *  Copyright (c) 2022-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.event

import love.forte.simbot.Bot
import love.forte.simbot.definition.FriendInfo


/**
 * 一个 **好友增加** 事件。
 *
 * 这代表的是好友 **已经** 被添加的事件，不同于 [UserRequestEvent]
 *
 *
 */
public interface FriendIncreaseEvent : IncreaseEvent<Bot, FriendInfo> {

}