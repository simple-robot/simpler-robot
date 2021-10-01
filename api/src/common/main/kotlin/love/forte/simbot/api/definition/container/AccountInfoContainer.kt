package love.forte.simbot.api.definition.container

import love.forte.simbot.api.definition.Container
import love.forte.simbot.api.definition.account.AccountInfo

/**
 * 对 [AccountInfo] 的容器。
 * @author ForteScarlet
 */
public interface AccountInfoContainer : Container {

    /**
     * 能够得到一个 [AccountInfo] 实例。
     */
    public val accountInfo: AccountInfo
}