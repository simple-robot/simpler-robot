/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCat.kt
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
@file:JvmName("LovelyCatApiTemplates")

package love.forte.simbot.component.lovelycat

import love.forte.common.impl.ParameterizedTypeImpl
import love.forte.simbot.component.lovelycat.message.*
import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory
import java.util.*


/**
 * lovely cat api template.
 */
public interface LovelyCatApiTemplate {

    //**************** 取自身数据相关 ****************//


    /**
     * 功能=取登录账号昵称
     * robot_wxid, 文本型
     * api=GetRobotName
     */
    fun getRobotName(robotWxid: String): RobotName


    /**
     * 功能=取登录账号头像
     * robot_wxid, 文本型
     * api=GetRobotHeadimgurl
     * @return RobotHeadImgUrl
     */
    fun getRobotHeadImgUrl(robotWxid: String): RobotHeadImgUrl


    /**
     * 功能=取登录账号列表
     * api=GetLoggedAccountList
     */
    fun getLoggedAccountList(): LoggedAccountList


    //**************** 发送消息相关 ****************//


    /**
     * 功能=发送文本消息
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * msg, 文本型, , 发送的内容
     * api=SendTextMsg
     */
    fun sendTextMsg(robotWxid: String, toWxid: String, msg: String): LovelyCatApiResult


    /**
     * 功能=发送图片消息
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * path, 文本型, , 图片文件的绝对路径/URL/图片数据BASE64（以BASE64:开头）
     * api=SendImageMsg
     */
    fun sendImageMsg(robotWxid: String, toWxid: String, path: String): LovelyCatApiResult


    /**
     * 功能=发送视频消息
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * path, 文本型, , 视频存放的绝对路径
     * api=SendVideoMsg
     */
    fun sendVideoMsg(robotWxid: String, toWxid: String, path: String): LovelyCatApiResult


    /**
     * 功能=发送文件消息
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * path, 文本型, , 文件的路径
     * api=SendFileMsg
     *
     */
    fun sendFileMsg(robotWxid: String, toWxid: String, path: String): LovelyCatApiResult


    /**
     *
     * 功能=发送名片消息
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * card_data, 文本型, , 名片格式数据
     * api=SendCardMsg
     */
    fun sendCardMsg(robotWxid: String, toWxid: String, cardData: String): LovelyCatApiResult


    /**
     * 功能=发送群消息并艾特
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * group_wxid, 文本型, , 要发送的群ID
     * member_wxid, 文本型, , 要艾特成员的ID
     * member_name, 文本型, , 要艾特成员的名字
     * msg, 文本型, , 消息内容
     * api=SendGroupMsgAndAt
     */
    fun sendGroupMsgAndAt(
        robotWxid: String,
        groupWxid: String,
        memberWxid: String,
        memberName: String?,
        msg: String,
    ): LovelyCatApiResult


    /**
     * 功能=发送动态表情
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * path, 文本型, , 表情的文件的绝对路径
     * api=SendEmojiMsg
     */
    fun sendEmojiMsg(robotWxid: String, toWxid: String, path: String): LovelyCatApiResult


    /**
     * 功能=发送分享链接
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * title, 文本型, , 链接标题
     * text, 文本型, , 链接内容
     * target_url, 文本型, 可空, 跳转链接
     * pic_url, 文本型, 可空, 图片的链接
     * icon_url, 文本型, 可空, 图标的链接
     * api=SendLinkMsg
     *
     */
    fun sendLinkMsg(
        robotWxid: String, toWxid: String, title: String, text: String,
        targetUrl: String?, picUrl: String?, iconUrl: String?,
    ): LovelyCatApiResult


    /**
     * 功能=发送音乐分享
     * robot_wxid, 文本型
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * name, 文本型
     * type, 整数型, , 0 随机模式 / 1 网易云音乐 / 2 酷狗音乐
     * api=SendMusicMsg
     */
    fun sendMusicMsg(robotWxid: String, toWxid: String, name: String, type: Int): LovelyCatApiResult

    //**************** 取数据相关 ****************//


