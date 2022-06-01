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
 */

package love.forte.simbot

import love.forte.simbot.definition.Friend
import love.forte.simbot.definition.Group
import love.forte.simbot.definition.Guild
import love.forte.simbot.definition.SuspendablePropertyContainer
import love.forte.simbot.utils.item.Items


/**
 * 与 [Bot] 的社交关系相关的容器。
 *
 */
public sealed interface BotSocialRelationsContainer : SuspendablePropertyContainer


/**
 * 应用于 [Bot] 中为其提供获取 [Friend] 相关的属性api。
 *
 * [Bot] 或许会存在一些 "好友" 对象。
 *
 * "好友"并_**不一定**_代表那些需要 "添加申请"、"同意" 后出现在好友列表中的好友 ——
 * 并非所有的组件都支持这种“好友”的概念。
 *
 * 对于一个以"频道"概念为主的组件就是最常见的例子 —— 它们通常没有真正的"好友"概念，
 * 至少对于bot来讲没有。取而代之的则通常是"频道成员"或者一个"会话"。
 *
 * 实际上，对于一个bot来讲"好友"的概念确实可有可无，它更需要"联系人"。目前阶段将会暂时保留
 * "[好友][Friend]" 容器，但是在未来可能会被标记过时并由其他概念代替。
 *
 *
 */
public interface FriendsContainer : BotSocialRelationsContainer {
    
    /**
     * 根据分组和限流信息得到此bot下的好友序列。
     *
     */
    public val friends: Items<Friend>
    
    
    /**
     * 通过唯一标识获取这个bot对应的某个好友，获取不到则为null。
     *
     * @param id 好友的唯一标识
     */
    @JvmSynthetic
    public suspend fun friend(id: ID): Friend?
    
    
    /**
     * 通过唯一标识获取这个bot对应的某个好友，获取不到则为null。
     *
     * @param id 好友的唯一标识
     *
     * @see friend
     */
    @Api4J
    public fun getFriend(id: ID): Friend?
    
    
}


/**
 * 应用于 [Bot] 中为其提供获取 [Group] 相关的属性api。
 *
 */
public interface GroupsContainer : BotSocialRelationsContainer {
    
    /**
     * 获取当前bot所处的群序列。
     *
     */
    public val groups: Items<Group>
    
    /**
     * 通过唯一标识获取这个bot对应的某个群，获取不到则为null。
     *
     * @param id 目标群唯一标识
     */
    @JvmSynthetic
    public suspend fun group(id: ID): Group?
    
    /**
     * 通过唯一标识获取这个bot对应的某个群，获取不到则为null。
     *
     * @param id 目标群唯一标识
     *
     * @see group
     */
    @Api4J
    public fun getGroup(id: ID): Group?
    
}


/**
 * 应用于 [Bot] 中为其提供获取 [Guild] 相关属性的api。
 *
 */
public interface GuildsContainer : BotSocialRelationsContainer {
    
    /**
     * 获取当前的所有频道服务器序列。
     */
    public val guilds: Items<Guild>
    
    
    /**
     * 通过唯一标识获取这个bot对应的某个频道，获取不到则为null。
     *
     * @param id 频道服务器唯一标识
     */
    @JvmSynthetic
    public suspend fun guild(id: ID): Guild?
    
    /**
     * 通过唯一标识获取这个bot对应的某个频道，获取不到则为null。
     *
     * @param id 频道服务器唯一标识
     *
     * @see guild
     */
    @Api4J
    public fun getGuild(id: ID): Guild?
    
}


