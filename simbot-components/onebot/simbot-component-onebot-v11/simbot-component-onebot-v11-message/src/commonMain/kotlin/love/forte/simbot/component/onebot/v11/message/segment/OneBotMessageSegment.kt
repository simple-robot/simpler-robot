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

@file:JvmName("OneBotMessageSegments")

package love.forte.simbot.component.onebot.v11.message.segment

import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import love.forte.simbot.component.onebot.v11.message.OneBotMessageElement
import love.forte.simbot.component.onebot.v11.message.resolveToMessageElement
import love.forte.simbot.message.*
import kotlin.jvm.JvmName

/**
 * OneBot11的 [消息段](https://github.com/botuniverse/onebot-11/blob/master/message/array.md)
 * 类型定义。
 * 其中，消息段的类型 `type` 由多态序列化类型名决定，而 `data` 的类型则由实现者决定。
 *
 * 定义的可序列化子类型会被统一以 [OneBotMessageSegment] 的多态类型被添加到 `OneBot11Component.serializersModule` 中。
 * (通过
 * [includeAllOneBotSegmentImpls][love.forte.simbot.component.onebot.v11.message.includeAllOneBotSegmentImpls]
 * )
 *
 * ## Message Element
 * 可以通过 [OneBotMessageSegmentElement] 将 [OneBotMessageSegment]
 * 包装为 [Message.Element] 的子类型。
 *
 * @author ForteScarlet
 */
public interface OneBotMessageSegment {
    /**
     * 消息段的内容。
     */
    public val data: Any?
}

/**
 * 由 [OneBotMessageSegment] 类型的实现类实现，
 * 表示它可以将自己转化为一个独特的 [OneBotMessageSegmentElement]
 * 类型，例如某个实现了 [MentionMessage] 的 [OneBotMessageSegmentElement] 实例。
 *
 * 其他没有实现 [OneBotMessageSegmentElementResolver] 的类型则可以直接使用
 * [OneBotMessageSegment.toElement] 包装。
 */
public interface OneBotMessageSegmentElementResolver {
    public fun toElement(): OneBotMessageSegmentElement
}

/**
 * 将 [OneBotMessageSegment] 转化或包装为 [OneBotMessageSegmentElement]。
 * 这并不是在收到消息时用于转换类型的直接方法，
 * 而是 [OneBotMessageSegment.resolveToMessageElement] ——
 * 后者会将一些与标准消息元素无差别的类型直接转为标准消息类型，
 * 例如将 [OneBotAt] 转为 [At] 或 [AtAll]、[OneBotFace] 转为 [Face]。
 */
public fun OneBotMessageSegment.toElement(): OneBotMessageSegmentElement =
    when (this) {
        is OneBotMessageSegmentElementResolver -> toElement()
        else -> DefaultOneBotMessageSegmentElement(this)
    }

/**
 * 消息段 [OneBotMessageSegment] 作为 [OneBotMessageElement] 的实现。
 *
 * [OneBotMessageSegment] 不能直接实现 [Message.Element]，
 * 因为消息段的多态序列化信息很可能会与其他序列化信息产生冲突（比如 `at`，没有特殊的前缀，容易引发歧义）。
 *
 * 因此需要使用 [OneBotMessageSegmentElement] 作为其包装。
 * 可以使用 [OneBotMessageSegment.toElement] 或此类的构造函数构建它。
 */
public abstract class OneBotMessageSegmentElement : OneBotMessageElement {
    public abstract val segment: OneBotMessageSegment
}

/**
 * [OneBotMessageSegmentElement] 的普通默认实现。
 */
@Serializable
@SerialName("ob11.segment.default")
public data class DefaultOneBotMessageSegmentElement(
    override val segment: OneBotMessageSegment
) : OneBotMessageSegmentElement()


/**
 * 向 [MessagesBuilder] 中直接添加一个 [OneBotMessageSegment]。
 *
 * @see OneBotMessageSegment.toElement
 */
public fun MessagesBuilder.add(segment: OneBotMessageSegment): MessagesBuilder =
    add(segment.toElement())

/**
 * 可用于便捷地直接对一个
 * [OneBotMessageSegment] 列表进行序列化地序列化器。
 *
 * 注意：序列化/反序列化时需要确保模型内添加了 `OneBot11.DefaultJson.`,
 * 因为这本质上是一个列表中的**多态**序列化器。
 */
public object OneBotMessageSegmentSerializer : KSerializer<List<OneBotMessageSegment>> by
ListSerializer(PolymorphicSerializer(OneBotMessageSegment::class))

/**
 * 判断 [this] 的类型是 [OneBotMessageSegmentElement]
 * 并且 [segment][OneBotMessageSegmentElement.segment]
 * 的类型是 [T]。
 *
 * @see oneBotSegment
 * @see oneBotSegmentOrNull
 */
public inline fun <reified T : OneBotMessageSegment> Message.Element.isOneBotSegment(): Boolean =
    this is OneBotMessageSegmentElement && segment is T

/**
 * 如果[this] 的类型是 [OneBotMessageSegmentElement]
 * 并且 [segment][OneBotMessageSegmentElement.segment]，则获取到对应的 [T] 结果，
 * 否则抛出 [ClassCastException]。
 *
 * @see isOneBotSegment
 * @see oneBotSegmentOrNull
 * @throws ClassCastException 如果类型不符
 */
public inline fun <reified T : OneBotMessageSegment> Message.Element.oneBotSegment(): T =
    (this as OneBotMessageSegmentElement).segment as T

/**
 * 如果[this] 的类型是 [OneBotMessageSegmentElement]
 * 并且 [segment][OneBotMessageSegmentElement.segment]，则获取到对应的 [T] 结果，
 * 否则得到 `null`。
 *
 * @see oneBotSegment
 * @see isOneBotSegment
 */
public inline fun <reified T : OneBotMessageSegment> Message.Element.oneBotSegmentOrNull(): T? =
    (this as? OneBotMessageSegmentElement)?.segment as? T
