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
import kotlinx.coroutines.await
import love.forte.simbot.annotations.InternalSimbotAPI
import org.khronos.webgl.Uint8Array
import org.w3c.files.Blob
import kotlin.js.Promise

/**
 * 由平台实现对二进制 `deflate` 压缩数据进行解压缩并转为字符串数据。
 *
 * 使用 [DecompressionStream](https://nodejs.org/api/webstreams.html#class-decompressionstream)
 * 对数据进行解压缩、并最终使用 [TextDecoder](https://developer.mozilla.org/en-US/docs/Web/API/TextDecoder) 解析字符串。
 *
 * _Note: JS 平台下的二进制数据是**实验性**的，不保证可用性，且在未来可能会修改/删除。_
 */
@InternalSimbotAPI
public actual suspend fun Frame.Binary.readToTextWithDeflated(): String {
    val uint8Array0 = Uint8Array(data.toTypedArray())

    @Suppress("UNUSED_VARIABLE")
    val blob = Blob(arrayOf(uint8Array0))

    val decompressionStream = js("new DecompressionStream('deflate')")
    val decompressedStream = js("blob.stream()").pipeThrough(decompressionStream)

    val reader = decompressedStream.getReader()

    val chunks = mutableListOf<Uint8Array>()
    var result = (reader.read() as Promise<dynamic>).await()

    while (!result.done as Boolean) {
        val rv = result.value
        @Suppress("UnsafeCastFromDynamic")
        chunks.add(rv)
        result = (reader.read() as Promise<dynamic>).await()
    }

    val uint8Array = Uint8Array(chunks.sumOf { it.length })
    var offset = 0
    chunks.forEach {
        uint8Array.set(it, offset)
        offset += it.length
    }

    return js("new TextDecoder()").decode(uint8Array) as String
}
