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

import love.forte.common.utils.timeBy
import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.api.message.containers.AccountContainer
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.results.BanInfo
import love.forte.simbot.api.message.results.BanList
import love.forte.simbot.component.mirai.message.MiraiMemberAccountInfo
import love.forte.simbot.component.mirai.message.toSimbotPermissions
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.NormalMember
import net.mamoe.mirai.contact.isMuted
import java.util.concurrent.TimeUnit

/**
 * mirai [BanList] 实现。
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class MiraiBanList(group: Group, limit: Int = -1) : BanList {

    override val results: List<BanInfo> by lazy(LazyThreadSafetyMode.PUBLICATION) {
        if (limit > 0) {
            group.members.asSequence().take(limit).mapNotNull {
                it.takeIf { m -> m.isMuted }?.let { m -> MiraiBanInfo(m) }
            }.toList()
        } else {
            group.members.mapNotNull {
                it.takeIf { m -> m.isMuted }?.let { m -> MiraiBanInfo(m) }
            }
        }
    }

    override val originalData: String = "MiraiBanList(group=$group)"
}

/**
 * ban info.
 */
public class MiraiBanInfo(member: NormalMember) : BanInfo, AccountContainer {
    override val lastTime: Long = (member.muteTimeRemaining timeBy TimeUnit.SECONDS).toMillis()
    override val originalData: String = "MiraiBanInfo(member=$member)"
    override val accountInfo: AccountInfo = MiraiMemberAccountInfo(member)
    override val permission: Permissions = member.toSimbotPermissions()
}