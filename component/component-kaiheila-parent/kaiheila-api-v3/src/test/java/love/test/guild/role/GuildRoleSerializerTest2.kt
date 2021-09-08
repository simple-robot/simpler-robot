package love.test.guild.role

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import love.forte.simbot.component.kaiheila.`object`.Permissions
import love.forte.simbot.component.kaiheila.khlJson
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class GuildRoleSerializerTest2 {


    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun serializeTest() {
        val json = """
            {
            "guild_id": "xxxx",
            "roles": [1,2,3,4,5]
        } 
        """.trimIndent()

        val resp = khlJson.decodeFromString<PermTest1>(json)

        println(resp)
        resp.roles.forEach { println(it) }

    }


}

@Serializable
public data class PermTest1(
    @SerialName("guild_id") val guildId: String,
    val roles: List<Permissions>,
)