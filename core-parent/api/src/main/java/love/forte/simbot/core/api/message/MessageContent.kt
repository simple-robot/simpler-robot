/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MessageContent.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

@file:JvmName("MessageContents")

package love.forte.simbot.core.api.message

import love.forte.catcode.CatCodeUtil

/*
 *
 * 定义消息正文接口及部分实现类
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */


/**
 * **消息内容**。
 *
 * 它被使用在[MessageEventGet] 接口的 [MessageEventGet.msgContent] 上，表示当前消息的正文内容。
 *
 * 一个 [MessageContent] 实例至少应该保证能够得到当前消息的 [消息字符串文本][msg]。
 *
 * 一般一些预期内的特殊msg，可以参考 [ExpectedMessageContent]。
 */
public interface MessageContent {
    /**
     * 消息字符串文本。
     */
    val msg: String?


    /**
     * 一个 [消息正文][MessageContent] 应当可以与其他消息进行拼接，并得到一个新的 [MessageContent]
     * @param msgContent MessageContent
     * @return MessageContent
     */
    operator fun plus(msgContent: MessageContent): MessageContent
}


/**
 * 预期内的特殊消息类型，提供一些可能会用到的特殊消息类型。
 */
public sealed class ExpectedMessageContent : MessageContent {
    override fun plus(msgContent: MessageContent): MessageContent = this compound msgContent
}


/**
 * 对于单一 [MessageContent] 的单层封装。
 * 通过 [toSingle] 进行构建，通过[isSingle] 进行判断。
 */
internal data class SingleMessageContent(val single: MessageContent) : ExpectedMessageContent() {
    override val msg: String?
        get() = single.msg

    override fun plus(msgContent: MessageContent): MessageContent {
        return when (msgContent) {
            is EmptyMessageContent -> this.copy()
            else -> this compound msgContent
        }
    }
}

/**
 * 转化为一个 Single [MessageContent]。
 */
public fun MessageContent.toSingle(): MessageContent = when (this) {
    is EmptyMessageContent -> EmptyMessageContent
    is SingleMessageContent -> copy()
    else -> SingleMessageContent(this)
}

/**
 * 判断是否为 [SingleMessageContent] 实例。
 */
public fun MessageContent.isSingle(): Boolean = this is SingleMessageContent


/**
 * 复合类型消息，连接两个 [MessageContent] 。
 * 复合类型消息通过 [compound] 进行构建、[isCompound] 进行判断，不能直接构建。
 * @see compound
 * @see isCompound
 */
internal data class CompoundMessageContent(
    val first: MessageContent,
    val second: MessageContent
) : ExpectedMessageContent() {
    /**
     * 消息字符串文本。
     */
    override val msg: String?
        get() = when {
            first.msg == null && second.msg == null -> null
            first.msg?.isBlank() == true && second.msg?.isBlank() == true -> ""

            else -> first.msg + second.msg
        }
}


/** 判断是否为复合msg。 */
public fun MessageContent.isCompound(): Boolean = this is CompoundMessageContent


/** 复合两个message。 */
public infix fun MessageContent.compound(other: MessageContent): MessageContent {
    return when {
        this is EmptyMessageContent && other is EmptyMessageContent -> EmptyMessageContent
        this is EmptyMessageContent -> other.toSingle()
        other is EmptyMessageContent -> this.toSingle()

        this is SingleMessageContent && other is SingleMessageContent ->
            CompoundMessageContent(this.single, other.single)
        this is SingleMessageContent -> CompoundMessageContent(this.single, other)
        other is SingleMessageContent -> CompoundMessageContent(this, other.single)

        else -> CompoundMessageContent(this, other)
    }
}


/**
 * 没有内容的[MessageContent]。
 */
public object EmptyMessageContent : ExpectedMessageContent() {
    override val msg: String? = null
    override fun plus(msgContent: MessageContent): MessageContent = msgContent
}


/**
 * 一个 [预期内的][ExpectedMessageContent] 以字符串消息为主体的 [MessageContent] 默认实现类。
 */
public data class TextMessageContent(override val msg: String?) : ExpectedMessageContent() {
    /**
     * 一个 [消息正文][MessageContent] 应当可以与其他消息进行拼接，并得到一个新的 [MessageContent]
     *
     * 将会直接进行字符串拼接
     */
    override fun plus(msgContent: MessageContent): MessageContent {
        return when {
            this.msg === null && msgContent.msg === null -> TextMessageContent(null)
            this.msg === null -> TextMessageContent(msgContent.msg)
            msgContent.msg === null -> this.copy()
            else -> TextMessageContent(this.msg + msgContent.msg)
        }
    }
}


/**
 * 一个 [预期内的][ExpectedMessageContent] 以图片作为消息主体的 [MessageContent] 默认实现类。
 * 图片消息的 [msg] 以猫猫码展示。
 *
 * @property id 此图片的ID, 如果是本地手动构建的则可能为空字符串。
 * @property path 图片的网络路径或者本地文件路径。
 * @property url 图片对应的网络链接地址。如果是通过本地文件构建的，则会抛出异常。
 */
public open class ImageMessageContent(
    open val id: String,
    open val path: String,
    open val url: String
) : ExpectedMessageContent() {

    /**
     * 消息字符串文本。
     *
     */
    override val msg: String?
        get() = CatCodeUtil.getStringCodeBuilder("image")
            .key("id").value(id)
            .key("file").value(path)
            .key("url").value(url)
            .build()

    /**
     * 一个 [消息正文][MessageContent] 应当可以与其他消息进行拼接，并得到一个新的 [MessageContent]
     * @param msgContent MessageContent
     * @return MessageContent
     */
    override fun plus(msgContent: MessageContent): MessageContent {
        TODO("Not yet implemented")
    }
}







