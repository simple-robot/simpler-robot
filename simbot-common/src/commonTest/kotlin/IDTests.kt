import love.forte.simbot.id.ID
import love.forte.simbot.id.StringID.Companion.ID
import love.forte.simbot.id.UUID
import love.forte.simbot.id.UUID.Companion.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IDTests {

    @Test
    fun uuidGenerateTest() {
        println(UUID.random())
        val id = UUID.random()
        val id2 = id.toString().UUID

        assertEquals(id, id2)
        assertEquals(id as ID, id2.toString().ID as ID)
        assertEquals(id as ID, id2.copy().toString().ID as ID)
        assertTrue(id.equalsExact(id2))
        assertTrue(id.equalsExact(id.copy()))
        assertTrue(id.equalsExact(id2.copy()))
    }

}
