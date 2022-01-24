/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
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