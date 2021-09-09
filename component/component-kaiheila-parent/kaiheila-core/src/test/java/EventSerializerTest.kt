import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class EventSerializerTest {

    @OptIn(ExperimentalSerializationApi::class)
    @org.junit.jupiter.api.Test
    fun test1() {
        val json = "{\"name\":\"Forte\",\"my_age\":24}"
        // val jsonStr = Json.encodeToString(Man("Forte", 24))

        Json.decodeFromString<Man>(json).also { println(it) }


    }


}


public interface Human {
    @SerialName("my_age")
    val myAge: Int
}

@Serializable
public data class Man(val name: String, @SerialName("my_age") override val myAge: Int) : Human