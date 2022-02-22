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
 *
 */

package love.forte.simbot.definition

import love.forte.simbot.Api4J
import love.forte.simbot.Bot
import love.forte.simbot.ID
import love.forte.simbot.utils.runInBlocking

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
    public suspend fun channel(): ChannelInfo

    /**
     * 当前 [channel][ChannelInfo].
     */
    @Api4J public val channel: ChannelInfo get() = runInBlocking { channel() }
}

/**
 * 存在 [ChannelInfo] 的容器。
 */
public interface GuildInfoContainer : SuspendablePropertyContainer {
    /**
     * 当前 [guild][GuildInfo].
     */
    public suspend fun guild(): GuildInfo

    /**
     * 当前 [guild][GuildInfo].
     */
    @Api4J public val guild: GuildInfo get() = runInBlocking { guild() }
}

/**
 * 存在 [GroupInfo] 的容器。
 */
public interface GroupInfoContainer : SuspendablePropertyContainer {
    /**
     * 当前 [group][GroupInfo].
     */
    public suspend fun group(): GroupInfo
    /**
     * 当前 [group][GroupInfo].
     */
    @Api4J public val group: GroupInfo get() = runInBlocking { group() }
}

/**
 * 存在 [UserInfo] 的容器。
 */
public interface UserInfoContainer : SuspendablePropertyContainer {

    /**
     * 当前 [user][UserInfo]
     */
    public suspend fun user(): UserInfo


    /**
     * 当前 [user][UserInfo]
     */
    @Api4J public val user: UserInfo get() = runInBlocking { user() }
}

/**
 * 存在 [MemberInfo] 的容器。
 */
public interface MemberInfoContainer : SuspendablePropertyContainer {
    /**
     * 当前 [member][MemberInfo].
     */
    public suspend fun member(): MemberInfo

    /**
     * 当前 [member][MemberInfo].
     */
    @Api4J public val member: MemberInfo get() = runInBlocking { member() }
}

/**
 * 存在 [FriendInfo] 的容器。
 */
public interface FriendInfoContainer : SuspendablePropertyContainer {
    /**
     * 当前 [friend][FriendInfo].
     */
    public suspend fun friend(): FriendInfo

    /**
     * 当前 [friend][FriendInfo].
     */
    @Api4J public val friend: FriendInfo get() = runInBlocking { friend() }
}

