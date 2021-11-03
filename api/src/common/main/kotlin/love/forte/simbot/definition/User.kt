@file:JvmName("AccountUtil")

package love.forte.simbot.definition

import love.forte.simbot.ID
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
    override val id: ID
        get() = accountInfo.id

    /**
     * 这个账号的 [账号信息][AccountInfo]。
     */
    public override val accountInfo: AccountInfo


    // 可交流的? Communicable
    // send to other account

}

