package love.forte.simbot.api.definition

/**
 * 一个 **结构化** 的定义。
 * 结构化的东西，他可以有一个 [上级][previous]，以及一个 [下级][next]。
 *
 * @author ForteScarlet
 */
public interface Structured<P, N> {

    /**
     * 上一级的内容。
     */
    public val previous: P


    /**
     * 下一级的内容。
     */
    public val next: N
}