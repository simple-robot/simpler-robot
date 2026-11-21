/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.ability

/**
 * 由 simbot 定义的标准回复选项。
 * @since 5.0
 */
public enum class StandardReplyOption : ReplyOption {
    /**
     * 发送不带消息引用的回复。
     *
     * 省略回复默认补全的引用及从消息内容继承的引用。若最终消息或选项显式指定了引用，
     * 在发送前抛出 [IllegalArgumentException]。不能满足此要求的实现应在发送前抛出
     * [UnsupportedOperationException]。平台用于处理回复的关联字段仍可按协议发送。
     * @since 5.0
     */
    NO_REFERENCE,
}
