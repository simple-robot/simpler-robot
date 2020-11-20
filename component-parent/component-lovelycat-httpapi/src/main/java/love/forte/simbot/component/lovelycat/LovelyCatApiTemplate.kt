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
import love.forte.simbot.http.template.assertBody
import love.forte.simbot.serialization.json.JsonSerializerFactory


/**
 * lovely cat api template.
 */
public interface LovelyCatApiTemplate {

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
        memberName: String,
        msg: String
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
        targetUrl: String?, picUrl: String?, iconUrl: String?
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



}


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
    private val jsonSerializerFactory: JsonSerializerFactory
) : LovelyCatApiTemplate {

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
        jsonSerializerFactory: JsonSerializerFactory
    ) : this(httpTemplate, "http://$ip:$host$path", jsonSerializerFactory)


    /**
     * do post.
     */
    private inline fun <reified T> post(vararg pair: Pair<String, *>): T {
        return post(mapOf(*pair))
    }

    /**
     * do post.
     * @throws love.forte.simbot.http.HttpTemplateException
     * @param requestBody Any?
     * @return T
     */
    private inline fun <reified T> post(requestBody: Any?): T {
        val resp = httpTemplate.post(url = url, headers = null, requestBody = requestBody, responseType = T::class.java)
        System.err.println(resp.content)
        return resp.assertBody()
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
        return postForLovelyCatResult(
            "api" to "GetRobotName",
            "robot_wxid" to robotWxid
        ).let { r ->
            r.letData("GetRobotName") { RobotName(it) }
        }
    }


    /**
     * 功能=取登录账号头像
     * robot_wxid, 文本型
     * api=GetRobotHeadimgurl
     * @return RobotHeadImgUrl
     */
    override fun getRobotHeadImgUrl(robotWxid: String): RobotHeadImgUrl {
        return postForLovelyCatResult(
            "api" to "GetRobotHeadimgurl",
            "robot_wxid" to robotWxid
        ).let { r ->
            r.letData("GetRobotHeadimgurl") { RobotHeadImgUrl(it) }
        }
    }


    /**
     * 功能=取登录账号列表
     * api=GetLoggedAccountList
     */
    override fun getLoggedAccountList(): LoggedAccountList {
        return postForLovelyCatResult("api" to "GetLoggedAccountList").let {
            LoggedAccountList(loggedAccountListSerializer.fromJson(it.data))
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
        val msg: String
    ){ val api = "SendTextMsg" }


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
        val path: String
    ){ val api = "SendImageMsg" }


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
         val path: String
    ){ val api = "SendVideoMsg" }


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
        val path: String
    ){ val api = "SendFileMsg" }


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
        val card_data: String
    ){ val api = "SendCardMsg" }


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
        memberName: String,
        msg: String
    ): LovelyCatApiResult {
        return post(SendGroupMsgAndAtRequestData(robotWxid, groupWxid, memberWxid, memberName, msg))
    }
    private data class SendGroupMsgAndAtRequestData(
        val robot_wxid: String,
        val group_wxid: String,
        val member_wxid: String,
        val member_name: String,
        val msg: String
    ){ val api = "SendGroupMsgAndAt" }


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
        val robot_wxid: String, val to_wxid: String, val path: String
    ){ val api = "SendEmojiMsg" }

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
        iconUrl: String?
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
        val icon_url: String?
    ) { val api = "SendLinkMsg" }


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
        val type: Int
    ) { val api = "SendMusicMsg" }
}

























private inline fun <R> LovelyCatApiResult.letData(api: String, block: (String) -> R): R {
    return this.data.takeIf { it != null }?.let(block)
        ?: throw LovelyCatApiException("Api '$api' result data is null: $this")
}


