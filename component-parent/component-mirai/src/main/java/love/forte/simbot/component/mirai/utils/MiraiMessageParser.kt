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
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import love.forte.catcode.*
import love.forte.catcode.codes.Nyanko
import love.forte.simbot.api.message.events.*
import love.forte.simbot.api.message.events.MessageContent
import love.forte.simbot.component.mirai.message.*
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.uploadAsImage
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream

/**
 * message chain 为 [EmptyMessageChain] 的 [MiraiMessageContent]。
 */
@get:JvmName("getEmptyMiraiMessageContent")
public val EmptyMiraiMessageContent: MiraiMessageContent = MiraiMessageChainContent(EmptyMessageChain)




/**
 * 将一个 [MessageContent] 转化为一个 [MiraiMessageContent]。
 */
public fun MessageContent.toMiraiMessageContent(): MiraiMessageContent {

    return if(this is MiraiMessageContent) {
        this
    } else {
        msg?.toMiraiMessageContent() ?: EmptyMiraiMessageContent
    }

    // return when (this) {
    //     is MiraiMessageContent -> return this
        // // 预期内的消息。
        // is ExpectedMessageContent -> when (this) {
        //     is BoxedMessageContent -> when (this) {
        //         EmptyMessageContent -> EmptyMiraiMessageContent
        //         is SingleMessageContent -> single.toMiraiMessageContent()
        //         is CompoundMessageContent -> {
        //             // 复合型，转化.
        //             val miraiMsgList =
        //                 msgList.map {
        //                     GlobalScope.async { it.toMiraiMessageContent() }
        //                 }.map { it.await() }
        //             MiraiListMessageContent(miraiMsgList)
        //         }
        //     }
        //
        //     // image content
        //     is ImageMessageContent -> {
        //         // image message content.
        //         if (this is MiraiImageMessageContent) {
        //             this
        //         } else {
        //             // 先尝试获取path
        //             val path: String? = this.path
        //             val pathFile: File? = this.path?.let {
        //                 FileUtil.file(it)
        //             }.takeIf { it?.exists() == true }
        //
        //             if (pathFile != null) {
        //                 path as String
        //                 MiraiImageMessageContent(
        //                     id = path,
        //                     path = path,
        //                     flash = this.flash
        //                 ) { c -> pathFile.uploadAsImage(c) }
        //             } else {
        //                 // 没有本地文件，查看网络文件
        //                 val url: String = path?.takeIf { it.startsWith("http") }
        //                     ?: this.getUrlOrNull()
        //                     ?: throw IllegalStateException("Unable to locate file: file path is not exists and no url exists.")
        //                 val imageURL = Url(url)
        //                 MiraiImageMessageContent(id = url, url = url, flash = this.flash) { c ->
        //                     imageURL.toStream().uploadAsImage(c)
        //                 }
        //             }
        //         }
        //     }
        //     // voice content.
        //     is VoiceMessageContent -> {
        //         if (this is MiraiVoiceMessageContent) {
        //             this
        //         } else {
        //             // 先尝试获取path
        //             val path: String? = this.path
        //             val pathFile: File? = this.path?.let {
        //                 FileUtil.file(it)
        //             }.takeIf { it?.exists() == true }
        //
        //             if (pathFile != null) {
        //                 path as String
        //                 MiraiVoiceMessageContent(
        //                     id = path,
        //                     path = path,
        //                 ) { c ->
        //                     if (c is Group) {
        //                         BufferedInputStream(pathFile.inputStream()).use { c.uploadVoice(it) }
        //                     } else throw IllegalStateException("Mirai does not support sending private voice.")
        //                 }
        //             } else {
        //                 // 没有本地文件，查看网络文件
        //                 val url: String = path?.takeIf { it.startsWith("http") }
        //                     ?: this.getUrlOrNull()
        //                     ?: throw IllegalStateException("Unable to locate file: file path is not exists and no url exists.")
        //                 val voiceURL = Url(url)
        //                 MiraiVoiceMessageContent(id = url, url = url) { c ->
        //                     if (c is Group) {
        //                         voiceURL.toStream().use { c.uploadVoice(it) }
        //                     } else throw IllegalStateException("Mirai does not support sending private voice.")
        //
        //                 }
        //             }
        //         }
        //     }
        //
        //     is TextMessageContent -> {
        //         // same as 'else'.
        //         msg?.toMiraiMessageContent() ?: EmptyMiraiMessageContent
        //     }
        // }
        // else -> {
        //     msg?.toMiraiMessageContent() ?: EmptyMiraiMessageContent
        // }
    // }

}


