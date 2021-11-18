package love.forte.test

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import love.forte.simbot.*
import kotlin.test.Test

@Serializable
data class User(
    val id: DoubleID,
    val age: Int,
)

/**
 *
 * @author ForteScarlet
 */
class IDTest {

    @Test
    fun idSerializerTest() {
        // val user = User(100L.ID, 24)
        val user = User(5.2355.ID, 24)

        println(user)

        val json = Json
        println(json.encodeToString(user))


    }


}