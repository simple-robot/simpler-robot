/*
 *     Copyright (c) 2023-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

@file:Suppress("unused")

package love.forte.simbot.kook.objects.kmd


import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.ExperimentalKookApi
import love.forte.simbot.kook.messages.MentionPart
import love.forte.simbot.kook.objects.MentionRolePart
import love.forte.simbot.kook.objects.kmd.AtTarget.*
import kotlin.jvm.JvmOverloads

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@DslMarker
public annotation class KookMarkdownBuilderDsl

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@DslMarker
public annotation class KookMarkdownBuilderTopDsl


/**
 * Kook 的 [KMarkdown](https://developer.kaiheila.cn/doc/kmarkdown).
 * 此接口中定义的三个属性主要用于 **接收消息** 用。
 *
 * @see RawValueKMarkdown
 * @see KMarkdownBuilder
 * @see KookMarkdownGrammar
 */
@ExperimentalKookApi
public interface KMarkdown {

    /**
     * 此 markdown 的最终字符串。
     */
    public val rawContent: String

    /**
     * 提及部分，参考自 [KMarkdown消息](https://developer.kaiheila.cn/doc/event/message#KMarkdown%E6%B6%88%E6%81%AF) 字段
     */
    public val mentionPart: List<MentionPart>

    /**
     * \@特定角色 的角色ID信息，与mention_roles中数据对应 -&gt; [ 角色id ]
     */
    public val mentionRolePart: List<MentionRolePart>

    public companion object {
        public val rawKMarkdownSerializer: KSerializer<RawValueKMarkdown> = RawValueKMarkdown.serializer()
    }
}

/**
 * 原始信息 [KMarkdown].
 */
@Serializable
@SerialName(RawValueKMarkdown.SERIAL_NAME)
@ExperimentalKookApi
public data class RawValueKMarkdown(
    @SerialName("raw_content")
    override val rawContent: String,
    @SerialName("mention_part")
    override val mentionPart: List<MentionPart> = emptyList(),
    @SerialName("mention_role_part")
    override val mentionRolePart: List<MentionRolePart> = emptyList(),
) : KMarkdown {
    public companion object {
        internal const val SERIAL_NAME = "raw.kmd"
    }


}


/**
 * [KMarkdown] 的构建器。
 * 可以通过自定义 [appender] 来提供自定义的字符串拼接器，默认使用 [StringBuilder].
 */
@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalKookApi
public class KMarkdownBuilder(public val appender: Appendable = StringBuilder()) {
    public constructor(capacity: Int) : this(StringBuilder(capacity))

    private fun ap(c: Char): Appendable = appender.also { it.append(c) }
    private fun ap(c: CharSequence): Appendable = appender.also { it.append(c) }

    // var mentionPart: MutableList<MentionPart> = LinkedList()
    // var mentionRolePart: MutableList<Role> = LinkedList()


    //********************************//

    /**
     * 拼接一个文本
     */
    @KookMarkdownBuilderDsl
    public fun text(text: CharSequence): KMarkdownBuilder =
        also { KookMarkdownGrammar.RawText.appendTo(text, appender) }

    /**
     * 拼接一个加粗文本
     */
    @KookMarkdownBuilderDsl
    public fun bold(value: CharSequence): KMarkdownBuilder = also { KookMarkdownGrammar.Bold.appendTo(value, appender) }

    /**
     * 拼接一个倾斜文本
     */
    @KookMarkdownBuilderDsl
    public fun italic(value: CharSequence): KMarkdownBuilder =
        also { KookMarkdownGrammar.Italic.appendTo(value, appender) }

    /**
     * 拼接一个加粗倾斜文本
     */
    @KookMarkdownBuilderDsl
    public fun boldItalic(value: CharSequence): KMarkdownBuilder =
        also { KookMarkdownGrammar.BoldItalic.appendTo(value, appender) }

    /**
     * 拼接一个删除线
     */
    @KookMarkdownBuilderDsl
    public fun strikethrough(value: CharSequence): KMarkdownBuilder =
        also { KookMarkdownGrammar.Strikethrough.appendTo(value, appender) }

    /**
     * 拼接一个链接。
     */
    @KookMarkdownBuilderDsl
    public fun link(link: MdLink): KMarkdownBuilder = also { KookMarkdownGrammar.Link.appendTo(link, appender) }

