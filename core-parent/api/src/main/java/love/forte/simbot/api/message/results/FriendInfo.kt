/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     FriendInfo.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.api.message.results

import love.forte.simbot.api.message.containers.AccountContainer


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

