/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     GroupMemberDecreaseEvent.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

@file:JvmName("LovelyCatGroupMemberDecreaseEvents")
package love.forte.simbot.component.lovelycat.message.event

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.containers.OperatorInfo
import love.forte.simbot.api.message.events.GroupMemberReduce
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory

public const val GROUP_MEMBER_DECREASE_EVENT = "EventGroupMemberDecrease"


/**
 * 群成员减少事件（群成员退出）
 *
 * 事件名=EventGroupMemberDecrease
 *
 */
public interface LovelyCatGroupMemberDecrease : LovelyCatMsg, GroupMemberReduce


public class LovelyCatGroupMemberDecreaseEvent(
    override val robotWxid: String,
    fromWxid: String,
    fromName: String,
    private val jsonMsg: String,
    api: LovelyCatApiTemplate,
    original: String
) : BaseLovelyCatMsg(GROUP_MEMBER_DECREASE_EVENT, original), LovelyCatGroupMemberDecrease {
    /**
     * bot信息
     */
    override val botInfo: BotInfo = lovelyCatBotInfo(robotWxid, api)

    /**
     * 账号的信息。
     * TODO 信息大概在 [jsonMsg] 中。
     */
    override val accountInfo: AccountInfo
        get() = TODO("Not yet implemented. ")

    /**
     * 有时候群友减少也可能代表是bot被踢出了某个群.
     * @return Boolean 事件主体是否为bot
     */
    override fun isBot(): Boolean = false

    /**
     * 减少类型,
     * TODO 暂时默认为自行离开。
     */
    override val reduceType: GroupMemberReduce.Type
        get() = GroupMemberReduce.Type.LEAVE

    override val groupInfo: GroupInfo = lovelyCatGroupInfo(fromWxid, fromName)

    /**
     * 得到一个操作者信息 可能会是null
     * TODO 信息大概在 [jsonMsg] 中。
     */
    override val operatorInfo: OperatorInfo?
        get() = null
}

/*
    事件名=EventGroupMemberDecrease	群成员减少事件（群成员退出）
    robot_wxid, 文本型, , 机器人账号id（就是这条消息是哪个机器人的，因为可能登录多个机器人）
    from_wxid, 文本型, , 来源群id
    from_name, 文本型, , 来源群名称
    json_msg, 文本型, , 退出人的id|退出人的昵称
 */


public object LovelyCatGroupMemberDecreaseEventParser : LovelyCatEventParser {
    override fun invoke(
        original: String,
        api: LovelyCatApiTemplate,
        jsonSerializerFactory: JsonSerializerFactory,
        params: Map<String, *>
    ): LovelyCatGroupMemberDecrease {
        return LovelyCatGroupMemberDecreaseEvent(
            params.orParamErr("robot_wxid").toString(),
            params.orParamErr("from_wxid").toString(),
            params.orParamErr("from_name").toString(),
            params.orParamErr("json_msg").toString(),
            api, original
        )
    }

    override fun type(): Class<out LovelyCatMsg> = LovelyCatGroupMemberDecrease::class.java
}




