/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

@file:JvmName("MiraiMessageParsers")

package love.forte.simbot.component.mirai.utils

import catcode.*
import catcode.codes.Nyanko
import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.resource.ResourceUtil
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.component.mirai.message.*
import love.forte.simbot.component.mirai.message.event.MiraiMessageMsgGet
import love.forte.simbot.component.mirai.sender.logger
import love.forte.simbot.processor.RemoteResourceContext
import love.forte.simbot.processor.RemoteResourceInProcessor
import love.forte.simbot.processor.doProcess
import love.forte.simbot.utils.onShutdown
import net.mamoe.mirai.contact.FileSupported
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.MiraiExperimentalApi
import net.mamoe.mirai.utils.RemoteFile
import net.mamoe.mirai.utils.RemoteFile.Companion.uploadFile
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.concurrent.ThreadLocalRandom
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.absoluteValue


internal const val CLASSPATH_HEAD = "classpath:"

/**
 * mirai消息转化器，将一个 [Neko] 转化为 [MiraiMessageContent].
 *
 * @see MiraiSingleMessageContent
 * @see MiraiMessageChainContent
 *
 */
public fun interface MiraiMessageParser {
    /**
     * 将一个 [Neko] 实例，根据 [当前监听事件监听到的消息(如果有)][message] 和 [mirai消息缓存器(如果有)][cache] 构建一个 [Mirai消息实例][MiraiMessageContent].
     *
     * 如果返回一个null，则视为 **解析失败**.
     *
     */
    fun parse(neko: Neko, message: MessageChain?, cache: MiraiMessageCache?): MiraiMessageContent?
}


/**
 * 额外的Mirai消息解析器列表。
 */
internal val parsers: MutableMap<String, MiraiMessageParser> = mutableMapOf()


private val FILE_PATH_SPLIT = Regex("[/\\\\]")


/**
 * 追加一条消息解析器。
 * 如果对应解析的类型 [forType] 已经存在解析器，则返回值得到被替换的解析器。
 *
 * 不会顶替内置的解析逻辑，但是会覆盖他们的流程。
 *
 */
@Suppress("unused")
@Synchronized
public fun addMiraiMessageParser(forType: String, parser: MiraiMessageParser): MiraiMessageParser? {
    return parsers.put(forType, parser)
}


/**
 * 将一个 [MessageContent] 转化为一个 [MiraiMessageContent]。
 */
public fun MessageContent.toMiraiMessageContent(
    message: MessageChain?,
    cache: MiraiMessageCache? = null,
    remoteResourceInProcessor: RemoteResourceInProcessor,
): MiraiMessageContent {
    return if (this is MiraiMessageContent) {
        this
    } else {
        msg.toMiraiMessageContent(message, cache, remoteResourceInProcessor)
    }
}


/**
 * 将可能存在catcode的字符串文本转化为 [MiraiMessageContent]。
 * @param message 当前发送消息的时候，如果是在某个事件中发送消息，此处为当前事件可能接收到的消息链。
 * @param cache mirai消息缓存器。
 */
public fun String.toMiraiMessageContent(
    message: MessageChain?,
    cache: MiraiMessageCache? = null,
    remoteResourceInProcessor: RemoteResourceInProcessor,
): MiraiMessageContent {
    return CatCodeUtil.split(this) {
        // cat code.
        if (startsWith(CAT_HEAD)) Nyanko.byCode(this).toMiraiMessageContent(message, cache, remoteResourceInProcessor)
        // normal text.
        else MiraiSingleMessageContent(PlainText(this.deCatText()))
    }.let { MiraiListMessageContent(it) }
}

/**
 * [Neko] 转化为 [MiraiMessageContent]。
 */
