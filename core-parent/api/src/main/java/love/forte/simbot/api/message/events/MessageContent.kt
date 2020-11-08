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
@file:JvmMultifileClass
package love.forte.simbot.api.message.events

import love.forte.catcode.CatCodeUtil
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

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
 * 从 [MessageGet.msgContent] 获取到的消息内容有可能是一个 **复合消息**，即一个 msgContent 中存在多个不同类型的消息，
 * 也有可能只是一个 **独立消息**，即其本身就是全部的消息内容。
 *
 * 它被使用在[MessageGet] 接口的 [MessageGet.msgContent] 上，表示当前消息的正文内容。
 *
 * 一个 [MessageContent] 实例至少应该保证能够得到当前消息的 [消息字符串文本][msg]。
 *
 * 一般一些预期内的特殊msg，可以参考 [ExpectedMessageContent]。
 *
 * 对于例如 [msg]、[images]等内容的获取，有些组件可能会需要使用懒加载来提高效率。
 * 在实现懒加载的时候不需要考虑线程安全，对于线程安全的问题应当由使用者自行考虑。
 *
 *
 * 这时候就有人会问了：“为什么不提供一个方法来获取一个 [MessageContent] 中的所有类型的消息链呢？”
 * 问得好。因为simbot几乎没有绝对定义的消息类型。at、image、text这些还好理解，因为它们很常见。
 * 但是除了这种十分常见的消息类型以外，我不能保证所有的组件中出现的消息类型都在我的预期之内。
 * 因此我只会陆续提供部分较为特殊的消息类型，例如 image 类型。
 * 而其他类型的消息，或许会陆续进行支持，也或许会仅仅提供一个 `type` 参数进行区分。
 *
 * 至于其他一切可能出现的类型，[msg] 与 [CatCodeUtil.split] 可以满足绝大部分的可能性与需求。
 *
 * 如果你不关心特殊消息的具体内容，而只需要消息中的纯文本部分的内容，那么你可以考虑使用 [MsgGet.text]。
 *
 * 如果你只需要一些目前可以提供的特殊消息内容，例如 **图片** 内容，那么你可以使用 [images]。
 *
 * 如果你想要自行解析任何 **可能** 出现的消息内容，例如可能会出现的 **face(表情)**、**nudge(头像戳一戳)**、**share(分享)** 等，
 * 那么你需要考虑使用 [MessageGet.msg] 或者 [MessageContent.msg] 来根据其中的CAT码自行进行解析。
 *
 * 但是需要注意的是，有些情况下，[MessageGet.msg] 相比较于 [MsgGet.text] 的效率会更加低下，而有些时候则恰恰相反，
 * 这取决于组件的实现细节，还需要仔细参阅对应组件的文档说明来进行抉择。
 *
 *
 */
public interface MessageContent {
    /**
     * 消息字符串文本。
     */
    val msg: String?

    // /**
    //  * 此消息的消息类型。
    //  */
    // val type: String

    /**
     * 尝试寻找消息内容中的 [图片消息][ImageMessageContent]。
     * 如果是复合消息，则寻找，如果是单独的消息，则除非自身即为图片消息，否则返回空集合。
     *
     * 返回的集合为read-only list.
     *
     */
    val images: List<ImageMessageContent>
}


/**
 * 预期内的特殊消息类型，提供一些可能会用到的特殊消息类型。
 */
public sealed class ExpectedMessageContent : MessageContent


/**
 * 包装性质的 [MessageContent].
 * 包装性质的实现类不应暴露其构造，而是通过对应方法获取实例。当然，[EmptyMessageContent] 除外。
 */
public sealed class BoxedMessageContent : ExpectedMessageContent() {
    /** 作为包装类型，都可以实现自我复制。 */
    abstract fun copy(): BoxedMessageContent
}


/**
 * 对于单一 [MessageContent] 的单层封装。
 * 通过 [toSingle] 进行构建，通过[isSingle] 进行判断。
 */
