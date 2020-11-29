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
@file:JvmName("MiraiMessageContents")

package love.forte.simbot.component.mirai.message

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import love.forte.catcode.CatCodeUtil
import love.forte.catcode.Neko
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.component.mirai.sender.isNotEmptyMsg
import love.forte.simbot.component.mirai.utils.toNeko
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.getFriendOrNull
import net.mamoe.mirai.message.action.Nudge
import net.mamoe.mirai.message.action.Nudge.Companion.sendNudge
import net.mamoe.mirai.message.data.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * 一个 mirai 组件所使用的 [MessageContent] 实现。
 */
public abstract class MiraiMessageContent : MessageContent {
    /**
     * 根据一个 [Contact] 实例获取一个 [Message]。
     */
    abstract suspend fun getMessage(contact: Contact): Message

    /**
     * 消息字符串文本。一般来讲，如果存在一些特殊消息，
     * 那么他们会作为 **Cat码** 字符串存在于消息中。
     * 如果类型为text，不会以cat码的格式被展示。
     */
    // @JvmDefault
    override val msg: String by lazy(LazyThreadSafetyMode.PUBLICATION) {
        when {
            cats.isEmpty() -> ""
            cats.size == 1 && cats.first().type == "text" -> cats.first()["text"] ?: ""
            else -> cats.joinToString("") {
                if (it.type == "text") {
                    it["text"] ?: ""
                } else {
                    it.toString()
                }
            }
        }
    }


    /**
     * mirai消息转化为cat码进行展示。
     */
    abstract override val cats: List<Neko>
}


/**
 * 存在多个列表的 Mirai list message content.
 */
public data class MiraiListMessageContent(val list: List<MiraiMessageContent>) : MiraiMessageContent() {
    override suspend fun getMessage(contact: Contact): Message {
        return when {
            list.isEmpty() -> EmptyMessageChain
            list.size == 1 -> list[0].getMessage(contact)
            list.size == 2 -> list.first().getMessage(contact) + list.last().getMessage(contact)
            else -> list.map { contact.async { it.getMessage(contact) } }.asSequence().map {
                runBlocking { it.await() }
            }.filter { it.isNotEmptyMsg() }.reduceOrNull { m1, m2 -> m1 + m2 } ?: EmptyMessageChain
        }

    }

    /**
     * lazy parse to cat.
     */
    override val cats: List<Neko> by lazy(LazyThreadSafetyMode.PUBLICATION) {
        list.asSequence().flatMap { it.cats.asSequence() }.toList()
    }

}


/**
 * mirai 组件所使用的一个 [SingleMessage] 实现。
 * 应当保证最终发送的时候被过滤掉。
 */
public object EmptySingleMessage : SingleMessage by PlainText("")


/**
 * mirai 的 single msg.
 */
public class MiraiSingleMessageContent(
    val singleMessage: (Contact) -> SingleMessage,
    private val neko: Neko?,
) : MiraiMessageContent() {

    constructor(singleMessage: SingleMessage, neko: Neko?) : this({ singleMessage }, neko)

    constructor(
        singleMessage: SingleMessage,
        neko: () -> Neko? = { singleMessage.toNeko() },
    ) : this({ singleMessage }, neko())

    // private val neko: Neko? by lazy(LazyThreadSafetyMode.PUBLICATION) { _neko() }

    override suspend fun getMessage(contact: Contact): Message = singleMessage(contact)

    override fun toString(): String = msg ?: "SingleMessage(null)"

    override val cats: List<Neko>
        get() = neko?.let { listOf(it) } ?: emptyList()
}


/**
 * mirai 的 nudge 消息。
 */
public class MiraiNudgedMessageContent(private val target: Long?) :
    MiraiMessageContent() {

    /**
     * nudge neko.
     */
    private val neko: Neko = CatCodeUtil.getNekoBuilder("nudge", false).apply {
        target?.let { key("target").value(it) }
    }.build()

    override val cats: List<Neko> = listOf(neko)

    override suspend fun getMessage(contact: Contact): Message {
        return when (contact) {
            // 如果是群
            is Group -> {
                val code: Long = target ?: throw IllegalArgumentException("cannot found nudge target: target is empty.")
                val nudge: Nudge = contact.getOrNull(code)?.nudge()
                    ?: throw NoSuchElementException("cannot found nudge target: no such member($code) in group(${contact.id}).")
                // 获取群员并发送
                contact.launch {
                    contact.sendNudge(nudge)
                }
                EmptySingleMessage
            }
            is User -> {
                val nudge: Nudge = contact.nudge()
                contact.launch {
                    contact.sendNudge(nudge)
                }
                EmptySingleMessage
            }
            // 是其他人
            else -> EmptySingleMessage
        }
    }


}


/**
 *
 * mirai的消息载体，一般内部存放着已经存在的 [MessageChain]。
 * 此类一般构建于接收到消息的时候，即有明确的 [消息链][MessageChain] 的时候。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class MiraiMessageChainContent(val message: MessageChain) : MiraiMessageContent() {
    override suspend fun getMessage(contact: Contact): Message = message
    override val cats: List<Neko> by lazy(LazyThreadSafetyMode.PUBLICATION) { message.toNeko() }
}


/**
 * at 一个指定的人的 message。
 */
public class MiraiSingleAtMessageContent(private val code: Long) : MiraiMessageContent() {
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

    override val cats: List<Neko> = listOf(CatCodeUtil.nekoTemplate.at(code))
}


/**
 * mirai 的 image content，代表为通过本地上传的图片信息。
 * 此实现中，image仅会被实例化一次，而后则会被缓存。
 * 线程并不安全。
 */
public class MiraiImageMessageContent(
    private val flash: Boolean = false,
    neko: Neko,
    private val imageFunction: suspend (Contact) -> Image,
) : MiraiMessageContent() {

    override fun toString(): String = "MiraiImageContent(flash=$flash,image=${
        if (!::image.isInitialized) "(Not initialized yet.)"
        else image.toString()
    })"

    @Volatile
    private lateinit var image: Image


    override val cats: List<Neko> = listOf(neko)

    /**
     * get image msg. 区分群消息与好友消息
     * @param contact Contact
     * @return Message
     */
    override suspend fun getMessage(contact: Contact): Message {
        return if (::image.isInitialized) {
            image
        } else {
            if (!::image.isInitialized) {
                val img = imageFunction(contact)
                if (!::image.isInitialized) {
                    try {
                        image = img
                    } catch (ignore: Exception) {
                    }
                }
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


}


/**
 * mirai 的 voice content.
 * 此实现类似于 [MiraiImageMessageContent]，Voice的实例化会被缓存，且存在锁来保证唯一性。
 */
public class MiraiVoiceMessageContent(
    neko: Neko,
    private val voiceFunction: suspend (Contact) -> Voice,
) : MiraiMessageContent() {

    @Volatile
    private lateinit var voice: Voice

    override val cats: List<Neko> = listOf(neko)

    override suspend fun getMessage(contact: Contact): Message {
        return if (::voice.isInitialized) {
            voice
        } else {
            if (!::voice.isInitialized) {
                val vo = voiceFunction(contact)
                if (!::voice.isInitialized) {
                    try {
                        voice = vo
                    } catch (ignore: Exception) {
                    }
                }

            }
            voice
        }
    }
}

