/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MessageParser.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

@file:JvmName("MiraiMessageParsers")

package love.forte.simbot.component.mirai.utils

import cn.hutool.core.io.FileUtil
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import love.forte.catcode.*
import love.forte.catcode.codes.Nyanko
import love.forte.simbot.component.mirai.message.*
import love.forte.simbot.api.message.events.*
import love.forte.simbot.api.message.events.MessageContent
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.uploadAsImage
import java.io.File
import java.io.InputStream
import java.net.URL

/**
 * message chain 为 [EmptyMessageChain] 的 [MiraiMessageContent]。
 */
@get:JvmName("getEmptyMiraiMessageContent")
public val EmptyMiraiMessageContent: MiraiMessageContent = MiraiMessageChainContent(EmptyMessageChain)

/**
 * 将一个 [MessageContent] 转化为一个 [MiraiMessageContent]。
 */
public suspend fun MessageContent.toMiraiMessageContent(): MiraiMessageContent {
    return when (this) {
        is MiraiMessageContent -> return this
        // 预期内的消息。
        is ExpectedMessageContent -> when (this) {
            is BoxedMessageContent -> when (this) {
                EmptyMessageContent -> EmptyMiraiMessageContent
                is SingleMessageContent -> single.toMiraiMessageContent()
                is CompoundMessageContent -> {
                    // 复合型，转化.
                    val miraiMsgList =
                        msgList.map {
                            GlobalScope.async { it.toMiraiMessageContent() }
                        }.map { it.await() }
                    MiraiListMessageContent(miraiMsgList)
                }
            }

            // image content
            is ImageMessageContent -> {
                // image message content.
                if (this is MiraiImageMessageContent) {
                    this
                } else {
                    // 先尝试获取path
                    val path: String? = this.path
                    val pathFile: File? = this.path?.let {
                        FileUtil.file(it)
                    }.takeIf { it?.exists() == true }

                    if (pathFile != null) {
                        path as String
                        MiraiImageMessageContent(
                            id = path,
                            path = path,
                            flash = this.flash
                        ) { c -> pathFile.uploadAsImage(c) }
                    } else {
                        // 没有本地文件，查看网络文件
                        val url: String = path?.takeIf { it.startsWith("http") }
                            ?: this.getUrlOrNull()
                            ?: throw IllegalStateException("Unable to locate file: file path is not exists and no url exists.")
                        val imageURL = URL(url)
                        MiraiImageMessageContent(id = url, url = url, flash = this.flash) { c ->
                            imageURL.toStream().uploadAsImage(c)
                        }
                    }
                }
            }
            is VoiceMessageContent -> {
                TODO()
            }

            is TextMessageContent -> {
                // same as 'else'.
                msg?.toMiraiMessageContent() ?: EmptyMiraiMessageContent
            }
        }
        else -> {
            msg?.toMiraiMessageContent() ?: EmptyMiraiMessageContent
        }
    }

}


/**
 * 将可能存在catcode的字符串文本转化为 [MiraiMessageContent]。
 */
public fun String.toMiraiMessageContent(): MiraiMessageContent {
    return CatCodeUtil.split(this) {
        // is a cat code.
        if (startsWith(CAT_HEAD)) Nyanko.byCode(this).toMiraiMessageContent()
        // not normal text.
        else MiraiSingleMessageContent(PlainText(this.deCatText()))
    }.toList().let { MiraiListMessageContent(it) }
}

/**
 * [Neko] 转化为 [MiraiMessageContent]。
 */
