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

@file:OptIn(ExperimentalStdlibApi::class)

package love.forte.simbot.qguild.model.forum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import kotlin.jvm.JvmExposeBoxed

/**
 * 富文本内容。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#RichObject)
 *
 * @property richType 富文本类型。
 * @property textInfo 文本信息。
 * @property atInfo `@` 信息。
 * @property urlInfo 链接信息。
 * @property emojiInfo 表情信息。
 * @property channelInfo 提到的子频道信息。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class RichObject @ApiModelConstructor internal constructor(
    @SerialName("type")
    @get:JvmExposeBoxed
    public val richType: RichType,
    @SerialName("text_info")
    public val textInfo: TextInfo,
    @SerialName("at_info")
    public val atInfo: String,
    @SerialName("url_info")
    public val urlInfo: String,
    @SerialName("emoji_info")
    public val emojiInfo: String,
    @SerialName("channel_info")
    public val channelInfo: String,
) {

    @Deprecated("Use richType instead.", ReplaceWith("richType.value"))
    public val type: Int
        get() = richType.value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RichObject) return false

        if (richType != other.richType) return false
        if (textInfo != other.textInfo) return false
        if (atInfo != other.atInfo) return false
        if (urlInfo != other.urlInfo) return false
        if (emojiInfo != other.emojiInfo) return false
        if (channelInfo != other.channelInfo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = richType.hashCode()
        result = 31 * result + textInfo.hashCode()
        result = 31 * result + atInfo.hashCode()
        result = 31 * result + urlInfo.hashCode()
        result = 31 * result + emojiInfo.hashCode()
        result = 31 * result + channelInfo.hashCode()
        return result
    }

    override fun toString(): String {
        return "RichObject(" +
            "type=$richType, " +
            "textInfo=$textInfo, " +
            "atInfo='$atInfo', " +
            "urlInfo='$urlInfo', " +
            "emojiInfo='$emojiInfo', " +
            "channelInfo='$channelInfo')"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("richType.value"))
    public operator fun component1(): Int = richType.value

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("textInfo"))
    public operator fun component2(): TextInfo = textInfo

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("atInfo"))
    public operator fun component3(): String = atInfo

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("urlInfo"))
    public operator fun component4(): String = urlInfo

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("emojiInfo"))
    public operator fun component5(): String = emojiInfo

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelInfo"))
    public operator fun component6(): String = channelInfo

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        type: Int = this.richType.value,
        textInfo: TextInfo = this.textInfo,
        atInfo: String = this.atInfo,
        urlInfo: String = this.urlInfo,
        emojiInfo: String = this.emojiInfo,
        channelInfo: String = this.channelInfo,
    ): RichObject = RichObject(
        richType = RichType.of(type),
        textInfo = textInfo,
        atInfo = atInfo,
        urlInfo = urlInfo,
        emojiInfo = emojiInfo,
        channelInfo = channelInfo,
    )
    //endregion
}

/**
 * 富文本类型的部分常量。
 *
 * 新代码请使用 [RichType]。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#RichObject)
 *
 * @author ForteScarlet
 */
public object RichTypes {
    /**
     * 普通文本。
     */
    @Deprecated("Use RichType.TEXT_VALUE instead.", ReplaceWith("RichType.TEXT_VALUE"))
    public const val TEXT: Int = RichType.TEXT_VALUE

    /**
     * `@` 信息。
     */
    @Deprecated("Use RichType.AT_VALUE instead.", ReplaceWith("RichType.AT_VALUE"))
    public const val AT: Int = RichType.AT_VALUE

    /**
     * URL 信息。
     */
    @Deprecated("Use RichType.URL_VALUE instead.", ReplaceWith("RichType.URL_VALUE"))
    public const val URL: Int = RichType.URL_VALUE

    /**
     * 表情。
     */
    @Deprecated("Use RichType.EMOJI_VALUE instead.", ReplaceWith("RichType.EMOJI_VALUE"))
    public const val EMOJI: Int = RichType.EMOJI_VALUE

    /**
     * 子频道信息。
     */
    @Deprecated("Use RichType.CHANNEL_VALUE instead.", ReplaceWith("RichType.CHANNEL_VALUE"))
    public const val CHANNEL: Int = RichType.CHANNEL_VALUE

    /**
     * 视频。
     */
    @Deprecated("Use RichType.VIDEO_VALUE instead.", ReplaceWith("RichType.VIDEO_VALUE"))
    public const val VIDEO: Int = RichType.VIDEO_VALUE