    /**
     * 功能=取好友列表
     * robot_wxid, 文本型, 可空, 如不填，则取的是所有登录账号的好友列表
     * is_refresh, 逻辑型, 可空, 为真将重新加载（注意切记不要频繁加载这里），不然将取缓存，默认为假
     * api=GetFriendList
     */
    fun getFriendList(robotWxid: String, isRefresh: Boolean): List<CatFriendInfo>

    @JvmDefault
    fun getFriendList(robotWxid: String) =
        getFriendList(robotWxid, false)


    /**
     * 功能=取群聊列表
     * robot_wxid, 文本型, 可空, 取哪个账号的列表，不填则取全部
     * is_refresh, 逻辑型, 可空, 为真将重新加载（注意切记不要频繁加载这里），不然将取缓存，默认为假
     * api=GetGroupList
     */
    fun getGroupList(robotWxid: String, isRefresh: Boolean): List<CatGroupInfo>

    @JvmDefault
    fun getGroupList(robotWxid: String) =
        getGroupList(robotWxid, false)


    /**
     * 功能=取群成员详细
     * robot_wxid, 文本型, , 已登机器人账号ID
     * group_wxid, 文本型, , 群ID
     * member_wxid, 文本型, , 群成员ID
     * is_refresh, 逻辑型, 可空, 为真将重新加载（注意切记不要频繁加载这里），不然将取缓存，默认为假
     * api=GetGroupMemberDetailInfo
     */
    fun getGroupMemberDetailInfo(
        robotWxid: String,
        groupWxid: String,
        memberWxid: String,
        isRefresh: Boolean,
    ): CatGroupMemberInfo

    @JvmDefault
    fun getGroupMemberDetailInfo(robotWxid: String, groupWxid: String, memberWxid: String) =
        getGroupMemberDetailInfo(robotWxid, groupWxid, memberWxid, false)


    /**
     * 功能=取群成员列表
     * robot_wxid, 文本型, , 已登账号ID
     * group_wxid, 文本型, , 群ID
     * is_refresh, 逻辑型, 可空, 为真将重新加载列表（注意切记不要频繁加载这里），不然将取缓存，默认为假
     * api=GetGroupMemberList
     */
    fun getGroupMemberList(robotWxid: String, groupWxid: String, isRefresh: Boolean): List<CatSimpleGroupMemberInfo>

    @JvmDefault
    fun getGroupMemberList(robotWxid: String, groupWxid: String) = getGroupMemberList(robotWxid, groupWxid, false)

    //**************** 请求相关 ****************//

    /**
     * 功能=接收好友转账
     * robot_wxid, 文本型, , 哪个机器人收到的好友转账，就填那个机器人的ID
     * from_wxid, 文本型, , 好友的ID（给你转账的那个人的ID）
     * json_msg, 文本型, , 请传入转账事件里的原消息
     * api=AcceptTransfer
     */
    fun acceptTransfer(robotWxid: String, fromWxid: String, jsonMsg: String): LovelyCatApiResult


    /**
     * 功能=同意群聊邀请
     * robot_wxid, 文本型, , 哪个机器人收到的群聊邀请，就填那个机器人的ID号
     * json_msg, 文本型, , 请传入事件的原消息
     * api=AgreeGroupInvite
     */
    fun agreeGroupInvite(robotWxid: String, jsonMsg: String): LovelyCatApiResult


    /**
     *功能=同意好友请求
     *robot_wxid, 文本型, , 哪个机器人收到的好友验证，就填哪个机器人的那个ID
     *json_msg, 文本型, , 请传入好友验证事件的原消息
     *api=AgreeFriendVerify
     *
     */
    fun agreeFriendVerify(robotWxid: String, jsonMsg: String): LovelyCatApiResult


    //**************** 功能性相关 ****************//


    /**
     * 功能=修改好友备注
     * robot_wxid, 文本型, , 要操作的机器人ID
     * friend_wxid, 文本型, , 要备注的好友ID
     * note, 文本型, , 新的备注
     * api=ModifyFriendNote
     */
    fun modifyFriendNote(robotWxid: String, friendWxid: String, note: String): LovelyCatApiResult


