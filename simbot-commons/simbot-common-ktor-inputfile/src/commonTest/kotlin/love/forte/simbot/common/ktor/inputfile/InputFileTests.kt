/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.common.ktor.inputfile

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs


/**
 *
 * @author ForteScarlet
 */
class InputFileTests {
    companion object {
        private const val CLRF = "\r\n"

        internal suspend inline fun assertInputFileByMockClient(
            length: Int, assertText: String,
            inputFile: InputFile,
        ) {
            HttpClient(MockEngine { req ->
                val body = req.body
                assertIs<MultiPartFormDataContent>(body)
                val boundary = body.boundary
                val bodyText = body.toByteArray().decodeToString()

                assertEquals(
                    "--$boundary$CLRF" +
                            "Content-Disposition: form-data; name=file$CLRF" +
                            "Content-Length: $length$CLRF" +
                            CLRF +
                            "$assertText$CLRF" +
                            "--$boundary--$CLRF",
                    bodyText
                )

                respondOk()
            }).use {
                it.post("") {
                    setBody(MultiPartFormDataContent(formData {
                        inputFile.includeTo("file", formBuilder = this)
                    }))
                }
            }
        }
    }

    @Test
    fun bytesInputFile() = runTest {
        val realText = "Hello, World"
        val bytes = realText.toByteArray()

        assertInputFileByMockClient(bytes.size, realText, InputFile(bytes))
    }

    @Test
    fun inputProviderInputFile() = runTest {
        val realText = "Hello, World"
        val bytes = realText.toByteArray()

        assertInputFileByMockClient(bytes.size, realText, InputFile(InputProvider(bytes.size.toLong()) { ByteReadPacket(bytes) }))
    }

    @Test
    fun channelProviderInputFile() = runTest {
        val realText = "Hello, World"
        val bytes = realText.toByteArray()

        assertInputFileByMockClient(bytes.size, realText, InputFile(ChannelProvider(bytes.size.toLong()) { ByteReadChannel(realText) }))
    }

    @Test
    fun byteReadPacketInputFile() = runTest {
        val realText = "Hello, World"
        val bytes = realText.toByteArray()

        assertInputFileByMockClient(bytes.size, realText, InputFile(ByteReadPacket(bytes)))
    }

}