    /**
     * 图片。
     */
    @Deprecated("Use RichType.IMAGE_VALUE instead.", ReplaceWith("RichType.IMAGE_VALUE"))
    public const val IMAGE: Int = RichType.IMAGE_VALUE
}

/**
 * 富文本普通文本信息。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#TextInfo)
 *
 * @property text 普通文本。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class TextInfo @ApiModelConstructor constructor(
    public val text: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TextInfo) return false
        return text == other.text
    }

    override fun hashCode(): Int = text.hashCode()

    override fun toString(): String = "TextInfo(text='$text')"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("text"))
    public operator fun component1(): String = text

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(text: String = this.text): TextInfo = TextInfo(text)
    //endregion
}

/**
 * 富文本 `@` 信息。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#AtInfo)
 *
 * @property atType `@` 类型。
 * @property userInfo 用户信息。
 * @property roleInfo 角色组信息。
 * @property guildInfo 频道信息。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class AtInfo
@ApiModelConstructor
@JvmExposeBoxed
internal constructor(
    @SerialName("type")
    @get:JvmExposeBoxed
    public val atType: AtType,
    @SerialName("user_info")
    public val userInfo: String,
    @SerialName("role_info")
    public val roleInfo: String,
    @SerialName("guild_info")
    public val guildInfo: String,
) {
    @Deprecated("Use atType instead.", ReplaceWith("atType.value"))
    public val type: Int
        get() = atType.value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AtInfo) return false

        if (atType != other.atType) return false
        if (userInfo != other.userInfo) return false
        if (roleInfo != other.roleInfo) return false
        if (guildInfo != other.guildInfo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = atType.hashCode()
        result = 31 * result + userInfo.hashCode()
        result = 31 * result + roleInfo.hashCode()
        result = 31 * result + guildInfo.hashCode()
        return result
    }

    override fun toString(): String {
        return "AtInfo(type=$atType, userInfo='$userInfo', roleInfo='$roleInfo', guildInfo='$guildInfo')"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("atType.value"))
    public operator fun component1(): Int = atType.value

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("userInfo"))
    public operator fun component2(): String = userInfo

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("roleInfo"))
    public operator fun component3(): String = roleInfo

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildInfo"))
    public operator fun component4(): String = guildInfo

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        type: Int = this.atType.value,
        userInfo: String = this.userInfo,
        roleInfo: String = this.roleInfo,
        guildInfo: String = this.guildInfo,
    ): AtInfo = AtInfo(
        atType = AtType.of(type),
        userInfo = userInfo,
        roleInfo = roleInfo,
        guildInfo = guildInfo,
    )
    //endregion
}

/**
 * `@` 类型的部分常量。
 *
 * 新代码请使用 [AtType]。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#AtInfo)
 *
 * @author ForteScarlet
 */
public object AtTypes {
    /**
     * `@` 特定用户。
     */
    @Deprecated("Use AtType.EXPLICIT_USER_VALUE instead.", ReplaceWith("AtType.EXPLICIT_USER_VALUE"))
    public const val AT_EXPLICIT_USER: Int = AtType.EXPLICIT_USER_VALUE

    /**
     * `@` 角色组所有人。
     */
    @Deprecated("Use AtType.ROLE_GROUP_VALUE instead.", ReplaceWith("AtType.ROLE_GROUP_VALUE"))
    public const val AT_ROLE_GROUP: Int = AtType.ROLE_GROUP_VALUE

    /**
     * `@` 频道所有人。
     */
    @Deprecated("Use AtType.GUILD_VALUE instead.", ReplaceWith("AtType.GUILD_VALUE"))
    public const val AT_GUILD: Int = AtType.GUILD_VALUE
}

