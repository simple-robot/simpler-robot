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

import catcode.CAT_HEAD
import catcode.CatCodeUtil
import catcode.codes.Nyanko
import love.forte.common.utils.Carrier
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.component.lovelycat.message.LovelyCatForSendMessageContent
import love.forte.simbot.component.lovelycat.message.event.GROUP_SUFFIX


private object Empty


/**
 * 可爱猫送信器。
 */
public class LovelyCatSender(
    private val botId: String,
    private val api: LovelyCatApiTemplate,
    private val def: Sender
) : Sender {

    /**
     * 通过携带 catCode 的文本消息进行发送消息发送。
     */
    private fun sendMsg(target: String, msg: String) {
            val needAtCode: MutableList<String> = mutableListOf()
            var atAll = false

            // msgList
            CatCodeUtil.split(msg) {
                if (startsWith(CAT_HEAD)) {
                    // is cat
                    val neko = Nyanko.byCode(this)
                    when (neko.type) {
                        // at.
                        "at" -> {
                            neko["all"]?.takeIf { it == "true" }?.let {
                                atAll = true
                            }

                            neko["code"]?.let {
                                if (it == "all") atAll = true
                                else needAtCode.add(it)
                            }

                        }
                        // send img
                        "image" -> {
                            // maybe... local file to base64
                            val path = neko["file"] ?: neko["url"] ?: throw IllegalArgumentException("cannot found param 'path' or 'url' in $neko.")
                            api.sendImageMsg(botId, target, path)
                        }

                        // send video
                        "video" -> {
                            // 只支持本地文件。
                            val path = neko["file"] ?: throw IllegalArgumentException("cannot found param 'path' in $neko.")
                            api.sendVideoMsg(botId, target, path)
                        }

                        // send file video
                        "file" -> {
                            val path = neko["file"] ?: throw IllegalArgumentException("cannot found param 'path' in $neko.")
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
                            // ignore?
                        }

                    }

                    Empty
                } else {
                    needAtCode.takeIf { it.isNotEmpty() }?.let {
                        val ats = needAtCode.joinToString(",")
                        needAtCode.clear()
                        api.sendGroupMsgAndAt(botId, target, ats, null, this)
                    } ?: run {
                        if (atAll) {
                            api.modifyGroupNotice(botId, target, this.takeIf { it.isNotBlank() } ?: "@全体成员")
                            atAll = false
                        } else {
                            api.sendTextMsg(botId, target, this)
                        }
                    }

                    Empty
                }
            }


            // need at.
            needAtCode.takeIf { it.isNotEmpty() }?.let {
                api.sendGroupMsgAndAt(botId, target, it.joinToString(","), null, " ")
            }
        // }
    }


    /**
     * 基于 [MessageContent] 发送一个消息。
     */
    private fun sendMsg(target: String, msg: MessageContent) {
        if (msg is LovelyCatForSendMessageContent) {
            // 先发image
            // 然后发at + msg
            val images = msg.image
            if (images.isNotEmpty()) {
                // not empty
                images.forEach {
                    api.sendImageMsg(botId, target, it)
                }
            }
            val msgText = msg.text
            val atAll = msg.atAll
            if (atAll) {
                api.modifyGroupNotice(botId, target, msgText)
            } else {
                val ats = msg.at
                if (ats.isNotEmpty()) {
                    api.sendGroupMsgAndAt(botId, target, ats.joinToString(","), null, msgText)
                } else {
                    api.sendTextMsg(botId, target, msgText)
                }
            }

            return
        }


        val needAtCode: MutableList<String> = mutableListOf()
        var atAll = false

        msg.cats.forEach { neko ->
            when(neko.type) {
                "at" -> {
                    neko["all"]?.takeIf { it == "true" }?.let {
                        atAll = true
                    }

                    neko["code"]?.let {
                        if (it == "all") atAll = true
                        else needAtCode.add(it)
                    }
                }
                // send img
                "image" -> {
                    // maybe... local file to base64
                    val path = neko["file"] ?: neko["url"] ?: throw IllegalArgumentException("cannot found param 'file' or 'url' in $neko.")
                    api.sendImageMsg(botId, target, path)
                }

                // send video
                "video" -> {
                    // 只支持本地文件。
                    val path = neko["file"] ?: throw IllegalArgumentException("cannot found param 'file' in $neko.")
                    api.sendVideoMsg(botId, target, path)
                }

                // send file video
                "file" -> {
                    val path = neko["file"] ?: throw IllegalArgumentException("cannot found param 'file' in $neko.")
                    api.sendFileMsg(botId, target, path)
                }

                // send card
                "card" -> {
                    val content = neko["content"] ?: throw IllegalArgumentException("cannot found param 'content' in $neko.")
                    api.sendCardMsg(botId, target, content)
                }

                "link", "share" -> {
                    val title = neko["title"] ?: throw IllegalArgumentException("cannot found param 'title' in $neko.")
                    val text = neko["text"] ?: throw IllegalArgumentException("cannot found param 'text' in $neko.")
                    val targetUrl = neko["target"]
                    val pic = neko["pic"]
                    val icon = neko["icon"]

                    api.sendLinkMsg(botId, target, title, text, targetUrl, pic, icon)
                }

                // text.
                "text" -> {
                    val textMsg = neko["text"] ?: ""

                    needAtCode.takeIf { it.isNotEmpty() }?.let {
                        val ats = needAtCode.joinToString(",")
                        needAtCode.clear()
                        api.sendGroupMsgAndAt(botId, target, ats, null, textMsg)
                    } ?: run {
                        if (atAll) {
                            api.modifyGroupNotice(botId, target, textMsg.takeIf { it.isNotBlank() } ?: "@全体成员")
                            atAll = false
                        } else {
                            api.sendTextMsg(botId, target, textMsg)
                        }
                    }
                }

                else -> {
                    // ignore?
                }
            }
        }

        // need at.
        needAtCode.takeIf { it.isNotEmpty() }?.let {
            api.sendGroupMsgAndAt(botId, target, it.joinToString(","), null, " ")
        }
    }





    /**
     * 发送一条群消息。 回执必然为空。
     *
     */
    override fun sendGroupMsg(group: String, msg: String): Carrier<out Flag<GroupMsg.FlagContent>> {
        sendMsg(group, msg)
        return Carrier.empty()
    }

    override fun sendGroupMsg(group: Long, msg: String): Carrier<out Flag<GroupMsg.FlagContent>> =
        sendGroupMsg("$group$GROUP_SUFFIX", msg)


    /**
     * 发送一条群消息。回执必然为空。
     *
     */
    override fun sendGroupMsg(group: String, msg: MessageContent): Carrier<out Flag<GroupMsg.FlagContent>> {
        sendMsg(group, msg)
        return Carrier.empty()
    }

    override fun sendGroupMsg(group: Long, msg: MessageContent): Carrier<out Flag<GroupMsg.FlagContent>> =
        sendGroupMsg("$group$GROUP_SUFFIX", msg)






    /**
     * 发送一条私聊消息。 回执必然为空。
     *
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
     * 发布群公告。微信发送群公告会at全体成员。
     * @param group 群号
     * @param title 标题
     * @param text   正文
     *
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
            Carrier.get(result.result?.toLowerCase() == "ok")

        } ?: Carrier.get(false)
    }

    override fun sendGroupNotice(
        group: Long,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean
    ): Carrier<Boolean> =
        sendGroupNotice("$group$GROUP_SUFFIX", title, text, popUp, top, toNewMember, confirm)



    /**
     * 不支持群签到。
     */
    @Deprecated("Not support")
    override fun sendGroupSign(group: String, title: String, message: String): Carrier<Boolean> {
        return def.sendGroupSign(group, title, message)
    }

    @Suppress("DEPRECATION")
    override fun sendGroupSign(group: Long, title: String, message: String): Carrier<Boolean> =
        sendGroupSign("$group$GROUP_SUFFIX", title, message)

}




