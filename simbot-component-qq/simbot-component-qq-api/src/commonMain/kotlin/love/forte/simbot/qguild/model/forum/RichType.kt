/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.qguild.model.forum

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic

/**
 * 富文本对象的类型。
 *
 * [RichObject.type] 使用此类型表示富文本对象类型。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#richtype)
 *
 * @since 5.0
 * @author ForteScarlet
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class RichType private constructor(public val value: Int) {
    public companion object {
        /**
         * 普通文本的原始值。
         */
        public const val TEXT_VALUE: Int = 1

        /**
         * `@` 信息的原始值。
         */
        public const val AT_VALUE: Int = 2

        /**
         * URL 信息的原始值。
         */
        public const val URL_VALUE: Int = 3

        /**
         * 表情信息的原始值。
         */
        public const val EMOJI_VALUE: Int = 4

        /**
         * 子频道信息的原始值。
         */
        public const val CHANNEL_VALUE: Int = 5

        /**
         * 视频信息的原始值。
         */
        public const val VIDEO_VALUE: Int = 10

        /**
         * 图片信息的原始值。
         */
        public const val IMAGE_VALUE: Int = 11

        /**
         * 普通文本类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Text: RichType = RichType(TEXT_VALUE)

        /**
         * `@` 信息类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val At: RichType = RichType(AT_VALUE)

        /**
         * URL 信息类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Url: RichType = RichType(URL_VALUE)

        /**
         * 表情信息类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Emoji: RichType = RichType(EMOJI_VALUE)

        /**
         * 子频道信息类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Channel: RichType = RichType(CHANNEL_VALUE)

        /**
         * 视频信息类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Video: RichType = RichType(VIDEO_VALUE)

        /**
         * 图片信息类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Image: RichType = RichType(IMAGE_VALUE)

        /**
         * 根据任意值构建 [RichType]。
         */
        @JvmStatic
        @JvmExposeBoxed
        public fun of(value: Int): RichType = RichType(value)
    }
}