    /**
     * 拼接一个链接。
     * @param name 可以为null.
     */
    @JvmOverloads
    @KookMarkdownBuilderDsl
    public fun link(name: String? = null, url: String): KMarkdownBuilder =
        also { KookMarkdownGrammar.Link.appendTo(name, url, appender) }


    /**
     * 追加一个分割线。不会自动在开头换行，但是会在结尾换行，也就是：`---\n`
     */
    @KookMarkdownBuilderDsl
    public fun divider(): KMarkdownBuilder = also { KookMarkdownGrammar.Divider.appendTo(appender) }

    /**
     * 引用。如果想要结束引用内容，需要连续换行两次。
     */
    @KookMarkdownBuilderDsl
    public fun quote(value: CharSequence): KMarkdownBuilder =
        also { KookMarkdownGrammar.Quote.appendTo(value, appender) }

    /**
     * 引用，并在结束后自动换行2次。
     */
    @KookMarkdownBuilderDsl
    public fun quoteAndEnd(value: CharSequence): KMarkdownBuilder =
        also { KookMarkdownGrammar.Quote.appendToEnd(value, appender) }

    /**
     * 追加下划线内容。
     */
    @KookMarkdownBuilderDsl
    public fun underscore(value: CharSequence): KMarkdownBuilder =
        also { KookMarkdownGrammar.Underscore.appendTo(value, appender) }

    /**
     * 追加隐藏内容。
     */
    @KookMarkdownBuilderDsl
    public fun hide(value: CharSequence): KMarkdownBuilder = also { KookMarkdownGrammar.Hide.appendTo(value, appender) }

    /**
     * 根据id追加一个emoji
     */
    @KookMarkdownBuilderDsl
    public fun emoji(id: String): KMarkdownBuilder = also { KookMarkdownGrammar.Emoji.appendTo(id, appender) }

    /**
     * 服务器表情。
     */
    @KookMarkdownBuilderDsl
    public fun serverEmoticons(value: MdServerEmoticons): KMarkdownBuilder =
        also { KookMarkdownGrammar.ServerEmoticons.appendTo(value, appender) }

    /**
     * 服务器表情。
     */
    @KookMarkdownBuilderDsl
    public fun serverEmoticons(name: CharSequence, id: CharSequence): KMarkdownBuilder =
        also { KookMarkdownGrammar.ServerEmoticons.appendTo(name, id, appender) }

    /**
     * 提及频道
     */
    @KookMarkdownBuilderDsl
    public fun channel(id: CharSequence): KMarkdownBuilder = also { KookMarkdownGrammar.Channel.appendTo(id, appender) }

    /**
     * at
     */
    @KookMarkdownBuilderDsl
    public fun at(target: AtTarget): KMarkdownBuilder = also { KookMarkdownGrammar.At.appendTo(target, appender) }

    /**
     * at
     */
    @KookMarkdownBuilderDsl
    public fun at(target: CharSequence): KMarkdownBuilder = also { KookMarkdownGrammar.At.appendTo(target, appender) }

    /**
     * role
     */
    @KookMarkdownBuilderDsl
    public fun role(roleId: CharSequence): KMarkdownBuilder =
        also { KookMarkdownGrammar.Role.appendTo(roleId, appender) }

    /**
     * 行内代码
     */
    @KookMarkdownBuilderDsl
    public fun inlineCode(code: CharSequence): KMarkdownBuilder =
        also { KookMarkdownGrammar.InlineCode.appendTo(code, appender) }

    /**
     * 代码块
     *
     * 结尾处会自动换行。
     */
    @KookMarkdownBuilderDsl
    public fun codeBlock(code: MdCodeBlock): KMarkdownBuilder =
        also { KookMarkdownGrammar.CodeBlock.appendTo(code, appender) }

    /**
     * 代码块
     *
     * 结尾处会自动换行。
     */
    @JvmOverloads
    @KookMarkdownBuilderDsl
    public fun codeBlock(language: CharSequence? = null, code: CharSequence): KMarkdownBuilder =
        also { KookMarkdownGrammar.CodeBlock.appendTo(language, code, appender) }


    //********************************//

