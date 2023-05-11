/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.definition

import love.forte.simbot.ID
import love.forte.simbot.JSTP
import love.forte.simbot.bot.Bot
import love.forte.simbot.resources.Resource

/**
 * 定义一个 **容器**。
 *
 * @author ForteScarlet
 */
public interface Container

/**
 * 标记这是一个可能同时存在 suspend property 和 普通api 的容器。
 */
public interface SuspendablePropertyContainer : Container


/**
 * 一个存在唯一标识的容器。
 */
public interface IDContainer : Container {
    
    /**
     * 唯一标识。
     */
    public val id: ID
}

/**
 * 比较两个 [IDContainer] 的 [IDContainer.id] 是否一致。
 *
 * ```kotlin
 * container1 sameIdWith container2
 * ```
 *
 */
public infix fun IDContainer.sameIdWith(other: IDContainer): Boolean = id == other.id


/**
 * 一个存在字符串类型唯一标识的容器。
 */
public interface IdValueContainer : Container {
    
    /**
     * 唯一标识字符串。
     */
    public val id: String
}

/**
 * 比较两个 [IdValueContainer] 的 [IdValueContainer.id] 是否一致。
 *
 * ```kotlin
 * container1 sameIdWith container2
 * ```
 *
 */
public infix fun IdValueContainer.sameIdWith(other: IdValueContainer): Boolean = id == other.id


/**
 * 一个 **Bot容器**.
 * 一般代表可以得到 [Bot] 的对象，例如非 [Bot] 的 [User]，比如 [Friend]，一个Bot所提供的事件，或者一个BOT所在的组织集会。
 *
 */
public interface BotContainer : Container {
    
    /**
     * 当前bot
     */
    public val bot: Bot
}

/**
 * 存在 [ChannelInfo] 的容器。
 */
public interface ChannelInfoContainer : SuspendablePropertyContainer {
    /**
     * 当前 [channel][ChannelInfo].
     */
    @JSTP
    public suspend fun channel(): ChannelInfo
}

/**
 * 将 [ChannelInfoContainer] 中的 [channel][ChannelInfoContainer.channel] 作为 [block] 中的接收者使用。
 */
public suspend inline fun <R> ChannelInfoContainer.inChannelInfo(block: ChannelInfo.() -> R): R = channel().let(block)

/**
 * 将 [ChannelInfoContainer] 中的 [channel][ChannelInfoContainer.channel] 作为 [block] 中的参数使用。
 */
public suspend inline fun <R> ChannelInfoContainer.useChannelInfo(block: (ChannelInfo) -> R): R = channel().let(block)


/**
 * 存在 [ChannelInfo] 的容器。
 */
public interface GuildInfoContainer : SuspendablePropertyContainer {
    /**
     * 当前 [guild][GuildInfo].
     */
    @JSTP
    public suspend fun guild(): GuildInfo
}

/**
 * 将 [GuildInfoContainer] 中的 [guild][GuildInfoContainer.guild] 作为 [block] 中的接收者使用。
 */
public suspend inline fun <R> GuildInfoContainer.inGuildInfo(block: GuildInfo.() -> R): R = guild().let(block)

/**
 * 将 [GuildInfoContainer] 中的 [guild][GuildInfoContainer.guild] 作为 [block] 中的参数使用。
 */
public suspend inline fun <R> GuildInfoContainer.useGuildInfo(block: (GuildInfo) -> R): R = guild().let(block)


/**
 * 存在 [GroupInfo] 的容器。
 */
public interface GroupInfoContainer : SuspendablePropertyContainer {
    /**
     * 当前 [group][GroupInfo].
     */
    @JSTP
    public suspend fun group(): GroupInfo
}

/**
 * 将 [GroupInfoContainer] 中的 [group][GroupInfoContainer.group] 作为 [block] 中的接收者使用。
 */
public suspend inline fun <R> GroupInfoContainer.inGroupInfo(block: GroupInfo.() -> R): R = group().let(block)

/**
 * 将 [GroupInfoContainer] 中的 [group][GroupInfoContainer.group] 作为 [block] 中的参数使用。
 */
public suspend inline fun <R> GroupInfoContainer.useGroupInfo(block: (GroupInfo) -> R): R = group().let(block)


/**
 * 存在 [UserInfo] 的容器。
 */
public interface UserInfoContainer : SuspendablePropertyContainer {
    
    /**
     * 当前 [user][UserInfo]
     */
    @JSTP
    public suspend fun user(): UserInfo
}

/**
 * 将 [UserInfoContainer] 中的 [user][UserInfoContainer.user] 作为 [block] 中的接收者使用。
 */
public suspend inline fun <R> UserInfoContainer.inUserInfo(block: UserInfo.() -> R): R = user().let(block)

/**
 * 将 [UserInfoContainer] 中的 [user][UserInfoContainer.user] 作为 [block] 中的参数使用。
 */
public suspend inline fun <R> UserInfoContainer.useUserInfo(block: (UserInfo) -> R): R = user().let(block)


/**
 * 存在 [MemberInfo] 的容器。
 */
public interface MemberInfoContainer : SuspendablePropertyContainer {
    /**
     * 当前 [member][MemberInfo].
     */
    @JSTP
    public suspend fun member(): MemberInfo
}

/**
 * 将 [MemberInfoContainer] 中的 [member][MemberInfoContainer.member] 作为 [block] 中的接收者使用。
 */
public suspend inline fun <R> MemberInfoContainer.inMemberInfo(block: MemberInfo.() -> R): R = member().let(block)

/**
 * 将 [MemberInfoContainer] 中的 [member][MemberInfoContainer.member] 作为 [block] 中的参数使用。
 */
public suspend inline fun <R> MemberInfoContainer.useMemberInfo(block: (MemberInfo) -> R): R = member().let(block)


/**
 * 存在 [FriendInfo] 的容器。
 */
public interface FriendInfoContainer : SuspendablePropertyContainer {
    /**
     * 当前 [friend][FriendInfo].
     */
    @JSTP
    public suspend fun friend(): FriendInfo
}

/**
 * 将 [FriendInfoContainer] 中的 [friend][FriendInfoContainer.friend] 作为 [block] 中的接收者使用。
 */
public suspend inline fun <R> FriendInfoContainer.inFriendInfo(block: FriendInfo.() -> R): R = friend().let(block)

/**
 * 将 [FriendInfoContainer] 中的 [friend][FriendInfoContainer.friend] 作为 [block] 中的参数使用。
 */
public suspend inline fun <R> FriendInfoContainer.useFriendInfo(block: (FriendInfo) -> R): R = friend().let(block)


/**
 * 存在 [Resource] 的容器。
 */
public interface ResourceContainer : SuspendablePropertyContainer {
    
    /**
     * 得到当前容器中的 [资源][Resource].
     */
    @JSTP
    public suspend fun resource(): Resource
    
}

/**
 * 使用 [ResourceContainer] 中的 [ResourceContainer.resource], 并在 [block] 结束后关闭它。
 *
 */
public suspend inline fun <R> ResourceContainer.useResource(block: (Resource) -> R): R = resource().use(block)
