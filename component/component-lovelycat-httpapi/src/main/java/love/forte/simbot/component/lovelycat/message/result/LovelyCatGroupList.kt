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

import love.forte.simbot.api.message.results.GroupList
import love.forte.simbot.component.lovelycat.message.CatGroupInfo


/**
 *
 * @author ForteScarlet
 */
class LovelyCatGroupList(override val results: List<CatGroupInfo>) : GroupList {
    override val originalData: String
        get() = results.toString()
}