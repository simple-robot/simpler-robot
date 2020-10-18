/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiMessageContent.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.message

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import love.forte.catcode.CatCodeUtil
import love.forte.simbot.component.mirai.utils.EmptyMiraiMessageContent
import love.forte.simbot.component.mirai.utils.toSimbotString
import love.forte.simbot.core.api.message.events.ImageMessageContent
import love.forte.simbot.core.api.message.events.MessageContent
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.getFriendOrNull
import net.mamoe.mirai.message.data.*

/**
 * 一个 mirai 组件所使用的 [MessageContent] 实现。
 */
public interface MiraiMessageContent : MessageContent {
    /**
     * 根据一个 [Contact] 实例获取一个 [Message]。
     */
    suspend fun getMessage(contact: Contact): Message

    /**
     * 消息字符串文本。一般来讲，如果存在一些特殊消息，
     * 那么他们会作为 **Cat码** 字符串存在于消息中。
     */
    @JvmDefault
    override val msg: String?
}


/**
 * 存在多个列表的 Mirai list message content.
 */
public data class MiraiListMessageContent(val list: List<MiraiMessageContent>) : MiraiMessageContent {
    override suspend fun getMessage(contact: Contact): Message {
        return when {
            list.isEmpty() -> EmptyMessageChain
            list.size == 1 -> list[0].getMessage(contact)
            list.size == 2 -> list.first().getMessage(contact) + list.last().getMessage(contact)
            else -> list.map { contact.async { it.getMessage(contact) } }.asSequence().map {
                runBlocking { it.await() }
            }.reduce { m1, m2 -> m1 + m2 }
        }

    }
    override val msg: String by lazy(LazyThreadSafetyMode.NONE) { list.joinToString("") { it.msg ?: "" } }

    override val images: List<ImageMessageContent> by lazy(LazyThreadSafetyMode.NONE) {
        list.flatMap { it.images }.takeIf { it.isNotEmpty() } ?: emptyList()
    }
}


// /**
//  * mirai 消息拼接。
//  */
// public fun MiraiMessageContent.plus(other: MiraiMessageContent) : MiraiMessageContent {
//     return when {
//         this is MiraiListMessageContent && other is MiraiListMessageContent ->
//             MiraiListMessageContent(this.list + other.list)
//
//     }
// }


/**
 * mirai 组件所使用的一个 [SingleMessage] 实现。
 * 最终发送的时候将会被过滤掉。
 */
public object EmptySingleMessage : SingleMessage {
    override fun contentToString(): String = ""
    override fun toString(): String = ""
    override fun equals(other: Any?): Boolean = other === this
    override fun contentEquals(another: Message, ignoreCase: Boolean): Boolean = another === this
    override fun contentEquals(another: String, ignoreCase: Boolean): Boolean = another == ""
}


/**
 * mirai 的 single msg.
 */
public class MiraiSingleMessageContent(val singleMessage: (Contact) -> SingleMessage, private val _msg: () -> String?) : MiraiMessageContent {

    constructor(singleMessage: SingleMessage, msg: String?): this({ singleMessage }, { msg })

    constructor(singleMessage: SingleMessage, msg: () -> String? = { singleMessage.toSimbotString() }): this({ singleMessage }, msg)

    override suspend fun getMessage(contact: Contact): Message = singleMessage(contact)

    override val msg: String? get() = _msg()

    override val images: List<ImageMessageContent> = emptyList()


}


