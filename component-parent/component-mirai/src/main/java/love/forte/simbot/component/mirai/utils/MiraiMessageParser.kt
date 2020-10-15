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
import love.forte.catcode.*
import love.forte.catcode.codes.Nyanko
import love.forte.simbot.component.mirai.message.*
import love.forte.simbot.core.api.message.*
import love.forte.simbot.core.api.message.MessageContent
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.uploadAsImage
import net.mamoe.mirai.utils.toExternalImage
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
        is ExpectedMessageContent -> when {
            this.isEmpty() -> EmptyMiraiMessageContent
            this.isSingle() -> this.single.toMiraiMessageContent()
            // compound content
            this.isCompound() -> {
                if (first is MiraiMessageContent && second is MiraiMessageContent) {
                    MiraiCompoundMessageContent((first as MiraiMessageContent), (second as MiraiMessageContent))
                } else {
                    val firstContentDef = GlobalScope.async { first.toMiraiMessageContent() }
                    val secondContentDef = GlobalScope.async { second.toMiraiMessageContent() }
                    val firstContent = firstContentDef.await()
                    val secondContent = secondContentDef.await()
                    MiraiCompoundMessageContent(firstContent, secondContent)
                }
            }

            // image content
            this is ImageMessageContent -> {
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
                        MiraiImageMessageContent(id = path, path = path, flash = this.flash) { c -> pathFile.uploadAsImage(c) }
                    } else {
                        // 没有本地文件，查看网络文件
                        val url: String = path?.takeIf { it.startsWith("http") }
                            ?: this.getUrlOrNull()
                            ?: throw IllegalStateException("Unable to locate file: file path is not exists and no url exists.")
                        val imageURL = URL(url)
                        MiraiImageMessageContent(id = url, url = url, flash = this.flash) { c -> imageURL.toStream().uploadAsImage(c) }
                    }
                }
            }
            this is VoiceMessageContent -> {
                TODO()
            }
            // this is TextMessageContent -> {
            // same as 'else'.
            // }
            else -> {
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
    }.reduce { acc, miraiMessageContent ->
        MiraiCompoundMessageContent(acc, miraiMessageContent)
    }
}

/**
 * [Neko] 转化为 [MiraiMessageContent]。
 */
public fun Neko.toMiraiMessageContent() : MiraiMessageContent {
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

        "face" -> {
            val id: Int = this["id"]?.toInt() ?: throw IllegalArgumentException("no face 'id' in $this.")
            MiraiSingleMessageContent(Face(id))
        }

        // image
        "image" -> {
            // file, or url
            val filePath = this["file"]
            val file: File? = filePath?.let { FileUtil.file(it) }?.takeIf { it.exists() }
            val flash: Boolean = this["flash"] == "true"
            if(file != null) {
                // 存在文件
                MiraiImageMessageContent(id = filePath, path = filePath, flash = flash) { c -> file.uploadAsImage(c) }
            } else {
                // 没有文件，看看有没有url。
                val url = filePath?.takeIf { it.startsWith("http") }?.let { URL(it) }
                    ?: this["url"]?.let { URL(it) }
                    ?: throw IllegalArgumentException("The img has no source in $this")

                val urlId = url.toExternalForm()
                MiraiImageMessageContent(id = urlId, url = urlId, flash = flash) { c -> url.toStream().uploadAsImage(c) }
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
    return when(this) {
        AtAll -> CatCodeUtil.stringTemplate.atAll()

        else -> "[Cat:TODO,text=${this.toString().deCatParam()}]"
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
private suspend fun URL.toStream(): InputStream {
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