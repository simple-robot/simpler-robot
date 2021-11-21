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

import love.forte.simbot.ID
import java.lang.reflect.Member

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
 * TODO 有关职能的约定仍需考虑。
 *
 *
 *
 * 在一些常见场景下，组织可以表示为一个群聊，或者一个频道。群聊是没有上下级的，但是频道会有。
 * 需要考虑的是，不同类型的组织可能所拥有的权能不同。有可能能够发送消息，有可能不能。
 *
 * 比如在khl中，一个服务器本身不能发送消息或者语音，必须在进入了文字频道后才可以发言，并且这个"频道"就是一个“群聊”，或者一个 “文字聊天室”
 * 而在YY中，
 *
 *
 *
 * @author ForteScarlet
 */
public interface Organization : Structured<Organization?, List<Organization>> {

    /**
     * 对于这个组织, 有一个唯一ID。
     */
    public val id: ID

    /**
     * 一个组织会有一个名称。
     */
    public val name: String

    /**
     * 上一级，或者说这个组织的上层。
     * 组织有可能是层级的，因此一个组织结构可能会有上一层的组织。
     * 当然，也有可能不存在。不存在的时候，那么这个组织就是顶层。
     */
    override val previous: Organization?


    /**
     * 下一级，即这个组织下属的其他组织。
     * 组织有可能是存在层级关系的，因此一个组织结构可能会存在下一层的次级组织。
     * 如果不存在，得到空列表。
     */
    override val next: List<Organization>


    /**
     * 一个组织中，可能存在[成员][members].
     */
    // 成员? 用户? 人?
    public val members: List<Member>

    // 资产？

}