    /**
     * 一个空格。
     */
    @KookMarkdownBuilderDsl
    public fun space(): KMarkdownBuilder = also { appender.append(' ') }

    /**
     * 新的一行。
     */
    @KookMarkdownBuilderDsl
    public fun newLine(): KMarkdownBuilder = also { appender.appendLine() }

    /**
     * 追加一个原始信息到md缓冲器中。
     * 会直接进行拼接，不做处理。
     */
    @KookMarkdownBuilderDsl
    public fun appendRawMd(raw: String): KMarkdownBuilder = also { ap(raw) }


    /**
     * 通过一个 [KookMarkdownGrammar] 来实现自定义拼接。
     */
    @KookMarkdownBuilderDsl
    public fun <P> append(grammar: KookMarkdownGrammar<P>, params: P): KMarkdownBuilder = also {
        grammar.appendTo(params, appender)
    }

    /**
     * 直接获取原始的markdown字符串。
     */
    public fun buildRaw(): String = appender.toString()


    /**
     * 构建一个 [KMarkdown] 实例。
     */
    public fun build(): KMarkdown = RawValueKMarkdown(buildRaw())


}

@KookMarkdownBuilderTopDsl
@ExperimentalKookApi
public inline fun KMarkdownBuilder.aroundLine(
    times: Int = 1,
    block: KMarkdownBuilder.() -> Unit,
): KMarkdownBuilder {
    return apply {
        for (i in 1..times) {
            newLine()
        }
        apply(block)
        for (i in 1..times) {
            newLine()
        }
    }
}

@KookMarkdownBuilderTopDsl
@ExperimentalKookApi
public inline fun KMarkdownBuilder.preLine(times: Int = 1, block: KMarkdownBuilder.() -> Unit): KMarkdownBuilder {
    return apply {
        for (i in 1..times) {
            newLine()
        }
        apply(block)
    }
}

@KookMarkdownBuilderTopDsl
@ExperimentalKookApi
public inline fun KMarkdownBuilder.postLine(
    times: Int = 1,
    block: KMarkdownBuilder.() -> Unit,
): KMarkdownBuilder {
    return apply {
        apply(block)
        for (i in 1..times) {
            newLine()
        }
    }
}


/**
 * Build kmarkdown for raw string.
 */
@KookMarkdownBuilderDsl
@ExperimentalKookApi
public inline fun buildRawKMarkdown(block: KMarkdownBuilder.() -> Unit): String {
    return KMarkdownBuilder().apply(block).buildRaw()
}

/**
 * Build [KMarkdown] instance.
 */
@KookMarkdownBuilderDsl
@ExperimentalKookApi
public inline fun buildKMarkdown(block: KMarkdownBuilder.() -> Unit): KMarkdown {
    return KMarkdownBuilder().apply(block).build()
}


/**
 * Kook  `KMarkdown` 语法封装.
 *
 * @param P 参数类型。一般情况下参数为字符串，但是有些时候参数可能是多个，则或许需要提供一个封装参数。
 */
public interface KookMarkdownGrammar<P> {

    /**
     * 语法来源。
     *
     * for kt like:
     * ```kotlin
     * Source.Markdown
     * ```
     *
     * for java like:
     * ```java
     * Source.Markdown.INSTANCE
     * ```
     *
     *
     * @see Source
     */
    public val grammarSource: Source

    /**
     * 通过一个 [参数][P] ，将转化结果追加到 [appendable] 中。
     */
    public fun appendTo(param: P, appendable: Appendable)


    /**
     * KMarkdown 的语法来源。
     *
     */
    public sealed class Source(public open val name: String) {

        /** 来源 - markdown官方 */
        public data object Markdown : Source("official")

        /** 来源 - Kook 官方 */
        public sealed class Kook(name: String) : Source(name) {
            /** Kook 官方 - 自定义 */
            public data object Custom : Kook("kook-custom")

            /** Kook 官方 - emoji */
            public data object Emoji : Kook("kook-emoji")
        }

        /** 其他自定义 */
        public data class Custom(override val name: String) : Source(name)
    }


    //**************** 已定义语法 ****************//

    /**
     * 追加一段原始字符串。即没有任何操作。
     */
    public object RawText : BaseMarkdownKookMarkdownGrammar<CharSequence>() {
        public override fun appendTo(param: CharSequence, appendable: Appendable) {
            appendable.append(param)
        }
    }