    /**
     * 功能=删除好友
     * robot_wxid, 文本型, , 要操作的机器人ID
     * friend_wxid, 文本型, , 要删除的好友ID
     * api=DeleteFriend
     */
    fun deleteFriend(robotWxid: String, friendWxid: String): LovelyCatApiResult


    /**
     * 功能=踢出群成员
     * robot_wxid, 文本型, , 要操作的机器人ID
     * group_wxid, 文本型, , 群ID
     * member_wxid, 文本型, , 群成员ID
     * api=RemoveGroupMember
     */
    fun removeGroupMember(robotWxid: String, groupWxid: String, memberWxid: String): LovelyCatApiResult


    /**
     * 功能=修改群名称
     * robot_wxid, 文本型, , 要操作的机器人ID
     * group_wxid, 文本型, , 群ID
     * group_name, 文本型, , 新的群名称
     * api=ModifyGroupName
     */
    fun modifyGroupName(robotWxid: String, groupWxid: String, groupName: String): LovelyCatApiResult


    /**
     * 功能=修改群公告
     * robot_wxid, 文本型, , 要操作的机器人ID
     * group_wxid, 文本型, , 群ID
     * content, 文本型, , 新公告内容
     * api=ModifyGroupNotice
     */
    fun modifyGroupNotice(robotWxid: String, groupWxid: String, content: String): LovelyCatApiResult


    /**
     * 功能=建立新群
     * robot_wxid, 文本型, , 要操作的机器人ID
     * friendArr, 文本型, 数组, 要建立新群的好友数组，至少要两个人以上
     * api=BuildingGroup
     */
    fun buildingGroup(robotWxid: String, friendArr: Array<String>): LovelyCatApiResult


    /**
     * 功能=退出群聊
     * robot_wxid, 文本型, , 要操作的机器人ID
     * group_wxid, 文本型, , 群ID
     * api=QuitGroup
     */
    fun quitGroup(robotWxid: String, groupWxid: String): LovelyCatApiResult


    /**
     * 功能=邀请加入群聊
     * robot_wxid, 文本型, , 要操作的机器人ID
     * group_wxid, 文本型, , 群ID
     * friend_wxid, 文本型, , 要邀请的好友ID
     * api=InviteInGroup
     */
    fun inviteInGroup(robotWxid: String, groupWxid: String, friendWxid: String): LovelyCatApiResult


    //**************** 插件相关 ****************//


    /**
     * 功能=取应用目录
     * api=GetAppDirectory
     */
    fun getAppDirectory(): LovelyCatApiResult


    /**
     * 功能=添加日志
     * msg1, 文本型
     * msg2, 文本型, 可空
     * api=AppendLogs
     */
    fun appendLogs(msg1: String, msg2: String?): LovelyCatApiResult


}


@Suppress("unused")
private abstract class RequestApiData(val api: String)


/**
 * loveCat [api模板][LovelyCatApiTemplate] 实现.
 * 其对照httpapi文档提供模板方法映射。
 * @property httpTemplate HttpTemplate
 * @constructor
 */
