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

@file:JvmName("LovelyCatGroupMsgEvents")
package love.forte.simbot.component.lovelycat.message.event

import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.events.*
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.component.lovelycat.message.*
import love.forte.simbot.serialization.json.JsonSerializerFactory


public const val GROUP_MSG_EVENT = "EventGroupMsg"

/**
 * lovely cat 群事件消息接口.
 */
public interface LovelyCatGroupMsgEvent : LovelyCatMsg {
    override val robotWxid: String
    override val type: Int
    val fromWxid: String
    val fromName: String
    val finalFromWxid: String
    val finalFromName: String
    val toWxid: String
}


/**
 * 可作为普通消息使用的 [LovelyCatGroupMsgEvent] 实现。
 */
public class LovelyCatTextAbleGroupMsgEvent(
    override val robotWxid: String,
    override val type: Int,
    override val fromWxid: String,
    override val fromName: String,
    override val finalFromWxid: String,
    override val finalFromName: String,
    override val toWxid: String,
    override val msgContent: MessageContent,
    override val originalData: String,
    api: LovelyCatApiTemplate?
) : BaseLovelyCatMsg(GROUP_MSG_EVENT, originalData), GroupMsg, LovelyCatGroupMsgEvent {
    /**
     * 暂时无法区分权限，所有人都视为群员。
     */
    override val permission: Permissions
        get() = Permissions.MEMBER

    /**
     * 获取群消息类型.
     * 如果 [toWxid] == [robotWxid], 则说明为bot收到了消息，否则视为某种系统消息。
     */
    override val groupMsgType: GroupMsg.Type
        get() = if (toWxid == robotWxid) GroupMsg.Type.NORMAL else GroupMsg.Type.SYS

    /**
     *  lovelycat 似乎不支持消息撤回.
     */
    override val flag: Flag<GroupMsg.FlagContent>
        get() = EmptyLovelyCatGroupMsgFlag

    /**
     * 账号的信息。
     */
    override val accountInfo: AccountInfo = lovelyCatAccountInfo(finalFromWxid, finalFromName)

    /**
     * 群信息。
     */
    override val groupInfo: GroupInfo = lovelyCatGroupInfo(fromWxid, fromName)

    /**
     * bot信息。
     */
    override val botInfo: BotInfo = lovelyCatBotInfo(robotWxid, api)
}


/**
 * 群邀请事件。
 */
public class LovelyCatGroupInviteGroupMsgEvent(
    override val robotWxid: String,
    override val type: Int,
    override val fromWxid: String,
    override val fromName: String,
    override val finalFromWxid: String,
    override val finalFromName: String,
    override val toWxid: String,
    override val originalData: String,
    override val text: String,
    api: LovelyCatApiTemplate?
) : BaseLovelyCatMsg(GROUP_MSG_EVENT, originalData), GroupAddRequest, LovelyCatGroupMsgEvent {

    /**
     * bot信息
     */
    override val botInfo: BotInfo = lovelyCatBotInfo(robotWxid, api)

    /**
     * 代表当前实际申请入群的用户。如果是别人申请入群，则此为这个要进群的用户的信息，
     * 如果是bot被邀请进某个群，那么这个就是bot的信息。
     */
    override val accountInfo: AccountInfo = lovelyCatAccountInfo(finalFromWxid, finalFromName)

    /** 当前被申请加入的群聊信息。 */
    override val groupInfo: GroupInfo = lovelyCatGroupInfo(fromWxid, fromName)

    /**
     * 当前请求的邀请者。在 **组件不支持** 、**请求非邀请** 等情况下可能为null。
     */
    override val invitor: GroupAddRequestInvitor = accountInfo.asInvitor()

    /**
     * 申请的类型
     */
    override val requestType: GroupAddRequest.Type
        get() = GroupAddRequest.Type.PASSIVE

    /**
     * 获取请求标识。
     *
     * @see GroupAddRequestIdFlagContent
     */
    override val flag: Flag<GroupAddRequest.FlagContent> = LovelyCatGroupInviteEventFlag(originalData)
}

/**
 * 记录了原始消息json的群邀请事件flag。
 */
internal class LovelyCatGroupInviteEventFlag(jsonMsg: String) : Flag<GroupAddRequest.FlagContent> {
    /**
     * 标识主体
     */
    override val flag: GroupAddRequest.FlagContent = GroupAddRequestIdFlagContent(jsonMsg)
}


/**
 * Group msg 事件数据映射。
 */
