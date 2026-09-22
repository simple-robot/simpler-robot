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

package love.forte.simbot.kook.event

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [Event] 的序列化器。
 *
 * Kotlin 2.4.20 的 Wasm 后端会为“泛型参数具有 class 上界”的可序列化类生成非法的
 * `deserialize` 引用类型，见 [KT-89275](https://youtrack.jetbrains.com/issue/KT-89275)。
 * 此处委托给没有 class 上界的内部传输类型，保留公开模型与 JSON 结构，同时避开错误代码生成路径。
 *
 * 待上游修复版本可用并验证后，可删除此临时序列化器并恢复编译器生成实现。
 */
internal class EventSerializer<E : EventExtra>(
    extraSerializer: KSerializer<E>
) : KSerializer<Event<E>> {
    private val delegate = SerializableEvent.serializer(extraSerializer)

    override val descriptor = delegate.descriptor

    override fun serialize(encoder: Encoder, value: Event<E>) {
        delegate.serialize(encoder, value.toSerializableEvent())
    }

    override fun deserialize(decoder: Decoder): Event<E> {
        return delegate.deserialize(decoder).toEvent()
    }
}

@Serializable
@SerialName("love.forte.simbot.kook.event.Event")
private data class SerializableEvent<E>(
    @SerialName("channel_type")
    val channelTypeValue: String,
    @SerialName("type")
    val typeValue: Int,
    @SerialName("target_id")
    val targetId: String,
    @SerialName("author_id")
    val authorId: String,
    val content: String,
    @SerialName("msg_id")
    val msgId: String,
    @SerialName("msg_timestamp")
    val msgTimestamp: Long,
    val nonce: String,
    val extra: E,
)

private fun <E : EventExtra> Event<E>.toSerializableEvent(): SerializableEvent<E> =
    SerializableEvent(
        channelTypeValue = channelTypeValue,
        typeValue = typeValue,
        targetId = targetId,
        authorId = authorId,
        content = content,
        msgId = msgId,
        msgTimestamp = msgTimestamp,
        nonce = nonce,
        extra = extra,
    )

private fun <E : EventExtra> SerializableEvent<E>.toEvent(): Event<E> =
    Event(
        channelTypeValue = channelTypeValue,
        typeValue = typeValue,
        targetId = targetId,
        authorId = authorId,
        content = content,
        msgId = msgId,
        msgTimestamp = msgTimestamp,
        nonce = nonce,
        extra = extra,
    )
