package love.forte.simbot.definition

import love.forte.simbot.ID

/**
 * 一个账号的 **信息**。
 * @author ForteScarlet
 */
public interface AccountInfo : People {

    /**
     * 这个账号的唯一ID.
     * 作为账号的信息，也可以作为账号这个[人][People]。
     */
    override val id: ID

    /**
     * 这个账号的用户名。
     * 在极端情况下，也许用户不存在用户名。那时，以 [id] 替之。
     */
    public val username: String


}