public data class SingleMessageContent
internal constructor(val single: MessageContent) : BoxedMessageContent() {
    override val msg: String?
        get() = single.msg

    override val images: List<ImageMessageContent> get() =
        if (single is ImageMessageContent) listOf(single)
        else emptyList()


    override fun copy(): SingleMessageContent = SingleMessageContent(single)

}

/**
 * 转化为一个 单只 [MessageContent]，其结果为一个包装类型的最终结果。
 */
public fun MessageContent.toSingle(): MessageContent = when (this) {
    is BoxedMessageContent -> copy()
    else -> SingleMessageContent(this)
}

/**
 * 判断是否为 [SingleMessageContent] 实例。
 * 通过 [MessageContent.plus] 或 [[MessageContent.toSingle] 可能会获取到此类实例。
 */
@OptIn(ExperimentalContracts::class)
public fun MessageContent.isSingle(): Boolean {
    contract {
        returns(true) implies (this@isSingle is SingleMessageContent)
    }
    return this is SingleMessageContent
}


/**
 * 消息链，复合型message。内部记录了多个 [MessageContent]。
 * 通过 [MessageContent.plus] 可能会获取到此类实例。
 */
public open class CompoundMessageContent
internal constructor(val msgList: List<MessageContent>) : BoxedMessageContent() {
    override val msg: String by lazy(LazyThreadSafetyMode.NONE) {
        msgList.joinToString { it.msg ?: "" }
    }
    override val images: List<ImageMessageContent> by lazy(LazyThreadSafetyMode.NONE) {
        msgList.flatMap { it.images }.takeIf { it.isNotEmpty() } ?: emptyList()
    }

    override fun copy(): CompoundMessageContent = CompoundMessageContent(msgList.toList())
}




/** 判断是否为复合msg。 */
@OptIn(ExperimentalContracts::class)
public fun MessageContent.isCompound(): Boolean {
    contract {
        returns(true) implies (this@isCompound is CompoundMessageContent)
    }
    return this is CompoundMessageContent
}


/** 尝试复合两个 message。 */
public infix operator fun MessageContent.plus(other: MessageContent): MessageContent {
    return when {
        // 两者都是box
        this is BoxedMessageContent && other is BoxedMessageContent -> when {
            this == EmptyMessageContent && other is EmptyMessageContent -> EmptyMessageContent
            this == EmptyMessageContent -> other.copy()
            other == EmptyMessageContent -> this.copy()

            // no empty.
            this is CompoundMessageContent && other is CompoundMessageContent ->
                CompoundMessageContent(this.msgList + other.msgList)
            this is CompoundMessageContent && other is SingleMessageContent ->
                CompoundMessageContent(this.msgList + listOf(other.single))
            this is SingleMessageContent && other is CompoundMessageContent ->
                CompoundMessageContent(listOf(this.single) + other.msgList)
            this is SingleMessageContent && other is SingleMessageContent ->
                CompoundMessageContent(listOf(this.single, other.single))
            // 不应出现的情况
            else -> throw IllegalStateException("What shouldn't happen: $this + $other") // CompoundMessageContent(listOf(this.copy(), other.copy()))
        }

        // this is box
        this is BoxedMessageContent -> when(this) {
            EmptyMessageContent -> other.toSingle()
            is SingleMessageContent -> CompoundMessageContent(listOf(this.single, other))
            is CompoundMessageContent -> CompoundMessageContent(this.msgList + listOf(other))

        }

        // other is box
        other is BoxedMessageContent -> when(other) {
            EmptyMessageContent -> this.toSingle()
            is SingleMessageContent -> CompoundMessageContent(listOf(this, other.single))
            is CompoundMessageContent -> CompoundMessageContent(listOf(this) + other.msgList)
        }

        // no box.
        else -> CompoundMessageContent(listOf(this, other))
    }
}


/**
 * 没有内容的 [MessageContent]。
 * 也属于一个 [BoxedMessageContent]。
 */
