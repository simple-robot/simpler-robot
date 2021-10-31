package love.forte.simbot.definition

/**
 *
 * 一个 **人**。所有与人有关或者说类似于人的，都是 [人][People].
 *
 * 比如说，一个账号，他可能会对应一个人，一个机器人，也类似于一个人——一个虚拟的人。
 *
 * @author ForteScarlet
 */
public interface People {
    /**
     * 这个人所对应的唯一ID。
     *
     */
    public val id: String
}