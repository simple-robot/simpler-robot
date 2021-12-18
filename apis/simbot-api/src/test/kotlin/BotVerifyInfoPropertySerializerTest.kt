import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromStringMap
import love.forte.simbot.asBotVerifyInfo


@Serializable
data class Hi(val name: String, val age: Int)


@OptIn(ExperimentalSerializationApi::class)
fun main() {
    val info = mapOf("name" to "forte", "age" to "123").asBotVerifyInfo("abc")

    val p = Properties(EmptySerializersModule)

    val hi = p.decodeFromStringMap(Hi.serializer(), info)

    println(hi)
}