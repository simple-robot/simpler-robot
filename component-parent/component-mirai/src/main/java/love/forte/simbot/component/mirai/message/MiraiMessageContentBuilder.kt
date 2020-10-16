/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiMessageContentBuilder.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.message

import cn.hutool.core.io.FileUtil
import love.forte.simbot.component.mirai.utils.toStream
import love.forte.simbot.core.api.message.MessageContentBuilder
import love.forte.simbot.core.api.message.MessageContentBuilderFactory
import love.forte.simbot.core.api.message.containers.AccountCodeContainer
import net.mamoe.mirai.message.data.AtAll
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.uploadAsImage
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URL

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

    private val contentList = mutableListOf<MiraiMessageContent>()

    override fun text(text: String): MessageContentBuilder {
        contentList.add(MiraiSingleMessageContent(PlainText(text)))
        return this
    }

    override fun atAll(): MiraiMessageContentBuilder {
        contentList.add(MiraiSingleMessageContent(AtAll))
        return this
    }

    private fun at0(code: Long): MiraiMessageContentBuilder {
        contentList.add(MiraiSingleAtMessageContent(code))
        return this
    }

    override fun at(code: String): MiraiMessageContentBuilder = at0(code.toLong())
    override fun at(code: Long): MessageContentBuilder = at0(code)
    override fun at(code: AccountCodeContainer): MessageContentBuilder = at(code.accountCodeNumber)


    private fun face0(id: Int): MiraiMessageContentBuilder {
        contentList.add(MiraiSingleMessageContent(Face(id)))
        return this
    }

    override fun face(id: String): MessageContentBuilder = face0(id.toInt())
    override fun face(id: Int): MessageContentBuilder = face0(id)


    override fun imageLocal(path: String, flash: Boolean): MiraiMessageContentBuilder {
        val file = FileUtil.file(path)?.takeIf { it.exists() } ?: throw FileNotFoundException(path)
        val imageContent = MiraiImageMessageContent(path, path = path, flash = flash) { file.uploadAsImage(it) }
        contentList.add(imageContent)
        return this
    }

    override fun imageUrl(url: String, flash: Boolean): MiraiMessageContentBuilder {
        val URL = URL(url)
        MiraiImageMessageContent(url, url = url, flash = flash) {
            URL.toStream().uploadAsImage(it)
        }.apply { contentList.add(this) }
        return this
    }

    override fun image(input: InputStream, flash: Boolean): MiraiMessageContentBuilder {
        MiraiImageMessageContent(id = "", flash){
            input.uploadAsImage(it)
        }.apply { contentList.add(this) }
        return this
    }

    override fun image(imgData: ByteArray, flash: Boolean): MiraiMessageContentBuilder {
        val input = ByteArrayInputStream(imgData)
        return image(input, flash)
    }

    override fun build(): MiraiMessageContent = MiraiListMessageContent(contentList.toList())
}