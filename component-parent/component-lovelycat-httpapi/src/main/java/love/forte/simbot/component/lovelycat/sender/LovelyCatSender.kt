/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatSender.kt
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

package love.forte.simbot.component.lovelycat.sender

import love.forte.catcode.CAT_HEAD
import love.forte.catcode.CatCodeUtil
import love.forte.catcode.codes.Nyanko
import love.forte.common.utils.Carrier
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.api.sender.ErrorSender
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate



private object Empty


/**
 * 可爱猫送信器
 */
public class LovelyCatSender(
    private val botId: String,
    private val api: LovelyCatApiTemplate
) : Sender {

    /**
     * 通过携带 catCode 的文本消息进行发送消息发送。
     */
    private fun sendMsg(target: String, msg: String) {
        // if (!msg.contains(CAT_HEAD)) {
        //     // only msg
        //     api.sendTextMsg(botId, group, msg)
        // } else {

            var needAtCode: String? = null

            // msgList
            CatCodeUtil.split(msg) {
                if (startsWith(CAT_HEAD)) {
                    // is cat
                    val neko = Nyanko.byCode(this)
                    when (neko.type) {
                        // at.
                        "at" -> {
                            val needAt = needAtCode

                            if (needAt != null) {
                                // send at.
                                api.sendGroupMsgAndAt(botId, target, needAt, "", "")
                            }
                            needAtCode = neko["code"]

                        }
                        // send img
                        "image" -> {
                            // maybe... local file to base64
                            val path = neko["path"] ?: neko["url"] ?: throw IllegalArgumentException("cannot found param 'path' or 'url' in $neko.")
                            api.sendImageMsg(botId, target, path)
                        }

                        // send video
                        "video" -> {
                            // 只支持本地文件。
                            val path = neko["path"] ?: throw IllegalArgumentException("cannot found param 'path' in $neko.")
                            api.sendVideoMsg(botId, target, path)
                        }

                        // send file video
                        "file" -> {
                            val path = neko["path"] ?: throw IllegalArgumentException("cannot found param 'path' in $neko.")
                            api.sendFileMsg(botId, target, path)
                        }

                        // send card
                        "card" -> {
                            val content = neko["content"] ?: throw IllegalArgumentException("cannot found param 'content' in $neko.")
                            api.sendCardMsg(botId, target, content)
                        }

                        "link", "share" -> {
                            /*
                            title, 文本型, , 链接标题
                            text, 文本型, , 链接内容
                            target_url, 文本型, 可空, 跳转链接
                            pic_url, 文本型, 可空, 图片的链接
                            icon_url, 文本型, 可空, 图标的链接
                             */
                            val title = neko["title"] ?: throw IllegalArgumentException("cannot found param 'title' in $neko.")
                            val text = neko["text"] ?: throw IllegalArgumentException("cannot found param 'text' in $neko.")
                            val targetUrl = neko["target"]
                            val pic = neko["pic"]
                            val icon = neko["icon"]

                            api.sendLinkMsg(botId, target, title, text, targetUrl, pic, icon)
                        }
                        else -> {
                            // todo ?
                        }

                    }

                    Empty
                } else {
                    needAtCode?.let {
                        api.sendGroupMsgAndAt(botId, target, it, "", this)
                    } ?: kotlin.run {
                        // no at
                        api.sendTextMsg(botId, target, this)
                    }
                    Empty
                }
            }

            // need at.
            needAtCode?.let {
                api.sendGroupMsgAndAt(botId, target, it, "", "")
            }
        // }
    }


    /**
     * 基于 [MessageContent] 发送一个消息。
     */
    private fun sendMsg(target: String, msg: MessageContent) {
        var needAtCode: String? = null

        msg.cats.forEach { neko ->
            when(neko.type) {
                "at" -> {
                    needAtCode?.let {
                        api.sendGroupMsgAndAt(botId, target, it, "", "")
                    }
                    needAtCode = neko["code"]
                }
                // send img
                "image" -> {
                    // maybe... local file to base64
                    val path = neko["path"] ?: neko["url"] ?: throw IllegalArgumentException("cannot found param 'path' or 'url' in $neko.")
                    api.sendImageMsg(botId, target, path)
                }

                // send video
                "video" -> {
                    // 只支持本地文件。
                    val path = neko["path"] ?: throw IllegalArgumentException("cannot found param 'path' in $neko.")
                    api.sendVideoMsg(botId, target, path)
                }

                // send file video
                "file" -> {
                    val path = neko["path"] ?: throw IllegalArgumentException("cannot found param 'path' in $neko.")
                    api.sendFileMsg(botId, target, path)
                }

                // send card
                "card" -> {
                    val content = neko["content"] ?: throw IllegalArgumentException("cannot found param 'content' in $neko.")
                    api.sendCardMsg(botId, target, content)
                }

                "link", "share" -> {
                    /*
                    title, 文本型, , 链接标题
                    text, 文本型, , 链接内容
                    target_url, 文本型, 可空, 跳转链接
                    pic_url, 文本型, 可空, 图片的链接
                    icon_url, 文本型, 可空, 图标的链接
                     */
                    val title = neko["title"] ?: throw IllegalArgumentException("cannot found param 'title' in $neko.")
                    val text = neko["text"] ?: throw IllegalArgumentException("cannot found param 'text' in $neko.")
                    val targetUrl = neko["target"]
                    val pic = neko["pic"]
                    val icon = neko["icon"]

                    api.sendLinkMsg(botId, target, title, text, targetUrl, pic, icon)
                }

                else -> {
                    // todo?
                }
            }
        }
    }





    /**
     * 发送一条群消息。
     * 不支持
     * @param group String 群号
     * @param msg String   消息正文
     * @return Carrier<Flag<GroupMsg.FlagContent>>. Flag为发出的消息的标识，可用于消息撤回。但是有可能为null（例如发送的消息无法撤回，比如戳一戳之类的，或者压根不支持撤回的）。
     */
    override fun sendGroupMsg(group: String, msg: String): Carrier<out Flag<GroupMsg.FlagContent>> {
        sendMsg(group, msg)
        return Carrier.empty()
    }

    /**
     * 发送一条群消息。
     * @param group String 群号
     * @param msg String   消息正文
     * @return Carrier<Flag<GroupMsg.FlagContent>>. Flag为发出的消息的标识，可用于消息撤回。但是有可能为null（例如发送的消息无法撤回，比如戳一戳之类的，或者压根不支持撤回的）。
     */
    override fun sendGroupMsg(group: String, msg: MessageContent): Carrier<out Flag<GroupMsg.FlagContent>> {
        sendMsg(group, msg)
        return Carrier.empty()
    }





    /**
     * 发送一条私聊消息
     * @param code String 好友账号或者接收人账号
     * @param group String? 如果你发送的是一个群临时会话，此参数代表为群号。可以为null。
     * @param msg String  消息正文
     * @return PrivateMsgReceipts 私聊回执
     */
    override fun sendPrivateMsg(code: String, group: String?, msg: String): Carrier<out Flag<PrivateMsg.FlagContent>> {
        sendMsg(code, msg)
        return Carrier.empty()
    }

    override fun sendPrivateMsg(
        code: String,
        msg: MessageContent
    ): Carrier<out Flag<PrivateMsg.FlagContent>> {
        sendMsg(code, msg)
        return Carrier.empty()
    }

    /**
     * 发布群公告
     * @param group 群号
     * @param title 标题
     * @param text   正文
     * @param popUp  不支持
     * @param top    不支持
     * @param toNewMember 不支持
     * @param confirm 不支持
     * @return 是否发布成功
     */
    override fun sendGroupNotice(
        group: String,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean
    ): Carrier<Boolean> {
        // compute total length.
        val length = (title?.length ?: 0) + (text?.length ?: 0)

        return length.takeIf { it > 0 }?.let { len ->

            val sb = StringBuilder(len)

            title?.let { sb.append(it).appendLine() }
            text?.let { sb.append(it) }

            val result = api.modifyGroupNotice(botId, group, sb.toString())
            Carrier.get(result.result.toLowerCase() == "ok")

        } ?: Carrier.get(false)
    }


    /**
     * 不支持群签到。
     */
    override fun sendGroupSign(group: String, title: String, message: String): Carrier<Boolean> {
        ErrorSender.sendGroupSign(group, title, message)
    }
}




