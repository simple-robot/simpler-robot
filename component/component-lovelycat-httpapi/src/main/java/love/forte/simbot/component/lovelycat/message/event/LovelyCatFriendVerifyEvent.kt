/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatFriendVerifyEvent.kt
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

@file:JvmName("LovelyCatEventFriendVerifies")
package love.forte.simbot.component.lovelycat.message.event

import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.assists.flag
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.events.FriendAddRequest
import love.forte.simbot.api.message.events.FriendAddRequestIdFlagContent
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory


public const val FRIEND_VERIFY_EVENT = "EventFriendVerify"


/**
 * 好友请求事件（插件3.0版本及以上）
 * 事件名=EventFriendVerify
 */
public interface LovelyCatFriendVerify: LovelyCatMsg, FriendAddRequest


/**
 * [LovelyCatFriendVerify] 事件实现。
 */
public class LovelyCatFriendVerifyEvent(
    override val robotWxid: String,
    fromWxid: String,
    fromName: String,
    private val jsonMsg: String,
    private val api: LovelyCatApiTemplate,
    originalData: String
) : BaseLovelyCatMsg(FRIEND_VERIFY_EVENT, originalData), LovelyCatFriendVerify {

    /**
     * 可以得到一个 **文本**。
     * 不存在文本。
     */
    override val text: String?
        get() = null

    /**
     * 申请人账号信息。
     */
    override val accountInfo: AccountInfo = lovelyCatAccountInfo(fromWxid, fromName)

    /**
     * bot信息。
     */
    override val botInfo: BotInfo = lovelyCatBotInfo(robotWxid, api)

    /**
     * 获取请求标识。
     *
     * @see FriendAddRequestIdFlagContent
     */
    override val flag: Flag<FriendAddRequest.FlagContent> = flag { FriendAddRequestIdFlagContent(jsonMsg) }
}

/*
    robot_wxid, 文本型, , 机器人账号id（这条消息是哪个机器人的，因为可能登录多个机器人）
    from_wxid, 文本型, , 陌生人用户id
    from_name, 文本型, , 陌生人用户昵称
    to_wxid, 文本型, , 忽略
    json_msg, 文本型, ,
    好友验证信息JSON对象
    （1/群内添加时，包含群id
    2/名片推荐添加时，包含推荐人id及昵称
    3/微信号、手机号搜索添加时），
    具体JSON结构请查看日志
*/

/**
 * [LovelyCatFriendVerify] 解析器
 */
public object LovelyCatFriendVerifyEventParser : LovelyCatEventParser {
    override fun invoke(
        original: String,
        api: LovelyCatApiTemplate,
        jsonSerializerFactory: JsonSerializerFactory,
        params: Map<String, *>
    ): LovelyCatFriendVerify {
        return LovelyCatFriendVerifyEvent(
            params.orParamErr("robot_wxid").toString(),
            params.orParamErr("from_wxid").toString(),
            params.orParamErr("from_name").toString(),
            params.orParamErr("json_msg").toString(),
            api, original
        )
    }
    override fun type(): Class<out LovelyCatMsg> = LovelyCatFriendVerify::class.java
}



















