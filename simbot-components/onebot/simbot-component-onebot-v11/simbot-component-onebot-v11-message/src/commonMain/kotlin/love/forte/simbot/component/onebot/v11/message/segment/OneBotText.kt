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

package love.forte.simbot.component.onebot.v11.message.segment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.message.PlainText
import love.forte.simbot.message.Text
import kotlin.jvm.JvmStatic

/**
 * [纯文本](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E7%BA%AF%E6%96%87%E6%9C%AC)
 *
 * 可用于发送，与直接使用 [Text] 无区别。
 * 在接收消息时不会被使用，而是始终被解析为 [Text]。
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(OneBotText.TYPE)
public class OneBotText private constructor(override val data: Data) :
    OneBotMessageSegment,
    OneBotMessageSegmentElementResolver {

    public companion object Factory {
        public const val TYPE: String = "text"

        @JvmStatic
        public fun create(text: String): OneBotText =
            OneBotText(Data(text))
    }

    @Serializable
    public data class Data internal constructor(val text: String)

    override fun toElement(): OneBotMessageSegmentElement =
        Element(this)

    @Serializable
    @SerialName("ob11.segment.text")
    public data class Element(override val segment: OneBotText) :
        OneBotMessageSegmentElement(), PlainText {
        override val text: String
            get() = segment.data.text
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OneBotText) return false

        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String {
        return "OneBotText(text=${data.text})"
    }
}
