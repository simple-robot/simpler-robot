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

package love.forte.simbot.component.onebot.v11.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.builtins.ListSerializer
import love.forte.simbot.message.Message


/**
 * OneBot组件中的 [Message.Element] 统一类型。
 *
 * [OneBotMessageElement] 接口本身由内部实现，不可用于第三方自定义实现。
 *
 * @author ForteScarlet
 */
public interface OneBotMessageElement : Message.Element

/**
 * 可用于便捷地直接对一个
 * [OneBotMessageElement] 列表进行序列化地序列化器。
 *
 * 注意：序列化/反序列化时需要确保模型内添加了 `OneBot11Component.SerializersModule`,
 * 因为这本质上是一个列表中的**多态**序列化器。
 */
public object OneBotMessageElementSerializer : KSerializer<List<OneBotMessageElement>> by
ListSerializer(PolymorphicSerializer(OneBotMessageElement::class))
