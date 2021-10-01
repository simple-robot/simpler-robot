package love.forte.simbot.api.definition.account

import love.forte.simbot.api.definition.People

/**
 * 一个账号的 **信息**。
 * @author ForteScarlet
 */
public interface AccountInfo : People {

    /**
     * 这个账号的唯一ID.
     * 作为账号的信息，也可以作为账号这个[人][People]。
     */
    override val id: String

    /**
     * 这个账号的用户名。
     * 在极端情况下，也许用户不存在用户名。那时，以 [id] 替之。
     */
    public val username: String


}


/**
 * 一个 **可操作** 的账号。代表这个账号中的部分信息可以进行修改或操作。
 */
public interface OperableAccountInfo: AccountInfo {
    override var username: String
}
