/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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
import js.typedarrays.Uint8Array
import love.forte.simbot.annotations.InternalSimbotAPI
import web.blob.Blob
import web.compression.CompressionFormat
import web.compression.DecompressionStream
import web.compression.deflate
import web.encoding.TextDecoder
import web.streams.read
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.unsafeCast

/**
 * 由平台实现对二进制 `deflate` 压缩数据进行解压缩并转为字符串数据。
 *
 * 使用 [DecompressionStream](https://nodejs.org/api/webstreams.html#class-decompressionstream)
 * 对数据进行解压缩、并最终使用 [TextDecoder](https://developer.mozilla.org/en-US/docs/Web/API/TextDecoder) 解析字符串。
 *
 * _Note: Web 平台下的二进制数据是**实验性**的，不保证可用性，且在未来可能会修改/删除。_
 */
@OptIn(ExperimentalWasmJsInterop::class)
@InternalSimbotAPI
public actual suspend fun Frame.Binary.readToTextWithDeflated(): String {
    val blob = dataToBlob()

    val decompressionStream = DecompressionStream(CompressionFormat.deflate)
    val decompressedStream = blob.stream().pipeThrough<Uint8Array<ArrayBuffer>>(decompressionStream.unsafeCast())

    val reader = decompressedStream.getReader()

    val chunks = mutableListOf<Uint8Array<ArrayBuffer>>()
    var result = reader.read()

    while (!result.done) {
        val rv = result.value
        if (rv != null) {
            chunks.add(rv)
        }
        result = reader.read()
    }

    val uint8Array = Uint8Array<ArrayBuffer>(chunks.sumOf { it.length })
    var offset = 0
    chunks.forEach {
        uint8Array.set(it, offset)
        offset += it.length
    }

    return TextDecoder().decode(uint8Array)
}

// 直接使用存在错误：
//   Cannot access 'Cloneable' which is a supertype of 'ByteArray'.
//   Check your module classpath for missing or conflicting dependencies.
internal expect fun Frame.Binary.dataToBlob(): Blob
