/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.model.forum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel

/**
 *
 * 富文本内容
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class RichObject(
    /**
     * 富文本类型
     *
     * @see RichTypes
     */
    val type: Int,
    /**
     * 文本
     * @see TextInfo
     */
    @SerialName("text_info") val textInfo: TextInfo,
    /**
     * `@` 内容
     * @see AtInfo
     */
    @SerialName("at_info") val atInfo: String,
    /**
     * 链接
     * @see URLInfo
     */
    @SerialName("url_info") val urlInfo: String,
    /**
     * 表情
     * @see EmojiInfo
     */
    @SerialName("emoji_info") val emojiInfo: String,
    /**
     * 提到的子频道
     * @see ChannelInfo
     */
    @SerialName("channel_info") val channelInfo: String,
)

/**
 * 富文本类型的部分常量
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
public object RichTypes {
    /**
     * 普通文本
     */
    public const val TEXT: Int = 1

    /**
     * at信息
     */
    public const val AT: Int = 2

    /**
     * url信息
     */
    public const val URL: Int = 3

    /**
     * 表情
     */
    public const val EMOJI: Int = 4

    /**
     * #子频道
     */
    public const val CHANNEL: Int = 5

    /**
     * 视频
     */
    public const val VIDEO: Int = 10

    /**
     * 图片
     */
    public const val IMAGE: Int = 11
}

/**
 * 富文本 - 普通文本
 *
 * @property text 普通文本
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class TextInfo(val text: String)

/**
 * 富文本 - @内容
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class AtInfo(
    /**
     * at类型
     *
     * @see AtTypes
     */
    val type: Int,
    /**
     * 用户
     *
     * @see AtUserInfo
     */
    @SerialName("user_info")
    val userInfo: String,
    /**
     * 角色组信息
     *
     * @see AtRoleInfo
     */
    @SerialName("role_info")
    val roleInfo: String,
    /**
     * 频道信息
     *
     * @see AtGuildInfo
     */
    @SerialName("guild_info")
    val guildInfo: String,
)

/**
 * `@`类型的常量
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * author ForteScarlet
 */
public object AtTypes {
    /**
     * at特定人
     */
    public const val AT_EXPLICIT_USER: Int = 1

    /**
     * at角色组所有人
     */
    public const val AT_ROLE_GROUP: Int = 2

    /**
     * at频道所有人
     */
    public const val AT_GUILD: Int = 3
}

/**
 * `@`用户信息
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * author ForteScarlet
 */
@ApiModel
@Serializable
public data class AtUserInfo(
    /**
     * 身份组ID
     */
    val id: String,
    /**
     * 用户昵称
     */
    val nick: String
)

/**
 * `@`身份组信息
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * author ForteScarlet
 */
@ApiModel
@Serializable
public data class AtRoleInfo(
    /**
     * 身份组ID
     */
    @SerialName("role_id") val roleId: String,
    /**
     * 身份组名称
     */
    val name: String,
    /**
     * 颜色值
     */
    val color: Int
)

/**
 * `@`频道信息
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * author ForteScarlet
 */
@ApiModel
@Serializable
public data class AtGuildInfo(
    /**
     * 频道ID
     */
    @SerialName("guild_id")
    val guildId: String,
    /**
     * 频道名称
     */
    @SerialName("guild_name")
    val guildName: String,
)

/**
 * 富文本 - 链接信息
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * author ForteScarlet
 */
@ApiModel
@Serializable
public data class URLInfo(
    /**
     * 链接地址
     */
    val url: String,
    /**
     * 链接显示文本
     */
    @SerialName("display_text")
    val displayText: String
)


/**
 * 富文本 - Emoji信息
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * author ForteScarlet
 */
@ApiModel
@Serializable
public data class EmojiInfo(
    /**
     * 表情id
     */
    val id: String,
    /**
     * 表情类型
     */
    val type: String,
    /**
     * 名称
     */
    val name: String,
    /**
     * 链接
     */
    val url: String,
)


/**
 * 富文本 - 子频道信息
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * author ForteScarlet
 */
