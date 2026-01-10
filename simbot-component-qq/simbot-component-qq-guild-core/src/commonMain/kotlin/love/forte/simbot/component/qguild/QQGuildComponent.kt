/*
 * Copyright (c) 2021-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild

import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import love.forte.simbot.bot.serializableBotConfigurationPolymorphic
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.component.*
import love.forte.simbot.component.qguild.bot.config.QGBotFileConfiguration
import love.forte.simbot.component.qguild.bot.config.TicketConfiguration
import love.forte.simbot.component.qguild.message.*
import love.forte.simbot.message.At
import love.forte.simbot.message.Message
import kotlin.jvm.JvmStatic


/**
 * QQ频道实现simbot相关组件的基本组件信息，可以用来获取组件ID、组件所用序列化器等等。
 *
 * @author ForteScarlet
 */
public class QQGuildComponent : Component {
    override val id: String
        get() = ID_VALUE

    override val serializersModule: SerializersModule
        get() = messageSerializersModule

    override fun toString(): String = TO_STRING_VALUE

    override fun equals(other: Any?): Boolean {
        return when {
            other === this -> true
            other !is QQGuildComponent -> false
            else -> other.id == id
        }
    }

    override fun hashCode(): Int = ID_VALUE_HASH

    /**
     * 组件 [QQGuildComponent] 的注册器。
     */
    public companion object Factory : ComponentFactory<QQGuildComponent, QQGuildComponentConfiguration> {
        /**
         * 组件的ID标识常量。
         */
        @Suppress("MemberVisibilityCanBePrivate")
        public const val ID_VALUE: String = "simbot.qqguild"

        private val ID_VALUE_HASH = ID_VALUE.hashCode()
        private const val TO_STRING_VALUE: String = "QQGuildComponent(id=$ID_VALUE)"


        /**
         * 在 [QGMessageContent] 中提及 `channel` 的时候，被转化的 [At] 所使用的 [At.type]。
         *
         * ```kotlin
         * // 在QQ频道相关事件中
         * val at = ...
         * if (at.type = AT_CHANNEL_TYPE) {
         *    // at是提及的channel类型
         * }
         * ```
         *
         * _需要注意的是在不同的组件中 at type 是可能重复的。_
         *
         */
        public const val AT_CHANNEL_TYPE: String = "channel"

        /**
         * 作为注册器时的标识。
         */
        override val key: ComponentFactory.Key = object : ComponentFactory.Key {}

        /**
         * 创建 [QQGuildComponent].
         */
        override fun create(
            context: ComponentConfigureContext,
            configurer: ConfigurerFunction<QQGuildComponentConfiguration>
        ): QQGuildComponent {
            QQGuildComponentConfiguration().invokeBy(configurer)

            return QQGuildComponent()
        }

        /**
         * QQ频道组件所使用到的特殊消息类型序列化信息。
         */
        @JvmStatic
        public val messageSerializersModule: SerializersModule = SerializersModule {
            fun PolymorphicModuleBuilder<QGMessageElement>.subclass0() {
                subclass(QGArk.serializer())
                subclass(QGMarkdown.serializer())
                subclass(QGAttachmentMessage.serializer())
                subclass(QGReplyTo.serializer())
                subclass(QGContentText.serializer())
                subclass(QGReference.serializer())
                subclass(QGEmbed.serializer())
                subclass(QGMedia.serializer())

                @Suppress("DEPRECATION")
                subclass(QGAtChannel.serializer())
            }

            polymorphic(QGMessageElement::class) {
                subclass0()
            }

            polymorphic(Message.Element::class) {
                subclass0()
            }

            serializableBotConfigurationPolymorphic {
                subclass(QGBotFileConfiguration.serializer())
            }

            polymorphic(TicketConfiguration::class) {
                defaultDeserializer { TicketConfiguration.Plain.serializer() }
            }
        }


    }

}


/**
 * 用于 [QQGuildComponent] 组件注册时的配置类信息。
 *
 * _Note: 目前 [QQGuildComponent] 暂无可配置内容。_
 *
 */
public class QQGuildComponentConfiguration

/**
 * [QQGuildComponent] 的注册器工厂，用于支持组件的自动加载。
 *
 */
public class QQGuildComponentFactoryProvider :
    ComponentFactoryProvider<QQGuildComponentConfiguration> {
    override fun loadConfigures(): Sequence<QQGuildComponentFactoryConfigurerProvider>? =
        loadQGComponentConfigurers()

    override fun provide(): ComponentFactory<*, QQGuildComponentConfiguration> = QQGuildComponent
}

/**
 * 用于 [QQGuildComponentFactoryProvider.loadConfigurers] 的可加载的额外配置器。
 *
 */
public fun interface QQGuildComponentFactoryConfigurerProvider :
    ComponentFactoryConfigurerProvider<QQGuildComponentConfiguration>


internal expect fun loadQGComponentConfigurers(): Sequence<QQGuildComponentFactoryConfigurerProvider>?