public object EmptyMessageContent : BoxedMessageContent() {
    override val msg: String? = null
    override val images: List<ImageMessageContent> = emptyList()
    override fun copy(): EmptyMessageContent = this
}


/**
 * 判断一个消息是否为 [EmptyMessageContent]。
 */
public fun MessageContent.isEmpty(): Boolean = this == EmptyMessageContent


/**
 * 一个 [预期内的][ExpectedMessageContent] 以字符串消息为主体的 [MessageContent] 默认实现类。
 */
public data class TextMessageContent(override val msg: String?) : ExpectedMessageContent() {
    /** 文本消息不存在images。 */
    override val images: List<ImageMessageContent> = emptyList()



}


/**
 * 代表一个可能获取到 [url] 的消息类型。
 */
public interface NetMessageContent : MessageContent {
    /**
     * 获取url。如果获取不到则抛出异常。
     * @throws IllegalStateException 获取不到时。
     */
    val url: String

    /**
     * 获取url，或者null。
     */
    fun getUrlOrNull(): String?
}


/**
 * 一个 [预期内的][ExpectedMessageContent] 以图片作为消息主体的 [MessageContent] 默认实现类。
 * 图片消息的 [msg] 以猫猫码展示。
 *
 * @property id 此图片的ID, 如果是本地手动构建的则可能为空字符串。
 * @property path 图片的网络路径或者本地文件路径。如果是接收到的图片，则可能为null。
 * @property url 图片对应的网络链接地址。如果是通过本地文件构建的，获取则可能为null。
 */
public open class ImageMessageContent(
    open val id: String,
    open val flash: Boolean,
    private val _path: () -> String?,
    private val _url: () -> String?
) : ExpectedMessageContent(), NetMessageContent {

    constructor(id: String, flash: Boolean, path: String?, url: String?) : this(id, flash, { path }, { url })

    public override val url: String get() = _url() ?: throw IllegalStateException("Unable to get url.")

    public open val path: String? get() = _path()

    public override fun getUrlOrNull(): String? = _url()

    /**
     * 消息字符串文本。
     *
     */
    override val msg: String by lazy(LazyThreadSafetyMode.NONE) {
        val builder = CatCodeUtil.getStringCodeBuilder("image").key("id").value(id)
        path?.let { builder.key("file").value(it) }
        getUrlOrNull()?.let { builder.key("url").value(it) }
        builder.build()
    }


    /**
     * 拷贝一个复制品。
     */
    protected open fun copy(): ImageMessageContent = ImageMessageContent(id, flash, _path, _url)


    override val images: List<ImageMessageContent> get() = listOf(this)
}

/**
 * 一个 [预期内的][ExpectedMessageContent] 以语音作为消息主体的 [MessageContent] 默认实现类。
 * 语音消息的 [msg] 以猫猫码展示。
 *
 * @property id 此语音的ID, 如果是本地手动构建的则可能为空字符串。
 * @property path 语音的网络路径或者本地文件路径。
 * @property url 语音对应的网络链接地址。如果是通过本地文件构建的，获取则会抛出异常。
 */
public open class VoiceMessageContent(
    open val id: String,
    private val _path: () -> String?,
    private val _url: () -> String?
) : ExpectedMessageContent(), NetMessageContent {

    public override val url: String get() = _url() ?: throw IllegalStateException("Unable to get url.")

    public open val path: String? = _path()

    public override fun getUrlOrNull(): String? = _url()

    /**
     * 消息字符串文本。
     *
     */
    override val msg: String by lazy(LazyThreadSafetyMode.NONE) {
        val builder = CatCodeUtil.getStringCodeBuilder("voice").key("id").value(id)
        path?.let { builder.key("file").value(it) }
        getUrlOrNull()?.let { builder.key("url").value(it) }
        builder.build()
    }


    /**
     * 拷贝一个复制品。
     */
    protected open fun copy(): VoiceMessageContent = VoiceMessageContent(id, _path, _url)

    override val images: List<ImageMessageContent> = emptyList()
}







