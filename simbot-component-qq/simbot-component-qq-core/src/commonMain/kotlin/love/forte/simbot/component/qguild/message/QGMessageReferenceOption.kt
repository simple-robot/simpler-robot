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

package love.forte.simbot.component.qguild.message

import love.forte.simbot.ability.ReplyOption
import love.forte.simbot.common.id.ID

/**
 * QQ 回复消息时使用的引用。
 *
 * 同一次操作中至多指定一次。移除此选项可以取消入口自动添加的引用。
 *
 * @since 5.0
 */
public class QGMessageReferenceOption private constructor(public val id: ID) : QGMessageReplyOption {
    companion {
        /**
         * 引用指定消息。
         * @since 5.0
         */
        public fun create(id: ID): QGMessageReferenceOption = QGMessageReferenceOption(id)
    }

    override fun equals(other: Any?): Boolean =
        this === other || other is QGMessageReferenceOption && id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "QGMessageReferenceOption(id=$id)"
}

internal fun List<ReplyOption>.qgMessageReference(): ID? {
    val references = filterIsInstance<QGMessageReferenceOption>()
    require(references.size <= 1) { "Duplicate QQ reply option: MessageReference" }
    return references.singleOrNull()?.id
}
