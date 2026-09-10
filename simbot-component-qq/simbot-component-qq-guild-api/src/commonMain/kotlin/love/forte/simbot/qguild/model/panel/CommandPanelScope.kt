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
         * 构建一个自定义值的 [CommandPanelScope]。
         */
        @JvmStatic
        @JvmExposeBoxed
        public fun of(scope: String): CommandPanelScope {
            return when (scope) {
                CommandPanelRecord.SCOPE_C2C -> C2C
                CommandPanelRecord.SCOPE_GROUP -> Group
                CommandPanelRecord.SCOPE_CHANNEL -> Channel
                CommandPanelRecord.SCOPE_DM -> DM
                else -> CommandPanelScope(scope)
            }
        }

        /**
         * C2C 单聊场景。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val C2C: CommandPanelScope = CommandPanelScope(CommandPanelRecord.SCOPE_C2C)

        /**
         * 群聊场景。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Group: CommandPanelScope = CommandPanelScope(CommandPanelRecord.SCOPE_GROUP)

        /**
         * 文字子频道场景。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Channel: CommandPanelScope = CommandPanelScope(CommandPanelRecord.SCOPE_CHANNEL)

        /**
         * 频道私信场景。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val DM: CommandPanelScope = CommandPanelScope(CommandPanelRecord.SCOPE_DM)
    }
}
