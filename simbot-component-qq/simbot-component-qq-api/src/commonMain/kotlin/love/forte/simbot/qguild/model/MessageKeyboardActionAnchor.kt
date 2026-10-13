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

package love.forte.simbot.qguild.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic

/**
 * 指令按钮的锚点操作类型。
 *
 * @see MessageKeyboard.Action.anchor
 * @property value 锚点操作的原始数值。
 *
 * @since 5.0
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class MessageKeyboardActionAnchor private constructor(public val value: Int) {
    @JvmExposeBoxed
    public companion object {
        /**
         * 唤起手机 QQ 选图器的原始值。
         */
        public const val SELECT_IMAGE_VALUE: Int = 1

        /**
         * 唤起手机 QQ 选图器。
         */
        @JvmStatic
        public val SelectImage: MessageKeyboardActionAnchor =
            MessageKeyboardActionAnchor(SELECT_IMAGE_VALUE)

        /**
         * 构建一个自定义的指令按钮锚点操作类型。
         */
        @JvmStatic
        public fun of(value: Int): MessageKeyboardActionAnchor = MessageKeyboardActionAnchor(value)
    }
}
