/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:JvmMultifileClass
@file:JvmName("Results")
package love.forte.simbot.api.message.results

import love.forte.simbot.api.message.containers.AccountContainer
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.emptyAccountInfo


/**
 *
 * 好友信息。其中包含了好友的 [账号信息][AccountContainer]
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface FriendInfo: Result, AccountInfo {
    /**
     * 好友所在分组。如果无法获取则可能得到一个 null 值。
     */
    val grouping: String?


}


/**
 * 好友列表。
 */
public interface FriendList: MultipleResults<FriendInfo>


/**
 * [FriendInfo] 的空值实现。
 */
public fun emptyFriendInfo(): FriendInfo = EmptyFriendInfo


private object EmptyFriendInfo : FriendInfo, AccountInfo by emptyAccountInfo() {
    override val originalData: String
        get() = "{}"
    override val grouping: String?
        get() = null

    override fun toString(): String {
        return "EmptyFriendInfo"
    }
}


public fun emptyFriendList() = object : FriendList {
    override val originalData: String
        get() = "[]"
    override val results: List<FriendInfo>
        get() = emptyList()

    override fun toString(): String {
        return "EmptyFriendList"
    }
}

