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
 * 消息按钮的操作类型。
 *
 * @see MessageKeyboard.Action.type
 * @property value 操作类型的原始数值。
 *
 * @since 5.0
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class MessageKeyboardActionType private constructor(public val value: Int) {
    @JvmExposeBoxed
    public companion object {
        /**
         * 跳转按钮的原始值。
         */
        public const val LINK_VALUE: Int = 0

        /**
         * 回调按钮的原始值。
         */
        public const val CALLBACK_VALUE: Int = 1

        /**
         * 指令按钮的原始值。
         */
        public const val COMMAND_VALUE: Int = 2

        /**
         * 跳转按钮。
         */
        @JvmStatic
        public val Link: MessageKeyboardActionType = MessageKeyboardActionType(LINK_VALUE)

        /**
         * 回调按钮。
         */
        @JvmStatic
        public val Callback: MessageKeyboardActionType = MessageKeyboardActionType(CALLBACK_VALUE)

        /**
         * 指令按钮。
         */
        @JvmStatic
        public val Command: MessageKeyboardActionType = MessageKeyboardActionType(COMMAND_VALUE)

        /**
         * 构建一个自定义的消息按钮操作类型。
         */
        @JvmStatic
        public fun of(value: Int): MessageKeyboardActionType = MessageKeyboardActionType(value)
    }
}
