/*
 * Copyright (c) 2022-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.simbot.definition

import kotlinx.coroutines.flow.count
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.ID
import love.forte.simbot.bot.Bot
import love.forte.simbot.utils.item.Items

/**
 * 社交关系容器。
 *
 * 通常与 [Bot] 的社交关系相关，大部分子类型也都通过 [Bot] 默认实现。
 *
 * @see Bot
 * @see FriendsContainer
 * @see ContactsContainer
 * @see GroupsContainer
 * @see GuildsContainer
 */
public sealed interface SocialRelationsContainer : SuspendablePropertyContainer


/**
 * 提供获取 [Friend] 的容器。
 *
 * 通常应用于 [Bot] 中为其提供获取 [Friend] 相关的属性api。
 *
 * [Bot] 或许会存在一些 "好友" 对象，但是 [Bot] 不会默认实现 [FriendsContainer]，
 * 取而代之的是 [ContactsContainer]。当组件存在支持 [Friend] 相关操作的时候可以进行额外实现。
 *
 * "好友"并 _**不一定**_ 代表那些需要 "添加申请"、"同意" 后出现在好友列表中的好友，
 * 也并非所有的组件都支持“好友”的概念。
 *
 * 对于一个以"频道"概念为主的组件就是最常见的例子（例如Kook） —— 它们通常没有真正的"好友"概念，
 * 至少对于bot来讲没有。取而代之的则通常是"频道成员"或者一个"会话"。
 *
 * 实际上，对于一个bot来讲"好友"的概念确实可有可无，它更需要"[联系人][Contact]"。
 *
 * 在一个容器同时支持 [FriendsContainer] 和 [ContactsContainer]
 * 的情况下，[FriendsContainer] 中能够得到的目标常常为 [ContactsContainer]
 * 的**子集**。_但是这并不绝对。_
 *
 * @see ContactsContainer
 *
 */
public interface FriendsContainer : SocialRelationsContainer {
    
    /**
     * 得到此容器下的好友序列。
     *
     * [friends] 可能是一个根据 [Items.batch] 批次发起某种网络请求的真实序列，
     * 也可能是由当前容器内部提前缓存好的伪序列，而不会发起真正的网络请求。
     */
    public val friends: Items<Friend>
    
    /**
     * 得到当前容器中所有[好友][Friend]的总数量。
     *
     * [friendCount] 可能每次请求都会发起某种网络请求，
     * 也可能仅跟随当前容器内部某种缓存机制刷新的数值，而不会发起真正的网络请求。
     *
     * 默认情况下（组件未实现、不支持直接查询数量等）相当于直接通过 [friends] 进行全量查询并计数。
     *
     * @since 3.0.0-RC.2
     */
    @JvmBlocking(suffix = "", asProperty = true)
    @JvmAsync(asProperty = true)
    public suspend fun friendCount(): Int {
        return friends.asFlow().count()
    }
    
    /**
     * 通过唯一标识获取这个容器对应的某个好友，获取不到则为null。
     *
     * @param id 好友的唯一标识
     */
    @JvmBlocking(baseName = "getFriend", suffix = "")
    @JvmAsync(baseName = "getFriend")
    public suspend fun friend(id: ID): Friend?
    
}


/**
 * 提供获取 [Contact] 的容器。
 *
 * 通常应用于 [Bot] 中为其提供获取 [Contact] 相关的属性api。
 *
 * [联系人][Contact] 通常代表为与当前容器存在"会话"或可以建立会话的目标，
 * 它们之间必须存在一种硬性关系（例如它们之间是 [好友][Friend] 关系）
 * 或者存在一个被创建过的"会话"（例如某联系人主动与bot进行过交流或者
 * 与当前容器（[Bot]）创建过与某个目标的会话）。
 *
 * 因上述约束，[ContactsContainer.contacts] 通常不具备检索 组织成员 [Member]
 * 这类**间接**联系人的能力, 尽管 [Member] 也属于 [Contact] 类型 ———— 除非它们与当前容器存在**直接**会话。
 *
 * 当一个bot中，所有可能的联系人都是与bot存在硬性关系（例如它们之间是 [好友][Friend] 关系）的时候，
 * [ContactsContainer] 的表现将会与 [FriendsContainer] 类似。
 *
 */
public interface ContactsContainer : SocialRelationsContainer {
    
    /**
     * 是否支持contacts相关的获取操作。当 [contacts] 和 [contact] 都不被支持时得到 `false`。
     * 默认情况下视其为 `true`，由实现者重写此属性来决定其可用性。
     *
     * 当不支持的情况下（[isContactsSupported] == false）, [contacts] 和 [contact] 不可用。
     * 不可用可能会表现为得到默认的[空序列][Items.emptyItems]（[contacts]）和 `null`（[contact]），
     * 也有可能会表现为抛出异常。
     *
     */
    public val isContactsSupported: Boolean get() = true
    
