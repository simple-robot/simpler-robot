package love.forte.simbot.component.qgguild.test

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.internal.FormatLanguage
import kotlinx.serialization.modules.plus
import love.forte.simbot.bot.SerializableBotConfiguration
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.bot.config.IntentsConfig
import love.forte.simbot.component.qguild.bot.config.QGBotFileConfiguration
import love.forte.simbot.component.qguild.bot.config.ShardConfig
import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.event.Intents
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull


/**
 *
 * @author ForteScarlet
 */
class FileConfigTest {
    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        serializersModule += QQGuildComponent.messageSerializersModule
    }

    @Test
    fun intentsConfigTest() {
        assertEquals(
            IntentsConfig.Names(setOf("PublicGuildMessages")).intents,
            EventIntents.PublicGuildMessages.intents
        )
        assertEquals(
            IntentsConfig.Names(setOf("public_guild_messages")).intents,
            EventIntents.PublicGuildMessages.intents
        )
        assertEquals(
            IntentsConfig.Names(setOf("PublicGuildMessages", "Guilds")).intents,
            EventIntents.Guilds.intents + EventIntents.PublicGuildMessages.intents
        )
        assertEquals(
            IntentsConfig.Names(setOf("public_guild_messages", "guilds")).intents,
            EventIntents.Guilds.intents + EventIntents.PublicGuildMessages.intents
        )
    }

    @Test
    fun intentsConfigBitTest() {
        assertEquals(
            IntentsConfig.Bits(
                setOf(
                    EventIntents.PublicGuildMessages.INTENTS_INDEX
                )
            ).intents,
            EventIntents.PublicGuildMessages.intents
        )
        assertEquals(
            IntentsConfig.Bits(
                setOf(
                    EventIntents.Guilds.INTENTS_INDEX,
                    EventIntents.PublicGuildMessages.INTENTS_INDEX
                )
            ).intents,
            EventIntents.Guilds.intents + EventIntents.PublicGuildMessages.intents
        )
    }

    @Test
    fun botFileConfigurationTest() {
        val jsonStr = """
            {
                "component": "simbot.qqguild",
                "ticket": {
                    "appId": "appId-value",
                    "secret": "secret-value",
                    "token": "token-value"
                },
                "config": {
                    "shard": {
                        "type": "full"
                    }
                }
            }
        """.trimIndent()

        val decoded = json.decodeFromString(SerializableBotConfiguration.serializer(), jsonStr)
        assertIs<QGBotFileConfiguration>(decoded)
        assertNotNull(decoded.config)
        assertIs<ShardConfig.Full>(decoded.config!!.shardConfig)
    }

    @OptIn(InternalSerializationApi::class)
    private fun configJson(
        @FormatLanguage(value = "json", prefix = CONFIG_JSON_PREFIX, suffix = CONFIG_JSON_SUFFIX)
        json: String): String {

        return "$CONFIG_JSON_PREFIX$json$CONFIG_JSON_SUFFIX"
    }

    @Test
    fun configIntentsBitsTest() {
        val decoded = json.decodeFromString<QGBotFileConfiguration>(
            configJson(//language=json
                """{"intents": {
                    "type": "bitBased",
                    "bits": [0, 1, 30]
              }}
            """.trimIndent()))

        val intents = decoded.config?.intentsConfig?.intents
        assertNotNull(intents)
        assertEquals(
            Intents(1 shl 0) + Intents(1 shl 1) + Intents(1 shl 30),
            intents
        )
    }

    @Test
    fun configIntentsNamesTest() {
        val decoded = json.decodeFromString<QGBotFileConfiguration>(
            configJson(//language=json
                """{"intents": {
                    "type": "nameBased",
                    "names": ["Guilds", "forums_event", "GUILD_MESSAGES"]
              }}
            """.trimIndent()))

        val intents = decoded.config?.intentsConfig?.intents
        assertNotNull(intents)
        assertEquals(
            EventIntents.Guilds.intents + EventIntents.ForumsEvent.intents + EventIntents.GuildMessages.intents,
            intents
        )
    }

    @Test
    fun configIntentsRawTest() {
        val decoded = json.decodeFromString<QGBotFileConfiguration>(
            configJson(//language=json
                """{"intents": {
                    "type": "raw",
                    "intents": 123456789
              }}
            """.trimIndent()))

        val intents = decoded.config?.intentsConfig?.intents
        assertNotNull(intents)
        assertEquals(
            123456789,
            intents.value
        )
    }

    companion object {
        private const val CONFIG_JSON_PREFIX =
            "{\"ticket\":{\"appId\":\"\",\"secret\":\"\",\"token\":\"\"},\"config\":"
        private const val CONFIG_JSON_SUFFIX = "}"
    }
}
