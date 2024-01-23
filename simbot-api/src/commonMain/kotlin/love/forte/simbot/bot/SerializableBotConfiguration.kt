/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.bot

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import love.forte.simbot.bot.NotSerializedBotConfiguration.Companion.resolveType
import love.forte.simbot.component.Component
import love.forte.simbot.resource.StringResource
import kotlin.jvm.JvmStatic

/**
 * 基于 `Kotlin Serialization` 的 bot 可序列化配置。
 * 实现 [SerializableBotConfiguration] 的第三方扩展类型必须至少标记 [Serializable]
 * 并可基于 `Kotlin Serialization` 实现字符串格式的反序列化（[kotlinx.serialization.StringFormat]）。
 *
 * 同时，在对应的 [Component.serializersModule][love.forte.simbot.component.Component.serializersModule]
 * 中提供以 [SerializableBotConfiguration] 为基准的多态序列化信息，例如：
 *
 * ```kotlin
 * @Serializable
 * @SerialName("foo")
 * private class FooBotConfiguration : SerializableBotConfiguration() {
 *     var name: String? = null
 * }
 *
 * // 多态序列化信息
 * val module = SerializersModule {
 *         serializableBotConfigurationPolymorphic {
 *             subclass(FooBotConfiguration.serializer())
 *         }
 *         // 或使用 `polymorphic(SerializableBotConfiguration::class) { ... }`
 *     }
 * ```
 *
 * [NotSerializedBotConfiguration] 是 [SerializableBotConfiguration] 的一个特殊实现，
 * 可在某些无法反序列化的情况下作为默认实现提供。
 *
 * ## 序列化器
 *
 * 目前仅建议使用下述的序列化器，因为它们支持将外层 [SerializableBotConfiguration] 的
 * `classDiscriminator` 重置为指定的 [`"component"`][Component.CLASS_DISCRIMINATOR]，
 * 而不影响实现内的其他多态类型。
 * 如果使用其他序列化器，需要使用 `type` 作为 `classDiscriminator`。
 *
 * ### JSON
 *
 * 当使用 `JSON` 序列化器时，`class` 为 [`"component"`][Component.CLASS_DISCRIMINATOR] 而不是 `type`。
 *
 * ```json
 * {
 *   "component": "aaa.bbb",
 *   ...
 * }
 * ```
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator(Component.CLASS_DISCRIMINATOR)
public abstract class SerializableBotConfiguration

/**
 * Configure polymorphic for [SerializableBotConfiguration].
 */
public inline fun SerializersModuleBuilder.serializableBotConfigurationPolymorphic(block: PolymorphicModuleBuilder<SerializableBotConfiguration>.() -> Unit) {
    polymorphic(SerializableBotConfiguration::class) {
        block(this)
    }
}

/**
 * [NotSerializedBotConfiguration] 是 [SerializableBotConfiguration] 的一个特殊实现，
 * 在部分场景中作为默认的实现目标，例如在某些无法反序列化的情况下作为默认实现提供。
 *
 * [NotSerializedBotConfiguration] 不是可序列化类型。
 *
 * @property text 配置信息源文本资源。可能来自文件或内存。
 * @property type 源文本的类型。
 * - 如果是源于文件，则应为文件扩展名（如果没有则为null），扩展名为第一个出现的 `"."` 后的所有，因此可能会出现 `"a.b.c"` 形式的内容。
 * - 其他来源：为 `null`。
 * 解析可参考使用 [resolveType]。
 */
public data class NotSerializedBotConfiguration(val text: StringResource, val type: String?) :
    SerializableBotConfiguration() {

    public companion object {
        private const val TYPE_CHAR = '.'

        /**
         * 解析一个文件名作为可提供给 [NotSerializedBotConfiguration.type] 的值。
         * 结果为第一个出现的 `"."` 后的所有，因此可能会出现 "a.b.c" 形式的内容。‘
         * 如果没有出现 `"."` 则返回 `null`。
         */
        @JvmStatic
        public fun resolveType(name: String): String? {
            val index = name.indexOf(TYPE_CHAR)
            return if (index == -1) null else name.substring(index + 1, name.length)
        }

    }
}