    /**
     * 得到当前容器中能够获取到的联系人序列。
     *
     * [contacts] 可能是一个根据 [Items.batch] 批次发起某种网络请求的真实序列，
     * 也可能是由当前容器内部提前缓存好的伪序列，而不会发起真正的网络请求。
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
     * 得到当前容器中所有[联系人][Contact]的总数量。
     *
     * [contactCount] 可能每次请求都会发起某种网络请求，
     * 也可能仅跟随当前容器内部某种缓存机制刷新的数值，而不会发起真正的网络请求。
     *
     * 默认情况下（组件未实现、不支持直接查询数量等）相当于直接通过 [contacts] 进行全量查询并计数。
     *
     * @since 3.0.0-RC.2
     */
    @JvmBlocking(suffix = "", asProperty = true)
    @JvmAsync(asProperty = true)
    public suspend fun contactCount(): Int {
        return contacts.asFlow().count()
    }
    
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
    @JvmAsync(baseName = "getContact")
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
     * 是否支持groups相关的获取操作。当 [groups] 和 [group] 都不被支持时得到 `false`。
     * 默认情况下视其为 `true`，由实现者重写此属性来决定其可用性。
     *
     * 当不支持的情况下（[isGroupsSupported] == false）, [groups] 和 [group] 不可用。
     * 不可用可能会表现为得到默认的[空序列][Items.emptyItems]（[groups]）和 `null`（[group]），
     * 也有可能会表现为抛出异常。
     *
     */
    public val isGroupsSupported: Boolean get() = true
    
    /**
     * 获取当前bot所处的群聊序列。
     *
     * [groups] 可能是一个根据 [Items.batch] 批次发起某种网络请求的真实序列，
     * 也可能是由当前容器内部提前缓存好的伪序列，而不会发起真正的网络请求。
     *
     */
    public val groups: Items<Group>
    
    /**
     * 得到当前容器中所有[群][Group]的总数量。
     *
     * [groupCount] 可能每次请求都会发起某种网络请求，
     * 也可能仅跟随当前容器内部某种缓存机制刷新的数值，而不会发起真正的网络请求。
     *
     * 默认情况下（组件未实现、不支持直接查询数量等）相当于直接通过 [groups] 进行全量查询并计数。
     *
     * @since 3.0.0-RC.2
     */
    @JvmBlocking(suffix = "", asProperty = true)
    @JvmAsync(asProperty = true)
    public suspend fun groupCount(): Int {
        return groups.asFlow().count()
    }
    
    /**
     * 通过唯一标识获取这个bot对应的某个群，获取不到则为null。
     *
     * @param id 目标群唯一标识
     */
    @JvmBlocking(baseName = "getGroup", suffix = "")
    @JvmAsync(baseName = "getGroup")
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
     * 是否支持guilds相关的获取操作。当 [guilds] 和 [guild] 都不被支持时得到 `false`。
     * 默认情况下视其为 `true`，由实现者重写此属性来决定其可用性。
     *
     * 当不支持的情况下（[isGuildsSupported] == false）, [guilds] 和 [guild] 不可用。
     * 不可用可能会表现为得到默认的[空序列][Items.emptyItems]（[guilds]）和 `null`（[guild]），
     * 也有可能会表现为抛出异常。
     *
     */
    public val isGuildsSupported: Boolean get() = true
    
    /**
     * 获取当前的所有频道服务器序列。
     *
     * [guilds] 可能是一个根据 [Items.batch] 批次发起某种网络请求的真实序列，
     * 也可能是由当前容器内部提前缓存好的伪序列，而不会发起真正的网络请求。
     */
    public val guilds: Items<Guild>
    
    /**
     * 得到当前容器中所有[频道服务器][Guild]的总数量。
     *
     * [guildCount] 可能每次请求都会发起某种网络请求，
     * 也可能仅跟随当前容器内部某种缓存机制刷新的数值，而不会发起真正的网络请求。
     *
     * 默认情况下（组件未实现、不支持直接查询数量等）相当于直接通过 [guilds] 进行全量查询并计数。
     *
     * @since 3.0.0-RC.2
     */
    @JvmBlocking(suffix = "", asProperty = true)
    @JvmAsync(asProperty = true)
    public suspend fun guildCount(): Int {
        return guilds.asFlow().count()
    }
    
    /**
     * 通过唯一标识获取这个bot对应的某个频道，获取不到则为null。
     *
     * @param id 频道服务器唯一标识
     */
    @JvmBlocking(baseName = "getGuild", suffix = "")
    @JvmAsync(baseName = "getGuild")
    public suspend fun guild(id: ID): Guild?
}


