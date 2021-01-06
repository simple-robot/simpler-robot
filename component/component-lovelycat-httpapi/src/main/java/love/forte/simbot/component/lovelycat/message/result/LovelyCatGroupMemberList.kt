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

import love.forte.simbot.api.message.results.GroupMemberInfo
import love.forte.simbot.api.message.results.GroupMemberList
import love.forte.simbot.component.lovelycat.message.CatGroupInfo
import love.forte.simbot.component.lovelycat.message.CatSimpleGroupMemberInfo


/**
 *
 * @author ForteScarlet
 */
public class LovelyCatGroupMemberList(
    override val groupInfo: CatGroupInfo,
    catGroupMemberInfo: List<CatSimpleGroupMemberInfo>
) : GroupMemberList {
    override val originalData: String
        get() = results.toString()

    override val results: List<GroupMemberInfo> = catGroupMemberInfo.map {
        LovelyCatGroupMemberInfo(it, groupInfo)
    }

    override fun toString(): String = originalData

}