/**
 *
 * mirai的消息载体，一般内部存放着已经存在的 [MessageChain]。
 * 此类一般构建于接收到消息的时候，即有明确的 [消息链][MessageChain] 的时候。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class MiraiMessageChainContent(val message: MessageChain) : MiraiMessageContent {

    override suspend fun getMessage(contact: Contact): Message = message

    override val msg: String by lazy(LazyThreadSafetyMode.NONE) { message.toSimbotString() }

    override val images: List<ImageMessageContent> by lazy(LazyThreadSafetyMode.NONE) {
        message.asSequence()
            .filter { it is Image || it is FlashImage }
            .map {
                val img: Image = if (it is FlashImage) it.image else it as Image
                MiraiImageMessageContent(
                    img.imageId,
                    { null }, // no path.
                    { runBlocking { img.queryUrl() } },
                    it is FlashImage,
                    { img }
                )
            }.toList()
    }
}


public fun List<Long>.atContent(): MiraiMessageContent = when {
    this.isEmpty() -> EmptyMiraiMessageContent
    this.size == 1 -> MiraiSingleAtMessageContent(this[0])
    else -> MiraiAtsMessageContent(this)
}


/**
 * at 一个指定的人的 message。
 */
public class MiraiSingleAtMessageContent(private val code: Long) : MiraiMessageContent {
    override suspend fun getMessage(contact: Contact): Message {
        return if (contact is Group) {
            At(contact[code])
        } else {
            if (contact is Member) {
                if (contact.id == code) {
                    At(contact)
                } else {
                    At(contact.group[code])
                }
            } else if (contact is Friend && contact.id == code) {
                PlainText("@${contact.nameCardOrNick}")
            } else {
                val name: String = contact.bot.getFriendOrNull(code)?.nameCardOrNick ?: code.toString()
                PlainText("@$name")
            }
        }
    }

    // at cat msg.
    override val msg: String by lazy(LazyThreadSafetyMode.NONE) { CatCodeUtil.stringTemplate.at(code) }

    override val images: List<ImageMessageContent> = emptyList()
}

/**
 * at 多个指定的人的 message。
 */
public class MiraiAtsMessageContent(private val codes: List<Long>) : MiraiMessageContent {
    override suspend fun getMessage(contact: Contact): Message {
        return if (contact is Group) {
            codes.asSequence().map { At(contact[it]) as MessageChain }.reduce { acc, at -> acc + at }
        } else {
            if (contact is Member) {
                codes.asSequence().map {
                    if (contact.id == it) {
                        At(contact)
                    } else {
                        At(contact.group[it])
                    } as MessageChain
                }.reduce { acc, at -> acc + at }

            } else {
                codes.asSequence().map {
                    if (contact is Friend && contact.id == it) {
                        PlainText("@${contact.nameCardOrNick}")
                    } else {
                        val name: String = contact.bot.getFriendOrNull(it)?.nameCardOrNick ?: it.toString()
                        PlainText("@$name")
                    } as MessageChain
                }.reduce { acc, at -> acc + at }
            }
        }
    }

    // at cat msg.
    override val msg: String by lazy(LazyThreadSafetyMode.NONE) {
        codes.joinToString("") { CatCodeUtil.stringTemplate.at(it) }
    }

    override val images: List<ImageMessageContent> = emptyList()
}


/**
 * mirai 的 image content，代表为通过本地上传的图片信息。
 */
public class MiraiImageMessageContent(
    id: String,
    path: () -> String? = { null },
    url: () -> String? = { null },
    override val flash: Boolean = false,
    private val imageFunction: suspend (Contact) -> Image
) : ImageMessageContent(
        id,
        flash,
        path,
        url
    ), MiraiMessageContent {

    constructor(
        id: String,
        path: String? = null,
        url: String? = null,
        flash: Boolean,
        imageFunction: suspend (Contact) -> Image
    ): this(id, { path }, { url }, flash, imageFunction)

    constructor(
        id: String,
        flash: Boolean,
        imageFunction: suspend (Contact) -> Image
    ): this(id, null, null, flash, imageFunction)

    private lateinit var image: Image


    override suspend fun getMessage(contact: Contact): Message =
        if (::image.isInitialized) {
            image
        } else {
            val img = imageFunction(contact)
            if (!::image.isInitialized) {
                image = img
            }
            image
        }.let {
            if (flash) {
                it.flash()
            } else {
                it
            }
        }
}
