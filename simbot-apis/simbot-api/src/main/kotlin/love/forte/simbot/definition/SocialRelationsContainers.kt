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

package love.forte.simbot.definition

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.utils.item.Items
import love.forte.simbot.bot.Bot

/**
 * 社交关系容器。
 *
 * 通常与 [Bot] 的社交关系相关，大部分子类型也都通过 [Bot] 默认实现。
 *
 * @see Bot
 */
public sealed interface SocialRelationsContainer : SuspendablePropertyContainer


/**
 * 提供获取 [Friend] 的容器。
 *
 * 通常应用于 [Bot] 中为其提供获取 [Friend] 相关的属性api。
 *
 * [Bot] 或许会存在一些 "好友" 对象，但是默认情况下 [Bot] 不会默认实现 [FriendsContainer]，
 * 取而代之的是 [ContactsContainer]。当组件存在支持 [Friend] 相关操作的时候可以进行额外实现。
 *
 * "好友"并 _**不一定**_ 代表那些需要 "添加申请"、"同意" 后出现在好友列表中的好友 ——
 * 并非所有的组件都支持这种“好友”的概念。
 *
 * 对于一个以"频道"概念为主的组件就是最常见的例子 —— 它们通常没有真正的"好友"概念，
 * 至少对于bot来讲没有。取而代之的则通常是"频道成员"或者一个"会话"。
 *
 * 实际上，对于一个bot来讲"好友"的概念确实可有可无，它更需要"[联系人][Contact]"。
 *
 * 在一个容器同时支持 [FriendsContainer] 和 [ContactsContainer]
 * 的情况下，[FriendsContainer] 中能够得到的目标常常为 [ContactsContainer]
 * 的子集。
 *
 * @see ContactsContainer
 *
 */
public interface FriendsContainer : SocialRelationsContainer {
    
    /**
     * 得到此容器下的好友序列。
     *
     */
    public val friends: Items<Friend>
    
    
    /**
     * 通过唯一标识获取这个容器对应的某个好友，获取不到则为null。
     *
     * @param id 好友的唯一标识
     */
    @JvmBlocking(baseName = "getFriend", suffix = "")
    @JvmAsync(baseName = "getFriend", suffix = "")
    public suspend fun friend(id: ID): Friend?
    
}


/**
 * 提供获取 [Contact] 的容器。
 *
 * 通常应用于 [Bot] 中为其提供获取 [Contact] 相关的属性api。
 *
 * [联系人][Contact] 通常代表为与当前容器存在"会话"的联系人，
 * 它们之间必须存在一种硬性关系（例如它们之间是 [好友][Friend] 关系）
 * 或者存在一个被创建过的"会话"（例如某联系人主动与bot进行过交流或者
 * 与当前容器创建过与某个目标的会话）。
 *
 * 因上述约束，[ContactsContainer.contacts] 通常不具备检索 组织成员 [Member]
 * 这类间接联系人的能力, 尽管 [Member] 也属于 [Contact] 类型 ———— 除非它们与当前容器存在可查会话。
 *
 * 当一个bot中，所有可能的联系人都是与bot存在硬性关系（例如它们之间是 [好友][Friend] 关系）的时候，
 * [ContactsContainer] 的表现将会与 [FriendsContainer] 类似。
 *
 */
public interface ContactsContainer : SocialRelationsContainer {
    
    /**
     * 得到当前容器中能够获取到的联系人序列。
     *
     * 联系人序列不能保证结果为 **_预期内的_ 全量** 序列，尤其是对于一个 [Bot] 而言。
     * 因为站在容器对于"联系人"的角度上来说，[Contact] 大多数情况下代表为一个"会话"。
     * 如果一个联系人与当前容器从未构建过任何会话，
     * 且二者之间没有硬性的"羁绊"（例如互为 [好友][Friend]），
     * 则 [contacts] 大概率无法包含这个"未曾交流过的"联系人。
     *
     * 在组件支持的情况下，[contact] 能够在 [contacts] 中不存在目标的情况下主动创建二者之间的会话。
     * 而此时 [contacts] 所得到的序列中也会有概率包含这些创建出的会话结果。
     *
     * 因此如果只是为了寻找部分具有**明确标识**的目标，考虑通过 [contact] 进行获取。
     *
     */
    public val contacts: Items<Contact>
    
    
    /**
     * 通过唯一标识获取对应的 [Contact] 实例。当且仅当因标识对应联系人不存在而导致无法获取时得到null。
     *
     * 在一些组件支持的情况下，[contact] 会在目标联系人会话不存在的时候尝试主动创建会话。
     * 假若这个过程中发生了预期内的错误（例如网络请求错误、[id] 格式错误等）时，
     * 不会视为"未找到联系人"，因此会抛出对应异常而不是返回null。
     *
     * @param id 目标唯一标识
     */
    @JvmBlocking(baseName = "getContact", suffix = "")
    @JvmAsync(baseName = "getContact", suffix = "")
    public suspend fun contact(id: ID): Contact?
}


/**
 * 提供获取 [Group] 的容器。
 *
 * 通常应用于 [Bot] 中为其提供获取 [Group] 相关的属性api。
 *
 */
public interface GroupsContainer : SocialRelationsContainer {
    
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
    @JvmBlocking(baseName = "getGroup", suffix = "")
    @JvmAsync(baseName = "getGroup", suffix = "")
    public suspend fun group(id: ID): Group?
}


/**
 * 提供获取 [Guild] 的容器。
 *
 * 通常应用于 [Bot] 中为其提供获取 [Guild] 相关属性的api。
 *
 */
public interface GuildsContainer : SocialRelationsContainer {
    
    /**
     * 获取当前的所有频道服务器序列。
     */
    public val guilds: Items<Guild>
    
    
    /**
     * 通过唯一标识获取这个bot对应的某个频道，获取不到则为null。
     *
     * @param id 频道服务器唯一标识
     */
    @JvmBlocking(baseName = "getGuild", suffix = "")
    @JvmAsync(baseName = "getGuild", suffix = "")
    public suspend fun guild(id: ID): Guild?
}


