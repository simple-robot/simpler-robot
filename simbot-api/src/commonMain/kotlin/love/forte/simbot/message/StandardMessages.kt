/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

@file:JvmName("StandardMessages")
@file:JvmMultifileClass

package love.forte.simbot.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.ID
import love.forte.simbot.message.At.Companion.equals
import love.forte.simbot.message.At.Companion.hashCode
import love.forte.simbot.message.OfflineImage.Companion.toOfflineImage
import love.forte.simbot.message.Text.Companion.of
import love.forte.simbot.resource.ByteArrayResource
import love.forte.simbot.resource.Resource
import love.forte.simbot.resource.ResourceBase64Serializer
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.js.JsName
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * 一些由核心提供的标准 [Message.Element] 类型。
 * 标准消息中，仅提供如下实现：
 * - [纯文本消息][PlainText]
 * - [AT消息][At]
 * - [OfflineImage]
 * - [RemoteImage]
 * - [表情消息][Face]
 * - [emoji][Emoji]
 *
 */
public sealed interface StandardMessage : Message.Element

//region Text
/**
 * 纯文本消息。代表一段只存在[文本][text]的消息。
 *
 * 实际上绝大多数情况下，都不需要独立实现 [PlainText] 类型，
 * [PlainText] 提供了最基础的实现类型 [Text]。
 *
 * @see Text
 */
public interface PlainText : StandardMessage {
    /**
     * 文本内容
     */
    public val text: String
}

/**
 * 一个文本消息 [Text]。[Text] 是 [PlainText] 基础实现类型。
 *
 * 文本消息可以存在多个，但是对于不同平台来讲，有可能存在差异。
 * 部分平台会按照正常的方式顺序排列消息，而有的则会组合消息列表中的所有文本消息为一个整体。
 *
 * @see toText
 * @see Text
 * @see of
 */
@Serializable
@SerialName("m.std.text")
public class Text private constructor(override val text: String) : PlainText {
    override fun toString(): String = "Text($text)"
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Text) return false
        return text == other.text
    }

    override fun hashCode(): Int = text.hashCode()

    public companion object {
        private val empty = Text("")

        /**
         * Creates a new instance of Text with the given text.
         *
         * @param text the text to create the Text instance with.
         * @return a new instance of Text with the given text.
         */
        @JvmStatic
        public fun of(text: String): Text {
            return if (text.isEmpty()) empty
            else Text(text)
        }
    }
}

/**
 * 将一个字符串转化为 [Text].
 * ```kotlin
 * val text: Text = "mua".toText()
 * ```
 */
public fun String.toText(): Text = of(this)

/**
 * 得到一个空的 [Text].
 *
 */
@JsName("emptyText")
public fun Text(): Text = of("")

/**
 * 构建一个 [Text].
 *
 * ```kotlin
 * val text: Text = Text { "Hello" }
 * ```
 *
 */
public inline fun Text(block: () -> String): Text = block().toText()
//endregion

//region At
/**
 * 代表一个描述“提及”的消息。常见表现形式即为 [At]。
 *
 * @see At
 * @see AtAll
 *
 */
public interface MentionMessage : StandardMessage

/**
 * 一个艾特消息。
 *
 * 是针对“提及”的常见标准表现形式。
 * 默认情况下 [At] 表现为针对某个用户的提及（即默认的 [type] 为 `"user"`）。
 *
 * 当同一个组件中可能存在多种类型的提及时，可以选择通过约定不同的 [type] 来区分，
 * 也可以选择实现更多扩展消息元素类型来做区分。
 *
 */
@Serializable
@SerialName("m.std.at")
public data class At @JvmOverloads constructor(
    public val target: ID,
    @SerialName("atType") public val type: String = DEFAULT_AT_TYPE,

    /**
     * 这个at在原始数据中或者原始事件中的样子。默认情况下，是字符串 '@[target]'。
     * 此值通常仅供参考，且不会参与 [equals] 于 [hashCode] 的计算。
     */
    public val originContent: String = "@$target",
) : MentionMessage {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is At) return false
        return other.target == target && other.type == type
    }


    override fun hashCode(): Int {
        return 31 * (target.hashCode() + type.hashCode())
    }

    override fun toString(): String {
        return "At(target=$target, type=$type, originContent=$originContent)"
    }

    public companion object {
        public const val DEFAULT_AT_TYPE: String = "user"
    }
}

/**
 * 一个“通知所有”的消息。
 * 如果在同一组件环境下的 “通知所有” 有多种表现形式，可考虑扩展更多消息元素类型。
 */
@Serializable
@SerialName("m.std.atAll")
public data object AtAll : MentionMessage

//endregion

//region Description
/**
 * 一个图片消息元素类型。
 *
 * 图片消息可能被分为 [离线图片][OfflineImage] 和 [远端图片][RemoteImage]。
 *
 * @see OfflineImage
 * @see RemoteImage
 */
public interface Image : StandardMessage

