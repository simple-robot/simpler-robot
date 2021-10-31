package love.forte.simbot.definition

/**
 * 一个 **组织** 结构（中的一员）。
 *
 *
 * <https://baike.baidu.com/item/组织/5105529>
 *
 *     1. 组织必须是以人为中心，把人、财、物合理配合为一体，并保持相对稳定而形成的一个社会实体。
 *     2. 组织必须具有为本组织全体成员所认可并为之奋斗的共同目标。
 *     3. 组织必须保持一个明确的边界，以区别于其他组织和外部环境。上述三条，是组织存在的必要条件。
 *
 * // 职能?
 * // 资产?
 *
 * @author ForteScarlet
 */
public interface Organization : Structured<Organization?, List<Organization>> {

    /**
     * 对于这个组织, 有一个唯一ID。
     */
    public val id: String

    /**
     * 这个组织，会有一个名称。在某些极端情况，组织没有名称的情况下，[id] 代之。
     */
    public val name: String

    // organization info?

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

    /**
     * 一个组织下的成员。
     */
    public interface Member : People {
        override val id: String
        // 职责？权限？阶级？
    }

}


