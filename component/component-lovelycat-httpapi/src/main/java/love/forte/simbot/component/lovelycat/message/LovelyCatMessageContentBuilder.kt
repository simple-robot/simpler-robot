/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatMessageContentBuilder.kt
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

package love.forte.simbot.component.lovelycat.message

import cn.hutool.core.codec.Base64
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.MessageContentBuilder
import love.forte.simbot.api.message.MessageContentBuilderFactory
import java.io.InputStream


/**
 * [MessageContentBuilderFactory] 实现。
 */
public object LovelyCatMessageContentBuilderFactory : MessageContentBuilderFactory {
    override fun getMessageContentBuilder() = LovelyCatMessageContentBuilder()
}


/**
 * [MessageContentBuilder] 的 可爱猫实现。
 */
public class LovelyCatMessageContentBuilder : MessageContentBuilder {

    private lateinit var _sb: StringBuilder
    private val sb: StringBuilder
    get() {
        if (!::_sb.isInitialized) {
            _sb = StringBuilder()
        }
        return _sb
    }
    private val sbOrEmpty: String
    get() = if (!::_sb.isInitialized) {
        ""
    } else {
        _sb.toString()
    }

    private lateinit var _img: MutableList<String>
    private val img: MutableList<String>
    get() {
        if (!::_img.isInitialized) {
            _img = mutableListOf()
        }
        return _img
    }
    private val imgOrEmpty: List<String>
    get() = if (!::_img.isInitialized) {
        emptyList()
    } else {
        _img
    }

    private lateinit var _at: MutableList<String>
    private val at: MutableList<String>
    get() {
        if (!::_at.isInitialized) {
            _at = mutableListOf()
        }
        return _at
    }
    private val atOrEmpty: List<String>
        get() = if (!::_at.isInitialized) {
            emptyList()
        } else {
            _at
        }

    private var atAll = false

    /** 最基础的消息类型。向当前构建的消息中追加一个 文本消息。 */
    override fun text(text: CharSequence): LovelyCatMessageContentBuilder = apply {
        sb.append(text)
    }

    /**
     *  向当前构建的消息中追加一个 'at全体'的消息。
     *  修改群公告就是at全体。
     * */
    override fun atAll(): LovelyCatMessageContentBuilder = apply {
        atAll = true
    }

    /** 向当前构建的消息中追加一个 'at某人'的消息。 */
    override fun at(code: String): LovelyCatMessageContentBuilder = apply {
        at.add(code)
    }

    /**
     *  向当前构建的消息中追加一个 '表情'消息。
     *  可爱猫似乎不支持发送表情。
     * */
    override fun face(id: String): LovelyCatMessageContentBuilder = apply {
        sb.append("[表情:$id]")
    }

    /** 向当前构建的消息中追加一个本地图片。 */
    override fun imageLocal(path: String, flash: Boolean): LovelyCatMessageContentBuilder = apply {
        img.add(path)
    }

    /** 向当前构建的消息中追加一个网络图片。 */
    override fun imageUrl(url: String, flash: Boolean): LovelyCatMessageContentBuilder = apply {
        img.add(url)
    }

    /** 向当前构建的消息中追加一个图片流。 */
    override fun image(input: InputStream, flash: Boolean): LovelyCatMessageContentBuilder = apply {
        val base64 = input.use { Base64.encode(it) }
        img.add("BASE64:$base64")
    }

    /** 向当前构建的消息中追加一个图片字节数组。 */
    override fun image(imgData: ByteArray, flash: Boolean): LovelyCatMessageContentBuilder = apply {
        val base64 = Base64.encode(imgData)
        img.add("BASE64:$base64")
    }

    /** 得到当前构建的消息。 */
    override fun build(): MessageContent {
        // println(atOrEmpty)
        return LovelyCatForSendMessageContent(
            sbOrEmpty,
            imgOrEmpty,
            atOrEmpty,
            atAll
        )
    }
}