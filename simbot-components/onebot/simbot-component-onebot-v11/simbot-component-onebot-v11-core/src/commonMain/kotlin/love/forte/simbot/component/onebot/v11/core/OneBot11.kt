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

package love.forte.simbot.component.onebot.v11.core

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import love.forte.simbot.annotations.FragileSimbotAPI
import love.forte.simbot.bot.serializableBotConfigurationPolymorphic
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotSerializableConfiguration
import love.forte.simbot.component.onebot.v11.message.OneBotMessageElement
import love.forte.simbot.component.onebot.v11.message.includeAllComponentMessageElementImpls
import love.forte.simbot.component.onebot.v11.message.includeAllOneBotSegmentImpls
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import love.forte.simbot.component.onebot.v11.message.segment.OneBotUnknownSegment
import love.forte.simbot.component.onebot.v11.message.segment.OneBotUnknownSegmentDeserializer
import love.forte.simbot.component.onebot.v11.message.segment.OneBotUnknownSegmentPolymorphicSerializer
import love.forte.simbot.message.messageElementPolymorphic
import kotlin.jvm.JvmField

/**
 * Some OneBot11 constants.
 *
 * @author ForteScarlet
 */
public object OneBot11 {
    /**
     * 添加了 [messageElementPolymorphic]、[OneBotMessageElement]
     * 和 [OneBotMessageSegment] 的多态序列化信息的
     * [SerializersModule]。
     */
    @OptIn(FragileSimbotAPI::class)
    @JvmField
    public val serializersModule: SerializersModule = SerializersModule {
        messageElementPolymorphic {
            includeAllComponentMessageElementImpls()
        }
        polymorphic(OneBotMessageElement::class) {
            includeAllComponentMessageElementImpls()
        }
        polymorphic(OneBotMessageSegment::class) {
            includeAllOneBotSegmentImpls()

            defaultDeserializer { OneBotUnknownSegmentDeserializer }
        }
        polymorphicDefaultSerializer(OneBotMessageSegment::class) { base ->
            if (base is OneBotUnknownSegment) {
                OneBotUnknownSegmentPolymorphicSerializer(base.type)
            } else {
                null
            }
        }
        serializableBotConfigurationPolymorphic {
            subclass(OneBotBotSerializableConfiguration.serializer())
        }
    }

    /**
     * 一个默认的 [Json] 序列化器。
     * 会在部分内部API中使用。
     */
    @OptIn(ExperimentalSerializationApi::class)
    @JvmField
    public val DefaultJson: Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        allowSpecialFloatingPointValues = true
        prettyPrint = false
        serializersModule = OneBot11.serializersModule
        allowTrailingComma = true
        // since 1.1.1
        coerceInputValues = true
    }
}
