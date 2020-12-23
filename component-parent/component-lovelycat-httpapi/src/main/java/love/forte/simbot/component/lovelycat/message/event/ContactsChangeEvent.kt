/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     ContactsChangeEvent.kt
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

@file:JvmName("LovelyCatEventContactsChanges")
package love.forte.simbot.component.lovelycat.message.event

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.events.MemberChangesEventGet
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory

public const val CONTACTS_CHANGES_EVENT = "EventContactsChange"

/**
 * 朋友变动事件（插件4.0版本及以上，当前为测试版，还未启用，留以备用）.
 * 事件名=EventContactsChange
 */
public interface LovelyCatContactsChange : LovelyCatMsg, MemberChangesEventGet


/**
 * [LovelyCatContactsChange] 事件实现。
 */
public class LovelyCatContactsChangeEvent(
    override val robotWxid: String,
    fromWxid: String,
    fromName: String,
    private val msg: String?,
    api: LovelyCatApiTemplate,
    originalData: String
) : BaseLovelyCatMsg(CONTACTS_CHANGES_EVENT, originalData), LovelyCatContactsChange {

    override val text: String?
        get() = msg

    /**
     * bot信息
     */
    override val botInfo: BotInfo = lovelyCatBotInfo(robotWxid, api)

    /**
     * 账号的信息。
     */
    override val accountInfo: AccountInfo = lovelyCatAccountInfo(fromWxid, fromName)
}

/*
    事件名=EventContactsChange	朋友变动事件（插件4.0版本及以上，当前为测试版，还未启用，留以备用）
    robot_wxid, 文本型
    type, 整数型
    from_wxid, 文本型, , 来源用户id
    from_name, 文本型, , 来源用户昵称
    msg, 文本型, , 消息内容
 */

/**
 * [LovelyCatContactsChange] 事件解析器。
 */
public object LovelyCatContactsChangeEventParser : LovelyCatEventParser {
    override fun invoke(
        original: String,
        api: LovelyCatApiTemplate,
        jsonSerializerFactory: JsonSerializerFactory,
        params: Map<String, *>
    ): LovelyCatContactsChange {
        return LovelyCatContactsChangeEvent(
            params.orParamErr("robot_wxid").toString(),
            params.orParamErr("from_wxid").toString(),
            params.orParamErr("from_name").toString(),
            params["msg"]?.toString(),
            api,
            original
        )

    }
}











