/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

package love.forte.simbot.component.mirai.message.event

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.GroupContainer
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.events.ChangedGet
import love.forte.simbot.api.message.results.GroupMemberInfo
import love.forte.simbot.component.mirai.message.result.MiraiGroupFullInfo
import love.forte.simbot.component.mirai.message.result.MiraiGroupMemberInfo
import net.mamoe.mirai.event.events.GroupTalkativeChangeEvent


/**
 * 群龙王发生改变时的事件。
 *
 * mirai 群龙王变更事件属于一个 [变动事件][ChangedGet]，变更主体为 [群成员][GroupMemberInfo].
 *
 * @author ForteScarlet
 */
public interface MiraiGroupTalkativeChanged :
    ChangedGet<GroupMemberInfo>,
    GroupContainer,
    MiraiSpecialEvent<GroupTalkativeChangeEvent>





public class MiraiGroupTalkativeChangedImpl(event: GroupTalkativeChangeEvent) :
    AbstractMiraiMsgGet<GroupTalkativeChangeEvent>(event),
    MiraiGroupTalkativeChanged {

    /** 当前监听事件消息的ID。*/
    override val id: String
        get() = "MGTCM-${event.hashCode()}"

    /**
     * 上一个龙王
     */
    override val beforeChange: GroupMemberInfo = MiraiGroupMemberInfo(event.previous)

    /**
     * 现在的龙王
     */
    override val afterChange: GroupMemberInfo = MiraiGroupMemberInfo(event.now)

    /**
     * 用户信息为变动后的用户。
     */
    override val accountInfo: AccountInfo get() = afterChange

    override val groupInfo: GroupInfo = MiraiGroupFullInfo(event.group)
}