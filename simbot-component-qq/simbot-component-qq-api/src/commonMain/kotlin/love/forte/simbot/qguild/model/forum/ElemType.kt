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
 * 富文本元素的类型。
 *
 * [Elem.type] 使用此类型表示元素类型。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#elemtype)
 *
 * @since 5.0
 * @author ForteScarlet
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class ElemType private constructor(public val value: Int) {
    @JvmExposeBoxed
    public companion object {
        /**
         * 文本元素的原始值。
         */
        public const val TEXT_VALUE: Int = 1

        /**
         * 图片元素的原始值。
         */
        public const val IMAGE_VALUE: Int = 2

        /**
         * 视频元素的原始值。
         */
        public const val VIDEO_VALUE: Int = 3

        /**
         * URL 元素的原始值。
         */
        public const val URL_VALUE: Int = 4

        /**
         * 文本元素类型。
         */
        @JvmStatic
        public val Text: ElemType = ElemType(TEXT_VALUE)

        /**
         * 图片元素类型。
         */
        @JvmStatic
        public val Image: ElemType = ElemType(IMAGE_VALUE)

        /**
         * 视频元素类型。
         */
        @JvmStatic
        public val Video: ElemType = ElemType(VIDEO_VALUE)

        /**
         * URL 元素类型。
         */
        @JvmStatic
        public val Url: ElemType = ElemType(URL_VALUE)

        /**
         * 根据任意值构建 [ElemType]。
         */
        @JvmStatic
        public fun of(value: Int): ElemType = ElemType(value)
    }
}