public fun Neko.toMiraiMessageContent(): MiraiMessageContent {
    return when (this.type) {
        "at" -> {
            val all = this["all"] == "true"
            val codes = this["code"]?.split(",")?.map { it.toLong() } ?: emptyList()
            if (all && codes.isEmpty()) {
                MiraiSingleMessageContent(AtAll)
            } else if (codes.isEmpty()) {
                throw IllegalArgumentException("There is no at target.")
            } else {
                // codes not empty.
                codes.atContent()
            }
        }

        // face
        "face" -> {
            val id: Int = this["id"]?.toInt() ?: throw IllegalArgumentException("no face 'id' in $this.")
            MiraiSingleMessageContent(Face(id))
        }

        // 戳一戳，窗口抖动
        "poke", "shake" -> {
            val type: Int = this["type"]?.toInt() ?: return MiraiSingleMessageContent(PokeMessage.Poke)
            val id: Int = this["id"]?.toInt() ?: -1
            val code: Long = this["code"]?.toLong() ?: -1L
            val catString = this.toString()
            MiraiSingleMessageContent({
                if (it is Group) {
                    // nudge, need code
                    if (code == -1L) {
                        throw IllegalStateException("Unable to locate the target for nudge: no 'code' parameter in cat ${this@toMiraiMessageContent}.")
                    }

                    val nudge = it.getOrNull(code)?.nudge()
                        ?: throw IllegalArgumentException("cannot found nudge target: no such member($code) in group($id).")
                    it.launch { nudge.sendTo(it) }
                    EmptySingleMessage
                } else {
                    // poke.
                    PokeMessage.values.find { it.type == type && it.id == id } ?: PokeMessage.Poke
                }
            }) { catString }
        }


        // image
        "image" -> {
            // file, or url
            val filePath = this["file"]
            val file: File? = filePath?.let { FileUtil.file(it) }?.takeIf { it.exists() }
            val flash: Boolean = this["flash"] == "true"
            if (file != null) {
                // 存在文件
                MiraiImageMessageContent(id = filePath, path = filePath, flash = flash) { c -> file.uploadAsImage(c) }
            } else {
                // 没有文件，看看有没有url。
                val url = filePath?.takeIf { it.startsWith("http") }?.let { URL(it) }
                    ?: this["url"]?.let { URL(it) }
                    ?: throw IllegalArgumentException("The img has no source in $this")

                val urlId = url.toExternalForm()
                MiraiImageMessageContent(id = urlId, url = urlId, flash = flash) { c ->
                    url.toStream().uploadAsImage(c)
                }
            }
        }

        // todo...

        else -> {
            MiraiSingleMessageContent(PlainText("[$type(not support)]"))
        }

    }


}


/**
 * 将一个 [MessageChain] 转化为携带catcode的字符串。
 */
public fun MessageChain.toSimbotString(): String {
    return this.asSequence().map { it.toSimbotString() }.joinToString()
}


/**
 * 将一个 [SingleMessage] 转化为携带catcode的字符串。
 */
public fun SingleMessage.toSimbotString(): String {
    return when (this) {
        AtAll -> CatCodeUtil.stringTemplate.atAll()
        is At -> CatCodeUtil.stringTemplate.at(target)
        // 普通文本, 转义
        is PlainText -> content.enCatText()
        is Face -> CatCodeUtil.stringTemplate.face(id.toString())
        is PokeMessage -> {
            // poke, 戳一戳
            CatCodeUtil.getStringCodeBuilder("poke")
                .key("type").value(type)
                .key("id").value(id)
                .build()
        }
        is Image -> {
            // cat code中不再携带url参数
            // CatCodeUtil.stringTemplate.image()
            CatCodeUtil.getStringCodeBuilder("image")
                .key("id").value(imageId)
                .build()
        }
        is FlashImage -> {
            val img = this.image
            // cat code中不再携带url参数
            // CatCodeUtil.stringTemplate.image()
            CatCodeUtil.getStringCodeBuilder("image")
                .key("id").value(img)
                .key("flash").value(true)
                .build()
        }
        is Voice -> {
            CatCodeUtil.getStringCodeBuilder("voice")
                .key("id").value("$fileName.$fileSize")
                .key("name").value(fileName)
                .key("size").value(fileSize).apply {
                    url?.let { key("url").value(it) }
                }
                .build()

        }
        // 引用回复
        is QuoteReply -> TODO("quoteReply to String.")
        // 富文本，xml或json
        is RichMessage -> TODO("rich message to String.")

        // else.
        else -> "${CAT_HEAD}mirai,text=${this.toString().deCatParam()}]"
    }
}


/**
 * ktor http client
 */
private val httpClient: HttpClient = HttpClient()


/**
 * 通过http网络链接得到一个输入流。
 * 通常认为是一个http-get请求
 */
public suspend fun URL.toStream(): InputStream {
    val urlString = this.toString()
    // QQLog.debug("mirai.http.connection.try", urlString)
    val response = httpClient.get<HttpResponse>(this)
    val status = response.status
    if (status.value < 300) {
        // QQLog.debug("mirai.http.connection.success", urlString)
        // success
        return response.content.toInputStream()
    } else {
        throw IllegalStateException("connection to '$urlString' failed ${status.value}: ${status.description}")
    }
}