/**
 * `@` 用户信息。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#AtUserInfo)
 *
 * @property id 用户身份 ID。
 * @property nick 用户昵称。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class AtUserInfo @ApiModelConstructor constructor(
    public val id: String,
    public val nick: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AtUserInfo) return false

        if (id != other.id) return false
        if (nick != other.nick) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + nick.hashCode()
        return result
    }

    override fun toString(): String = "AtUserInfo(id='$id', nick='$nick')"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("id"))
    public operator fun component1(): String = id

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("nick"))
    public operator fun component2(): String = nick

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        id: String = this.id,
        nick: String = this.nick,
    ): AtUserInfo = AtUserInfo(id, nick)
    //endregion
}

/**
 * `@` 角色组信息。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#AtRoleInfo)
 *
 * @property roleId 角色组 ID。
 * @property name 角色组名称。
 * @property color 颜色值。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class AtRoleInfo @ApiModelConstructor constructor(
    @SerialName("role_id")
    public val roleId: String,
    public val name: String,
    public val color: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AtRoleInfo) return false

        if (roleId != other.roleId) return false
        if (name != other.name) return false
        if (color != other.color) return false

        return true
    }

    override fun hashCode(): Int {
        var result = roleId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + color
        return result
    }

    override fun toString(): String = "AtRoleInfo(roleId='$roleId', name='$name', color=$color)"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("roleId"))
    public operator fun component1(): String = roleId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("name"))
    public operator fun component2(): String = name

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("color"))
    public operator fun component3(): Int = color

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        roleId: String = this.roleId,
        name: String = this.name,
        color: Int = this.color,
    ): AtRoleInfo = AtRoleInfo(roleId, name, color)
    //endregion
}

/**
 * `@` 频道信息。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#AtGuildInfo)
 *
 * @property guildId 频道 ID。
 * @property guildName 频道名称。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class AtGuildInfo @ApiModelConstructor constructor(
    @SerialName("guild_id")
    public val guildId: String,
    @SerialName("guild_name")
    public val guildName: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AtGuildInfo) return false

        if (guildId != other.guildId) return false
        if (guildName != other.guildName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guildId.hashCode()
        result = 31 * result + guildName.hashCode()
        return result
    }

    override fun toString(): String = "AtGuildInfo(guildId='$guildId', guildName='$guildName')"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildId"))
    public operator fun component1(): String = guildId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildName"))
    public operator fun component2(): String = guildName

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        guildId: String = this.guildId,
        guildName: String = this.guildName,
    ): AtGuildInfo = AtGuildInfo(guildId, guildName)
    //endregion
}

/**
 * 富文本链接信息。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#URLInfo)
 *
 * @property url 链接地址。
 * @property displayText 链接显示文本。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class URLInfo @ApiModelConstructor constructor(
    public val url: String,
    @SerialName("display_text")
    public val displayText: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is URLInfo) return false

        if (url != other.url) return false
        if (displayText != other.displayText) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + displayText.hashCode()
        return result
    }

    override fun toString(): String = "URLInfo(url='$url', displayText='$displayText')"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("url"))
    public operator fun component1(): String = url

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("displayText"))
    public operator fun component2(): String = displayText

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        url: String = this.url,
        displayText: String = this.displayText,
    ): URLInfo = URLInfo(url, displayText)
    //endregion
}

/**
 * 富文本 Emoji 信息。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#EmojiInfo)
 *
 * @property id 表情 ID。
 * @property type 表情类型。
 * @property name 表情名称。
 * @property url 表情链接。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class EmojiInfo @ApiModelConstructor constructor(
    public val id: String,
    public val type: String,
    public val name: String,
    public val url: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmojiInfo) return false

        if (id != other.id) return false
        if (type != other.type) return false
        if (name != other.name) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + url.hashCode()
        return result
    }

    override fun toString(): String = "EmojiInfo(id='$id', type='$type', name='$name', url='$url')"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("id"))
    public operator fun component1(): String = id

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("type"))
    public operator fun component2(): String = type

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("name"))
    public operator fun component3(): String = name

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("url"))
    public operator fun component4(): String = url

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        id: String = this.id,
        type: String = this.type,
        name: String = this.name,
        url: String = this.url,
    ): EmojiInfo = EmojiInfo(id, type, name, url)
    //endregion
}

/**
 * 富文本子频道信息。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#ChannelInfo)
 *
 * @property channelId 子频道 ID。
 * @property channelName 子频道名称。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class ChannelInfo @ApiModelConstructor constructor(
    @SerialName("channel_id")
    public val channelId: String,
    @SerialName("channel_name")
    public val channelName: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChannelInfo) return false

        if (channelId != other.channelId) return false
        if (channelName != other.channelName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channelId.hashCode()
        result = 31 * result + channelName.hashCode()
        return result
    }

    override fun toString(): String = "ChannelInfo(channelId='$channelId', channelName='$channelName')"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelId"))
    public operator fun component1(): String = channelId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelName"))
    public operator fun component2(): String = channelName

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        channelId: String = this.channelId,
        channelName: String = this.channelName,
    ): ChannelInfo = ChannelInfo(channelId, channelName)
    //endregion
}

/**
 * 富文本内容。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#RichText)
 *
 * @property paragraphs 段落列表。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class RichText @ApiModelConstructor constructor(
    public val paragraphs: List<Paragraph>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RichText) return false
        return paragraphs == other.paragraphs
    }

    override fun hashCode(): Int = paragraphs.hashCode()

    override fun toString(): String = "RichText(paragraphs=$paragraphs)"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("paragraphs"))
    public operator fun component1(): List<Paragraph> = paragraphs

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(paragraphs: List<Paragraph> = this.paragraphs): RichText = RichText(paragraphs)
    //endregion
}

/**
 * 富文本段落结构。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#Paragraph)
 *
 * @property elems 元素列表。
 * @property props 段落属性。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class Paragraph @ApiModelConstructor constructor(
    public val elems: Elem,
    public val props: ParagraphProps,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Paragraph) return false

        if (elems != other.elems) return false
        if (props != other.props) return false

        return true
    }

    override fun hashCode(): Int {
        var result = elems.hashCode()
        result = 31 * result + props.hashCode()
        return result
    }

    override fun toString(): String = "Paragraph(elems=$elems, props=$props)"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("elems"))
    public operator fun component1(): Elem = elems

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("props"))
    public operator fun component2(): ParagraphProps = props

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        elems: Elem = this.elems,
        props: ParagraphProps = this.props,
    ): Paragraph = Paragraph(elems, props)
    //endregion
}

/**
 * 富文本元素列表结构。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#Elem)
 *
 * @property text 文本元素。
 * @property image 图片元素。
 * @property video 视频元素。
 * @property url URL 元素。
 * @property type 元素类型。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class Elem
@ApiModelConstructor
@JvmExposeBoxed
internal constructor(
    public val text: TextElem,
    public val image: ImageElem,
    public val video: VideoElem,
    public val url: URLElem,
    @SerialName("type")
    @get:JvmExposeBoxed
    public val elemType: ElemType,
) {
    @Deprecated("Use elemType instead.", ReplaceWith("elemType.value"))
    public val type: Int
        get() = elemType.value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Elem) return false

        if (text != other.text) return false
        if (image != other.image) return false
        if (video != other.video) return false
        if (url != other.url) return false
        if (elemType != other.elemType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + video.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + elemType.hashCode()
        return result
    }

    override fun toString(): String {
        return "Elem(text=$text, image=$image, video=$video, url=$url, type=$elemType)"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("text"))
    public operator fun component1(): TextElem = text

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("image"))
    public operator fun component2(): ImageElem = image

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("video"))
    public operator fun component3(): VideoElem = video

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("url"))
    public operator fun component4(): URLElem = url

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("elemType.value"))
    public operator fun component5(): Int = elemType.value

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        text: TextElem = this.text,
        image: ImageElem = this.image,
        video: VideoElem = this.video,
        url: URLElem = this.url,
        type: Int = this.elemType.value,
    ): Elem = Elem(
        text = text,
        image = image,
        video = video,
        url = url,
        elemType = ElemType.of(type),
    )
    //endregion
}

/**
 * 富文本元素类型的部分常量。
 *
 * 新代码请使用 [ElemType]。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#Elem)
 *
 * @author ForteScarlet
 */
