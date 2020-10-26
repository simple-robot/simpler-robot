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
@file:JvmName("MiraiMessageContents")
package love.forte.simbot.component.mirai.message

import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import love.forte.catcode.CatCodeUtil
import love.forte.simbot.api.message.events.ImageMessageContent
import love.forte.simbot.api.message.events.MessageContent
import love.forte.simbot.api.message.events.VoiceMessageContent
import love.forte.simbot.component.mirai.sender.isNotEmptyMsg
import love.forte.simbot.component.mirai.utils.EmptyMiraiMessageContent
import love.forte.simbot.component.mirai.utils.toSimbotString
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
public interface MiraiMessageContent : MessageContent {
    /**
     * 根据一个 [Contact] 实例获取一个 [Message]。
     */
    suspend fun getMessage(contact: Contact): Message

    /**
     * 消息字符串文本。一般来讲，如果存在一些特殊消息，
     * 那么他们会作为 **Cat码** 字符串存在于消息中。
     */
    // @JvmDefault
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
            }.filter { it.isNotEmptyMsg() }.reduceOrNull { m1, m2 -> m1 + m2 } ?: EmptyMessageChain
        }

    }
    override val msg: String by lazy(LazyThreadSafetyMode.NONE) { list.joinToString("") { it.msg ?: "" } }

    override val images: List<ImageMessageContent> by lazy(LazyThreadSafetyMode.NONE) {
        list.flatMap { it.images }.takeIf { it.isNotEmpty() } ?: emptyList()
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
public class MiraiSingleMessageContent(val singleMessage: (Contact) -> SingleMessage, private val _msg: () -> String?) : MiraiMessageContent {

    constructor(singleMessage: SingleMessage, msg: String?): this({ singleMessage }, { msg })

    constructor(singleMessage: SingleMessage, msg: () -> String? = { singleMessage.toSimbotString() }): this({ singleMessage }, msg)

    override suspend fun getMessage(contact: Contact): Message = singleMessage(contact)

    override val msg: String? get() = _msg()

    override val images: List<ImageMessageContent> get() = emptyList()

    override fun toString(): String = msg ?: "SingleMessage(null)"
}


/**
 * mirai 的 nudge 消息。
 */
public class MiraiNudgedMessageContent(private val target: Long?) : MiraiMessageContent by MiraiSingleMessageContent({ contact ->
    when(contact) {
        // 如果是群
        is Group -> {
            val code: Long = target ?: throw IllegalArgumentException("cannot found nudge target: target is empty.")
            val nudge: Nudge = contact.getOrNull(code)?.nudge() ?: throw NoSuchElementException("cannot found nudge target: no such member($code) in group(${contact.id}).")
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
}, { null }) {
    override val msg: String = CatCodeUtil.getStringCodeBuilder("nudge").apply {
        target?.let { key("target").value(it) }
    }.build()
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
 * 此实现中，image仅会被实例化一次，而后则会被缓存。
 * 实例化会由锁保证其唯一性。
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

    @Volatile
    private lateinit var image: Image

    private val lock: Lock = ReentrantLock()

    override suspend fun getMessage(contact: Contact): Message =
        if (::image.isInitialized) {
            image
        } else {
            lock.lock()
            try {
                if (!::image.isInitialized) {
                    val img = imageFunction(contact)
                    image = img
                }
                image
            }finally {
                lock.unlock()
            }
        }.let {
            if (flash) {
                it.flash()
            } else {
                it
            }
        }
}


/**
 * mirai 的 voice content.
 * 此实现类似于 [MiraiImageMessageContent]，Voice的实例化会被缓存，且存在锁来保证唯一性。
 */
public class MiraiVoiceMessageContent(
    id: String,
    path: () -> String? = { null },
    url: () -> String? = { null },
    private val voiceFunction: suspend (Contact) -> Voice
) : VoiceMessageContent(
    id, path, url
), MiraiMessageContent {

    constructor(
        id: String,
        path: String? = null,
        url: String? = null,
        voiceFunction: suspend (Contact) -> Voice
    ): this(id, { path }, { url }, voiceFunction)

    constructor(
        id: String,
        voiceFunction: suspend (Contact) -> Voice
    ): this(id, null, null, voiceFunction)

    @Volatile
    private lateinit var voice: Voice

    private val lock: Lock = ReentrantLock()

    override suspend fun getMessage(contact: Contact): Message {
        return if (::voice.isInitialized) {
            voice
        } else {
            lock.lock()
            try {
                if (!::voice.isInitialized) {
                    val vo = voiceFunction(contact)
                    voice = vo
                }
                voice
            }finally {
                lock.unlock()
            }
        }
    }
}

