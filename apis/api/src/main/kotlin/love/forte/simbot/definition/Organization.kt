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

package love.forte.simbot.definition

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import love.forte.simbot.*

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
 * ## 人
 * 一个组织下，可以存在多个 [成员][Member]. 且成员中可能存在拥有一定程度权限的管理员。
 *
 *
 * ## 财产
 * 在组织下，此组织可能存在一定程度的 "财产". 财产的表现形式是多样化的，例如在QQ群中保存的各种文库文件、相册图片等。应由实现者自行实现。
 *
 *
 * ## 职能 (?
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
 *
 *
 * @author ForteScarlet
 */
public interface Organization : Something, Structured<Organization?, Flow<Organization>>, BotContainer {

    /**
     * 这个组织一定是属于某一个Bot之下的。
     */
    override val bot: Bot

    /**
     * 对于这个组织, 有一个唯一ID。
     */
    override val id: ID

    /**
     * 一个组织会有一个名称。
     */
    public val name: String

    /**
     * 得到这个组织的信息。
     */
    public suspend fun info(): OrganizationInfo
    public val info: OrganizationInfo get() = runBlocking { info() }

    /**
     * 上一级，或者说这个组织的上层。
     * 组织有可能是层级的，因此一个组织结构可能会有上一层的组织。
     * 当然，也有可能不存在。不存在的时候，那么这个组织就是顶层。
     */
    override suspend fun previous(): Organization?


    /**
     * 得到下一级的数据内容。
     *
     * 提供 grouping 查询分组信息。
     *
     * 实现者应当考虑处理 [Grouping] 允许实现 [Limiter] 的情况。
     *
     */
    override suspend fun children(grouping: Grouping): Flow<Organization>

    /**
     * 一个组织中，可能存在[成员][members].
     * @param limiter 对于多条数据的限流器。
     */
    public suspend fun members(limiter: Limiter = Limiter): Flow<Member>

    // 资产？

}

/**
 *
 * 一个组织的部分最基础的信息。
 *
 */
public interface OrganizationInfo {

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



    /**
     * 组织的拥有者信息。
     */
    public suspend fun owner(): Member
    @Api4J
    public val owner: Member get() = runBlocking { owner() }

    //// 上面的信息，大概率是可以得到的。
    //// 下面的信息均存在无法获取的可能。

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


////
