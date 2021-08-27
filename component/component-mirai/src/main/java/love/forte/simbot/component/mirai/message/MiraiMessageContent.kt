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
@file:JvmName("MiraiMessageContents")

package love.forte.simbot.component.mirai.message

import catcode.CatCodeUtil
import catcode.Neko
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.ReconstructorFunction
import love.forte.simbot.component.mirai.sender.isNotEmptyMsg
import love.forte.simbot.component.mirai.utils.toNeko
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.action.Nudge
import net.mamoe.mirai.message.action.Nudge.Companion.sendNudge
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.MiraiExperimentalApi


internal interface NekoAble {
    val neko: Neko?
}


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
    // 
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
            else -> list.map { it.getMessage(contact) }
                .asSequence()
                .filter { it.isNotEmptyMsg() }
                .reduceOrNull { m1, m2 -> m1 + m2 } ?: EmptyMessageChain
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
 * mirai 的 single msg, 其中包含一个可用于发送的 [singleMessage获取函数][singleMessage].
 * 此函数根据参数 [发送目标][Contact] 得到一个可发送的消息并进行发送。
 * 如果消息发送无法得到singleMessage（例如戳一戳那种异步消息），可以返回一个 [EmptySingleMessage].
 */
public class MiraiSingleMessageContent(
    val singleMessage: suspend (Contact) -> SingleMessage,
    override val neko: Neko?,
) : MiraiMessageContent(), NekoAble {

    companion object Empty : MiraiMessageContent() {
        /**
         * empty single message.
         */
        override suspend fun getMessage(contact: Contact): Message {
            return EmptySingleMessage
        }

        /**
         * empty list.
         */
        override val cats: List<Neko>
            get() = emptyList()


        override fun equals(other: Any?): Boolean {
            if (other === null) {
                return false
            }
            if (other === Empty) {
                return true
            }
            if (other is MiraiSingleMessageContent) {
                return other.neko == null
            }

            return false
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other === null) {
            return false
        }
        if (other === Empty) {
            return neko == null
        }

        if (other is MiraiSingleMessageContent) {
            return neko == other.neko
        }
        return false
    }

    override fun hashCode(): Int = neko.hashCode()

    constructor(
        singleMessage: SingleMessage,
        neko: () -> Neko? = { singleMessage.toNeko() },
    ) : this({ singleMessage }, neko())

    // private val neko: Neko? by lazy(LazyThreadSafetyMode.PUBLICATION) { _neko() }

    override suspend fun getMessage(contact: Contact): Message = singleMessage(contact)

    override fun toString(): String = msg

    override val cats: List<Neko>
        get() = neko?.let { listOf(it) } ?: emptyList()
}


/**
 * mirai 的 nudge 消息。
 */
