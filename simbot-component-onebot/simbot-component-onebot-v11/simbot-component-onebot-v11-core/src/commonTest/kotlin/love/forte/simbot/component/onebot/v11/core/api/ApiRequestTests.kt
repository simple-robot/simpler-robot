/*
 *     Copyright (c) 2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.component.onebot.v11.core.api

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor
import love.forte.simbot.component.onebot.v11.core.OneBot11
import love.forte.simbot.component.onebot.v11.message.segment.OneBotFace
import love.forte.simbot.component.onebot.v11.message.segment.OneBotText
import love.forte.simbot.logger.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 *
 * @author ForteScarlet
 */
@OptIn(ApiResultConstructor::class)
class ApiRequestTests {
    private val logger = LoggerFactory.getLogger("love.forte.simbot.component.onebot.ApiRequestTests")

    private inline fun <R : Any> createClient(
        action: String,
        crossinline respDataSer: () -> KSerializer<R>,
        crossinline respData: () -> R
    ): HttpClient {
        return HttpClient(
            MockEngine { req ->
                assertEquals(action, req.url.segments.last())
                logger.debug("Data: {}", req.body.toByteArray().decodeToString())
                respondOk(
                    OneBot11.DefaultJson.encodeToString(
                        OneBotApiResult.serializer(respDataSer()),
                        OneBotApiResult(
                            OneBotApiResult.RETCODE_SUCCESS,
                            null,
                            respData()
                        )
                    )
                )
            }
        )
    }

    @Test
    fun sendPrivateMsgApiTest() = runTest {
        val client = createClient(
            "send_private_msg",
            respDataSer = { SendMsgResult.serializer() },
            respData = { SendMsgResult(123.ID) }
        )

        val data = SendPrivateMsgApi.create(
            123.ID,
            OneBotMessageOutgoing.create(
                listOf(
                    OneBotText.create("Hello, World"),
                    OneBotFace.create(555.ID)
                )
            ),
        ).requestData(
            client,
            "http://127.0.0.1:8080/",
        )

        assertEquals("123", data.messageId.literal)
    }

    @Test
    fun customGetApiTest() = runTest {
        @Serializable
        data class CustomResult(val name: String)
        class MyCustomApi : OneBotApi<CustomResult> {
            override val method: HttpMethod = HttpMethod.Get
            override val action: String = "custom_action"
            override val body: Any? = null
            override val resultDeserializer: DeserializationStrategy<CustomResult> = CustomResult.serializer()
            override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<CustomResult>> =
                OneBotApiResult.serializer(CustomResult.serializer())

            override fun resolveUrlExtensions(urlBuilder: URLBuilder) {
                urlBuilder.parameters.append("name", "forte")
            }
        }

        val client = createClient(
            "custom_action",
            respDataSer = { CustomResult.serializer() },
            respData = { CustomResult("forte") }
        )

        val resp = MyCustomApi().request(client, "http://127.0.0.1:8080/")
        assertEquals(HttpMethod.Get, resp.request.method)
        assertEquals("forte", resp.request.url.parameters["name"])

        val data = MyCustomApi().requestData(client, "http://127.0.0.1:8080/")
        assertEquals("forte", data.name)
    }
}
