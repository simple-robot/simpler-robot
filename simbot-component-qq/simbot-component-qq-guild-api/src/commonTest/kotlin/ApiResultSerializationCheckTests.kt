package test

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import love.forte.simbot.qguild.QQGuild
import love.forte.simbot.qguild.QQGuildResultSerializationException
import love.forte.simbot.qguild.api.GatewayApis
import love.forte.simbot.qguild.api.channel.GetChannelApi
import love.forte.simbot.qguild.api.checkStatus
import love.forte.simbot.qguild.api.request
import love.forte.simbot.qguild.api.requestData
import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertIs


/**
 *
 * @author ForteScarlet
 */
class ApiResultSerializationCheckTests {

    @Test
    fun statusDeserializationHtmlBadResultTest() = runTest {
        val client = HttpClient(
            MockEngine.invoke { // request ->
                respond(
                    content = ByteReadChannel("""<html></html>"""),
                    status = HttpStatusCode.BadRequest,
//                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }) {

        }

        val resp = GatewayApis.Normal.request(client, "test")
        val ex = assertFails { checkStatus(resp.bodyAsText(), QQGuild.DefaultJson, resp.status, resp) }
        assertIs<QQGuildResultSerializationException>(ex)
        assertIs<SerializationException>(ex.cause ?: ex.suppressedExceptions.firstOrNull())
    }

    @Test
    fun statusDeserializationHtmlOKResultTest() = runTest {
        val client = HttpClient(
            MockEngine.invoke { // request ->
                respond(
                    content = ByteReadChannel("""<html></html>"""),
                    status = HttpStatusCode.OK,
                )
            }) {

        }

        val ex = assertFails { GetChannelApi.create("test").requestData(client, "test") }
        assertIs<QQGuildResultSerializationException>(ex)
        assertIs<SerializationException>(ex.cause ?: ex.suppressedExceptions.firstOrNull())
    }

}
