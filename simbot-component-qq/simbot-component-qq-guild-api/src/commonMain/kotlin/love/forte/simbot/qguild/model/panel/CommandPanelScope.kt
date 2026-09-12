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

package love.forte.simbot.qguild.model.panel

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic

/**
 * 面板生效场景 `scope`。
 *
 * @since 5.0
 * @author Forte Scarlet
 */
@JvmInline
@JvmExposeBoxed
@OptIn(ExperimentalStdlibApi::class)
@Serializable
public value class CommandPanelScope private constructor(public val scope: String) {
    public companion object {
        /**
         * C2C 单聊场景的原始值。
         */
        public const val C2C_VALUE: String = "c2c"

        /**
         * 群聊场景的原始值。
         */
        public const val GROUP_VALUE: String = "group"

        /**
         * 文字子频道场景的原始值。
         */
        public const val CHANNEL_VALUE: String = "channel"

        /**
         * 频道私信场景的原始值。
         */
        public const val DM_VALUE: String = "dm"

        /**
         * C2C 单聊场景。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val C2C: CommandPanelScope = CommandPanelScope(C2C_VALUE)

        /**
         * 群聊场景。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Group: CommandPanelScope = CommandPanelScope(GROUP_VALUE)

        /**
         * 文字子频道场景。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Channel: CommandPanelScope = CommandPanelScope(CHANNEL_VALUE)

        /**
         * 频道私信场景。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val DM: CommandPanelScope = CommandPanelScope(DM_VALUE)

        /**
         * 构建一个自定义值的 [CommandPanelScope]。
         */
        @JvmStatic
        @JvmExposeBoxed
        public fun of(scope: String): CommandPanelScope {
            // 内联类内部使用 when (scope) { ... , else } 似乎是无意义的，
            // 针对 JVM 的 box 行为都是在内联之后，因此 val C2C 之类的‘对象’
            // 也都是 return box-impl(C2C常量)，返回的对象仍然是一个新对象，而不是固定对象。
            // 所以直接返回内联对象就行，对已知量枚举是无意义的。
            return CommandPanelScope(scope)
        }
    }
}
