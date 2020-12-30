/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     GroupMemberAddEvent.kt
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

@file:JvmName("LovelyCatGroupMemberAddEvents")
package love.forte.simbot.component.lovelycat.message.event

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.containers.OperatorInfo
import love.forte.simbot.api.message.events.GroupMemberIncrease
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory


public const val GROUP_MEMBER_ADD_EVENT = "EventGroupMemberAdd"


/**
 * 群成员增加事件（新人进群）
 *
 * 事件名=EventGroupMemberAdd
 */
public interface LovelyCatGroupMemberAdd : LovelyCatMsg, GroupMemberIncrease



public class LovelyCatGroupMemberAddEvent(
    override val robotWxid: String,
    fromWxid: String,
    fromName: String,
    private val jsonMsg: String,
    api: LovelyCatApiTemplate,
    original: String,
) : BaseLovelyCatMsg(GROUP_MEMBER_ADD_EVENT, original), LovelyCatGroupMemberAdd {
    /**
     * bot信息
     */
    override val botInfo: BotInfo = lovelyCatBotInfo(robotWxid, api)

    /**
     * 账号的信息。
     *
     * TODO 可能信息存在于 [jsonMsg] 中
     *
     */
    override val accountInfo: AccountInfo
        get() = TODO()

    /**
     * 有时候群友增加也可能代表是bot进入了某个群.
     * @return Boolean 事件主体是否为bot
     */
    override fun isBot(): Boolean = false

    /**
     * 操作者。
     * 不支持, 返回null。
     *
     * TODO 可能信息存在于 [jsonMsg] 中
     *
     */
    override val operatorInfo: OperatorInfo?
        get() = null

    /**
     * 增加类型，默认为 [邀请进入][GroupMemberIncrease.Type.INVITED]
     */
    override val increaseType: GroupMemberIncrease.Type
        get() = GroupMemberIncrease.Type.INVITED

    /**
     * 群信息。
     */
    override val groupInfo: GroupInfo = lovelyCatGroupInfo(fromWxid, fromName)
}

/*
    robot_wxid, 文本型, , 机器人账号id（就是这条消息是哪个机器人的，因为可能登录多个机器人）
    from_wxid, 文本型, , 来源群id
    from_name, 文本型, , 来源群名称
    json_msg, 文本型, , 新成员增加消息JSON对象，具体JSON结构请查看日志
 */


/**
 * [LovelyCatGroupMemberAdd] 解析器。
 */
public object LovelyCatGroupMemberAddEventParser : LovelyCatEventParser {
    override fun invoke(
        original: String,
        api: LovelyCatApiTemplate,
        jsonSerializerFactory: JsonSerializerFactory,
        params: Map<String, *>
    ): LovelyCatGroupMemberAdd {
        return LovelyCatGroupMemberAddEvent(
            params.orParamErr("robot_wxid").toString(),
            params.orParamErr("from_wxid").toString(),
            params.orParamErr("from_name").toString(),
            params.orParamErr("json_msg").toString(),
            api, original
        )
    }
}
