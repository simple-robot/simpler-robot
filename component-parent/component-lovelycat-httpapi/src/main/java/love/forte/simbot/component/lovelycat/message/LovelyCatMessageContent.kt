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

@file:JvmName("LovelyCatMessageContents")
package love.forte.simbot.component.lovelycat.message

import love.forte.catcode.CatCodeUtil
import love.forte.catcode.CatEncoder
import love.forte.catcode.Neko
import love.forte.simbot.api.message.MessageContent


/**
 * 可爱猫文本消息正文。
 */
public data class LovelyCatTextMessageContent(override val msg: String): MessageContent {
    /**
     * 获取此消息中的所有可能包含的cat码。
     * text message content中仅包含一个纯文本neko。
     */
    override val cats: List<Neko> = listOf(CatCodeUtil.toNeko("text", false, "text=${CatEncoder.encodeParams(msg)}"))
}


/**
 * 仅存在一个 [Neko] 信息的 message content.
 */
public sealed class LovelyCatNekoMessageContent(neko: Neko): MessageContent {
    /**
     * 消息字符串文本。
     */
    override val msg: String = neko.toString()
    /**
     * 获取此消息中的所有可能包含的cat码。
     */
    override val cats: List<Neko> = listOf(neko)
}


/**
 * 可爱猫图片消息正文。
 * (type=3,47)
 * 可爱猫的图片消息是一个本地缓存文件的绝对路径。
 * 也不知道哪个天才想到的。
 */
public data class LovelyCatImageMessageContent(private val path: String):
    LovelyCatNekoMessageContent(CatCodeUtil.nekoTemplate.image(path))

/**
 * 可爱猫视频消息正文。
 * (type=43)
 * 爱猫的视频消息也是一个本地缓存文件的绝对路径。
 */
public data class LovelyCatVideoMessageContent(private val path: String):
    LovelyCatNekoMessageContent(CatCodeUtil.getNekoBuilder("video", true).key("file").value(path).build())

/**
 * 可爱猫语音消息正文。
 * (type=34)
 * 爱猫的语言消息也是一个本地缓存文件的绝对路径(.silk)。
 */
public data class LovelyCatRecordMessageContent(private val path: String):
    LovelyCatNekoMessageContent(CatCodeUtil.nekoTemplate.record(path))

/**
 * 可爱猫分享链接正文。
 * (type=49)
 * gif图片好像也是这个类型。
 */
public data class LovelyCatShareLinkMessageContent(private val share: String):
    LovelyCatNekoMessageContent(CatCodeUtil.getNekoBuilder("share", true).key("content").value(share).build())


/**
 * 可爱猫分享名片正文。
 * (type=42)
 */
public data class LovelyCatShareCardMessageContent(private val card: String):
    LovelyCatNekoMessageContent(CatCodeUtil.getNekoBuilder("card", true).key("content").value(card).build())


/**
 * 可爱猫地理位置分享内容
 */
public data class LovelyCatLocation(
    val x: String,
    val y: String,
    val desc: String,
    val title: String
)

/**
 * 可爱猫分享地理位置正文。
 * {
     * "x":"36.678349",
     * "y":"117.041023",
     * "desc":"明湖天地D座(济南市天桥区明湖东路8号)",
     * "title":"天桥区明湖东路10-6号"
 * }
 * (type=48)
 */
public data class LovelyCatLocationMessageContent(
    private val location: LovelyCatLocation
): LovelyCatNekoMessageContent(CatCodeUtil.getNekoBuilder("location", true)
        .key("x").value(location.x)
        .key("y").value(location.y)
        .key("desc").value(location.desc)
        .key("title").value(location.title)
        .build())


/**
 * 红包消息。
 * type=2001
 * neko type: red-envelope
 */
public data class LovelyCatRedEnvelopeMessageContent(override val msg: String): MessageContent {
    companion object: Neko by CatCodeUtil.toNeko("red-envelope") {
        private val nekoList = listOf(this)
    }
    override val cats: List<Neko> get() = nekoList
}

/**
 * 小程序消息。
 * type=2002
 */
public data class LovelyCatAppMessageContent(private val app: String): LovelyCatNekoMessageContent(CatCodeUtil.getNekoBuilder("app", true).key("content").value(app).build())

/**
 * 其他未知类型消息正文。
 */
public data class LovelyCatUnknownMessageContent(private val content: String, private val type: Int): LovelyCatNekoMessageContent(CatCodeUtil.getNekoBuilder("unknown", true).key("content").value(content).key("type").value(type).build())