/**
 * 将可能存在catcode的字符串文本转化为 [MiraiMessageContent]。
 */
public fun String.toMiraiMessageContent(): MiraiMessageContent {
    return CatCodeUtil.split(this) {
        // cat code.
        if (startsWith(CAT_HEAD)) Nyanko.byCode(this).toMiraiMessageContent()
        // normal text.
        else MiraiSingleMessageContent(PlainText(this.deCatText()))
    }.let { MiraiListMessageContent(it) }
}

/**
 * [Neko] 转化为 [MiraiMessageContent]。
 */
public fun Neko.toMiraiMessageContent(): MiraiMessageContent {
    return when (this.type) {
        "at" -> {
            val all = this["all"] == "true"
            val code = this["code"]?.toLong()
            if (all) {
                MiraiSingleMessageContent(AtAll)
            } else {
                // codes not empty.
                code?.let { MiraiSingleAtMessageContent(it) } ?: throw IllegalArgumentException("no at 'code' in $this.")
            }
        }

        "atAll", "atall" -> MiraiSingleMessageContent(AtAll)

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
            val cat = this
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
                    PokeMessage.values.find { p -> p.type == type && p.id == id } ?: PokeMessage.Poke
                }
            }) { cat }
        }

        // 头像抖动
        "nudge" -> {
            val target = this["target"]
            MiraiNudgedMessageContent(target?.toLong())
        }


        // image
        "image" -> {
            // file, or url
            val filePath = this["file"]
            val file: File? = filePath?.let { FileUtil.file(it) }?.takeIf { it.exists() }
            val flash: Boolean = this["flash"] == "true"
            if (file != null) {
                // 存在文件
                MiraiImageMessageContent(flash = flash) { c -> file.uploadAsImage(c) }
            } else {
                // 没有文件，看看有没有url。
                val url = filePath?.takeIf { it.startsWith("http") }?.let { Url(it) }
                    ?: this["url"]?.let { Url(it) }
                    ?: throw IllegalArgumentException("The img has no source in $this")

                // val urlId = url.encodedPath
                MiraiImageMessageContent(flash = flash) { c ->
                    url.toStream().uploadAsImage(c)
                }
            }
        }

        // voice
        "voice" -> {
            // file, or url
            val filePath = this["file"]
            val file: File? = filePath?.let { FileUtil.file(it) }?.takeIf { it.exists() }
            if (file != null) {
                // 存在文件
                MiraiVoiceMessageContent(id = filePath, path = filePath) { c ->
                    if (c is Group) {
                        BufferedInputStream(file.inputStream()).use { c.uploadVoice(it) }
                    } else throw IllegalStateException("Mirai does not support sending private voice.")
                }
            } else {
                // 没有文件，看看有没有url。
                val url = filePath?.takeIf { it.startsWith("http") }?.let { Url(it) }
                    ?: this["url"]?.let { Url(it) }
                    ?: throw IllegalArgumentException("The voice has no source in $this")

                val urlId = url.encodedPath
                MiraiVoiceMessageContent(id = urlId, url = urlId) { c ->
                    if (c is Group) {
                        url.toStream().use { c.uploadVoice(it) }
                    } else throw IllegalStateException("Mirai does not support sending private voice.")
                }
            }
        }

        // 分享
        "share" -> {
            // 至少需要一个url
            val url: String = this["url"] ?: throw IllegalArgumentException("The 'url' could not be found in $this.")
            val title: String? =this["title"]
            val content: String? = this["content"]
            val coverUrl: String? = this["coverUrl"]

            MiraiSingleMessageContent(RichMessage.share(url, title, content, coverUrl))
        }

        "rich" -> {
            val content: String = this["content"] ?: "{}"
            // 如果没有serviceId，认为其为lightApp
            val serviceId: Int = this["serviceId"]?.toInt() ?: return MiraiSingleMessageContent(LightApp(content))
            MiraiSingleMessageContent(ServiceMessage(serviceId, content))
        }


        "app", "json" -> {
            val content: String = this["content"] ?: "{}"
            MiraiSingleMessageContent(LightApp(content))
        }


        "xml" -> {
            val xmlCode = this
            // 解析的参数
            val serviceId = this["serviceId"]?.toInt() ?: 60
            // 构建xml
            val xml = buildXmlMessage(serviceId) {
                // action
                xmlCode["action"]?.also { this.action = it }
                // 一般为点击这条消息后跳转的链接
                xmlCode["actionData"]?.also { this.actionData = it }
                /*
                   摘要, 在官方客户端内消息列表中显示
                 */
                xmlCode["brief"]?.also { this.brief = it }
                xmlCode["flag"]?.also { this.flag = it.toInt() }
                xmlCode["url"]?.also { this.url = it }
                // sourceName 好像是名称
                xmlCode["sourceName"]?.also { this.sourceName = it }
                // sourceIconURL 好像是图标
                xmlCode["sourceIconURL"]?.also { this.sourceIconURL = it }

                // builder
//                val keys = xmlCode.params.keys

                this.item {
                    xmlCode["bg"]?.also { this.bg = it.toInt() }
                    xmlCode["layout"]?.also { this.layout = it.toInt() }
                    // picture(coverUrl: String)
                    xmlCode["picture_coverUrl"]?.also { this.picture(it) }
                    // summary(text: String, color: String = "#000000")
                    xmlCode["summary_text"]?.also {
                        val color: String = xmlCode["summary_color"] ?: "#000000"
                        this.summary(it, color)
                    }
                    // title(text: String, size: Int = 25, color: String = "#000000")
                    xmlCode["title_text"]?.also {
                        val size: Int = xmlCode["title_size"]?.toInt() ?: 25
                        val color: String = xmlCode["title_color"] ?: "#000000"
                        this.title(it, size, color)
                    }

                }
            }
            MiraiSingleMessageContent(xml)
        }

        // 引用回复
        "quote" -> {
            val id = this["id"] ?: throw IllegalArgumentException("The 'id' cannot be found in $this")



            TODO()
        }


        else -> {
            MiraiSingleMessageContent(PlainText("(type $type not support)code=$this"))
        }

    }


}


