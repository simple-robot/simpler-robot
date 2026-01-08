import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder

fun main() {

    val json = Json {
        isLenient = true
        coerceInputValues = true
    }




    println(json.encodeToString(String.serializer(), "hi\nhello"))
    println(json.encodeToString(Double.serializer(), 55.0))
    println(json.encodeToString(Char.serializer(), 'a'))
    println(json.encodeToString(Int.serializer(), 1))
    println(json.encodeToString(Boolean.serializer(), true))

}
