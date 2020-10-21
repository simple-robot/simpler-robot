/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiGroupMemberList.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.message.result

import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.results.GroupMemberInfo
import love.forte.simbot.api.message.results.GroupMemberList
import net.mamoe.mirai.contact.Group

/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class MiraiGroupMemberList(group: Group, limit: Int = -1) : GroupMemberList {
    override val results: List<GroupMemberInfo> by lazy(LazyThreadSafetyMode.PUBLICATION) {
        if (limit > 0) {
            group.members.asSequence().take(limit).map { MiraiGroupMemberInfo(it) }.toList()
        } else {
            group.members.map { MiraiGroupMemberInfo(it) }
        }
    }
    override val originalData: String = "MiraiGroupMemberList(group=$group)"
    override val groupInfo: GroupInfo = MiraiGroupInfo(group)
}