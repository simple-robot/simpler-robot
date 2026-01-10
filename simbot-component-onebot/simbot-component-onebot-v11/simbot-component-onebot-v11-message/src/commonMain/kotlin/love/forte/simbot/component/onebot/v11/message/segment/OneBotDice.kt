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

/**
 * [掷骰子魔法表情](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E6%8E%B7%E9%AA%B0%E5%AD%90%E9%AD%94%E6%B3%95%E8%A1%A8%E6%83%85)
 *
 */
@Serializable
@SerialName(OneBotDice.TYPE)
public object OneBotDice :
    OneBotMessageSegment,
    OneBotMessageSegmentElementResolver {
    public const val TYPE: String = "dice"

    override val data: Unit
        get() = Unit

    override fun toElement(): OneBotMessageSegmentElement = Element

    @Serializable
    @SerialName("ob11.segment.dice")
    public data object Element : OneBotMessageSegmentElement() {
        override val segment: OneBotDice
            get() = OneBotDice
    }
}
