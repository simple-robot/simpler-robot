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