    /** 加粗 */
    public object Bold : SymmetricalMarkdownKookMarkdownGrammar("**")

    /** 斜体 */
    public object Italic : SymmetricalMarkdownKookMarkdownGrammar("*")

    /** 加粗&斜体 */
    public object BoldItalic : SymmetricalMarkdownKookMarkdownGrammar("***")

    /** 删除线 */
    public object Strikethrough : SymmetricalMarkdownKookMarkdownGrammar("~~")

    /**
     * 	链接.
     *
     * 	仅允许 http, https 的链接
     */
    public object Link : BaseMarkdownKookMarkdownGrammar<MdLink>() {
        // [name](url)

        override fun appendTo(param: MdLink, appendable: Appendable) {
            val (name, url) = param
            appendTo(name, url, appendable)
        }

        @JvmOverloads
        public fun appendTo(name: CharSequence? = null, url: CharSequence, appendable: Appendable) {
            appendable
                .append('[').append(name ?: url)
                .append(']').append('(')
                .append(url).append(')')
        }
    }

    /**
     * 分隔线
     * 会自动在尾部换行。
     */
    public object Divider : ValueAppenderMarkdownKookMarkdownGrammar<String>("---\n")

    /**
     * 引用.
     *
     * 换行会一直作用，直到遇见两个换行(\n\n),这两个换行实际不会显示换行
     */
    public object Quote : BaseMarkdownKookMarkdownGrammar<CharSequence>() {
        public override fun appendTo(param: CharSequence, appendable: Appendable) {
            appendable.append("> ").append(param)
        }

        public fun appendToEnd(param: CharSequence, appendable: Appendable) {
            appendTo(param, appendable)
            appendable.appendLine().appendLine()
        }
    }

    /**
     * 下划线
     */
    public object Underscore : SymmetricalKookCustomKookMarkdownGrammar("(ins)")


    /**
     * 剧透
     * 内容默认是遮住的，只有用户点击才会显示
     */
    public object Hide : SymmetricalKookCustomKookMarkdownGrammar("(spl)")

    /**
     * emoji.
     *
     * 基本与emoji的 [shortcode](https://www.webfx.com/tools/emoji-cheat-sheet/) 写法保持一致
     *
     */
    public object Emoji : SymmetricalKookEmojiKookMarkdownGrammar(":")


    /**
     * 服务器表情，需要有服务器发送服务器表情的权限
     *
     * ```
     *  (emj)服务器表情(emj)[服务器表情ID]
     * ```
     *
     * 实际命名应该是 `GuildEmoji`
     */
    public object ServerEmoticons : BaseKaiheilaCustomKookMarkdownGrammar<MdServerEmoticons>() {
        // (emj)服务器表情(emj)[服务器表情ID]

        override fun appendTo(param: MdServerEmoticons, appendable: Appendable) {
            val (name, id) = param
            appendTo(name, id, appendable)
        }

        public fun appendTo(name: CharSequence, id: CharSequence, appendable: Appendable) {
            appendable.append("(emj)").append(name).append("(emj)[").append(id).append(']')
        }
    }


    /**
     * 频道。提及频道
     * ```
     * (chn)Channel ID(chn)
     * ```
     */
    public object Channel : SymmetricalKookCustomKookMarkdownGrammar("(chn)")

    /**
     * \@用户。
     * all 代表 @所有用户，here 代表 @所有在线用户
     *
     * @see AtTarget
     */
    public object At : SymmetricalKookCustomKookMarkdownGrammar("(met)")


    /**
     * 	\@某角色所有用户
     */
    public object Role : SymmetricalKookCustomKookMarkdownGrammar("(rol)")

    /**
     * 行内代码.
     */
    public object InlineCode : SymmetricalMarkdownKookMarkdownGrammar("`")


    /**
     * 代码块.
     *
     * 最后会自动换行。
     */
    public object CodeBlock : BaseMarkdownKookMarkdownGrammar<MdCodeBlock>() {
        override fun appendTo(param: MdCodeBlock, appendable: Appendable) {
            val (language, value) = param
            appendTo(language, value, appendable)
        }

