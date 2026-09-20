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
 * 消息按钮操作权限的类型。
 *
 * @see MessageKeyboard.ActionPermission.type
 * @property value 权限类型的原始数值。
 *
 * @since 5.0
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class MessageKeyboardActionPermissionType private constructor(public val value: Int) {
    public companion object {
        /**
         * 指定用户可操作的原始值。
         */
        public const val SPECIFIED_USER_VALUE: Int = 0

        /**
         * 仅管理者可操作的原始值。
         */
        public const val ADMIN_ONLY_VALUE: Int = 1

        /**
         * 所有人可操作的原始值。
         */
        public const val ALL_ACCESSIBLE_VALUE: Int = 2

        /**
         * 指定身份组可操作的原始值。
         */
        public const val SPECIFIED_ROLE_VALUE: Int = 3

        /**
         * 指定用户可操作。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val SpecifiedUser: MessageKeyboardActionPermissionType =
            MessageKeyboardActionPermissionType(SPECIFIED_USER_VALUE)

        /**
         * 仅管理者可操作。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val AdminOnly: MessageKeyboardActionPermissionType =
            MessageKeyboardActionPermissionType(ADMIN_ONLY_VALUE)

        /**
         * 所有人可操作。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val AllAccessible: MessageKeyboardActionPermissionType =
            MessageKeyboardActionPermissionType(ALL_ACCESSIBLE_VALUE)

        /**
         * 指定身份组可操作。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val SpecifiedRole: MessageKeyboardActionPermissionType =
            MessageKeyboardActionPermissionType(SPECIFIED_ROLE_VALUE)

        /**
         * 构建一个自定义的消息按钮操作权限类型。
         */
        @JvmStatic
        @JvmExposeBoxed
        public fun of(value: Int): MessageKeyboardActionPermissionType =
            MessageKeyboardActionPermissionType(value)
    }
}
