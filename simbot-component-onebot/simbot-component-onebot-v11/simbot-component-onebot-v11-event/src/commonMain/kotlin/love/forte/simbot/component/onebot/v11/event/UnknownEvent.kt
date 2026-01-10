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

package love.forte.simbot.component.onebot.v11.event

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonObject
import love.forte.simbot.annotations.FragileSimbotAPI
import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.common.annotations.InternalOneBotAPI

/**
 * 用于“兜底”的 [RawEvent] 类型实现。
 * 当出现了尚未支持或某种未知的事件体，无法对应到任何现有的已定义结构时，
 * 则应当将其解析并包装为 [UnknownEvent]。
 *
 * [UnknownEvent] 要求事件体必须包括 [time], [selfId] 和 [postType]。
 *
 * 如果 [UnknownEvent] 是由于某些异常而产生（例如原本事件进行序列化但是失败了），
 * 那么失败的原因则会通过 [reason] 提供。
 *
 * ### 内部构造
 *
 * [UnknownEvent] 应当始终由内部使用、构造，
 * 不要在其他地方自行构造 [UnknownEvent]，
 * 它的构造函数不保证任何源码或二进制兼容。
 *
 * ### FragileAPI
 *
 * 这是一个具有**特殊规则**的事件类型。
 * 随着版本地更新，[UnknownEvent] 中可能出现的事件类型会越来越少。
 * 因此此类型只适用于“兜底”，不应过度依赖。
 *
 * @author ForteScarlet
 */
@FragileSimbotAPI
public class UnknownEvent @InternalOneBotAPI constructor(
    override val time: Long,
    override val selfId: LongID,
    override val postType: String,
    /**
     * 原始的JSON字符串，
     * 也是判断 [UnknownEvent] 之间是否相同的**唯一依据**。
     */
    public val raw: String,

    /**
     * [raw] 对应解析的 [JsonObject] 对象。
     */
    public val rawJson: JsonObject,

    /**
     * 如果是由于异常而产生，则此处为异常的原因。
     * 通常会是 [SerializationException]。
     */
    public val reason: Throwable? = null,

) : RawEvent {
    override fun toString(): String =
        "UnknownEvent(time=$time, selfId=$selfId, postType='$postType')"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnknownEvent) return false

        if (raw != other.raw) return false

        return true
    }

    override fun hashCode(): Int {
        return raw.hashCode()
    }
}