        @JvmOverloads
        public fun appendTo(language: CharSequence? = null, value: CharSequence, appendable: Appendable) {
            appendable.append("```")
            if (language != null) {
                appendable.append(language)
            }
            appendable.appendLine().append(value).appendLine().append("```").appendLine()
        }

    }


}

/** 用于 [超链接语法][KookMarkdownGrammar.Link] 的参数 */
public data class MdLink
@JvmOverloads
constructor(public val name: CharSequence? = null, public val url: CharSequence)


/** 用于 [服务器表情][KookMarkdownGrammar.ServerEmoticons] 的参数 */
public data class MdServerEmoticons(public val name: CharSequence, public val id: CharSequence)

/**
 * 可用于 [At][KookMarkdownGrammar.At] 的目标类型，分为[指定用户][User]、[全体][All]和[在线][Here]。
 */
public sealed class AtTarget : CharSequence {

    /**
     * at 一个具体的ID
     *
     * @see AtTarget
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public class User(public val id: String) : AtTarget() {
        override val length: Int get() = id.length
        override fun get(index: Int): Char = id[index]
        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = id.subSequence(startIndex, endIndex)
    }

    /**
     * at 'all', 即at全体。
     *
     * @see AtTarget
     */
    public data object All : AtTarget() {
        private const val DELEGATE = "all"
        override val length: Int get() = DELEGATE.length
        override fun get(index: Int): Char = DELEGATE[index]
        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
            DELEGATE.subSequence(startIndex, endIndex)
    }

    /**
     * at 'here', 即at在线用户。
     *
     * @see AtTarget
     */
    public data object Here : AtTarget() {
        private const val DELEGATE = "here"
        override val length: Int get() = DELEGATE.length
        override fun get(index: Int): Char = DELEGATE[index]
        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
            DELEGATE.subSequence(startIndex, endIndex)
    }

}

/**
 * 代码块参数
 */
public data class MdCodeBlock
@JvmOverloads
constructor(public val language: CharSequence? = null, public val value: CharSequence)


/**
 * 通过一个单独的 [语法][KookMarkdownGrammar] 构建一个单独的 [String] 结果。
 */
public fun <P> KookMarkdownGrammar<P>.build(param: P): String =
    StringBuilder().also { b -> this.appendTo(param, b) }.toString()


/**
 * 提供一个简单的 [KookMarkdownGrammar] 抽象类, 并将 [grammarSource] 提取至构造中。
 *
 * @see BaseMarkdownKookMarkdownGrammar
 * @see BaseKaiheilaCustomKookMarkdownGrammar
 * @see BaseKaiheilaEmojiKookMarkdownGrammar
 * @see BaseCustomKookMarkdownGrammar
 */
public abstract class BaseKookMarkdownGrammar<P>(override val grammarSource: KookMarkdownGrammar.Source) :
    KookMarkdownGrammar<P>

//**************** 基于 source 的部分整合 ****************//

public abstract class BaseMarkdownKookMarkdownGrammar<P> :
    BaseKookMarkdownGrammar<P>(KookMarkdownGrammar.Source.Markdown)

public abstract class BaseKaiheilaCustomKookMarkdownGrammar<P> :
    BaseKookMarkdownGrammar<P>(KookMarkdownGrammar.Source.Kook.Custom)

public abstract class BaseKaiheilaEmojiKookMarkdownGrammar<P> :
    BaseKookMarkdownGrammar<P>(KookMarkdownGrammar.Source.Kook.Emoji)

public abstract class BaseCustomKookMarkdownGrammar<P>(source: KookMarkdownGrammar.Source.Custom) :
    BaseKookMarkdownGrammar<P>(source) {
    public constructor(name: String) : this(KookMarkdownGrammar.Source.Custom(name))
}


/**
 * 前后追加元素对称的 Grammar. 例如：`Hello World` -> `**Hello World**` 这种，向前后追加相同元素的内容的，即认为为对称Grammar。
 *
 * 提供前后一致的 [wing] 值.
 *
 * @see SymmetricalMarkdownKookMarkdownGrammar
 * @see SymmetricalKookCustomKookMarkdownGrammar
 * @see SymmetricalKookEmojiKookMarkdownGrammar
 * @see SymmetricalCustomKookMarkdownGrammar
 */