/**
 * 将一个 [MessageChain] 转化为携带catcode的字符串。
 */
public fun MessageChain.toCatCode(): String {
    return this.asSequence().map { it.toCatCode() }.joinToString()
}

/**
 * 将一个 [MessageChain] 转化为携带 [Neko] 的列表。
 */
public fun MessageChain.toNeko(): List<Neko> {
    return this.mapNotNull {
        if (it is MessageSource) {
            null
        } else {
            it.toNeko()
        }
    }
}


/**
 * 将一个 [SingleMessage] 转化为携带cat字符串。
 */
public fun SingleMessage.toCatCode(): String {
    return when (this) {
        // at all
        AtAll -> CatCodeUtil.stringTemplate.atAll()
        // at
        is At -> CatCodeUtil.stringTemplate.at(target)
        // 普通文本, 转义
        is PlainText -> content.enCatText()
        is Face -> CatCodeUtil.stringTemplate.face(id.toString())
        is VipFace -> CatCodeUtil.getStringCodeBuilder("vipFace")
            .key("kindId").value(this.kind.id)
            .key("kindName").value(this.kind.name)
            .key("count").value(this.count)
            .build()

        is PokeMessage -> {
            // poke, 戳一戳
            CatCodeUtil.getStringCodeBuilder("poke", false)
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
        is QuoteReply -> {
            CatCodeUtil.getStringCodeBuilder("quote")
                .key("id").value(with(this.source){ "$fromId.$id" })
                // 此项参数是否需要存在?
                // .key("msg").value(this.source.originalMessage.toSimbotString())
                .build()
        }

        // 富文本，xml或json
        is RichMessage -> CatCodeUtil.getStringCodeBuilder("rich")
            .key("content").value(content)
            .build()

        // else.
        else -> {
            // "[mirai:at:$target,$display]"
            // val miraiCode = this.toString()
            // CatCodeUtil.getStringCodeBuilder(this.content)
            CatCodeUtil.getStringCodeBuilder("miraiCode")
                .key("string").value(this.toString()).build()
        }
    }
}


/**
 * 将一个 [SingleMessage] 转化为携带cat字符串。
 * 普通文本会被转化为 [CAT:text,text=xxx]
 */
public fun SingleMessage.toNeko(): Neko {
    return when (this) {
        // at all
        AtAll -> CatCodeUtil.nekoTemplate.atAll()
        // at
        is At -> CatCodeUtil.nekoTemplate.at(target)
        // 普通文本, 转义
        is PlainText -> CatCodeUtil.getNekoBuilder("text", true).key("text").value(content).build()
        is Face -> CatCodeUtil.nekoTemplate.face(id.toString())
        is VipFace -> CatCodeUtil.getNekoBuilder("vipFace", true)
            .key("kindId").value(this.kind.id)
            .key("kindName").value(this.kind.name)
            .key("count").value(this.count)
            .build()

        is PokeMessage -> {
            // poke, 戳一戳
            CatCodeUtil.getNekoBuilder("poke", false)
                .key("type").value(type)
                .key("id").value(id)
                .build()
        }
        is Image -> {
            CatCodeUtil.getLazyNekoBuilder("image", true)
                .key("id").value(imageId)
                .key("url").value { runBlocking { this@toNeko.queryUrl() } }
                .build()
        }
        is FlashImage -> {
            val img = this.image
            // cat code中不再携带url参数
            // CatCodeUtil.stringTemplate.image()
            CatCodeUtil.getLazyNekoBuilder("image", true)
                .key("id").value(img.imageId)
                .key("url").value { runBlocking { img.queryUrl() } }
                .key("flash").value(true)
                .build()
        }
        is Voice -> {
            CatCodeUtil.getLazyNekoBuilder("voice", true)
                .key("id").value("$fileName.$fileSize")
                .key("name").value(fileName)
                .key("size").value(fileSize).apply {
                    url?.let { key("url").value(it) }
                }
                .build()

        }
        // 引用回复
        is QuoteReply -> {
            CatCodeUtil.getNekoBuilder("quote", true)
                .key("id").value(with(this.source){ "$fromId.$id" })
                // 此项参数是否需要存在?
                .key("quote").value { this.source.originalMessage.toCatCode() }
                .build()
        }

        // 富文本，xml或json
        is RichMessage -> CatCodeUtil.getNekoBuilder("rich", true)
            .key("content").value(content)
            .build()

        // else.
        else -> {
            // "[mirai:at:$target,$display]"
            // val miraiCode = this.toString()
            // CatCodeUtil.getStringCodeBuilder(this.content)
            CatCodeUtil.getNekoBuilder("miraiCode", true)
                .key("string").value(this.toString()).build()
        }
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
public suspend fun Url.toStream(): InputStream {
    val urlString = this.toString()
    // debug("mirai.http.connection.try", urlString)
    val response = httpClient.get<HttpResponse>(this)
    val status = response.status
    if (status.value < 300) {
        // debug("mirai.http.connection.success", urlString)
        // success
        return response.content.toInputStream()
    } else {
        throw IllegalStateException("connection to '$urlString' failed ${status.value}: ${status.description}")
    }
}