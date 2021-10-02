package love.forte.test

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import love.forte.simbot.api.interaction.SuccessResult
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class ResultSerializerTest {

    @Test
    fun test() {
        val result = SuccessResult(20)

        println(Json.encodeToString(result))

    }

}