package love.forte.simbot.common.api.messages.results

import love.forte.simbot.common.api.messages.containers.AccountContainer
import love.forte.simbot.common.api.messages.containers.PermissionContainer


/**
 *
 * 被禁言人列表。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface BanList : MultipleResults<BanInfo>


/**
 * 被禁言者的信息。其中包括 [账号信息][AccountContainer]、[权限信息][PermissionContainer]
 */
public interface BanInfo : Result, AccountContainer, PermissionContainer {
    /**
     * 剩余禁言时间。如果不支持则有可能为null。
     */
    val lastTime: Long
}