@ApiModel
@Serializable
public data class ChannelInfo(
    /**
     * 子频道id
     */
    @SerialName("channel_id")
    val channelId: String,
    /**
     * 子频道名称
     */
    @SerialName("channel_name")
    val channelName: String,
)

/**
 * 富文本内容
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * author ForteScarlet
 */
@ApiModel
@Serializable
public data class RichText(
    /**
     * 段落，一段落一行，段落内无元素的为空行
     */
    val paragraphs: List<Paragraph>
)

/**
 * 富文本 - 段落结构
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * author ForteScarlet
 */
@ApiModel
@Serializable
public data class Paragraph(
    /**
     * 元素列表
     */
    val elems: Elem,
    /**
     * 段落属性
     */
    val props: ParagraphProps,
)

/**
 * 富文本 - 元素列表结构
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * author ForteScarlet
 */
@ApiModel
@Serializable
public data class Elem(
    /**
     * 文本元素
     */
    val text: TextElem,
    /**
     * 图片元素
     */
    val image: ImageElem,
    /**
     * 视频元素
     */
    val video: VideoElem,
    /**
     * URL元素
     */
    val url: URLElem,
    /**
     * 元素类型
     *
     * @see ElemTypes
     */
    val type: Int,
)

/**
 * 富文本元素类型
 *
 * @see Elem.type
 */
public object ElemTypes {
    /**
     * 文本
     */
    public const val TEXT: Int = 1

    /**
     * 图片
     */
    public const val IMAGE: Int = 2

    /**
     * 视频
     */
    public const val VIDEO: Int = 3

    /**
     * URL
     */
    public const val URL: Int = 4
}

/**
 * 富文本 - 文本属性
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class TextElem(
    /**
     * 正文
     */
    val text: String,
    /**
     * 文本属性
     *
     * @see TextProps
     */
    val props: TextProps
)

/**
 * 富文本 - 文本段落属性
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class TextProps(
    /**
     * 加粗
     */
    @SerialName("font_bold")
    val fontBold: Boolean,
    /**
     * 斜体
     */
    val italic: Boolean,
    /**
     * 下划线
     */
    val underline: Boolean,
)

/**
 * 富文本 - 图片属性
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class ImageElem(
    /**
     * 第三方图片链接
     */
    @SerialName("third_url")
    val thirdUrl: String,
    /**
     * 宽度比例（缩放比，在屏幕里显示的比例）
     */
    @SerialName("width_percent")
    val widthPercent: Double,
)

/**
 * 富文本 - 平台图片属性
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class PlatImage(
    /**
     * 架平图片链接
     */
    val url: String,
    /**
     * 图片宽度
     */
    val width: Int,
    /**
     * 图片高度
     */
    val height: Int,
    /**
     * 图片ID
     */
    @SerialName("image_id")
    val imageId: String,
)


/**
 * 富文本 - 视频属性
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class VideoElem(
    /**
     * 第三方视频文件链接
     */
    @SerialName("third_url")
    val thirdUrl: String,
)

/**
 * 富文本 - 平台视频属性
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class PlatVideo(
    /**
     * 架平图片链接
     */
    val url: String,
    /**
     * 图片宽度
     */
    val width: Int,
    /**
     * 图片高度
     */
    val height: Int,
    /**
     * 视频ID
     */
    @SerialName("video_id")
    val videoId: String,
    /**
     * 视频时长
     */
    val duration: Int,
    /**
     * 视频封面图属性
     */
    val cover: PlatImage,
)

/**
 * 富文本 - URL属性
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class URLElem(
    /**
     * URL链接
     */
    val url: String,
    /**
     * URL描述
     */
    val desc: String,
)


/**
 * 富文本 - 段落属性
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * author ForteScarlet
 */
@ApiModel
@Serializable
public data class ParagraphProps(
    /**
     * 段落对齐方向属性，数值可以参考 [Alignments]
     *
     * @see Alignments
     */
    val alignment: Int
)


/**
 * 段落对齐方式常量
 *
 * @see ParagraphProps.alignment
 */
public object Alignments {
    /**
     * 左对齐
     */
    public const val LEFT: Int = 0

    /**
     * 居中
     */
    public const val MIDDLE: Int = 1

    /**
     * 右对齐
     */
    public const val RIGHT: Int = 2
}
