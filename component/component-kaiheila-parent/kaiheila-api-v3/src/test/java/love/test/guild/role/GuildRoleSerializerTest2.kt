/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.test.guild.role

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import love.forte.simbot.component.kaiheila.khlJson
import love.forte.simbot.component.kaiheila.objects.Permissions
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