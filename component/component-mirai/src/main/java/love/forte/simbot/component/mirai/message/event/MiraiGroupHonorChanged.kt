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

package love.forte.simbot.component.mirai.message.event

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.events.ChangedGet
import love.forte.simbot.component.mirai.message.MiraiMemberAccountInfo
import net.mamoe.mirai.data.GroupHonorType
import net.mamoe.mirai.event.events.MemberHonorChangeEvent
import net.mamoe.mirai.utils.MiraiExperimentalApi

/**
 * 群成员荣耀信息变更事件。
 *
 * 荣耀信息变更事件属于一种 [变更事件][ChangedGet]，变更主体是mirai的[荣誉类型][GroupHonorType]。
 *
 * 荣耀变更分为 **获得** 和 **失去**。
 * 当类型为 **获得** 时，[beforeChange] 为null。
 * 当类型为 **失去** 时，[afterChange] 为null。
 *
 */
@OptIn(MiraiExperimentalApi::class)
public interface MiraiGroupHonorChanged: ChangedGet<GroupHonorType>




@OptIn(MiraiExperimentalApi::class)
public class MiraiGroupHonorChangedImpl(event: MemberHonorChangeEvent) : AbstractMiraiMsgGet<MemberHonorChangeEvent>(event),
    MiraiGroupHonorChanged {
    /** 当前监听事件消息的ID。*/
    override val id: String
        get() = "MGHCM-${event.hashCode()}"

    /**
     * 账号的信息。指的是荣耀发生变更的用户信息。
     */
    override val accountInfo: AccountInfo = MiraiMemberAccountInfo(event.member)

    /**
     * 如果为 **失去** 类型则存在值，否则为null。
     */
    override val beforeChange: GroupHonorType? = if (event is MemberHonorChangeEvent.Lose) event.honorType else null

    /**
     * 如果为 **获取** 类型则存在值，否则为null。
     */
    override val afterChange: GroupHonorType? = if (event is MemberHonorChangeEvent.Achieve) event.honorType else null

}