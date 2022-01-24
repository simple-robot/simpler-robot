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

package love.forte.simboot.annotation

import love.forte.simbot.event.*
import kotlin.annotation.AnnotationTarget.FUNCTION

/**
 * @see FriendIncreaseEvent
 */
@Target(FUNCTION)
@Listen(FriendIncreaseEvent::class)
@MustBeDocumented
public annotation class OnFriendIncrease

/**
 * @see FriendDecreaseEvent
 */
@Target(FUNCTION)
@Listen(FriendDecreaseEvent::class)
@MustBeDocumented
public annotation class OnFriendDecrease


/**
 * @see MemberIncreaseEvent
 */
@Target(FUNCTION)
@Listen(MemberIncreaseEvent::class)
@MustBeDocumented
public annotation class OnMemberIncrease

/**
 * @see MemberDecreaseEvent
 */
@Target(FUNCTION)
@Listen(MemberDecreaseEvent::class)
@MustBeDocumented
public annotation class OnMemberDecrease


/**
 * @see ContactMessageEvent
 */
@Target(FUNCTION)
@Listen(ContactMessageEvent::class)
@MustBeDocumented
public annotation class OnContactMessage

/**
 * @see FriendMessageEvent
 */
@Target(FUNCTION)
@Listen(FriendMessageEvent::class)
@MustBeDocumented
public annotation class OnFriendMessage

/**
 * @see ChatroomMessageEvent
 */
@Target(FUNCTION)
@Listen(ChatroomMessageEvent::class)
@MustBeDocumented
public annotation class OnChatroomMessage

/**
 * @see GroupMessageEvent
 */
@Target(FUNCTION)
@Listen(GroupMessageEvent::class)
@MustBeDocumented
public annotation class OnGroupMessage

/**
 * @see ChannelMessageEvent
 */
@Target(FUNCTION)
@Listen(ChannelMessageEvent::class)
@MustBeDocumented
public annotation class OnChannelMessage

/**
 * @see MessageReactedEvent
 */
@Target(FUNCTION)
@Listen(MessageReactedEvent::class)
@MustBeDocumented
public annotation class OnMessageReacted


/**
 * @see JoinRequestEvent
 */
@Target(FUNCTION)
@Listen(JoinRequestEvent::class)
@MustBeDocumented
public annotation class OnJoinRequest

/**
 * @see GuildJoinRequestEvent
 */
@Target(FUNCTION)
@Listen(GuildJoinRequestEvent::class)
@MustBeDocumented
public annotation class OnGuildJoinRequest

/**
 * @see GroupJoinRequestEvent
 */
@Target(FUNCTION)
@Listen(GroupJoinRequestEvent::class)
@MustBeDocumented
public annotation class OnGroupJoinRequest

/**
 * @see ChannelRequestEvent
 */
@Target(FUNCTION)
@Listen(ChannelRequestEvent::class)
@MustBeDocumented
public annotation class OnChannelRequest

/**
 * @see FriendAddRequestEvent
 */
@Target(FUNCTION)
@Listen(FriendAddRequestEvent::class)
@MustBeDocumented
public annotation class OnFriendAddRequest