/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.component.kook


import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import love.forte.simbot.bot.serializableBotConfigurationPolymorphic
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.component.Component
import love.forte.simbot.component.ComponentConfigureContext
import love.forte.simbot.component.ComponentFactory
import love.forte.simbot.component.ComponentFactoryProvider
import love.forte.simbot.component.kook.bot.KookBotVerifyInfoConfiguration
import love.forte.simbot.component.kook.message.KookMessageElement
import love.forte.simbot.component.kook.message.includeKookMessageElements
import love.forte.simbot.kook.ExperimentalKookApi
import love.forte.simbot.kook.objects.kmd.KMarkdown
import love.forte.simbot.kook.objects.kmd.RawValueKMarkdown
import love.forte.simbot.message.messageElementPolymorphic
import kotlin.jvm.JvmStatic

/**
 * KOOK 组件的 [Component] 实现。
 *
 * @author ForteScarlet
 */
public class KookComponent : Component {
    /**
     * 组件的唯一标识ID。
     */
    override val id: String
        get() = ID_VALUE

    /**
     * Kook 组件中所涉及到的序列化模块。
     *
     */
    override val serializersModule: SerializersModule
        get() = messageSerializersModule

    override fun toString(): String = TO_STRING_VALUE

    override fun equals(other: Any?): Boolean {
        if (other === this) return true

        return other is KookComponent
    }

    override fun hashCode(): Int {
        return ID_VALUE_HASH_CODE
    }

    /**
     * 组件 [KookComponent] 的注册器。
     *
     */
    public companion object Factory : ComponentFactory<KookComponent, KookComponentConfiguration> {

        /**
         * 组件 [KookComponent] 的ID
         */
        @Suppress("MemberVisibilityCanBePrivate")
        public const val ID_VALUE: String = "simbot.kook"

        private val ID_VALUE_HASH_CODE = ID_VALUE.hashCode()
        private const val TO_STRING_VALUE = "KookComponent(id=$ID_VALUE)"


        /**
         * 注册器的唯一标识。
         */
        override val key: ComponentFactory.Key = object : ComponentFactory.Key {}

        override fun create(
            context: ComponentConfigureContext,
            configurer: ConfigurerFunction<KookComponentConfiguration>
        ): KookComponent {
            KookComponentConfigurationInstance.invokeBy(configurer)
            return KookComponent()
        }

        /**
         * [KookComponent] 组件所使用的消息序列化信息。
         */
        @OptIn(ExperimentalKookApi::class)
        @get:JvmStatic
        public val messageSerializersModule: SerializersModule = SerializersModule {
            polymorphic(KookMessageElement::class) {
                includeKookMessageElements()
            }

            messageElementPolymorphic {
                includeKookMessageElements()
            }

            polymorphic(KMarkdown::class) {
                subclass(RawValueKMarkdown::class, RawValueKMarkdown.serializer())
            }

            serializableBotConfigurationPolymorphic {
                subclass(KookBotVerifyInfoConfiguration.serializer())
            }
        }

    }
}

/**
 *
 * [KookComponent] 注册时所使用的配置类。
 *
 * 目前对于 KOOK 组件来讲没有需要配置的内容，因此 [KookComponentConfiguration] 中暂无可配置属性。
 *
 */
public class KookComponentConfiguration internal constructor()

private val KookComponentConfigurationInstance = KookComponentConfiguration()

/**
 * [KookComponent] 的注册器工厂，用于支持组件的自动加载。
 *
 */
public class KookComponentFactoryProvider :
    ComponentFactoryProvider<KookComponentConfiguration> {
    override fun provide(): ComponentFactory<*, KookComponentConfiguration> =
        KookComponent
}
