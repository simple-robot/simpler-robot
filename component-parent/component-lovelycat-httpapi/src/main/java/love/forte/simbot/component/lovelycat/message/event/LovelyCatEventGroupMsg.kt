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

package love.forte.simbot.component.lovelycat.message.event

import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.component.lovelycat.message.*


/**
 * 事件名=EventGroupMsg，
 * 群消息事件（收到群消息时，运行这里）
 * @author ForteScarlet
 */
public class LovelyCatEventGroupMsg
private constructor(
    private val robotWxid: String,
    override val type: Int,
    private val fromWxid: String,
    private val fromName: String,
    private val finalFromWxid: String,
    private val finalFromName: String,
    private val toWxid: String,
    private val _msg: String,
    override val originalData: String,
    private val api: LovelyCatApiTemplate?
) : BaseLovelyCatMsg("EventGroupMsg", originalData), GroupMsg {
    data class DataMapping(
        val robotWxid: String,
        val type: Int,
        val fromWxid: String,
        val fromName: String,
        val finalFromWxid: String,
        val finalFromName: String,
        val toWxid: String,
        val msg: String,
    ) : BaseLovelyCatMsg.LovelyCatDataMapping<LovelyCatEventGroupMsg>() {
        override fun mapTo(originalData: String, api: LovelyCatApiTemplate?): LovelyCatEventGroupMsg {
            return LovelyCatEventGroupMsg(
                robotWxid,
                type,
                fromWxid,
                fromName,
                finalFromWxid,
                finalFromName,
                toWxid,
                msg,
                originalData,
                api
            )
        }
    }
    /*
    robot_wxid, 文本型, , 机器人账号id（就是这条消息是哪个机器人的，因为可能登录多个机器人）
    type, 整数型, ,
        1/文本消息
        3/图片消息
        34/语音消息
        42/名片消息
        43/视频
        47/动态表情
        48/地理位置
        49/分享链接
        2001/红包
        2002/小程序
        2003/群邀请
        更多请参考sdk模块常量值
    from_wxid, 文本型, , 来源群id
    from_name, 文本型, , 来源群昵称
    final_from_wxid, 文本型, , 具体发消息的群成员id
    final_from_name, 文本型, , 具体发消息的群成员昵称
    to_wxid, 文本型, , 接收消息的人id，（一般是机器人收到了，也有可能是机器人发出的消息，别人收到了，那就是别人）
    msg, 文本型, , 消息内容
     */

    // /**
    //  * 接收到的消息类型。某些事件中也可能是 'json_msg'
    //  */
    // override val msg: String
    //     get() = TODO("Not yet implemented")

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


    /**
     *  消息事件的消息正文文本。
     *
     * [消息正文][msgContent] 不允许为`null`，但是其中的 [msg][MessageContent.msg] 则就不保证了。
     */
    override val msgContent: MessageContent = when (type) {
        /*
            1/文本消息
            3/图片消息
            47/动态表情
            34/语音消息
            42/名片消息
            43/视频
            48/地理位置
            49/分享链接
            2001/红包
            2002/小程序
            2003/群邀请
         */
        // text type.
        1 -> LovelyCatTextMessageContent(_msg)
        // img type.
        3, 47 -> LovelyCatImageMessageContent(_msg)
        // record type.
        34 -> LovelyCatRecordMessageContent(_msg)
        // share card type.
        42 -> LovelyCatShareCardMessageContent(_msg)
        // video type.
        43 -> LovelyCatVideoMessageContent(_msg)
        // location type.
        // {"x":"36.678349","y":"117.041023","desc":"明湖天地D座(济南市天桥区明湖东路8号)","title":"天桥区明湖东路10-6号"}
        // TODO
        // 48 -> LovelyCatLocationMessageContent(_msg)
        // share link type.
        49 -> LovelyCatShareLinkMessageContent(_msg)
        // 红包消息.
        // {msg=收到红包，请在手机上查看, from_wxid=wxid_khv2ht7uwa5x22, Event=EventFriendMsg, from_name=主号 , type=2001, robot_wxid=wxid_bqy1ezxxkdat22, to_wxid=wxid_bqy1ezxxkdat22}
        2001 -> LovelyCatRedEnvelopeMessageContent(_msg)
        // 2002/小程序
        2002 -> LovelyCatAppMessageContent(_msg)
        // 2003/群邀请 TODO

        else -> LovelyCatUnknownMessageContent(_msg, type)
    }


}