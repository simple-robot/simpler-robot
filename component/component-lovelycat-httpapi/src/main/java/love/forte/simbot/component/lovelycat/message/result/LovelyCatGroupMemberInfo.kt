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

import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.api.message.containers.GroupAccountInfo
import love.forte.simbot.api.message.results.GroupMemberInfo
import love.forte.simbot.api.message.results.SimpleGroupInfo
import love.forte.simbot.component.lovelycat.message.CatGroupInfo


/**
 *
 * @author ForteScarlet
 */
public class LovelyCatGroupMemberInfo(
    private val catGroupAccountInfo: GroupAccountInfo,
    private val catGroupInfo: CatGroupInfo
) : GroupMemberInfo,
    SimpleGroupInfo by catGroupInfo,
    GroupAccountInfo by catGroupAccountInfo {


    /**
     * 无法断定群员权限，因此全部定义为 **群员**。
     */
    override val permission: Permissions
        get() = Permissions.MEMBER

    override val originalData: String
        get() = catGroupAccountInfo.toString()

    override fun toString(): String {
        return "LovelyCatGroupMemberInfo(permission=$permission, accountInfo=$catGroupAccountInfo, groupInfo=$groupInfo)"
    }


}