// 离线图片？
// 远程图片？

/**
 * 一个离线图片消息元素类型。
 *
 * “离线图片”即代表一个在当前机器中本地存在的图片资源。
 * 它可能是内存中的一段二进制数据，或本地文件系统中的某个文件。
 *
 * “离线”主要表示此图片并未上传到某个目标平台中，也没有与某个远程服务器互相对应的唯一标识。
 */
public interface OfflineImage : Image {
    /**
     * 得到图片的二进制数据
     */
    @Throws(Exception::class)
    public fun data(): ByteArray

    public companion object {
        /**
         * Converts a byte array to an offline image.
         *
         * @return [OfflineImage] - The offline image representation of the byte array.
         */
        @JvmStatic
        @JvmName("ofBytes")
        public fun ByteArray.toOfflineImage(): OfflineImage = OfflineByteArrayImage(this)

        /**
         * 将给定的 [Resource] 转换为 [OfflineImage]。
         *
         * @return [OfflineImage] object representing the converted Resource.
         *
         * @see toOfflineImage
         */
        @JvmStatic
        @JvmName("ofResource")
        public fun Resource.toOfflineImage(): OfflineImage = when (this) {
            is ByteArrayResource -> OfflineByteArrayImage(data())
            else -> toOfflineResourceImage()
        }

    }
}

/**
 * 一个基于 [Resource] 的 [OfflineImage] 实现。
 */
public interface OfflineResourceImage : OfflineImage {
    /**
     * 关联的资源对象
     */
    public val resource: Resource

    /**
     * 字节数据。默认通过 [Resource.data] 获取。
     */
    @Throws(Exception::class)
    override fun data(): ByteArray = resource.data()
}

/**
 * 将 [Resource] 转化为 [OfflineResourceImage]。
 *
 */
public expect fun Resource.toOfflineResourceImage(): OfflineResourceImage

/**
 * 最基础的、基于 [Resource] 实现的 [OfflineResourceImage]。
 *
 * ## 序列化
 *
 * 序列化中 [resource] 会使用基于 `Base64` 实现的 [ResourceBase64Serializer] 来作为其序列化器。
 * 请谨慎使用，频繁地将具有一定尺寸的 bytes 数据进行 base64 编码或解码可能会存在一定的性能损耗。
 */
@OptIn(ExperimentalEncodingApi::class)
@Serializable
@SerialName("m.std.img.offline.resource")
public data class SimpleOfflineResourceImage(@Serializable(ResourceBase64Serializer::class) override val resource: Resource) :
    OfflineResourceImage


/**
 * 直接针对一个 [ByteArray] 进行包装的 [OfflineImage] 实现。
 */
@Serializable
@SerialName("m.std.img.offline.bytes")
public data class OfflineByteArrayImage(private val data: ByteArray) : OfflineImage {
    override fun data(): ByteArray = data

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OfflineByteArrayImage) return false

        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return data.contentHashCode()
    }
}

/**
 * 一个远程图片消息元素类型。
 *
 * 远程图片通常是通过事件推送、主动上传等手段得到的、有与某个远程服务器互相对应的唯一标识的图片。
 * 这个标识可能是一个ID，或一个访问链接。
 *
 * @see RemoteIDImage
 */
public interface RemoteImage : Image {
    /**
     * 在远程服务器上的唯一标识。
     *
     * 可能是一个ID，也可能是一个资源定位符（例如图片链接）。
     */
    public val id: ID
}

/**
 * 一个仅基于 [ID] 的 [RemoteImage] 基础实现。
 */
@Serializable
@SerialName("m.std.img.remote.id")
public data class RemoteIDImage(override val id: ID) : RemoteImage

/**
 * 一个可以感知或查询到图片链接的远程图片消息元素类型。
 *
 * [id] 与 [url] 的查询结果有可能是一样的。
 *
 * @see RemoteImage
 */
public interface RemoteUrlAwareImage : RemoteImage {
    /**
     * 获取或查询此图片的链接。
     */
    public suspend fun url(): String
}

//endregion

//region Emoticon
/**
 * 表示某种表情符号的消息元素类型。
 * 常见表现形式有某平台的系统表情或一定范围内的 `emoji` 表情。
 *
 */
public interface EmoticonMessage : StandardMessage

/**
 * 一个 `emoji` 表情。
 *
 * [Emoji] 主要服务于那些只能提供指定范围内 `emoji` 表情的场景，
 * 例如针对某个消息的 `reaction`。
 *
 * 现代绝大多数的平台中，如果希望在普通的文本消息中插入 `emoji` 不需要使用特殊的消息类型，
 * 仅需要添加在字符串中即可。
 *
 */
@Serializable
@SerialName("m.std.emoji")
public data class Emoji(public val id: ID) : EmoticonMessage

/**
 * 一个表情。一般代表平台提供的自带系统表情。
 */
@Serializable
@SerialName("m.std.face")
public data class Face(public val id: ID) : EmoticonMessage
//endregion

