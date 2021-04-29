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

package love.forte.simbot.component.mirai.message

import catcode.CatCodeUtil
import catcode.Neko
import cn.hutool.core.io.FileUtil
import io.ktor.http.*
import love.forte.simbot.api.message.MessageContentBuilder
import love.forte.simbot.api.message.MessageContentBuilderFactory
import love.forte.simbot.api.message.containers.AccountCodeContainer
import love.forte.simbot.component.mirai.utils.toStream
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

/**
 * [MiraiMessageContentBuilder]'s factory.
 */
public sealed class MiraiMessageContentBuilderFactory : MessageContentBuilderFactory {
    /** Mirai组件所使用的消息构建器。 */
    abstract override fun getMessageContentBuilder(): MiraiMessageContentBuilder

    /** 普通的图片上传策略。 */
    internal object MiraiMessageContentBuilderFactoryImgNormal : MiraiMessageContentBuilderFactory() {
        override fun getMessageContentBuilder(): MiraiMessageContentBuilder = MiraiMessageContentBuilderImgNormal()
    }

    /** 优先尝试通过一个任意的群进行上传的图片上传策略。 */
    internal object MiraiMessageContentBuilderFactoryImgGroupFirst : MiraiMessageContentBuilderFactory() {
        override fun getMessageContentBuilder(): MiraiMessageContentBuilder = MiraiMessageContentBuilderImgGroupFirst()
    }

    companion object {
        fun instance(imgGroupFirst: Boolean = false): MiraiMessageContentBuilderFactory {
            return if (imgGroupFirst) {
                MiraiMessageContentBuilderFactoryImgGroupFirst
            } else {
                MiraiMessageContentBuilderFactoryImgNormal
            }
        }
    }
}


/**
 * mirai对 [MessageContentBuilder] 的实现。
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@Suppress("unused")
public sealed class MiraiMessageContentBuilder : MessageContentBuilder {
    private val texts = StringBuilder()
    private val contentList = mutableListOf<MiraiMessageContent>()
    private fun checkText() {
        if (texts.isNotEmpty()) {
            contentList.add(MiraiSingleMessageContent(PlainText(texts)))
            texts.clear()
        }
    }

    override fun text(text: CharSequence): MessageContentBuilder {
        texts.append(text)
        return this
    }

    override fun atAll(): MiraiMessageContentBuilder {
        checkText()
        contentList.add(MiraiSingleMessageContent(AtAll))
        return this
    }

    private fun at0(code: Long): MiraiMessageContentBuilder {
        checkText()
        contentList.add(MiraiSingleAtMessageContent(code))
        return this
    }

    override fun at(code: String): MiraiMessageContentBuilder = at0(code.toLong())
    override fun at(code: Long): MessageContentBuilder = at0(code)
    override fun at(code: AccountCodeContainer): MessageContentBuilder = at(code.accountCodeNumber)

    private fun face0(id: Int): MiraiMessageContentBuilder {
        checkText()
        contentList.add(MiraiSingleMessageContent(Face(id)))
        return this
    }

    override fun face(id: String): MessageContentBuilder = face0(id.toInt())
    override fun face(id: Int): MessageContentBuilder = face0(id)

    override fun imageLocal(path: String, flash: Boolean): MessageContentBuilder {
        val file: File = FileUtil.file(path)?.takeIf { it.exists() } ?: throw FileNotFoundException(path)
        val imageNeko: Neko = CatCodeUtil
            .getNekoBuilder("image", true)
            .key("file").value(path)
            .key("type").value("local")
            .apply {
                if (flash) {
                    key("flash").value(true)
                }
            }.build()
        val imageContent = imageLocal0(file, imageNeko, flash)
        checkText()
        contentList.add(imageContent)
        return this
    }

    abstract fun imageLocal0(file: File, imageNeko: Neko, flash: Boolean): MiraiMessageContent


    override fun imageUrl(url: String, flash: Boolean): MessageContentBuilder {
        val imageNeko: Neko = CatCodeUtil
            .getNekoBuilder("image", true)
            .key("file").value(url)
            .key("type").value("network")
            .apply {
                if (flash) {
                    key("flash").value(true)
                }
            }.build()
        imageUrl0(url, imageNeko, flash).apply {
            checkText()
            contentList.add(this)
        }
        return this
    }

    abstract fun imageUrl0(url: String, imageNeko: Neko, flash: Boolean): MiraiMessageContent


    override fun image(input: InputStream, flash: Boolean): MiraiMessageContentBuilder {
        val imageNeko: Neko = CatCodeUtil
            .getNekoBuilder("image", true)
            .key("type").value("stream")
            .apply {
                if (flash) {
                    key("flash").value(true)
                }
            }.build()
        image0(input, imageNeko, flash).apply {
            checkText()
            contentList.add(this)
        }
        return this
    }

    abstract fun image0(input: InputStream, imageNeko: Neko, flash: Boolean): MiraiMessageContent


    override fun image(imgData: ByteArray, flash: Boolean): MiraiMessageContentBuilder {
        val input = ByteArrayInputStream(imgData)
        return image(input, flash)
    }


    /**
     * 直接追加一个 [MiraiMessageContent].
     */
    fun messageContent(content: MiraiMessageContent): MiraiMessageContentBuilder {
        checkText()
        contentList.add(content)
        return this
    }

    /**
     * 直接追加一个mirai原生 [Message] 实例。
     */
    fun singleMessage(singleMessage: SingleMessage): MiraiMessageContentBuilder {
        checkText()
        contentList.add(MiraiSingleMessageContent(singleMessage))
        return this
    }
    /**
     * 直接追加一个mirai原生 [Message] 实例。
     */
    fun message(message: Message): MiraiMessageContentBuilder {
        when (message) {
            is SingleMessage -> {
                checkText()
                contentList.add(MiraiSingleMessageContent(message))
            }
            is MessageChain -> {
                checkText()
                contentList.add(MiraiMessageChainContent(message))
            }
            else -> {
                throw IllegalArgumentException("Unknown message type. Message must be SingleMessage or MessageChain.")
            }
        }
        return this
    }

    /**
     * 通过消息构建函数构建一个 [SingleMessage]
     * for kt.
     */
    @JvmSynthetic
    fun message(neko: Neko?, messageBlock: suspend (Contact) -> SingleMessage) : MiraiMessageContentBuilder {
        val msg = MiraiSingleMessageContent(messageBlock, neko)
        checkText()
        contentList.add(msg)
        return this
    }

    @Suppress("FunctionName")
    @JvmName("messageLazy")
    fun __messageBlocking(neko: Neko?, messageBlock: (Contact) -> SingleMessage) : MiraiMessageContentBuilder {
        val msg = MiraiSingleMessageContent({ c -> messageBlock(c) }, neko)
        checkText()
        contentList.add(msg)
        return this
    }






    override fun build(): MiraiMessageContent {
        checkText()
        return MiraiListMessageContent(contentList.toList())
    }
}


