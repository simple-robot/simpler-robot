package love.test.guild.role

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import love.forte.simbot.component.kaiheila.api.v3.guild.role.GuildRoleCreateResp
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
            "role_id": 5712,
            "name": "新角色1",
            "color": 3066993,
            "position": 1,
            "hoist": 0,
            "mentionable": 0,
            "permissions": 147643914
        } 
        """.trimIndent()

        val resp = khlJson.decodeFromString<GuildRoleCreateResp>(json)

        println(resp)

    }


}