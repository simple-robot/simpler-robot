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

package love.forte.simbot.qguild.model.group

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic

/**
 * 机器人在 QQ 群内的消息接收设置。
 *
 * 已知取值为 [All]、[OnlyMention] 和 [MentionAndContext]；
 * 未知平台取值通过 [of] 保留。
 *
 * @since 5.0
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class GroupBotReceiveMessageSetting private constructor(public val value: String) {
    @JvmExposeBoxed
    public companion object {
        /**
         * 接收所有消息的原始值。
         */
        public const val ALL_VALUE: String = "all"

        /**
         * 只接收提及机器人的消息的原始值。
         */
        public const val ONLY_MENTION_VALUE: String = "only_mention"

        /**
         * 接收提及机器人及其上下文消息的原始值。
         */
        public const val MENTION_AND_CONTEXT_VALUE: String = "mention_and_context"

        /**
         * 接收所有消息。
         */
        @JvmStatic
        public val All: GroupBotReceiveMessageSetting = GroupBotReceiveMessageSetting(ALL_VALUE)

        /**
         * 只接收提及机器人的消息。
         */
        @JvmStatic
        public val OnlyMention: GroupBotReceiveMessageSetting =
            GroupBotReceiveMessageSetting(ONLY_MENTION_VALUE)

        /**
         * 接收提及机器人及其上下文消息。
         */
        @JvmStatic
        public val MentionAndContext: GroupBotReceiveMessageSetting =
            GroupBotReceiveMessageSetting(MENTION_AND_CONTEXT_VALUE)

        /**
         * 根据平台原始值构造消息接收设置，保留未知值。
         */
        @JvmStatic
        public fun of(value: String): GroupBotReceiveMessageSetting = GroupBotReceiveMessageSetting(value)
    }
}
