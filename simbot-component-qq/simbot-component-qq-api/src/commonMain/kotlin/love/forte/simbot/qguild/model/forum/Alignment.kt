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
 * 富文本段落的对齐方式。
 *
 * [ParagraphProps.alignment] 使用此类型表示段落对齐方式。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#Alignment)
 *
 * @since 5.0
 * @author ForteScarlet
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class Alignment private constructor(public val value: Int) {
    public companion object {
        /**
         * 左对齐的原始值。
         */
        public const val LEFT_VALUE: Int = 0

        /**
         * 居中的原始值。
         */
        public const val MIDDLE_VALUE: Int = 1

        /**
         * 右对齐的原始值。
         */
        public const val RIGHT_VALUE: Int = 2

        /**
         * 左对齐。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Left: Alignment = Alignment(LEFT_VALUE)

        /**
         * 居中对齐。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Middle: Alignment = Alignment(MIDDLE_VALUE)

        /**
         * 右对齐。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Right: Alignment = Alignment(RIGHT_VALUE)

        /**
         * 根据任意值构建 [Alignment]。
         */
        @JvmStatic
        @JvmExposeBoxed
        public fun of(value: Int): Alignment = Alignment(value)
    }
}
