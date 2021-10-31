@file:JvmName("AccountUtil")

package love.forte.simbot.definition

import love.forte.simbot.definition.account.AccountInfo
import love.forte.simbot.definition.account.OperableAccountInfo
import love.forte.simbot.definition.container.AccountInfoContainer
import kotlin.jvm.JvmName

/**
 * 一个 **用户**。
 *
 * @author ForteScarlet
 */
public interface User : People, AccountInfoContainer {

    /**
     * 这个账号的唯一ID。
     */
    override val id: String
        get() = accountInfo.id

    /**
     * 这个账号的 [账号信息][AccountInfo]。
     */
    public override val accountInfo: AccountInfo


    // 可交流的? Communicable
    // send to other account

}


/**
 * 一个 **可操作** 的账号。
 */
public interface OperableAccount : User {
    /** 这个账号可操作的账号信息。 */
    override var accountInfo: OperableAccountInfo
}


//
public inline var OperableAccount.username: String
    get() = accountInfo.username
    set(value) {
        accountInfo.username = value
    }
