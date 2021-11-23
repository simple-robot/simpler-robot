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

package love.forte.simbot

import kotlinx.coroutines.flow.Flow
import love.forte.simbot.definition.Friend
import love.forte.simbot.definition.User
import love.forte.simbot.definition.UserInfo


/**
 *
 * 一个 [Bot]. 同时, [Bot] 也属于一个用户 [User]。
 *
 * @author ForteScarlet
 */
public interface Bot : User {
    /**
     * 每个bot都肯定会由一个 [BotManager] 进行管理。
     *
     */
    public val manager: BotManager<Bot>

    /**
     * 每个Bot都有一个所属组件。
     *
     */
    public val component: Component

    // info
    /**
     * 得到此Bot的一些基础信息。
     */
    override val info: BotInfo


    // friends
    /**
     * 根据分组和限流信息得到此bot下的好友列表。
     *
     * 分组不一定存在，限流器也不一定生效，这两个参数的有效情况取决于当前 [Bot] 的实现情况。
     *
     */
    public suspend fun friends(grouping: Grouping, limiter: Limiter): Flow<Friend>


    // organizations




    // other..?

}

/**
 * 一个Bot的信息。同时其也属于一个 [UserInfo].
 */
public interface BotInfo : UserInfo {
    override val id: ID
    override val avatar: String
    override val username: String
}