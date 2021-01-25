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

package love.forte.simbot.component.mirai.message.result

import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.results.GroupAdmin
import love.forte.simbot.api.message.results.GroupFullInfo
import love.forte.simbot.api.message.results.GroupOwner
import love.forte.simbot.api.message.results.SimpleGroupInfo
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.isAdministrator

/**
 * mirai group info.
 */
public class MiraiGroupInfo(group: Group) : SimpleGroupInfo {
    override val groupCode: String = group.id.toString()
    override val groupCodeNumber: Long = group.id
    override val groupAvatar: String = group.avatarUrl
    override val groupName: String = group.name

    override fun toString(): String {
        return "MiraiGroupInfo(groupCode='$groupCode', groupCodeNumber=$groupCodeNumber, groupAvatar='$groupAvatar', groupName='$groupName')"
    }

    override val originalData: String get() = toString()

}


/**
 * mirai 的 [GroupInfo] 实现。
 */
public class MiraiGroupFullInfo(group: Group) : GroupInfo by MiraiGroupInfo(group), GroupFullInfo {
    @Deprecated("无法获取人数上限")
    override val maximum: Int = -1

    /** 群人数。 */
    override val total: Int = group.members.size + 1

    @Deprecated("无法获取建群时间。")
    override val createTime: Long = -1

    @Deprecated("无法获取群简介。")
    override val simpleIntroduction: String? = null

    @Deprecated("无法获取群简介。")
    override val fullIntroduction: String? = null

    /** owner info. */
    override val owner: GroupOwner = MiraiGroupOwnerInfo(group.owner)

    /**
     * 管理员与群主。
     */
    override val admins: List<GroupAdmin> by lazy(LazyThreadSafetyMode.PUBLICATION) {
        (sequenceOf(owner) + group.members.asSequence()
            .filter { it.isAdministrator() }
            .map { MiraiGroupAdminInfo(it) }).toList()
    }


    override val originalData: String = group.toString()
}