@Suppress("NOTHING_TO_INLINE")
internal inline fun Contact.findAnyGroup(): Group? {
    return bot.groups.firstOrNull()
        ?: Bot.instancesSequence.flatMap { b -> b.groups }.firstOrNull()
}

/**
 * [MiraiMessageContentBuilder] 实现。
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
internal class MiraiMessageContentBuilderImgGroupFirst : MiraiMessageContentBuilder() {

    override fun imageLocal0(file: File, imageNeko: Neko, flash: Boolean): MiraiImageMessageContent {
        return MiraiImageMessageContent(flash, imageNeko) { contact ->
            contact.findAnyGroup()?.let { group ->
                file.uploadAsImage(group)
            } ?: file.uploadAsImage(contact)
        }
    }

    override fun imageUrl0(url: String, imageNeko: Neko, flash: Boolean): MiraiMessageContent {
        return MiraiImageMessageContent(flash, imageNeko) { contact ->
            Url(url).toStream().use { stream ->
                contact.findAnyGroup()?.let { group ->
                    stream.uploadAsImage(group)
                } ?: stream.uploadAsImage(contact)
            }

        }
    }

    override fun image0(input: InputStream, imageNeko: Neko, flash: Boolean): MiraiMessageContent {
        return MiraiImageMessageContent(flash, imageNeko) { contact ->
            input.use { inp ->
                contact.findAnyGroup()?.let { group ->
                    inp.uploadAsImage(group)
                } ?: inp.uploadAsImage(contact)
            }
        }
    }
}


/**
 * [MiraiMessageContentBuilder] 实现。其中，对于图片的上传为正常的上传模式，即当前为好友就使用好友上传，是群就使用群上传。
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
internal class MiraiMessageContentBuilderImgNormal : MiraiMessageContentBuilder() {
    override fun imageLocal0(file: File, imageNeko: Neko, flash: Boolean): MiraiImageMessageContent {
        return MiraiImageMessageContent(flash, imageNeko) { file.uploadAsImage(it) }
    }

    override fun imageUrl0(url: String, imageNeko: Neko, flash: Boolean): MiraiMessageContent {
        return MiraiImageMessageContent(flash, imageNeko) { contact ->
            Url(url).toStream().use { stream -> stream.uploadAsImage(contact) }
        }
    }

    override fun image0(input: InputStream, imageNeko: Neko, flash: Boolean): MiraiMessageContent {
        return MiraiImageMessageContent(flash, imageNeko) { contact ->
            input.use { inp -> inp.uploadAsImage(contact) }
        }
    }
}