@OptIn(MiraiExperimentalApi::class)
public fun Neko.toMiraiMessageContent(
    message: MessageChain?,
    cache: MiraiMessageCache? = null,
    remoteResourceInProcessor: RemoteResourceInProcessor,
): MiraiMessageContent {
    return parsers[this.type]?.parse(this, message, cache)
        ?: when (this.type) {
            "text", "message" -> this["text"]?.let {
                MiraiSingleMessageContent(PlainText(it))
            } ?: MiraiSingleMessageContent(EmptySingleMessage)

            "at" -> {
                val all = this["all"] == "true"
                val code = this["code"]?.toLong()
                if (all) {
                    MiraiSingleMessageContent(AtAll)
                } else {
                    // codes not empty.
                    code?.let { MiraiSingleAtMessageContent(it) }
                        ?: throw IllegalArgumentException("no at 'code' in $this.")
                }
            }

            // at all
            "atAll", "atall" -> MiraiSingleMessageContent(AtAll)

            // face
            "face" -> {
                val id: Int = this["id"]?.toInt() ?: throw IllegalArgumentException("No face 'id' in $this.")
                MiraiSingleMessageContent(Face(id))
            }

            // 戳一戳，窗口抖动
            "poke", "shake" -> {
                val type: Int = this["type"]?.toInt() ?: return MiraiSingleMessageContent(PokeMessage.ChuoYiChuo)
                val id: Int = this["id"]?.toInt() ?: -1
                // val code: Long = this["code"]?.toLong() ?: -1L
                val cat = this
                val poke = PokeMessage.values.find { p -> p.pokeType == type && p.id == id } ?: PokeMessage.ChuoYiChuo
                MiraiSingleMessageContent(poke) { cat }
            }

            // 头像抖动
            "nudge" -> {
                val target = this["target"]
                val from = this["from"]
                MiraiNudgedMessageContent(from?.toLong(), target?.toLong())
            }


            // image
            "image" -> {
                val id = this["id"]
                val flash = this["flash"] == "true"

                if (id != null) {
                    // id, if contains
                    if (message != null) {
                        val foundImg = message.find {
                            (it is Image && it.imageId == id) ||
                                    (it is FlashImage && it.image.imageId == id)
                        }
                        if (foundImg != null) {
                            if (foundImg is Image) {
                                return if (flash) {
                                    MiraiSingleMessageContent(foundImg.flash())
                                } else {
                                    MiraiSingleMessageContent(foundImg)
                                }
                            }
                            if (foundImg is FlashImage) {
                                return if (flash) {
                                    MiraiSingleMessageContent(foundImg.image.flash())
                                } else {
                                    MiraiSingleMessageContent(foundImg.image)
                                }
                            }
                        }
                    }

                    try {
                        return MiraiSingleMessageContent(Image(id))
                    // } catch(e: IllegalArgumentException) {
                    } catch(e: Exception) {
                        logger.warn("Cannot create Image by id {}, {}", id, e.localizedMessage)
                        if (logger.isDebugEnabled) {
                            logger.debug("Cannot create Image by id $id", e)
                        }
                    }
                }


                // file, or url
                val filePath = this["file"]

                // if resource
                if (filePath != null && filePath.startsWith(CLASSPATH_HEAD)) {
                    val filePath0 = filePath.substring(CLASSPATH_HEAD.length)
                    val classPathUrl: URL? = ResourceUtil.getResource(filePath0)
                    if (classPathUrl != null) {
                        val imageNeko = CatCodeUtil.nekoTemplate.image(filePath0)
                        return MiraiImageMessageContent(flash, imageNeko) { c ->
                            classPathUrl.externalResource().use { res -> c.uploadImage(res) }
                        }
                    }
                }

                val file: File? = filePath?.let { FileUtil.file(it) }?.takeIf { it.exists() }
                // val flash: Boolean = this["flash"] == "true"
                if (file != null) {
                    // 存在文件
                    val imageNeko = CatCodeUtil.nekoTemplate.image(filePath)
                    MiraiImageMessageContent(flash, imageNeko) { c -> file.uploadAsImage(c) }
                } else {
                    // 没有文件，看看有没有url。
                    // val url = filePath?.takeIf { it.startsWith("http") }?.let { Url(it) }
                    //     ?: this["url"]?.let { Url(it) }
                    //     ?: throw IllegalArgumentException("The img has no source in $this")

                    // remoteResourceInProcessor
                    val urlString = filePath?.takeIf { it.startsWith("http") }
                        ?: this["url"]
                        ?: throw IllegalArgumentException("The img has no source in $this")


                    // val urlId = url.encodedPath
                    val imageNeko = CatCodeUtil.nekoTemplate.image(urlString)
                    MiraiImageMessageContent(flash, imageNeko) { c ->
                        val context = RemoteResourceContext(urlString, id)
                        remoteResourceInProcessor.getStream(context).use { s -> s.uploadAsImage(c) }
                    }
                }
            }

            // voice or record
            "voice", "record" -> {
                val id = this["id"]

                if (message != null) {
                    if (id != null) {
                        val findVoice = message.find { it is Voice && it.id == id }
                        if (findVoice != null) {
                            return MiraiSingleMessageContent(findVoice)
                        }
                    }
                }


                // file, or url
                val filePath = this["file"]

                // if resource
                if (filePath != null && filePath.startsWith(CLASSPATH_HEAD)) {
                    val filePath0 = filePath.substring(CLASSPATH_HEAD.length)
                    val classPathUrl: URL? = ResourceUtil.getResource(filePath0)
                    val recordNeko = CatCodeUtil.nekoTemplate.record(filePath)
                    if (classPathUrl != null) {
                        return MiraiVoiceMessageContent(recordNeko) { c ->
                            if (c is Group) {
                                classPathUrl.externalResource().use { c.uploadVoice(it) }
                            } else throw IllegalStateException("Mirai only support sending group voice.")
                        }
                    }
                }


                val file: File? = filePath?.let { FileUtil.file(it) }?.takeIf { it.exists() }
                if (file != null) {
                    // 存在文件
                    val recordNeko = CatCodeUtil.nekoTemplate.record(filePath)
                    MiraiVoiceMessageContent(recordNeko) { c ->
                        if (c is Group) {
                            file.toExternalResource().use { c.uploadVoice(it) }
                        } else throw IllegalStateException("Mirai only support sending group voice.")
                    }
                } else {
                    // 没有文件，看看有没有url。
                    // val url = filePath?.takeIf { it.startsWith("http") }?.let { Url(it) }
                    //     ?: this["url"]?.let { Url(it) }
                    //     ?: throw IllegalArgumentException("The voice has no source in $this")
                    val urlString = filePath?.takeIf { it.startsWith("http") }
                        ?: this["url"]
                        ?: throw IllegalArgumentException("The voice has no source in $this")

                    // val urlId = url.encodedPath
                    val recordNeko = CatCodeUtil.nekoTemplate.record(urlString)
                    MiraiVoiceMessageContent(recordNeko) { c ->
                        if (c is Group) {
                            val context = RemoteResourceContext(urlString, id)
                            remoteResourceInProcessor.getStream(context).toExternalResource()
                                .use { resource -> c.uploadVoice(resource) }

                            // url.toStream().use { s ->
                            //     s.toExternalResource().use { c.uploadVoice(it) }
                            // }
                        } else throw IllegalStateException("Mirai only support sending group voice.")
                    }
                }
            }


            // 群文件上传
            "file" -> {
                // 上传文件路径
                val filePath = this["file"] ?: this["url"]

                val formatName = this["formatName"]

                // 上传到的路径
                val path: String? = this["path"]?.let { p ->
                    if (p.endsWith('/')) p.substringBeforeLast('/') else p
                }

                if (filePath == null) {
                    if (path == null) {
                        throw IllegalArgumentException("No 'file' or 'path' in $this")
                    } else {
                        // mkdir
                        val fileNeko = CatCodeUtil.getNekoBuilder("file", true)
                            .key("path").value(path)
                            .build()
                        return MiraiSingleMessageContent(singleMessage = { c ->
                            if (c is FileSupported) {
                                val resolve = c.filesRoot.resolve(path)
                                if (!resolve.exists()) {
                                    val mkdir = resolve.mkdir()
                                    if (!mkdir) {
                                        logger.warn("Path {} mkdir in {} failed: return false.", path, c.id)
                                    }
                                }
                                EmptySingleMessage
                            } else throw IllegalStateException("Classpath file only support upload to 'FileSupported' instance, but '${c::class.java}'")
                        }, fileNeko)
                    }
                }

                /*
                ?: if (formatName != null) RemoteFile.ROOT_PATH + UUID.idString() + "." + formatName
                    else RemoteFile.ROOT_PATH + UUID.idString()
                 */

                // if classpath
                if (filePath.startsWith(CLASSPATH_HEAD)) {
                    val filePath0 = filePath.substring(CLASSPATH_HEAD.length)
                    val path0 = path ?: run {
                        val fileName =
                            filePath0.split(FILE_PATH_SPLIT).lastOrNull { it.isNotBlank() } ?: UUID.idString()
                        if (formatName != null) RemoteFile.ROOT_PATH + fileName + "." + formatName
                        else RemoteFile.ROOT_PATH + fileName
                    }


                    val classPathUrl: URL? = ResourceUtil.getResource(filePath0)
                    if (classPathUrl != null) {
                        val fileNeko = CatCodeUtil.getNekoBuilder("file", true)
                            .key("file").value(filePath0)
                            .key("path").value(path0)
                            .apply {
                                if (formatName != null) {
                                    key("formatName").value(formatName)
                                }
                            }
                            .build()

                        // return Mirai
                        return MiraiFileMessageContent(fileNeko, path0) { c ->
                            if (c is FileSupported) {
                                val resolve = c.filesRoot.resolve(path0.substringBeforeLast("/"))
                                if (!resolve.exists()) {
                                    val mkdir = resolve.mkdir()
                                    if (!mkdir) {
                                        logger.warn("Path {} mkdir in {} failed: return false.", path, c.id)
                                    }
                                }
                                classPathUrl.externalResource(formatName).use { res ->
                                    c.uploadFile(path0, res)
                                }
                            } else throw IllegalStateException("Classpath file only support upload to 'FileSupported' instance, but '${c::class.java}'")
                        }
                    } else throw IllegalArgumentException("Cannot resolve classpath file: $filePath0")
                }

                val file: File? = filePath.let { FileUtil.file(it) }?.takeIf { it.exists() }
                if (file != null) {
                    // 存在文件

                    val path0 = path ?: run {
                        if (formatName != null) RemoteFile.ROOT_PATH + file.nameWithoutExtension + "." + formatName
                        else RemoteFile.ROOT_PATH + file.name
                    }

                    val fileNeko = CatCodeUtil.getNekoBuilder("file", true)
                        .key("file").value(filePath)
                        .key("path").value(path0)
                        .apply {
                            if (formatName != null) {
                                key("formatName").value(formatName)
                            }
                        }
                        .build()
                    MiraiFileMessageContent(fileNeko, path0) { c ->
                        if (c is FileSupported) {
                            val resolve = c.filesRoot.resolve(path0.substringBeforeLast("/"))
                            if (!resolve.exists()) {
                                val mkdir = resolve.mkdir()
                                if (!mkdir) {
                                    logger.warn("Path {} mkdir in {} failed: return false.", path, c.id)
                                }
                            }
                            file.toExternalResource(formatName).use { res ->
                                c.uploadFile(path0, res)
                            }
                        } else throw IllegalStateException("File only support upload to 'FileSupported' instance. but ${c::class.java}")
                    }
                } else {
                    // 没有文件，看看有没有url
                    // val url = filePath.takeIf { it.startsWith("http") }?.let { Url(it) }
                    //     ?: throw IllegalArgumentException("There is no 'file' or 'url' starts with 'http' in $this")
                    val urlString = filePath.takeIf { it.startsWith("http") }
                        ?: throw IllegalArgumentException("There is no 'file' or 'url' starts with 'http' in $this")

                    val path0 = path
                        ?: if (formatName != null) RemoteFile.ROOT_PATH + UUID.idString() + "." + formatName
                        else RemoteFile.ROOT_PATH + UUID.idString()

                    // val urlId = id
                    val fileNeko = CatCodeUtil.getNekoBuilder("file", true)
                        .key("file").value(urlString)
                        .key("path").value(path0)
                        .key("url").value(urlString).apply {
                            if (formatName != null) {
                                key("formatName").value(formatName)
                            }
                        }
                        .build()

                    // val fileNeko = CatCodeUtil.toNeko("file", "file" cTo urlId, "url" cTo urlId)

                    MiraiFileMessageContent(fileNeko, path0) { c ->
                        if (c is FileSupported) {
                            val context = RemoteResourceContext(urlString)
                            remoteResourceInProcessor.getStream(context).use { s ->
                                s.toExternalResource(formatName).use {
                                    c.uploadFile(path0, it)
                                }
                            }
                            // url.toStream().use { s ->
                            //     s.toExternalResource(formatName).use {
                            //         c.uploadFile(path0, it)
                            //     }
                            // }
                        } else throw IllegalStateException("Remote file only support upload to 'FileSupported' instance, but '${c::class.java}'")
                    }

                }
            }


            // 分享
            "share" -> {
                // 至少需要一个url
                val url: String =
                    this["url"] ?: throw IllegalArgumentException("The 'url' could not be found in $this.")
                val title: String = this["title"] ?: "链接分享"
                val content: String = this["content"] ?: "链接分享"
                val coverUrl: String? = this["coverUrl"] ?: this["image"]

                coverUrl?.let { cov -> MiraiSingleMessageContent(RichMessage.share(url, title, content, cov)) }
                    ?: run {
                        val neko = CatCodeUtil.getNekoBuilder("share", true)
                            .value("url", url)
                            .value("title", title)
                            .value("content", content)
                            .value("coverUrl", coverUrl)
                            .build()
                        MiraiSingleMessageContent({ c -> RichMessage.share(url, title, content, c.bot.avatarUrl) },
                            neko)
                    }
            }

            "rich" -> {
                val content: String = this["content"] ?: "{}"
                // 如果没有serviceId，认为其为lightApp
                val serviceId: Int = this["serviceId"]?.toInt() ?: return MiraiSingleMessageContent(LightApp(content))
                MiraiSingleMessageContent(SimpleServiceMessage(serviceId, content))
            }


            "app", "json" -> {
                val content: String = this["content"] ?: "{}"
                MiraiSingleMessageContent(LightApp(content))
            }

            // 骰子
            "dice" -> {
                val dice: Dice = if (this["random"] == "true") Dice.random()
                else this["value"]?.let { v ->
                    v.toInt().absoluteValue.let { vInt ->
                        if (vInt in 1..6) Dice(vInt)
                        else Dice((vInt % 6) + 1)
                    }
                } ?: Dice.random()

                MiraiSingleMessageContent(dice)
            }

            "xml" -> {
                val xmlCode = this
                // 解析的参数
                val serviceId = this["serviceId"]?.toInt() ?: 60

                // 构建xml
                val xml = this["content"]?.let { content ->
                    SimpleServiceMessage(serviceId, content)
                } ?: buildXmlMessage(serviceId) {
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

                    item {
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


            // 音乐分享
            "music", "musicShare" -> {
                val kindString =
                    this["type"] ?: this["kind"] ?: throw IllegalArgumentException("No 'type' or 'kind' in $this")
                val musicUrl =
                    this["musicUrl"] ?: this["audio"]
                    ?: throw IllegalArgumentException("No 'musicUrl' or 'audio' in $this")

                // `neteaseCloud`、`qq`、`migu`

                var musicKindDisplay: String
                var musicPictureUrl: String
                var musicJumpUrl: String

                @Suppress("SpellCheckingInspection")
                val musicKind = when (kindString) {
                    "neteaseCloud", "NeteaseCloud", "neteaseCloudMusic", "NeteaseCloudMusic", "163", MusicKind.NeteaseCloudMusic.name -> MusicKind.NeteaseCloudMusic.also {
                        musicKindDisplay = "网易云音乐"
                        musicPictureUrl = "https://s4.music.126.net/style/web2/img/default/default_album.jpg"
                        musicJumpUrl = "https://music.163.com/"
                    }
                    "QQ", "qq", "qqMusic", "QQMusic", MusicKind.QQMusic.name -> MusicKind.QQMusic.also {
                        musicKindDisplay = "QQ音乐"
                        musicPictureUrl = "https://y.gtimg.cn/mediastyle/app/download/img/logo.png?max_age=2592000"
                        musicJumpUrl = "https://y.qq.com/"
                    }
                    "migu", "Migu", "miguMusic", "MiguMusic", MusicKind.MiguMusic.name -> MusicKind.MiguMusic.also {
                        musicKindDisplay = "咪咕音乐"
                        musicPictureUrl =
                            "https://cdnmusic.migu.cn/tycms_picture/20/10/294/201020171104983_90x26_2640.png"
                        musicJumpUrl = "https://music.migu.cn/"
                    }
                    else -> throw NoSuchElementException("Music kind: $kindString")
                }

                // title
                val title = this["title"] ?: musicKindDisplay

                // jump url
                val jumpUrl = this["jumpUrl"] ?: this["jump"] ?: musicJumpUrl

                // 消息图片url
                val pictureUrl = this["pictureUrl"] ?: this["picture"] ?: musicPictureUrl

                val brief = this["brief"] ?: "[分享]$musicKindDisplay"

                // 消息卡片内容
                val summary = this["summary"] ?: this["content"] ?: brief


                MiraiSingleMessageContent(MusicShare(musicKind, title, summary, jumpUrl, pictureUrl, musicUrl, brief))
            }


            // 引用回复，当不支持缓存、无法获取等情况的时候会忽略。
            "quote" -> {
                this["id"]?.let { id ->
                    val cacheMsg =
                        cache?.getGroupMsg(id)
                            ?: cache?.getPrivateMsg(id)
                            ?: message?.takeIf { m ->
                                m.findIsInstance<MessageSource>()?.cacheId?.let { mid -> mid == id }
                                    ?: false
                            }
                            ?: return@let MiraiSingleMessageContent

                    when (cacheMsg) {
                        is MiraiMessageMsgGet<*> -> MiraiSingleMessageContent(QuoteReply(cacheMsg.message))
                        is MessageChain -> MiraiSingleMessageContent(QuoteReply(cacheMsg))
                        is MessageSource -> MiraiSingleMessageContent(QuoteReply(cacheMsg))
                        else -> MiraiSingleMessageContent
                    }

                    // if (cacheMsg is MessageChain) {
                    //     return@let MiraiSingleMessageContent(QuoteReply(cacheMsg))
                    // }
                    // if (cacheMsg is MessageSource) {
                    //     return@let MiraiSingleMessageContent(QuoteReply(cacheMsg))
                    // }
                    // if (cacheMsg !is MiraiMessageMsgGet<*>) {
                    //     return@let MiraiSingleMessageContent
                    // }
                    //
                    // MiraiSingleMessageContent(QuoteReply(cacheMsg.message))
                } ?: run {
                    // 不存在ID，尝试通过messageChain
                    message?.quote()?.let { MiraiSingleMessageContent(it) }
                        ?: MiraiSingleMessageContent
                }
            }

            // 未知消息
            "unsupported" -> {
                val struct: String = this["struct"] ?: return MiraiSingleMessageContent

                val structByteArray = struct.hexStringToByteArray()

                MiraiSingleMessageContent(UnsupportedMessage(structByteArray))
            }

            else -> {
                val kvs = this.entries.joinToString(", ") { it.key + "=" + it.value }
                MiraiSingleMessageContent(PlainText("$type($kvs)"))
            }

        }


}


/**
 * 将一个 [MessageChain] 转化为携带catcode的字符串。
 */
public fun MessageChain.toCatCode(cache: MiraiMessageCache? = null): String {
    return this.asSequence().map { it.toNeko(cache) }.joinToString("") { it.toString() }
}

/**
 * 将一个 [MessageChain] 转化为携带 [Neko] 的列表。
 */
public fun MessageChain.toNeko(cache: MiraiMessageCache? = null): List<Neko> {
    return this.mapNotNull {
        if (it is MessageSource) {
            null
        } else {
            it.toNeko(cache)
        }
    }
}


/**
 * 将一个 [SingleMessage] 转化为携带cat字符串。
 * 普通文本会被转化为 [CAT:text,text=xxx]
 */
@OptIn(MiraiExperimentalApi::class)
public fun SingleMessage.toNeko(cache: MiraiMessageCache? = null): Neko {
    return when (this) {
        // at all
        AtAll -> CatCodeUtil.nekoTemplate.atAll()
        // at
        is At -> CatCodeUtil.nekoTemplate.at(target)
        // 普通文本, 转义
        is PlainText -> CatCodeUtil.toNeko("text", false, "text=${CatEncoder.encodeParams(content)}")
        // face
        is Face -> CatCodeUtil.nekoTemplate.face(id.toString())

        // market face
        is MarketFace -> CatCodeUtil.getNekoBuilder("marketFace", true)
            .key("id").value(id)
            .key("name").value(name)
            .build()

        // vip face
        is VipFace -> CatCodeUtil.getNekoBuilder("vipFace", true)
            .key("kindId").value(this.kind.id)
            .key("kindName").value(this.kind.name)
            .key("count").value(this.count)
            .build()

        is PokeMessage -> {
            // poke, 戳一戳
            CatCodeUtil.getNekoBuilder("poke", false)
                .key("type").value(pokeType)
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
                .key("id").value { id }
                .key("name").value(fileName)
                .key("size").value(fileSize).apply {
                    url?.let { key("url").value(it) }
                }
                .key("md5").value { this.md5.byteArrayToHexString() }
                .build()

        }
        is FileMessage -> {
            CatCodeUtil.getNekoBuilder("file", true)
                .key("id").value(id)
                .key("internalId").value(internalId)
                .key("name").value(name)
                .key("size").value(size)
                .build()
        }


        // 引用回复
        is QuoteReply -> {
            val id = source.cacheId
            CatCodeUtil.getLazyNekoBuilder("quote", true)
                .key("id").value(id)
                .key("quote").value { this.source.originalMessage.toCatCode(cache) }
                .build()
        }

        is Dice -> {
            CatCodeUtil.getNekoBuilder("dice", false)
                .key("value").value(value)
                .key("random").value(true)
                .build()
        }

        // 转发消息
        is ForwardMessage -> {
            // TODO
            CatCodeUtil.getNekoBuilder("forward", true)
                /*
                title: String,
                brief: String,
                source: String,
                summary: String
                 */
                .key("title").value(title)
                .key("brief").value(brief)
                .key("source").value(source)
                .key("summary").value(summary)
                /*
                    maybe nodes..?
                 */
                .build()
        }

        is MusicShare -> {
            // 音乐分享
            CatCodeUtil.getNekoBuilder("music", true)
                .key("kind").value(kind.showKind)
                .key("musicUrl").value(musicUrl)
                .key("title").value(title)
                .key("jumpUrl").value(jumpUrl)
                .key("pictureUrl").value(pictureUrl)
                .key("summary").value(summary)
                .key("brief").value(brief)
                .build()
        }

        // 富文本，xml或json
        is RichMessage -> CatCodeUtil.getNekoBuilder("rich", true)
            .key("content").value(content).let {
                if (this is ServiceMessage) {
                    it.key("serviceId").value(this.serviceId)
                } else it
            }.build()

        // mirai不支持的消息
        is UnsupportedMessage -> {
            CatCodeUtil.getNekoBuilder("unsupported", true)
                .key("struct").value(struct.byteArrayToHexString())
                .build()
        }

        // else.
        else -> {
            CatCodeUtil.getNekoBuilder("other", true)
                .key("code").value(this.toString()).build()
        }
    }
}


/**
 * ktor http client
 */
private val httpClient: HttpClient = HttpClient() {
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 20_000
    }
}.also {
    onShutdown("MiraiParser-httpClient") { it.close() }
}


public suspend fun RemoteResourceInProcessor.getStream(context: RemoteResourceContext): InputStream =
    this.doProcess(context)


/**
 * 通过http网络链接得到一个输入流。
 * 通常认为是一个http-get请求
 */
public suspend fun Url.toStream(): InputStream {
    val urlString = this.toString()
    // bot?.logger?.debug("mirai.http.connection.try", urlString)
    val response = httpClient.get<HttpResponse>(this)
    val status = response.status
    if (status.value < 300) {
        // debug("mirai.http.connection.success", urlString)
        // success
        return response.content.toInputStream()
    } else {
        throw IllegalStateException("Connection to '$urlString' failed. ${status.value}: ${status.description}")
    }
}


@OptIn(MiraiExperimentalApi::class)
private val Voice.id: String
    get() = md5.byteArrayToHexString()


private val hexArray = charArrayOf(
    '0', '1', '2', '3', '4', '5', '6', '7',
    '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
)

//将byte数组转换成16进制字符串
private fun ByteArray.byteArrayToHexString(): String {
    val resultSb = StringBuilder()
    for (b in this) {
        var n = b.toInt()
        if (n < 0) {
            n += 256
        }
        val d1 = n / 16
        val d2 = n % 16

        resultSb.append(hexArray[d1]).append(hexArray[d2])
    }
    return resultSb.toString()
}

/**
 * 将十六进制字符串转化为字节数组
 */
private fun String.hexStringToByteArray(): ByteArray {
    if (length % 2 != 0) {
        throw IllegalArgumentException("Hex str need % 2 == 0, but length $length in $this")
    }

    val arrayLength = length / 2

    val byteArray = ByteArray(arrayLength)

    val builder = StringBuilder()

    for (i in 0 until arrayLength) {
        val hex0 = builder.append(this[i * 2]).append(this[(i * 2) + 1]).toString()
        byteArray[i] = hex0.toByte(16)
        builder.clear()
    }

    return byteArray
}


private fun <T> CodeBuilder<T>.value(key: String, value: Any?): CodeBuilder<T> =
    value?.let { v -> key(key).value(v) } ?: this


private val MusicKind.showKind: String
    get() = when (this) {
        MusicKind.NeteaseCloudMusic -> "neteaseCloud"
        MusicKind.QQMusic -> "QQ"
        MusicKind.MiguMusic -> "migu"
        // else -> "Unknown"
    }


/**
 * 获取 [URL] 的 [ExternalResource]。
 */
private suspend fun URL.externalResource(formatName: String? = null): ExternalResource {
    val stream = stream()
    return withContext(Dispatchers.IO) {
        stream.use { s -> s.toExternalResource(formatName) }
    }
}

/**
 * 获取 [URL] 的输入流。
 */
private fun URL.stream(): InputStream = openStream()


private inline val UUID: Id
    get() {
        return ThreadLocalRandom.current().let { r ->
            val randomBytes = ByteArray(16)
            r.nextBytes(randomBytes)

            randomBytes[6] = randomBytes[6] and 0x0f
            randomBytes[6] = randomBytes[6] or 0x40
            randomBytes[8] = randomBytes[8] and 0x3f
            randomBytes[8] = randomBytes[8] or 0x80.toByte()

            Id(randomBytes)
        }
    }


@JvmInline
private value class Id(private val data: ByteArray) {

    fun idString(): String {
        var msb: Long = 0
        var lsb: Long = 0
        for (i in 0..7) {
            msb = msb shl 8 or (data[i] and 0xff.toByte()).toLong()
        }
        for (i in 8..15) {
            lsb = lsb shl 8 or (data[i] and 0xff.toByte()).toLong()
        }

        val mostSigBits: Long = msb

        val leastSigBits: Long = lsb

        val builder = StringBuilder(32).run {
            append(digits(mostSigBits shr 32, 8))
            append(digits(mostSigBits shr 16, 4))
            append(digits(mostSigBits, 4))
            append(digits(leastSigBits shr 48, 4))
            append(digits(leastSigBits, 12))
        }

        return builder.toString()
    }

}

// ------------------------------------------------------------------------------------------------------------------- Private method start
/**
 * 返回指定数字对应的hex值
 *
 * @param val    值
 * @param digits 位
 * @return 值
 */
private fun digits(`val`: Long, digits: Int): String {
    val hi = 1L shl digits * 4
    return (hi or (`val` and hi - 1)).toString(16).substring(1)
    // return java.lang.Long.toHexString().substring(1)
}