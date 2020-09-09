package love.forte.simbot.common.api.messages.results

import love.forte.simbot.common.api.messages.containers.AccountContainer


/**
 *
 * 好友信息。其中包含了好友的 [账号信息][AccountContainer]
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface FriendInfo: Result, AccountContainer {
    /**
     * 好友所在分组。如果无法获取则可能得到一个 null 值。
     */
    val grouping: String?
}


/**
 * 好友列表。
 */
public interface FriendList: MultipleResults<FriendInfo>