data class GroupMsgDataMapping(
    val robotWxid: String,
    val type: Int,
    val fromWxid: String,
    val fromName: String,
    val finalFromWxid: String,
    val finalFromName: String,
    val toWxid: String,
    /** 只允许两种值：1，string，2：单层map */
    val msg: Any,
) : BaseLovelyCatMsg.LovelyCatDataMapping<LovelyCatGroupMsgEvent>() {
    companion object {
        internal fun typeTo(type: Int): Class<out LovelyCatGroupMsgEvent> {
            return if (type == 2003) LovelyCatGroupInviteGroupMsgEvent::class.java
            else LovelyCatTextAbleGroupMsgEvent::class.java
        }
    }
    override fun mapTo(
        originalData: String,
        api: LovelyCatApiTemplate?,
        jsonSerializerFactory: JsonSerializerFactory
    ): LovelyCatGroupMsgEvent {
        val msgContent: MessageContent =
            when (type) {
                /*
                    1/文本消息
                    3/图片消息
                    47/动态表情
                    34/语音消息
                    42/名片消息
                    43/视频
                    49/分享链接
                    2001/红包
                    2002/小程序
                    48/地理位置 // location json.

                    2003/群邀请 // invite
                 */
                // text type.
                1 -> LovelyCatTextMessageContent(msg.toString())
                // img type.
                3, 47 -> LovelyCatImageMessageContent(msg.toString())
                // record type.
                34 -> LovelyCatRecordMessageContent(msg.toString())
                // share card type.
                42 -> LovelyCatShareCardMessageContent(msg.toString())
                // video type.
                43 -> LovelyCatVideoMessageContent(msg.toString())
                // location type.
                // {"x":"36.678349","y":"117.041023","desc":"明湖天地D座(济南市天桥区明湖东路8号)","title":"天桥区明湖东路10-6号"}
                48 -> {
                    @Suppress("UNCHECKED_CAST")
                    val paramMap = msg as Map<String, *>
                    val location = LovelyCatLocation(
                        paramMap["x"]?.toString() ?: "",
                        paramMap["y"]?.toString() ?: "",
                        paramMap["desc"]?.toString() ?: "",
                        paramMap["title"]?.toString() ?: ""
                    )
                    LovelyCatLocationMessageContent(location)
                }
                // share link type.
                49 -> LovelyCatShareLinkMessageContent(msg.toString())
                // 红包消息.
                // {msg=收到红包，请在手机上查看, from_wxid=wxid_khv2ht7uwa5x22, Event=EventFriendMsg, from_name=主号 , type=2001, robot_wxid=wxid_bqy1ezxxkdat22, to_wxid=wxid_bqy1ezxxkdat22}
                2001 -> LovelyCatRedEnvelopeMessageContent(msg.toString())
                // 2002/小程序
                2002 -> LovelyCatAppMessageContent(msg.toString())
                // 2003/群邀请, 视为text消息。
                2003 -> {
                    // 群邀请，使用群邀请消息类型
                    return LovelyCatGroupInviteGroupMsgEvent(
                        robotWxid, type, fromWxid, fromName, finalFromWxid, finalFromName,
                        toWxid, originalData, msg.toString(), api
                    )
                }
                // other unknown msg.
                else -> LovelyCatUnknownMessageContent(msg.toString(), type)
            }

        return LovelyCatTextAbleGroupMsgEvent(
            robotWxid, type, fromWxid, fromName, finalFromWxid, finalFromName,
            toWxid, msgContent, originalData, api
        )
    }
}


/**
 * parser event: [GROUP_MSG_EVENT]
 */
public object LovelyCatGroupMsgEventParser : LovelyCatEventParser {
    override fun invoke(
        original: String,
        api: LovelyCatApiTemplate,
        jsonSerializerFactory: JsonSerializerFactory,
        params: Map<String, *>
    ): LovelyCatGroupMsgEvent {
        return GroupMsgDataMapping(
            params.orParamErr("robot_wxid").toString(),
            params.orParamErr("type") as Int,
            params.orParamErr("from_wxid").toString(),
            params.orParamErr("from_name").toString(),
            params.orParamErr("final_from_wxid").toString(),
            params.orParamErr("final_from_name").toString(),
            params.orParamErr("to_wxid").toString(),
            params.orParamErr("msg").toString(),
        ).mapTo(original, api, jsonSerializerFactory)
    }

    override fun type(): Class<out LovelyCatMsg> =LovelyCatGroupMsgEvent::class.java
}




