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
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.action.MuteSupport
import love.forte.simbot.utils.item.Items
import kotlin.time.Duration


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
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
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
    @JvmBlocking
    @JvmAsync
    override suspend fun unmute(): Boolean
    
    
    override val maximumMember: Int
    override val currentMember: Int
    
    /**
     * 上一级，或者说这个组织的上层。
     * 组织有可能是层级的，因此一个组织结构可能会有上一层的组织。
     * 当然，也有可能不存在。不存在的时候，那么这个组织就是顶层。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    public suspend fun previous(): Organization?
    
    
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
    @JvmBlocking(baseName = "getChild", suffix = "")
    @JvmAsync(baseName = "getChild", suffix = "")
    public suspend fun child(id: ID): Organization?
    
    /**
     * 获取当前组织中的成员列表。
     */
    public val members: Items<Member>
    
    
    /**
     * 尝试通过ID获取一个成员，无法获取则得到null。
     */
    @JvmBlocking(baseName = "getMember", suffix = "")
    @JvmAsync(baseName = "getMember", suffix = "")
    public suspend fun member(id: ID): Member?
    
    
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
