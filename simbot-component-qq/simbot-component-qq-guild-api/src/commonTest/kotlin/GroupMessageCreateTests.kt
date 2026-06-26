package test

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import love.forte.simbot.qguild.event.GroupMessageAuthorRole
import love.forte.simbot.qguild.event.GroupMessageCreate
import love.forte.simbot.qguild.event.resolveDispatchSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull

/**
 *
 * @author ForteScarlet
 */
class GroupMessageCreateTests {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun groupMessageCreateDispatchResolveTest() {
        val raw = """
            {
              "op": 0,
              "s": 1,
              "t": "GROUP_MESSAGE_CREATE",
              "id": "event-id",
              "d": {
                "author": {
                  "member_openid": "member-openid"
                },
                "content": "hello",
                "group_openid": "group-openid",
                "id": "message-id",
                "timestamp": "2023-11-06T13:37:18+08:00"
              }
            }
        """.trimIndent()

        val element = json.decodeFromString(JsonElement.serializer(), raw).jsonObject
        val serializer = assertNotNull(resolveDispatchSerializer(element))
        val event = assertIs<GroupMessageCreate>(json.decodeFromJsonElement(serializer, element))

        assertEquals("event-id", event.id)
        assertEquals(1, event.seq)
        assertEquals("message-id", event.data.id)
        assertEquals("group-openid", event.data.groupOpenid)
        assertEquals("member-openid", event.data.author.memberOpenid)
        assertEquals(GroupMessageAuthorRole.MEMBER, event.data.author.memberRole)
        assertFalse(event.data.author.bot)
    }
}
