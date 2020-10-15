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
import love.forte.simbot.core.api.message.ImageMessageContent
import love.forte.simbot.core.api.message.MessageContent
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


public data class MiraiCompoundMessageContent(
    val first: MiraiMessageContent,
    val second: MiraiMessageContent
) : MiraiMessageContent {
    override suspend fun getMessage(contact: Contact): Message {
        val f = contact.async { first.getMessage(contact) }
        val s = contact.async { second.getMessage(contact) }
        return f.await() + s.await()
    }

    override val msg: String?
        get() = "CompoundMessageContent(${(first.msg)}, ${(second.msg)})"
}


/**
 * mirai 的 single msg.
 */
public class MiraiSingleMessageContent(val singleMessage: SingleMessage) : MiraiMessageContent {
    override suspend fun getMessage(contact: Contact): Message = singleMessage

    // TODO msg
    override val msg: String = singleMessage.toString()
}


/**
 *
 * mirai的消息载体，一般内部存放着已经存在的 [MessageChain]。
 * 此类一般构建于接收到消息的时候。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class MiraiMessageChainContent(val messageChain: MessageChain) : MiraiMessageContent {

    override suspend fun getMessage(contact: Contact): Message = messageChain

    // TODO msg
    override val msg: String = messageChain.toString()
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
}


/**
 * mirai 的 image content，代表为通过本地上传的图片信息。
 */
public class MiraiImageMessageContent(
    id: String,
    path: String? = null,
    url: String? = null,
    override val flash: Boolean = false,
    private val imageFunction: suspend (Contact) -> Image
) :
    ImageMessageContent(
        id,
        flash,
        { path },
        { url }
    ), MiraiMessageContent {

    override suspend fun getMessage(contact: Contact): Message = imageFunction(contact).let {
        if (flash) {
            it.flash()
        } else {
            it
        }
    }

    override val msg: String
        get() = super.msg

}