public data class MiraiNudgedMessageContent(private val from: Long?, private val target: Long?) :
    MiraiMessageContent() {

    /**
     * nudge neko.
     */
    private val neko: Neko = CatCodeUtil.getNekoBuilder("nudge", false).apply {
        from?.let { key("from").value(it) }
        target?.let { key("target").value(it) }
    }.build()

    override val cats: List<Neko> = listOf(neko)

    @OptIn(MiraiExperimentalApi::class)
    override suspend fun getMessage(contact: Contact): Message {
        return when (contact) {
            // 如果是群
            is Group -> {
                val code: Long = target ?: throw IllegalArgumentException("Cannot found nudge target: target is empty.")
                val nudge: Nudge = contact[code]?.nudge()
                    ?: throw NoSuchElementException("Cannot found nudge target: no such member($code) in group(${contact.id}).")
                // 获取群员并发送
                contact.sendNudge(nudge)
                EmptySingleMessage
            }
            is User -> {
                val nudge: Nudge = contact.nudge()
                contact.sendNudge(nudge)
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
public class MiraiMessageChainContent constructor(
    val message: MessageChain,
    private var cache: MiraiMessageCache? = null,
) : MiraiMessageContent() {
    override suspend fun getMessage(contact: Contact): Message = message
    private lateinit var _cats: List<Neko>

    // override val cats: List<Neko> by lazy(LazyThreadSafetyMode.PUBLICATION) { message.toNeko(cache) }
    override val cats: List<Neko>
        get() {
            if (!::_cats.isInitialized) {
                _cats = message.toNeko(cache)
                cache = null
            }
            return _cats
        }


    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other is MiraiMessageChainContent) {
            return other.message == message
        }

        return false
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }

    override fun toString(): String {
        return "MiraiMessageChainContent(originalMessage=$message)"
    }


    /**
     * 消息重构
     *
     * @see MiraiMessageChainReconstructor
     */
    override fun refactor(messageReconstructor: ReconstructorFunction): MessageContent {
        return MiraiMessageChainReconstructor(this).apply { messageReconstructor(this) }.build()
    }

}


/**
 * at 一个指定的人的 message。
 */
public data class MiraiSingleAtMessageContent(private val code: Long) : MiraiMessageContent() {
    override suspend fun getMessage(contact: Contact): Message {
        return if (contact is Group) {
            At(contact.getOrFail(code))
        } else {
            if (contact is Member) {
                if (contact.id == code) {
                    At(contact)
                } else {
                    At(contact.group.getOrFail(code))
                }
            } else if (contact is Friend && contact.id == code) {
                PlainText("@${contact.nameCardOrNick}")
            } else {
                val name: String = contact.bot.getFriend(code)?.nameCardOrNick ?: code.toString()
                PlainText("@$name")
            }
        }
    }

    override val cats: List<Neko> = listOf(CatCodeUtil.nekoTemplate.at(code))
}


/**
 * mirai 的 image content，代表为通过本地上传的图片信息。
 *
 * 此实现中，image仅会被实例化一次，而后则会被缓存。
 */
public class MiraiImageMessageContent
constructor(
    private val flash: Boolean = false,
    override val neko: Neko,
    /** 是否优先上传到某个群。 */
    // private val groupFirst: Boolean = false,
    private val imageFunction: suspend (Contact) -> Image,
) : MiraiMessageContent(), NekoAble {

    override fun toString(): String = "MiraiImageContent(flash=$flash,image=${
        if (!::image.isInitialized) "(Not initialized yet.)"
        else image.toString()
    })"

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other is MiraiImageMessageContent) {
            return flash == other.flash && (
                    neko["id"]?.equals(other.neko["id"]) == true
                            || neko["file"]?.equals(other.neko["file"]) == true
                            || neko["url"]?.equals(other.neko["url"]) == true
                    )
        }

        if (other is NekoAble) {
            return neko == other.neko
        }

        return false
    }


    override fun hashCode(): Int = hash

    @Volatile
    private lateinit var image: Image

    /** lock */
    private val lock = Mutex()

    override val cats: List<Neko> = listOf(neko)

    private val hash = neko.hashCode()

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
                lock.withLock {
                    if (!::image.isInitialized) {
                        image = imageFunction(contact)
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
@Deprecated("Use MiraiAudioMessageContent", replaceWith = ReplaceWith("MiraiAudioMessageContent"))
public class MiraiVoiceMessageContent(
    override val neko: Neko,
    @Suppress("DEPRECATION") private val voiceFunction: suspend (Contact) -> Voice,
) : MiraiMessageContent(), NekoAble {

    @Suppress("DEPRECATION")
    @Volatile
    private lateinit var voice: Voice

    /** lock */
    private val lock = Mutex()

    private val hash = neko.hashCode()
    override val cats: List<Neko> = listOf(neko)

    override suspend fun getMessage(contact: Contact): Message {
        return if (::voice.isInitialized) {
            voice
        } else {
            if (!::voice.isInitialized) {
                lock.withLock {
                    if (!::voice.isInitialized) {
                        voice = voiceFunction(contact)
                    }
                }

            }
            voice
        }
    }

    override fun hashCode(): Int {
        return hash
    }
    @Suppress("DEPRECATION")
    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other is NekoAble) {
            val otherNeko = if (other is MiraiVoiceMessageContent) other.neko else other.neko ?: return false
            return with(otherNeko.type) { this == "voice" || this == "record" }
                    && (
                    neko["id"]?.equals(otherNeko["id"]) == true
                            || neko["file"]?.equals(otherNeko["file"]) == true
                            || neko["url"]?.equals(otherNeko["url"]) == true
                    )
        }


        return false
    }

}

/**
 * mirai 的 voice content.
 * 此实现类似于 [MiraiImageMessageContent]，Voice的实例化会被缓存，且存在锁来保证唯一性。
 */
public class MiraiAudioMessageContent(
    override val neko: Neko,
    private val audioFunction: suspend (Contact) -> Audio,
) : MiraiMessageContent(), NekoAble {

    @Volatile
    private lateinit var audio: Audio

    /** lock */
    private val lock = Mutex()

    private val hash = neko.hashCode()
    override val cats: List<Neko> = listOf(neko)

    override suspend fun getMessage(contact: Contact): Message {
        return if (::audio.isInitialized) {
            audio
        } else {
            if (!::audio.isInitialized) {
                lock.withLock {
                    if (!::audio.isInitialized) {
                        audio = audioFunction(contact)
                    }
                }

            }
            audio
        }
    }

    override fun hashCode(): Int {
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other is NekoAble) {
            val otherNeko = if (other is MiraiAudioMessageContent) other.neko else other.neko ?: return false
            return with(otherNeko.type) { this == "voice" || this == "record" }
                    && (
                    neko["id"]?.equals(otherNeko["id"]) == true
                            || neko["file"]?.equals(otherNeko["file"]) == true
                            || neko["url"]?.equals(otherNeko["url"]) == true
                    )
        }


        return false
    }

}


/**
 * mirai 的 file content，代表为上传的群文件信息。
 */
public class MiraiFileMessageContent
constructor(
    override val neko: Neko,
    private val uploadPath: String,
    /** 是否优先上传到某个群。 */
    private val fileFunction: suspend (Contact) -> FileMessage,
) : MiraiMessageContent(), NekoAble {

    override fun toString(): String = "MiraiFileContent(file=${
        if (!::file.isInitialized) "(Not initialized yet.)"
        else file.toString()
    })"

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other is MiraiFileMessageContent) {
            return neko["file"] == other.neko["file"] && uploadPath == other.uploadPath
        }

        if (other is NekoAble) {
            return neko == other.neko
        }

        return false
    }


    override fun hashCode(): Int = hash

    @Volatile
    private lateinit var file: FileMessage

    /** lock */
    private val lock = Mutex()

    override val cats: List<Neko> = listOf(neko)

    private val hash = neko.hashCode()

    /**
     * get image msg. 区分群消息与好友消息
     * @param contact Contact
     * @return Message
     */
    override suspend fun getMessage(contact: Contact): Message {
        return if (::file.isInitialized) {
            file
        } else {
            if (!::file.isInitialized) {
                lock.withLock {
                    if (!::file.isInitialized) {
                        file = fileFunction(contact)
                    }
                }
            }
            file
        }
    }


}