public class LovelyCatApiTemplateImpl
constructor(
    private val httpTemplate: HttpTemplate,
    private val url: String,
    private val jsonSerializerFactory: JsonSerializerFactory,
    private val lovelyCatApiCache: LovelyCatApiCache,
) : LovelyCatApiTemplate {


    // private val infoBuff: Map<String, Any> =


    private val loggedAccountListSerializer =
        jsonSerializerFactory.getJsonSerializer<List<CatBotInfo>>(
            ParameterizedTypeImpl(
                List::class.java,
                arrayOf(CatBotInfo::class.java)
            )
        )

    @JvmOverloads
    constructor(
        httpTemplate: HttpTemplate,
        ip: String,
        host: Int,
        // 默认路径，一般情况下不可修改也不需要修改
        path: String = "/httpAPI",
        jsonSerializerFactory: JsonSerializerFactory,
        lovelyCatApiCache: LovelyCatApiCache,
    ) : this(httpTemplate, "http://$ip:$host$path", jsonSerializerFactory, lovelyCatApiCache)


    /**
     * do post.
     */
    private inline fun <reified T> post(vararg pair: Pair<String, *>): T {
        return post(if (pair.size == 1) Collections.singletonMap(pair[0].first, pair[0].second) else mapOf(*pair))
    }

    /**
     * do post.
     * @throws love.forte.simbot.http.HttpTemplateException
     * @param requestBody Any?
     * @return T
     */
    private inline fun <reified T> post(requestBody: Any?): T {
        try {
            val resp = httpTemplate.post(url = url,
                headers = null,
                cookies = null,
                requestBody = requestBody,
                // responseType = T::class.java)
                responseType = String::class.java)
            val jsonP = jsonSerializerFactory.getJsonSerializer(T::class.java)
            return jsonP.fromJson(resp.body)
            // return
            // return resp.assertBody()!!
        } catch (e: Exception) {
            throw IllegalStateException("Post $url for '${T::class}' failed.", e)
        }

    }


    private fun postForLovelyCatResult(vararg pair: Pair<String, *>): LovelyCatApiResult {
        return post(*pair)
    }

    /**
     * 功能=取登录账号昵称
     * robot_wxid, 文本型
     * api=GetRobotName
     */
    override fun getRobotName(robotWxid: String): RobotName {
        return lovelyCatApiCache.computeBotName {
            postForLovelyCatResult(
                "api" to "GetRobotName",
                "robot_wxid" to robotWxid
            ).let { r ->
                r.letData("GetRobotName") { RobotName(it) }
            }
        }
        // return postForLovelyCatResult(
        //     "api" to "GetRobotName",
        //     "robot_wxid" to robotWxid
        // ).let { r ->
        //     r.letData("GetRobotName") { RobotName(it) }
        // }
    }


    /**
     * 功能=取登录账号头像
     * robot_wxid, 文本型
     * api=GetRobotHeadimgurl
     * @return RobotHeadImgUrl
     */
    override fun getRobotHeadImgUrl(robotWxid: String): RobotHeadImgUrl {
        return lovelyCatApiCache.computeBotHeadImgUrl {
            postForLovelyCatResult(
                "api" to "GetRobotHeadimgurl",
                "robot_wxid" to robotWxid
            ).let { r ->
                r.letData("GetRobotHeadimgurl") { RobotHeadImgUrl(it) }
            }
        }
        // return postForLovelyCatResult(
        //     "api" to "GetRobotHeadimgurl",
        //     "robot_wxid" to robotWxid
        // ).let { r ->
        //     r.letData("GetRobotHeadimgurl") { RobotHeadImgUrl(it) }
        // }
    }


    /**
     * 功能=取登录账号列表
     * api=GetLoggedAccountList
     */
    override fun getLoggedAccountList(): LoggedAccountList {
        return lovelyCatApiCache.computeLoggedAccountList {
            LoggedAccountList(loggedAccountListSerializer.fromJson(postForLovelyCatResult("api" to "GetLoggedAccountList").data))
        }
    }

    /**
     * 功能=发送文本消息
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * msg, 文本型, , 发送的内容
     * api=SendTextMsg
     */
    override fun sendTextMsg(robotWxid: String, toWxid: String, msg: String): LovelyCatApiResult {
        return post(SendTextMsgRequestData(robotWxid, toWxid, msg))
    }

    private data class SendTextMsgRequestData(
        val robot_wxid: String,
        val to_wxid: String,
        val msg: String,
    ) : RequestApiData("SendTextMsg")


    /**
     * 功能=发送图片消息
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * path, 文本型, , 图片文件的绝对路径/URL/图片数据BASE64（以BASE64:开头）
     * api=SendImageMsg
     */
    override fun sendImageMsg(robotWxid: String, toWxid: String, path: String): LovelyCatApiResult {
        return post(SendImageMsgRequestData(robotWxid, toWxid, path))
    }

    private data class SendImageMsgRequestData(
        val robot_wxid: String,
        val to_wxid: String,
        val path: String,
    ) : RequestApiData("SendImageMsg")


    /**
     * 功能=发送视频消息
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * path, 文本型, , 视频存放的绝对路径
     * api=SendVideoMsg
     */
    override fun sendVideoMsg(robotWxid: String, toWxid: String, path: String): LovelyCatApiResult {
        return post(SendVideoMsgRequestData(robotWxid, toWxid, path))
    }

    private data class SendVideoMsgRequestData(
        val robot_wxid: String,
        val to_wxid: String,
        val path: String,
    ) : RequestApiData("SendVideoMsg")

    /**
     * 功能=发送文件消息
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * path, 文本型, , 文件的路径
     * api=SendFileMsg
     *
     */
    override fun sendFileMsg(robotWxid: String, toWxid: String, path: String): LovelyCatApiResult {
        return post(SendFileMsgRequestData(robotWxid, toWxid, path))
    }

    private data class SendFileMsgRequestData(
        val robot_wxid: String,
        val to_wxid: String,
        val path: String,
    ) : RequestApiData("SendFileMsg")


    /**
     *
     * 功能=发送名片消息
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * card_data, 文本型, , 名片格式数据
     * api=SendCardMsg
     */
    override fun sendCardMsg(robotWxid: String, toWxid: String, cardData: String): LovelyCatApiResult {
        return post(SendCardMsgRequestData(robotWxid, toWxid, cardData))
    }

    private data class SendCardMsgRequestData(
        val robot_wxid: String,
        val to_wxid: String,
        val card_data: String,
    ) : RequestApiData("SendCardMsg")


    /**
     * 功能=发送群消息并艾特
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * group_wxid, 文本型, , 要发送的群ID
     * member_wxid, 文本型, , 要艾特成员的ID
     * member_name, 文本型, , 要艾特成员的名字
     * msg, 文本型, , 消息内容
     * api=SendGroupMsgAndAt
     */
    override fun sendGroupMsgAndAt(
        robotWxid: String,
        groupWxid: String,
        memberWxid: String,
        memberName: String?,
        msg: String,
    ): LovelyCatApiResult {
        return post(SendGroupMsgAndAtRequestData(robotWxid, groupWxid, memberWxid, memberName, msg))
    }

    private data class SendGroupMsgAndAtRequestData(
        val robot_wxid: String,
        val group_wxid: String,
        val member_wxid: String,
        val member_name: String?,
        val msg: String,
    ) : RequestApiData("SendGroupMsgAndAt")


    /**
     * 功能=发送动态表情
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * path, 文本型, , 表情的文件的绝对路径
     * api=SendEmojiMsg
     */
    override fun sendEmojiMsg(robotWxid: String, toWxid: String, path: String): LovelyCatApiResult {
        return post(SendEmojiMsgRequestData(robotWxid, toWxid, path))
    }

    private data class SendEmojiMsgRequestData(
        val robot_wxid: String, val to_wxid: String, val path: String,
    ) : RequestApiData("SendEmojiMsg")


    /**
     * 功能=发送分享链接
     * robot_wxid, 文本型, , 用哪个机器人发这条消息
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * title, 文本型, , 链接标题
     * text, 文本型, , 链接内容
     * target_url, 文本型, 可空, 跳转链接
     * pic_url, 文本型, 可空, 图片的链接
     * icon_url, 文本型, 可空, 图标的链接
     * api=SendLinkMsg
     *
     */
    override fun sendLinkMsg(
        robotWxid: String,
        toWxid: String,
        title: String,
        text: String,
        targetUrl: String?,
        picUrl: String?,
        iconUrl: String?,
    ): LovelyCatApiResult {
        return post(SendLinkMsgRequestData(robotWxid, toWxid, title, text, targetUrl, picUrl, iconUrl))
    }

    private data class SendLinkMsgRequestData(
        val robot_wxid: String,
        val to_wxid: String,
        val title: String,
        val text: String,
        val target_url: String?,
        val pic_url: String?,
        val icon_url: String?,
    ) : RequestApiData("SendLinkMsg")


    /**
     * 功能=发送音乐分享
     * robot_wxid, 文本型
     * to_wxid, 文本型, , 对方的ID（支持好友/群ID）
     * name, 文本型
     * type, 整数型, , 0 随机模式 / 1 网易云音乐 / 2 酷狗音乐
     * api=SendMusicMsg
     */
    override fun sendMusicMsg(robotWxid: String, toWxid: String, name: String, type: Int): LovelyCatApiResult {
        return post(SendMusicMsgRequestData(robotWxid, toWxid, name, type))
    }

    private data class SendMusicMsgRequestData(
        val robot_wxid: String,
        val to_wxid: String,
        val name: String,
        val type: Int,
    ) : RequestApiData("SendMusicMsg")


    /**
     * 功能=取好友列表
     * robot_wxid, 文本型, 可空, 如不填，则取的是所有登录账号的好友列表
     * is_refresh, 逻辑型, 可空, 为真将重新加载（注意切记不要频繁加载这里），不然将取缓存，默认为假
     * api=GetFriendList
     */
    override fun getFriendList(robotWxid: String, isRefresh: Boolean): List<CatFriendInfo> {
        return lovelyCatApiCache.computeCatFriendInfoList {
            post<CatFriendListResult>(GetFriendListRequestData(robotWxid, isRefresh)).data
        }
        // return post<CatFriendListResult>(GetFriendListRequestData(robotWxid, isRefresh)).data
    }

    private data class GetFriendListRequestData(
        val robot_wxid: String,
        val is_refresh: Boolean,
    ) : RequestApiData("GetFriendList")

    /**
     * 功能=取群聊列表
     * robot_wxid, 文本型, 可空, 取哪个账号的列表，不填则取全部
     * is_refresh, 逻辑型, 可空, 为真将重新加载（注意切记不要频繁加载这里），不然将取缓存，默认为假
     * api=GetGroupList
     */
    override fun getGroupList(robotWxid: String, isRefresh: Boolean): List<CatGroupInfo> {
        return lovelyCatApiCache.computeCatGroupInfoList {
            post<CatGroupListResult>(GetGroupListRequestData(robotWxid, isRefresh)).data
        }
    }

    private data class GetGroupListRequestData(
        val robot_wxid: String,
        val is_refresh: Boolean,
    ) : RequestApiData("GetGroupList")

    /**
     * 功能=取群成员详细
     * robot_wxid, 文本型, , 已登机器人账号ID
     * group_wxid, 文本型, , 群ID
     * member_wxid, 文本型, , 群成员ID
     * is_refresh, 逻辑型, 可空, 为真将重新加载（注意切记不要频繁加载这里），不然将取缓存，默认为假
     * api=GetGroupMemberDetailInfo
     */
    override fun getGroupMemberDetailInfo(
        robotWxid: String,
        groupWxid: String,
        memberWxid: String,
        isRefresh: Boolean,
    ): CatGroupMemberInfo {
        return lovelyCatApiCache.computeCatGroupMemberInfo {
            post<GroupMemberDetailInfoResult>(
                GetGroupMemberDetailInfoRequestData(
                    robotWxid,
                    groupWxid,
                    memberWxid,
                    isRefresh
                )
            ).data
        }
    }

    private data class GetGroupMemberDetailInfoRequestData(
        val robot_wxid: String,
        val group_wxid: String,
        val member_wxid: String,
        val is_refresh: Boolean,
    ) : RequestApiData("GetGroupMemberDetailInfo")


    /**
     * 功能=取群成员列表
     * robot_wxid, 文本型, , 已登账号ID
     * group_wxid, 文本型, , 群ID
     * is_refresh, 逻辑型, 可空, 为真将重新加载列表（注意切记不要频繁加载这里），不然将取缓存，默认为假
     * api=GetGroupMemberList
     */
    override fun getGroupMemberList(
        robotWxid: String,
        groupWxid: String,
        isRefresh: Boolean,
    ): List<CatSimpleGroupMemberInfo> {
        return lovelyCatApiCache.computeCatSimpleGroupMemberInfoList {
            post<GroupMemberListResult>(GetGroupMemberListRequestData(robotWxid, groupWxid, isRefresh)).data
        }
    }

    private data class GetGroupMemberListRequestData(
        val robot_wxid: String,
        val group_wxid: String,
        val is_refresh: Boolean,
    ) : RequestApiData("GetGroupMemberList")

    /**
     * 功能=接收好友转账
     * robot_wxid, 文本型, , 哪个机器人收到的好友转账，就填那个机器人的ID
     * from_wxid, 文本型, , 好友的ID（给你转账的那个人的ID）
     * json_msg, 文本型, , 请传入转账事件里的原消息
     * api=AcceptTransfer
     */
    override fun acceptTransfer(robotWxid: String, fromWxid: String, jsonMsg: String): LovelyCatApiResult {
        return post(AcceptTransferRequestData(robotWxid, fromWxid, jsonMsg))
    }

    private data class AcceptTransferRequestData(
        val robot_wxid: String,
        val from_wxid: String,
        val json_msg: String,
    ) : RequestApiData("AcceptTransfer")


    /**
     * 功能=同意群聊邀请
     * robot_wxid, 文本型, , 哪个机器人收到的群聊邀请，就填那个机器人的ID号
     * json_msg, 文本型, , 请传入事件的原消息
     * api=AgreeGroupInvite
     */
    override fun agreeGroupInvite(robotWxid: String, jsonMsg: String): LovelyCatApiResult {
        return post(AgreeGroupInviteRequestData(robotWxid, jsonMsg))
    }

    private data class AgreeGroupInviteRequestData(
        val robot_wxid: String,
        val json_msg: String,
    ) : RequestApiData("AgreeGroupInvite")

    /**
     *功能=同意好友请求
     *robot_wxid, 文本型, , 哪个机器人收到的好友验证，就填哪个机器人的那个ID
     *json_msg, 文本型, , 请传入好友验证事件的原消息
     *api=AgreeFriendVerify
     *
     */
    override fun agreeFriendVerify(robotWxid: String, jsonMsg: String): LovelyCatApiResult {
        return post(AgreeFriendVerifyRequestData(robotWxid, jsonMsg))
    }

    private data class AgreeFriendVerifyRequestData(
        val robot_wxid: String,
        val json_msg: String,
    ) : RequestApiData("AgreeGroupInvite")


    /**
     * 功能=修改好友备注
     * robot_wxid, 文本型, , 要操作的机器人ID
     * friend_wxid, 文本型, , 要备注的好友ID
     * note, 文本型, , 新的备注
     * api=ModifyFriendNote
     */
    override fun modifyFriendNote(robotWxid: String, friendWxid: String, note: String): LovelyCatApiResult {
        return post(ModifyFriendNoteRequestData(robotWxid, friendWxid, note))
    }

    private data class ModifyFriendNoteRequestData(
        val robot_wxid: String,
        val friend_wxid: String,
        val note: String,
    ) : RequestApiData("ModifyFriendNote")

    /**
     * 功能=删除好友
     * robot_wxid, 文本型, , 要操作的机器人ID
     * friend_wxid, 文本型, , 要删除的好友ID
     * api=DeleteFriend
     */
    override fun deleteFriend(robotWxid: String, friendWxid: String): LovelyCatApiResult {
        return post(DeleteFriendRequestData(robotWxid, friendWxid))
    }

    private data class DeleteFriendRequestData(
        val robot_wxid: String,
        val friend_wxid: String,
    ) : RequestApiData("DeleteFriend")

    /**
     * 功能=踢出群成员
     * robot_wxid, 文本型, , 要操作的机器人ID
     * group_wxid, 文本型, , 群ID
     * member_wxid, 文本型, , 群成员ID
     * api=RemoveGroupMember
     */
    override fun removeGroupMember(robotWxid: String, groupWxid: String, memberWxid: String): LovelyCatApiResult {
        return post(RemoveGroupMemberRequestData(robotWxid, groupWxid, memberWxid))
    }

    private data class RemoveGroupMemberRequestData(
        val robot_wxid: String,
        val group_wxid: String,
        val member_wxid: String,
    ) : RequestApiData("RemoveGroupMember")

    /**
     * 功能=修改群名称
     * robot_wxid, 文本型, , 要操作的机器人ID
     * group_wxid, 文本型, , 群ID
     * group_name, 文本型, , 新的群名称
     * api=ModifyGroupName
     */
    override fun modifyGroupName(robotWxid: String, groupWxid: String, groupName: String): LovelyCatApiResult {
        return post(ModifyGroupNameRequestData(robotWxid, groupWxid, groupName))
    }

    private data class ModifyGroupNameRequestData(
        val robot_wxid: String,
        val group_wxid: String,
        val group_name: String,
    ) : RequestApiData("ModifyGroupName")

    /**
     * 功能=修改群公告
     * robot_wxid, 文本型, , 要操作的机器人ID
     * group_wxid, 文本型, , 群ID
     * content, 文本型, , 新公告内容
     * api=ModifyGroupNotice
     */
    override fun modifyGroupNotice(robotWxid: String, groupWxid: String, content: String): LovelyCatApiResult {
        return post(ModifyGroupNoticeRequestData(robotWxid, groupWxid, content))
    }

    private data class ModifyGroupNoticeRequestData(
        val robot_wxid: String,
        val group_wxid: String,
        val content: String,
    ) : RequestApiData("ModifyGroupNotice")

    /**
     * 功能=建立新群
     * robot_wxid, 文本型, , 要操作的机器人ID
     * friendArr, 文本型, 数组, 要建立新群的好友数组，至少要两个人以上
     * api=BuildingGroup
     */
    override fun buildingGroup(robotWxid: String, friendArr: Array<String>): LovelyCatApiResult {
        return post(BuildingGroupRequestData(robotWxid, friendArr.asList()))
    }

    private data class BuildingGroupRequestData(
        val robot_wxid: String,
        val friend_arr: List<String>,
    ) : RequestApiData("BuildingGroup")

    /**
     * 功能=退出群聊
     * robot_wxid, 文本型, , 要操作的机器人ID
     * group_wxid, 文本型, , 群ID
     * api=QuitGroup
     */
    override fun quitGroup(robotWxid: String, groupWxid: String): LovelyCatApiResult {
        return post(QuitGroupRequestData(robotWxid, groupWxid))
    }

    private data class QuitGroupRequestData(
        val robot_wxid: String,
        val group_wxid: String,
    ) : RequestApiData("QuitGroup")

    /**
     * 功能=邀请加入群聊
     * robot_wxid, 文本型, , 要操作的机器人ID
     * group_wxid, 文本型, , 群ID
     * friend_wxid, 文本型, , 要邀请的好友ID
     * api=InviteInGroup
     */
    override fun inviteInGroup(robotWxid: String, groupWxid: String, friendWxid: String): LovelyCatApiResult {
        return post(InviteInGroupRequestData(robotWxid, groupWxid, friendWxid))
    }

    private data class InviteInGroupRequestData(
        val robot_wxid: String,
        val group_wxid: String,
        val friend_wxid: String,
    ) : RequestApiData("InviteInGroup")

    /**
     * 功能=取应用目录
     * api=GetAppDirectory
     */
    override fun getAppDirectory(): LovelyCatApiResult {
        return post("api" to "GetAppDirectory")
    }

    /**
     * 功能=添加日志
     * msg1, 文本型
     * msg2, 文本型, 可空
     * api=AppendLogs
     */
    override fun appendLogs(msg1: String, msg2: String?): LovelyCatApiResult {
        return post(AppendLogsRequestData(msg1, msg2))
    }

    private data class AppendLogsRequestData(
        val msg1: String,
        val msg2: String?,
    ) : RequestApiData("AppendLogs")

}


private inline fun <R> LovelyCatApiResult.letData(api: String, block: (String) -> R): R {
    return this.data.takeIf { it != null }?.let(block)
        ?: throw LovelyCatApiException("Api '$api' result data is null: $this")
}


