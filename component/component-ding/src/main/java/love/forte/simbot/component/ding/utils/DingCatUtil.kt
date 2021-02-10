/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  component-ding
 * File     DingCQUtil.kt
 * Date  2020/8/8 下午5:44
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

@file:Suppress("MemberVisibilityCanBePrivate")

package love.forte.simbot.component.ding.utils

import love.forte.catcode.CatCodeUtil
import love.forte.catcode.CodeTemplate
import love.forte.catcode.Neko
import love.forte.simbot.component.ding.exception.DingSpecialMessageException
import love.forte.simbot.component.ding.exception.DingUnsupportedCodeException
import love.forte.simbot.component.ding.messages.*

/**
 * 钉钉消息与CQ码之间的转化工具类
 */
object DingCatUtil {
    /**
     * 将字符串消息转化为钉钉特殊消息链
     */
    fun msgToDing(msg: String): DingSpecialMessageChain {
        val splitMsg = CatCodeUtil.split(msg)
        val msgList = splitMsg.asSequence().map {
            if (msg.startsWith("[CQ:")) {
                catCodeToDing(Neko.of(msg))
            } else msg.toDingText()
        }.toList()
        return DingSpecialMessageChain(msgList.toTypedArray())
    }


    /**
     * 将[KQCode] 转化为 [DingSpecialMessage]
     */
    fun catCodeToDing(kqCode: Neko): DingSpecialMessage {
        return when (kqCode.type) {
            "at" -> {
                // 获取qq参数
                val codes = kqCode["qq"] ?: kqCode["code"] ?: ""
                return CatDingTemplate.at(codes)
            }
            "ding-markdown" -> {
                val title = kqCode["title"] ?: throw DingSpecialMessageException("noCqParam: markdown, title")
                val text = kqCode["text"] ?: ""
                DingMarkdown(title, text)
            }
            // type link
            "ding-link" -> {
                val title = kqCode["title"] ?: throw DingSpecialMessageException("noCqParam: link, title")
                val text = kqCode["text"] ?: ""
                val messageUrl = kqCode["messageUrl"] ?: throw DingSpecialMessageException("noCqParam: link, messageUrl")
                val picUrl = kqCode["picUrl"]
                DingLink(title, text, messageUrl, picUrl)
            }

            // actionCard
            // [CQ:actionCard,...btnTitle=,btnUrl=]
            "ding-actionCard" -> {
                val title = kqCode["title"] ?: throw DingSpecialMessageException("noCqParam: actionCard, title")
                val text = kqCode["text"] ?: ""
                val btnOrientation = kqCode["text"] ?: "1"
                val btnTitle = kqCode["btnTitle"] ?: throw DingSpecialMessageException("noCqParam: actionCard, btnTitle")
                val btnUrl = kqCode["btnUrl"] ?: throw DingSpecialMessageException("noCqParam: actionCard, btnUrl")
                val btnTitleList = btnTitle.split(",")
                val btnUrlList = btnUrl.split(",")
                if(btnTitleList.size != btnUrlList.size){
                    throw DingSpecialMessageException("paramErr: actionCard. btnTitle.size != btnUrl.size")
                }
                if(btnTitleList.isEmpty()){
                    throw DingSpecialMessageException("paramErr: actionCard. btn param is empty.")
                }


                // return
                if(btnTitleList.size == 1){
                    DingWholeActionCard(title, text, btnOrientation, btnTitleList.first(), btnUrlList.first())
                }else{
                    val btns = btnTitleList.mapIndexed { index, btnT ->
                        val btnU = btnUrlList[index]
                        DingAutonomyActionCardButtons(btnT, btnU)
                    }
                    DingAutonomyActionCard(title, text, btnOrientation, btns.toTypedArray())
                }
            }

//            "ding-feedCard" -> {
//                // feed card 转化为CQ还挺麻烦的, 毕竟设计到数组
//
//
//
//            }
            else -> DingText(kqCode.toString())
        }
    }

}


@Suppress("NOTHING_TO_INLINE")
internal inline fun notSupport(name: String): Nothing {
    throw DingUnsupportedCodeException("notSupport: $name")
}



/**
 * 钉钉消息对于[CodeTemplate]的实现，目前只支持很少的类型：
 * - [CatDingTemplate.at]
 * - [CatDingTemplate.atAll]
 * - [CatDingTemplate.image]
 * 其余消息计划均转化为text类型的消息
 *
 */
object CatDingTemplate : CodeTemplate<DingSpecialMessage> {
    /**
     * at类型的Ding消息
     * 此处如果是多个code，可以用逗号分隔
     */
    override fun at(code: String): DingAt {
        return if (code.isEmpty()) {
            return DingAt.empty
        } else at(code.split(","))
    }

    /**
     * at类型的Ding消息
     * 可以同时存在code和all
     */
    fun at(code: List<String>): DingAt {
        val codeList = code.toMutableList()
        val isAtAll = codeList.remove("all")
        return DingAt(codeList.toTypedArray(), isAtAll)
    }

    /**
     * @see at(List)
     */
    fun at(codes: Array<String>): DingAt = at(codes.asList())


    /**
     * at all, 使用的是[DingAt.atAll]静态单例
     */
    override fun atAll(): DingAt = DingAt.atAll


    /**
     * 此类型将会被转化为[DingLink]类型，且跳转链接与图片链接一样
     * @param file 图片的链接
     * @param flash 无效参数
     */
    override fun image(file: String, flash: Boolean): DingLink {
        return DingLink("图片", "", file, file)
    }


    @Deprecated("notSupport")
    override fun bface(id: String): DingSpecialMessage {
        notSupport("bface")
    }

    override fun customMusic(url: String, audio: String, title: String, content: String?, image: String?): DingSpecialMessage {
        notSupport("customMusic")
    }

    override fun dice(): DingSpecialMessage {
        notSupport("dice")
    }

    override fun dice(type: String): DingSpecialMessage {
        notSupport("dice")
    }

    override fun face(id: String): DingSpecialMessage {
        notSupport("face")
    }


    override fun music(type: String, id: String, style: String?): DingSpecialMessage {
        notSupport("music")
    }

    override fun record(file: String, magic: Boolean): DingSpecialMessage {
        notSupport("record")
    }

    override fun rps(): DingSpecialMessage {
        notSupport("rps")
    }

    override fun rps(type: String): DingSpecialMessage {
        notSupport("rps")
    }

    override fun sface(id: String): DingSpecialMessage {
        notSupport("sface")
    }

    override fun shake(): DingSpecialMessage {
        notSupport("shake")
    }

    override fun share(url: String, title: String, content: String?, image: String?): DingSpecialMessage {
        notSupport("share")
    }

}
