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

import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.action.MuteSupport
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.runInBlocking
import kotlin.time.Duration


/**
 * 一个 **组织** 结构（中的一员）。
 *
 *
 * [组织-百度百科](https://baike.baidu.com/item/组织/5105529):
 *
 *     1. 组织必须是以人为中心，把人、财、物合理配合为一体，并保持相对稳定而形成的一个社会实体。
 *     2. 组织必须具有为本组织全体成员所认可并为之奋斗的共同目标。
 *     3. 组织必须保持一个明确的边界，以区别于其他组织和外部环境。上述三条，是组织存在的必要条件。
 *
 * ## 成员
 * 一个组织下，可以存在多个 [成员][Member]. 且成员中可能存在拥有一定程度权限的管理员。
 *
 * 查询所有管理员：
 * ```kotlin
 *    organization.members().filter { it.isAdmin() }
 * ```
 *
 *
 * ## 财产
 * 在组织下，此组织可能存在一定程度的 "财产". 财产的表现形式是多样化的，例如在QQ群中保存的各种文库文件、相册图片等。应由实现者自行实现。
 *
 *
 * ## 职能
 * 一个组织可能存在各种职能，例如一个“文字频道”，其职能允许成员们在其中进行文字交流，而一个“语音频道”则可能允许其成员们在其中进行语音聊天。
 *
 * 对于能够交流的组织（下的成员），将其定义为一个 [聊天室][ChatRoom]。 聊天室应当实现于 [Organization] 下的接口并为其提供消息发送的能力。
 *
 *
 * _有关职能的约定仍需考虑。_
 *
 *
 *
 * 在一些常见场景下，组织可以表示为一个群聊，或者一个频道。群聊是没有上下级的，但是频道会有。
 * 需要考虑的是，不同类型的组织可能所拥有的权能不同。有可能能够发送消息，有可能不能。
 *
 * 你可以参考 [组织概述](https://www.yuque.com/simpler-robot/simpler-robot-doc/dt3ukr) 中的相关对比图。
 *
 * @see ChatRoom
 * @see Group
 * @see Guild
 * @see Channel
 *
 * @author ForteScarlet
 */
public interface Organization : Objective, OrganizationInfo, MuteSupport, BotContainer {
    
    /**
     * 这个组织一定是属于某一个Bot之下的。
     *
     * 这个所属bot在当前组织中所扮演的角色。
     */
    override val bot: OrganizationBot
    
    /**
     * 对于这个组织, 有一个唯一ID。
     */
    override val id: ID
    
    // region from organization info
    override val name: String
    override val icon: String
    override val description: String
    override val createTime: Timestamp
    override val ownerId: ID
    // endregion
    
    /**
     * 组织的拥有者信息。
     */
    @JvmSynthetic
    public suspend fun owner(): Member
    
    /**
     * 对整个组织进行禁言。
     *
     */
    @JvmSynthetic
    override suspend fun mute(duration: Duration): Boolean
    
    /**
     * 结束整个群的禁言。
     */
    @JvmSynthetic
    override suspend fun unmute(): Boolean
    
    @Api4J
    public val owner: Member
        get() = runInBlocking { owner() }
    
    override val maximumMember: Int
    override val currentMember: Int
    
    /**
     * 上一级，或者说这个组织的上层。
     * 组织有可能是层级的，因此一个组织结构可能会有上一层的组织。
     * 当然，也有可能不存在。不存在的时候，那么这个组织就是顶层。
     */
    @JvmSynthetic
    public suspend fun previous(): Organization?
    
    
    /**
     * 上一级，或者说这个组织的上层。
     * 组织有可能是层级的，因此一个组织结构可能会有上一层的组织。
     * 当然，也有可能不存在。不存在的时候，那么这个组织就是顶层。
     */
    @Api4J
    public val previous: Organization?
    
    
    /**
     * 得到下一级的数据内容。
     *
     */
    public val children: Items<Organization>
    
    /**
     * 根据指定ID尝试获取一个匹配的下级[组织][Organization]。
     *
     * 当无法获取时得到null。
     */
    @JvmSynthetic
    public suspend fun child(id: ID): Organization?
    
    /**
     * 根据指定ID尝试阻塞的获取一个匹配的下级[组织][Organization]。
     *
     * 当无法获取时得到null。
     */
    @Api4J
    public fun getChild(id: ID): Organization?
    
    /**
     * 获取当前组织中的成员列表。
     */
    public val members: Items<Member>
    
    
    // region member 获取
    /**
     * 尝试通过ID获取一个成员，无法获取则得到null。
     */
    @JvmSynthetic
    public suspend fun member(id: ID): Member?
    
    /**
     * 尝试通过ID获取一个成员，无法获取则得到null。
     */
    @Api4J
    public fun getMember(id: ID): Member?
    // endregion
    
    
    /**
     * 根据分组ID和限流器尝试获取当前组织下的所有角色。
     */
    public val roles: Items<Role>
    
}

/**
 *
 * 一个组织的部分最基础的信息。
 *
 * [OrganizationInfo] 支持解构：
 * ```kotlin
 * val (id, name, icon) = organizationInfo
 * ```
 *
 */
public interface OrganizationInfo : IDContainer {
    
    /**
     * 此组织的唯一标识.
     */
    override val id: ID
    
    /**
     * 组织的名称。
     */
    public val name: String
    
    /**
     * 组织的图标/头像。
     */
    public val icon: String
    
    /**
     * 组织的对外描述信息。
     */
    public val description: String
    
    /**
     * 组织的创建时间。
     */
    public val createTime: Timestamp
    
    /**
     * 组织的拥有者的ID。
     */
    public val ownerId: ID
    
    
    //// 上面的信息，大概率是可以得到的。
    //// 下面的信息均存在无法获取的可能。
    
    /**
     * 此组织的"分组"。
     *
     * 通常情况下，只有 [GroupInfo] 或 [ChannelInfo] 存在 "分组" 概念的概率会大一些，
     * 但是无法保证组件存在分组概念或支持分组的获取。
     *
     * 因此当不支持获取分组、不存在分组等情况下，[category] 将会得到null。
     */
    public val category: Category? get() = null
    
    /**
     * 当前组织内成员最大承载量。
     * 如果无法获取，得到-1.
     */
    public val maximumMember: Int
    
    /**
     * 当前组织内已存在成员数量。
     * 如果无法获取，得到-1.
     */
    public val currentMember: Int
    
}


/**
 * [OrganizationInfo] 解构扩展。第1个参数，相当于 [OrganizationInfo.id]
 * ```kotlin
 * val (id, name, icon) = organizationInfo
 * ```
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun OrganizationInfo.component1(): ID = id

/**
 * [OrganizationInfo] 解构扩展。第2个参数，相当于 [OrganizationInfo.name]
 * ```kotlin
 * val (id, name, icon) = organizationInfo
 * ```
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun OrganizationInfo.component2(): String = name

/**
 * [OrganizationInfo] 解构扩展。第3个参数，相当于 [OrganizationInfo.icon]
 * ```kotlin
 * val (id, name, icon) = organizationInfo
 * ```
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun OrganizationInfo.component3(): String = icon



////
