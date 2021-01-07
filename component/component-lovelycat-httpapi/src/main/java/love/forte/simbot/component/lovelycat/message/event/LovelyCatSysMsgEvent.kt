/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatSysMsgEvent.kt
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

@file:JvmName("LovelyCatSysMsgEvents")
package love.forte.simbot.component.lovelycat.message.event

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.containers.botAsAccountInfo
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory

public const val SYS_MSG_EVENT = "EventSysMsg"


/**
 * 系统消息事件
 *
 * 事件名=EventSysMsg
 */
public interface LovelyCatSysMsg : LovelyCatMsg, BotContainer {
    val jsonMsg: String
}

public class LovelyCatSysMsgEvent(
    override val robotWxid: String,
    override val type: Int,
    override val jsonMsg: String,
    api: LovelyCatApiTemplate,
    original: String
) : BaseLovelyCatMsg(SYS_MSG_EVENT, original), LovelyCatSysMsg {
    /**
     * 不支持进行text过滤，因为无法确认 [jsonMsg] 的具体内容。
     */
    override val text: String?
        get() = null

    /**
     * bot信息
     */
    override val botInfo: BotInfo = lovelyCatBotInfo(robotWxid, api)

    /**
     * 账号的信息, 即为bot自身。
     */
    override val accountInfo: AccountInfo = botInfo.botAsAccountInfo()

}

/*
事件名=EventSysMsg	系统消息事件
robot_wxid, 文本型, , 机器人账号id（就是这条消息是哪个机器人的，因为可能登录多个机器人）
type, 整数型
json_msg, 文本型
 */

/**
 * [LovelyCatSysMsg] 解析器。
 */
public object LovelyCatSysMsgEventParser : LovelyCatEventParser {
    override fun invoke(
        original: String,
        api: LovelyCatApiTemplate,
        jsonSerializerFactory: JsonSerializerFactory,
        params: Map<String, *>
    ): LovelyCatSysMsg {
        return LovelyCatSysMsgEvent(
            params.orParamErr("robot_wxid").toString(),
            params.orParamErr("type") as Int,
            params.orParamErr("json_msg").toString(),
            api, original
        )
    }

    override fun type(): Class<out LovelyCatMsg> = LovelyCatSysMsg::class.java
}
