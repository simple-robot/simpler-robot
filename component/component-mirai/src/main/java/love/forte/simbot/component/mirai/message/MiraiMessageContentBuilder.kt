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

import cn.hutool.core.io.FileUtil
import io.ktor.http.*
import love.forte.catcode.CatCodeUtil
import love.forte.catcode.Neko
import love.forte.simbot.api.message.MessageContentBuilder
import love.forte.simbot.api.message.MessageContentBuilderFactory
import love.forte.simbot.api.message.containers.AccountCodeContainer
import love.forte.simbot.component.mirai.utils.toStream
import net.mamoe.mirai.message.data.AtAll
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

/**
 * [MiraiMessageContentBuilder]'s factory.
 */
public object MiraiMessageContentBuilderFactory : MessageContentBuilderFactory {
    override fun getMessageContentBuilder(): MessageContentBuilder = MiraiMessageContentBuilder()
}



/**
 * mirai对 [MessageContentBuilder] 的实现。
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class MiraiMessageContentBuilder : MessageContentBuilder {

    private var texts = StringBuilder()

    private val contentList = mutableListOf<MiraiMessageContent>()

    private fun checkText() {
        if (texts.isNotEmpty()) {
            contentList.add(MiraiSingleMessageContent(PlainText(texts.toString())))
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


    override fun imageLocal(path: String, flash: Boolean): MiraiMessageContentBuilder {
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
        val imageContent = MiraiImageMessageContent(flash, imageNeko) { file.uploadAsImage(it) }
        checkText()
        contentList.add(imageContent)
        return this
    }

    override fun imageUrl(url: String, flash: Boolean): MiraiMessageContentBuilder {
        val u = Url(url)
        val imageNeko: Neko = CatCodeUtil
            .getNekoBuilder("image", true)
            .key("file").value(url)
            .key("type").value("network")
            .apply {
                if (flash) {
                    key("flash").value(true)
                }
            }.build()
        MiraiImageMessageContent(flash, imageNeko) { contact ->
            u.toStream().use { stream -> stream.uploadAsImage(contact) }
        }.apply {
            checkText()
            contentList.add(this)
        }
        return this
    }

    override fun image(input: InputStream, flash: Boolean): MiraiMessageContentBuilder {
        val imageNeko = CatCodeUtil
            .getNekoBuilder("image", true)
            .key("type").value("stream")
            .apply {
                if (flash) {
                   key("flash").value(true)
                }
            }.build()
        MiraiImageMessageContent(flash, imageNeko){ contact ->
            input.use { inp -> inp.uploadAsImage(contact) }
        }.apply {
            checkText()
            contentList.add(this)
        }
        return this
    }

    override fun image(imgData: ByteArray, flash: Boolean): MiraiMessageContentBuilder {
        val input = ByteArrayInputStream(imgData)
        return image(input, flash)
    }

    override fun build(): MiraiMessageContent {
        checkText()
        return MiraiListMessageContent(contentList.toList())
    }
}