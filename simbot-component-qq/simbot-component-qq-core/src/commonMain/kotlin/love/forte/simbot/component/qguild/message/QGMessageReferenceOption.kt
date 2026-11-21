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
import love.forte.simbot.ability.StandardReplyOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReference
import love.forte.simbot.message.Messages
import love.forte.simbot.qguild.model.Message as QGSourceMessage

/**
 * QQ 回复消息时使用的引用。
 *
 * 同一次操作中至多指定一次。缺少此选项时，频道、频道私信和群回复默认引用原消息，C2C 回复不添加引用。
 * [StandardReplyOption.NO_REFERENCE] 与此选项互斥。
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

private fun List<ReplyOption>.qgMessageReference(): ID? {
    val references = filterIsInstance<QGMessageReferenceOption>()
    require(references.size <= 1) { "Duplicate QQ reply option: MessageReference" }
    return references.singleOrNull()?.id
}

/**
 * 在解析和拆分消息前校验显式引用，并为每个实际发送请求解析引用字段。
 *
 * @param defaultMessageId 支持自动引用的回复入口所使用的原消息 ID。
 */
internal fun QGMessageReplyContext.replyReferenceResolver(
    defaultMessageId: String? = null,
): (QGSourceMessage.Reference?) -> QGSourceMessage.Reference? {
    val explicitReference = options.qgMessageReference()
    if (StandardReplyOption.NO_REFERENCE !in options) {
        val fallback = (explicitReference?.literal ?: defaultMessageId)?.let { QGSourceMessage.Reference(it) }
        return { it ?: fallback }
    }

    require(explicitReference == null) { "NO_REFERENCE conflicts with QGMessageReferenceOption" }
    val outgoing = message
    if (outgoing is InteractionMessage.Extension) {
        throw UnsupportedOperationException("Cannot verify NO_REFERENCE for this QQ message")
    }
    // 有的图片元素在解析过程中会上传资源，因此先检查消息链。
    require(outgoing !is InteractionMessage.Message || !outgoing.message.hasReference()) {
        "NO_REFERENCE conflicts with a message reference"
    }
    return { existing ->
        require(existing == null || outgoing is InteractionMessage.MessageContent) {
            "NO_REFERENCE conflicts with a request message reference"
        }
        null
    }
}

/**
 * 找出会写入 QQ 请求的显式引用元素。
 */
private fun Message.hasReference(): Boolean = when (this) {
    is MessageReference -> true
    is Messages -> any { it is MessageReference }
    else -> false
}