public abstract class SymmetricalKookMarkdownGrammar(
    private val wing: CharSequence,
    grammarSource: KookMarkdownGrammar.Source,
) : BaseKookMarkdownGrammar<CharSequence>(grammarSource) {
    override fun appendTo(param: CharSequence, appendable: Appendable) {
        appendable.append(wing).append(param).append(wing)
    }
}

public abstract class SymmetricalMarkdownKookMarkdownGrammar(wing: CharSequence) :
    SymmetricalKookMarkdownGrammar(wing, KookMarkdownGrammar.Source.Markdown)

public abstract class SymmetricalKookCustomKookMarkdownGrammar(wing: CharSequence) :
    SymmetricalKookMarkdownGrammar(wing, KookMarkdownGrammar.Source.Kook.Custom)

public abstract class SymmetricalKookEmojiKookMarkdownGrammar(wing: CharSequence) :
    SymmetricalKookMarkdownGrammar(wing, KookMarkdownGrammar.Source.Kook.Emoji)

public abstract class SymmetricalCustomKookMarkdownGrammar(
    wing: CharSequence,
    source: KookMarkdownGrammar.Source.Custom,
) :
    SymmetricalKookMarkdownGrammar(wing, source) {
    public constructor(wing: CharSequence, name: String) : this(wing, KookMarkdownGrammar.Source.Custom(name))
}


/**
 * 不需要参数的语法，例如一个删除线，它不需要任何内容，也不应该有任何内容。
 *
 * @see NoParamMarkdownKookMarkdownGrammar
 * @see NoParamKookCustomKookMarkdownGrammar
 * @see NoParamKookEmojiKookMarkdownGrammar
 * @see NoParamCustomKookMarkdownGrammar
 */
public abstract class NoParamKookMarkdownGrammar(grammarSource: KookMarkdownGrammar.Source) :
    BaseKookMarkdownGrammar<Nothing?>(grammarSource) {
    override fun appendTo(param: Nothing?, appendable: Appendable) {
        appendTo(appendable)
    }

    /**
     * 追加元素.
     */
    public abstract fun appendTo(appendable: Appendable)
}

public abstract class NoParamMarkdownKookMarkdownGrammar :
    NoParamKookMarkdownGrammar(KookMarkdownGrammar.Source.Markdown)

public abstract class NoParamKookCustomKookMarkdownGrammar :
    NoParamKookMarkdownGrammar(KookMarkdownGrammar.Source.Kook.Custom)

public abstract class NoParamKookEmojiKookMarkdownGrammar :
    NoParamKookMarkdownGrammar(KookMarkdownGrammar.Source.Kook.Emoji)

public abstract class NoParamCustomKookMarkdownGrammar(source: KookMarkdownGrammar.Source.Custom) :
    NoParamKookMarkdownGrammar(source) {
    public constructor(name: String) : this(KookMarkdownGrammar.Source.Custom(name))
}


/**
 * [NoParamKookMarkdownGrammar] 的子集，直接拼接一个固定的元素.
 */
public abstract class ValueAppenderKookMarkdownGrammar<V : CharSequence>(
    private val value: V,
    grammarSource: KookMarkdownGrammar.Source,
) : NoParamKookMarkdownGrammar(grammarSource) {
    override fun appendTo(appendable: Appendable) {
        appendable.append(value)
    }
}


public abstract class ValueAppenderMarkdownKookMarkdownGrammar<V : CharSequence>(value: V) :
    ValueAppenderKookMarkdownGrammar<V>(value, KookMarkdownGrammar.Source.Markdown)

public abstract class ValueAppenderKookCustomKookMarkdownGrammar<V : CharSequence>(value: V) :
    ValueAppenderKookMarkdownGrammar<V>(value, KookMarkdownGrammar.Source.Kook.Custom)

public abstract class ValueAppenderKookEmojiKookMarkdownGrammar<V : CharSequence>(value: V) :
    ValueAppenderKookMarkdownGrammar<V>(value, KookMarkdownGrammar.Source.Kook.Emoji)

public abstract class ValueAppenderCustomKookMarkdownGrammar<V : CharSequence>(
    value: V,
    source: KookMarkdownGrammar.Source.Custom,
) : ValueAppenderKookMarkdownGrammar<V>(value, source) {
    public constructor(value: V, name: String) : this(value, KookMarkdownGrammar.Source.Custom(name))
}
