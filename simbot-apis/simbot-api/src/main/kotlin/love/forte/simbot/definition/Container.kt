/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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
 */

package love.forte.simbot.definition

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.ID
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
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
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
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
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
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
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
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
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
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
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
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
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
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    public suspend fun resource(): Resource
}

/**
 * 使用 [ResourceContainer] 中的 [ResourceContainer.resource], 并在 [block] 结束后关闭它。
 *
 */
public suspend inline fun <R> ResourceContainer.useResource(block: (Resource) -> R): R = resource().use(block)
