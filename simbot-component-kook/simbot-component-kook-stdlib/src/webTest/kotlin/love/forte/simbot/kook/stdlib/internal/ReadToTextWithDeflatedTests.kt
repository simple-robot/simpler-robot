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

package love.forte.simbot.kook.stdlib.internal

import io.ktor.websocket.*
import js.buffer.ArrayBuffer
import js.core.JsPrimitives.toKotlinUByte
import js.typedarrays.Uint8Array
import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.InternalSimbotAPI
import web.blob.Blob
import web.compression.CompressionFormat
import web.compression.CompressionStream
import web.compression.deflate
import web.encoding.TextEncoder
import web.streams.ReadableWritablePair
import web.streams.read
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.toJsArray
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [Frame.Binary.readToTextWithDeflated] function in web platform.
 *
 * This test suite verifies the deflate decompression functionality
 * used for processing binary WebSocket frames from Kook API.
 */
@OptIn(InternalSimbotAPI::class, ExperimentalWasmJsInterop::class)
class ReadToTextWithDeflatedTests {

    /**
     * Helper function to compress text using deflate algorithm.
     * This creates test data that can be decompressed by [readToTextWithDeflated].
     */
    private suspend fun compressWithDeflate(text: String): ByteArray {
        val encoder = TextEncoder()
        val data = encoder.encode(text)
        val blob = Blob(arrayOf(data).toJsArray())

        val compressionStream = CompressionStream(CompressionFormat.deflate)
        val compressedStream = blob.stream().pipeThrough(
            compressionStream.unsafeCast<ReadableWritablePair<Uint8Array<ArrayBuffer>, Uint8Array<ArrayBuffer>>>()
        )

        val reader = compressedStream.getReader()
        val chunks = mutableListOf<Uint8Array<ArrayBuffer>>()
        var result = reader.read()

        while (!result.done) {
            val rv = result.value
            if (rv != null) {
                chunks.add(rv)
            }
            result = reader.read()
        }

        val totalLength = chunks.sumOf { it.length }
        val uint8Array = Uint8Array<ArrayBuffer>(totalLength)
        var offset = 0
        chunks.forEach {
            uint8Array.set(it, offset)
            offset += it.length
        }

        return ByteArray(uint8Array.length) { uint8Array[it].toKotlinUByte().toByte() }
    }

    /**
     * Helper function to create a Frame.Binary from compressed data.
     */
    private fun createBinaryFrame(compressedData: ByteArray): Frame.Binary {
        return Frame.Binary(fin = true, data = compressedData)
    }

    // ==================== Basic Functionality Tests ====================

    /**
     * Test decompression of simple ASCII text.
     */
    @Test
    fun testDecompressSimpleText() = runTest {
        val originalText = "Hello, World!"
        val compressedData = compressWithDeflate(originalText)
        val frame = createBinaryFrame(compressedData)

        val result = frame.readToTextWithDeflated()

        assertEquals(originalText, result)
    }

    /**
     * Test decompression of empty string.
     */
    @Test
    fun testDecompressEmptyString() = runTest {
        val originalText = ""
        val compressedData = compressWithDeflate(originalText)
        val frame = createBinaryFrame(compressedData)

        val result = frame.readToTextWithDeflated()

        assertEquals(originalText, result)
    }

    /**
     * Test decompression of single character.
     */
    @Test
    fun testDecompressSingleCharacter() = runTest {
        val originalText = "A"
        val compressedData = compressWithDeflate(originalText)
        val frame = createBinaryFrame(compressedData)

        val result = frame.readToTextWithDeflated()

        assertEquals(originalText, result)
    }

    // ==================== JSON Data Tests ====================

    /**
     * Test decompression of JSON object (common use case for Kook events).
     */
    @Test
    fun testDecompressJsonObject() = runTest {
        val originalText = """{"s":0,"d":{"code":0},"sn":1}"""
        val compressedData = compressWithDeflate(originalText)
        val frame = createBinaryFrame(compressedData)

        val result = frame.readToTextWithDeflated()

        assertEquals(originalText, result)
    }

    /**
     * Test decompression of Kook event signal format.
     */
    @Test
    fun testDecompressKookEventSignal() = runTest {
        val originalText = buildString {
            append("""{"s":0,"d":{"channel_type":"GROUP","type":1,""")
            append(""""target_id":"123","author_id":"456","content":"test",""")
            append(""""msg_id":"789","msg_timestamp":1234567890,"nonce":"abc",""")
            append(""""extra":{"type":1,"guild_id":"guild123"}},""")
            append(""""sn":42}""")
        }
        val compressedData = compressWithDeflate(originalText)
        val frame = createBinaryFrame(compressedData)

        val result = frame.readToTextWithDeflated()

        assertEquals(originalText, result)
    }

    // ==================== Multi-byte Character Tests ====================

    /**
     * Test decompression of Chinese text.
     */
    @Test
    fun testDecompressChineseText() = runTest {
        val originalText = "你好，世界！这是一段中文测试文本。"
        val compressedData = compressWithDeflate(originalText)
        val frame = createBinaryFrame(compressedData)

        val result = frame.readToTextWithDeflated()

        assertEquals(originalText, result)
    }

    /**
     * Test decompression of mixed text (ASCII + Chinese + special chars).
     */
    @Test
    fun testDecompressMixedText() = runTest {
        val originalText = "Hello 你好 World 世界 123 ！@#"
        val compressedData = compressWithDeflate(originalText)
        val frame = createBinaryFrame(compressedData)

        val result = frame.readToTextWithDeflated()

        assertEquals(originalText, result)
    }

    // ==================== Large Data Tests ====================

    /**
     * Test decompression of large text data.
     */
    @Test
    fun testDecompressLargeText() = runTest {
        val originalText = "A".repeat(10000)
        val compressedData = compressWithDeflate(originalText)
        val frame = createBinaryFrame(compressedData)

        val result = frame.readToTextWithDeflated()

        assertEquals(originalText, result)
    }

    /**
     * Test decompression of large JSON array.
     */
    @Test
    fun testDecompressLargeJsonArray() = runTest {
        val originalText = buildString {
            append("[")
            repeat(100) { i ->
                if (i > 0) append(",")
                append("""{"id":$i,"name":"item$i"}""")
            }
            append("]")
        }
        val compressedData = compressWithDeflate(originalText)
        val frame = createBinaryFrame(compressedData)

        val result = frame.readToTextWithDeflated()

        assertEquals(originalText, result)
    }
}