public object ElemTypes {
    /**
     * 文本元素。
     */
    @Deprecated("Use ElemType.TEXT_VALUE instead.", ReplaceWith("ElemType.TEXT_VALUE"))
    public const val TEXT: Int = ElemType.TEXT_VALUE

    /**
     * 图片元素。
     */
    @Deprecated("Use ElemType.IMAGE_VALUE instead.", ReplaceWith("ElemType.IMAGE_VALUE"))
    public const val IMAGE: Int = ElemType.IMAGE_VALUE

    /**
     * 视频元素。
     */
    @Deprecated("Use ElemType.VIDEO_VALUE instead.", ReplaceWith("ElemType.VIDEO_VALUE"))
    public const val VIDEO: Int = ElemType.VIDEO_VALUE

    /**
     * URL 元素。
     */
    @Deprecated("Use ElemType.URL_VALUE instead.", ReplaceWith("ElemType.URL_VALUE"))
    public const val URL: Int = ElemType.URL_VALUE
}

/**
 * 富文本文本属性。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#TextElem)
 *
 * @property text 正文。
 * @property props 文本属性。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class TextElem @ApiModelConstructor constructor(
    public val text: String,
    public val props: TextProps,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TextElem) return false

        if (text != other.text) return false
        if (props != other.props) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + props.hashCode()
        return result
    }

    override fun toString(): String = "TextElem(text='$text', props=$props)"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("text"))
    public operator fun component1(): String = text

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("props"))
    public operator fun component2(): TextProps = props

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        text: String = this.text,
        props: TextProps = this.props,
    ): TextElem = TextElem(text, props)
    //endregion
}

/**
 * 富文本文本段落属性。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#TextProps)
 *
 * @property fontBold 是否加粗。
 * @property italic 是否斜体。
 * @property underline 是否下划线。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class TextProps @ApiModelConstructor constructor(
    @SerialName("font_bold")
    public val fontBold: Boolean,
    public val italic: Boolean,
    public val underline: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TextProps) return false

        if (fontBold != other.fontBold) return false
        if (italic != other.italic) return false
        if (underline != other.underline) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fontBold.hashCode()
        result = 31 * result + italic.hashCode()
        result = 31 * result + underline.hashCode()
        return result
    }

    override fun toString(): String = "TextProps(fontBold=$fontBold, italic=$italic, underline=$underline)"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("fontBold"))
    public operator fun component1(): Boolean = fontBold

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("italic"))
    public operator fun component2(): Boolean = italic

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("underline"))
    public operator fun component3(): Boolean = underline

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        fontBold: Boolean = this.fontBold,
        italic: Boolean = this.italic,
        underline: Boolean = this.underline,
    ): TextProps = TextProps(fontBold, italic, underline)
    //endregion
}

/**
 * 富文本图片属性。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#ImageElem)
 *
 * @property thirdUrl 第三方图片链接。
 * @property widthPercent 宽度比例。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class ImageElem @ApiModelConstructor constructor(
    @SerialName("third_url")
    public val thirdUrl: String,
    @SerialName("width_percent")
    public val widthPercent: Double,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImageElem) return false

        if (thirdUrl != other.thirdUrl) return false
        if (widthPercent != other.widthPercent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = thirdUrl.hashCode()
        result = 31 * result + widthPercent.hashCode()
        return result
    }

    override fun toString(): String = "ImageElem(thirdUrl='$thirdUrl', widthPercent=$widthPercent)"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("thirdUrl"))
    public operator fun component1(): String = thirdUrl

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("widthPercent"))
    public operator fun component2(): Double = widthPercent

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        thirdUrl: String = this.thirdUrl,
        widthPercent: Double = this.widthPercent,
    ): ImageElem = ImageElem(thirdUrl, widthPercent)
    //endregion
}

/**
 * 富文本平台图片属性。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#PlatImage)
 *
 * @property url 图片链接。
 * @property width 图片宽度。
 * @property height 图片高度。
 * @property imageId 图片 ID。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class PlatImage @ApiModelConstructor constructor(
    public val url: String,
    public val width: Int,
    public val height: Int,
    @SerialName("image_id")
    public val imageId: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlatImage) return false

        if (url != other.url) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (imageId != other.imageId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + imageId.hashCode()
        return result
    }

    override fun toString(): String = "PlatImage(url='$url', width=$width, height=$height, imageId='$imageId')"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("url"))
    public operator fun component1(): String = url

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("width"))
    public operator fun component2(): Int = width

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("height"))
    public operator fun component3(): Int = height

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("imageId"))
    public operator fun component4(): String = imageId

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        url: String = this.url,
        width: Int = this.width,
        height: Int = this.height,
        imageId: String = this.imageId,
    ): PlatImage = PlatImage(url, width, height, imageId)
    //endregion
}

/**
 * 富文本视频属性。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#VideoElem)
 *
 * @property thirdUrl 第三方视频文件链接。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class VideoElem @ApiModelConstructor constructor(
    @SerialName("third_url")
    public val thirdUrl: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VideoElem) return false
        return thirdUrl == other.thirdUrl
    }

    override fun hashCode(): Int = thirdUrl.hashCode()

    override fun toString(): String = "VideoElem(thirdUrl='$thirdUrl')"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("thirdUrl"))
    public operator fun component1(): String = thirdUrl

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(thirdUrl: String = this.thirdUrl): VideoElem = VideoElem(thirdUrl)
    //endregion
}

/**
 * 富文本平台视频属性。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#PlatVideo)
 *
 * @property url 视频链接。
 * @property width 视频宽度。
 * @property height 视频高度。
 * @property videoId 视频 ID。
 * @property duration 视频时长。
 * @property cover 视频封面图属性。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class PlatVideo @ApiModelConstructor constructor(
    public val url: String,
    public val width: Int,
    public val height: Int,
    @SerialName("video_id")
    public val videoId: String,
    public val duration: Int,
    public val cover: PlatImage,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlatVideo) return false

        if (url != other.url) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (videoId != other.videoId) return false
        if (duration != other.duration) return false
        if (cover != other.cover) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + videoId.hashCode()
        result = 31 * result + duration
        result = 31 * result + cover.hashCode()
        return result
    }

    override fun toString(): String {
        return "PlatVideo(" +
            "url='$url', " +
            "width=$width, " +
            "height=$height, " +
            "videoId='$videoId', " +
            "duration=$duration, " +
            "cover=$cover)"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("url"))
    public operator fun component1(): String = url

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("width"))
    public operator fun component2(): Int = width

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("height"))
    public operator fun component3(): Int = height

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("videoId"))
    public operator fun component4(): String = videoId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("duration"))
    public operator fun component5(): Int = duration

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("cover"))
    public operator fun component6(): PlatImage = cover

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        url: String = this.url,
        width: Int = this.width,
        height: Int = this.height,
        videoId: String = this.videoId,
        duration: Int = this.duration,
        cover: PlatImage = this.cover,
    ): PlatVideo = PlatVideo(url, width, height, videoId, duration, cover)
    //endregion
}

/**
 * 富文本 URL 属性。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#URLElem)
 *
 * @property url URL 链接。
 * @property desc URL 描述。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class URLElem @ApiModelConstructor constructor(
    public val url: String,
    public val desc: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is URLElem) return false

        if (url != other.url) return false
        if (desc != other.desc) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + desc.hashCode()
        return result
    }

    override fun toString(): String = "URLElem(url='$url', desc='$desc')"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("url"))
    public operator fun component1(): String = url

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("desc"))
    public operator fun component2(): String = desc

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        url: String = this.url,
        desc: String = this.desc,
    ): URLElem = URLElem(url, desc)
    //endregion
}

/**
 * 富文本段落属性。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#ParagraphProps)
 *
 * @property paragraphAlignment 段落对齐方向。
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class ParagraphProps
@ApiModelConstructor
@JvmExposeBoxed
internal constructor(
    @SerialName("alignment")
    @get:JvmExposeBoxed
    public val paragraphAlignment: Alignment,
) {
    @Deprecated("Use paragraphAlignment instead.", ReplaceWith("paragraphAlignment.value"))
    public val alignment: Int
        get() = paragraphAlignment.value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ParagraphProps) return false
        return paragraphAlignment == other.paragraphAlignment
    }

    override fun hashCode(): Int = paragraphAlignment.hashCode()

    override fun toString(): String = "ParagraphProps(alignment=$paragraphAlignment)"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("paragraphAlignment.value"))
    public operator fun component1(): Int = paragraphAlignment.value

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        alignment: Int = this.paragraphAlignment.value
    ): ParagraphProps = ParagraphProps(Alignment.of(alignment))
    //endregion
}

/**
 * 段落对齐方式常量。
 *
 * 新代码请使用 [Alignment]。
 *
 * @see ParagraphProps.paragraphAlignment
 * @author ForteScarlet
 */
public object Alignments {
    /**
     * 左对齐。
     */
    @Deprecated("Use Alignment.LEFT_VALUE instead.", ReplaceWith("Alignment.LEFT_VALUE"))
    public const val LEFT: Int = Alignment.LEFT_VALUE

    /**
     * 居中。
     */
    @Deprecated("Use Alignment.MIDDLE_VALUE instead.", ReplaceWith("Alignment.MIDDLE_VALUE"))
    public const val MIDDLE: Int = Alignment.MIDDLE_VALUE

    /**
     * 右对齐。
     */
    @Deprecated("Use Alignment.RIGHT_VALUE instead.", ReplaceWith("Alignment.RIGHT_VALUE"))
    public const val RIGHT: Int = Alignment.RIGHT_VALUE
}
