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

import kotlinx.coroutines.Job
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.subclass
import love.forte.simbot.bot.*
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.component.Component
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.plugin.PluginFactory
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull

/**
 *
 * @author ForteScarlet
 */
class BotConfigurationTests {

    @Serializable
    @SerialName("test.foo")
    private data class FooConfiguration(var name: String? = null) : SerializableBotConfiguration()

    private data object FooComponent : Component {
        override val id: String
            get() = "test.foo"

        override val serializersModule: SerializersModule = SerializersModule {
            serializableBotConfigurationPolymorphic {
                subclass(FooConfiguration.serializer())
            }
        }
    }

    private class FooBotPlugin : AutoConfigurableBotPlugin {
        override fun configurable(configuration: SerializableBotConfiguration): Boolean =
            configuration is FooConfiguration

        override fun register(configuration: SerializableBotConfiguration): FooBot {
            configuration as? FooConfiguration ?: throw UnsupportedBotConfigurationException(configuration::class.toString())
            return FooBot(configuration.name ?: "<NULL>")
        }

        companion object Factory : PluginFactory<FooBotPlugin, Unit> {
            override val key: PluginFactory.Key = object : PluginFactory.Key {}

            override fun create(context: PluginConfigureContext, configurer: ConfigurerFunction<Unit>): FooBotPlugin =
                FooBotPlugin()

        }
    }

    private data class FooBot(override val name: String) : JobBasedBot() {
        override val job: Job = Job()
        override val coroutineContext: CoroutineContext = job
        override val id: ID = UUID.random()
        override val component: Component = FooComponent

        override fun isMe(id: ID): Boolean = id == this.id

        override suspend fun start() {
        }

        override val guildRelation: GuildRelation? get() = null
        override val groupRelation: GroupRelation? get() = null
        override val contactRelation: ContactRelation? get() = null
    }

    @Test
    fun polymorphicBotConfigurationTest() {
        val configJson = """{"component": "test.foo", "name": "forte"}"""
        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
            classDiscriminator = Component.CLASS_DISCRIMINATOR
            serializersModule = FooComponent.serializersModule
        }

        val configuration = json.decodeFromString(SerializableBotConfiguration.serializer(), configJson)
        assertIs<FooConfiguration>(configuration)

        val fooBotPlugin = FooBotPlugin()
        val bot = fooBotPlugin.tryRegister(configuration)
        assertNotNull(bot)
    }


}
