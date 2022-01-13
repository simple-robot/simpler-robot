/*
 *  Copyright (c) 2021-2022 ForteScarlet <https://github.com/ForteScarlet>
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

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.Bot

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
 * 一个 **Bot容器**.
 * 一般代表可以得到 [Bot] 的对象，例如非 [Bot] 的 [User]，比如 [Friend]，一个Bot所提供的事件，或者一个BOT所在的组织集会。
 *
 */
public interface BotContainer : Container {
    public val bot: Bot
}

/**
 * 存在 [ChannelInfo] 的容器。
 */
public interface ChannelInfoContainer : SuspendablePropertyContainer {
    public suspend fun channel(): ChannelInfo
    @Api4J public val channel: ChannelInfo get() = runBlocking { channel() }
}

/**
 * 存在 [ChannelInfo] 的容器。
 */
public interface GuildInfoContainer : SuspendablePropertyContainer {
    public suspend fun guild(): GuildInfo
    @Api4J public val guild: GuildInfo get() = runBlocking { guild() }
}

/**
 * 存在 [GroupInfo] 的容器。
 */
public interface GroupInfoContainer : SuspendablePropertyContainer {
    public suspend fun group(): GroupInfo
    @Api4J public val group: GroupInfo get() = runBlocking { group() }
}

/**
 * 存在 [UserInfo] 的容器。
 */
public interface UserInfoContainer : SuspendablePropertyContainer {
    public suspend fun user(): UserInfo
    @Api4J public val user: UserInfo get() = runBlocking { user() }
}

/**
 * 存在 [MemberInfo] 的容器。
 */
public interface MemberInfoContainer : SuspendablePropertyContainer {
    public suspend fun member(): MemberInfo
    @Api4J public val member: MemberInfo get() = runBlocking { member() }
}

/**
 * 存在 [FriendInfo] 的容器。
 */
public interface FriendInfoContainer : SuspendablePropertyContainer {
    public suspend fun friend(): FriendInfo
    @Api4J public val friend: FriendInfo get() = runBlocking { friend() }
}

