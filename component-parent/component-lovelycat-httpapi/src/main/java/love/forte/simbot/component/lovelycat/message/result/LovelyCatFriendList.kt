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

package love.forte.simbot.component.lovelycat.message.result

import love.forte.simbot.api.message.results.FriendList
import love.forte.simbot.component.lovelycat.message.CatFriendInfo


/**
 *
 * @author ForteScarlet
 */
public class LovelyCatFriendList(
    override val results: List<CatFriendInfo>,
) : FriendList {
    override val originalData: String
        get() = results.toString()
}