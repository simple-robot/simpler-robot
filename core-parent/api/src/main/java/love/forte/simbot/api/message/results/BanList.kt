/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     BanList.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.api.message.results

import love.forte.simbot.api.message.containers.AccountContainer
import love.forte.simbot.api.message.containers.PermissionContainer


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
     * 剩余禁言时间。如果不支持则有可能为 -1。
     * 而没有被禁言一般代表为 `lastTime == 0`。
     */
    val lastTime: Long
}

