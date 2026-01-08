/*
 * Copyright (c) 2023-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api.message

import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.nio.*
import io.ktor.utils.io.streams.*
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.name

/**
 * JVM平台下所能支持的针对 [MessageSendApi.Body.fileImage] 的解析。
 *
 * 处理当类型为:
 *
 * - [File]
 * - [Path]
 * - [URL]
 * - [URI]
 *
 * 时的表单提交方式。
 *
 */
public actual fun FormBuilder.resolveOther(fileImage: Any?) {
    when (fileImage) {
        is File -> {
            val imgHeaders = Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"${fileImage.name}\"")
            }
            append(key = "file_image", ChannelProvider { fileImage.readChannel() }, imgHeaders)
        }

        is Path -> {
            val imgHeaders = Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"${fileImage.name}\"")
            }
            append(
                key = "file_image",
                InputProvider { FileChannel.open(fileImage, StandardOpenOption.READ).asInput() },
                imgHeaders
            )
        }

        is URL -> {
            val imgHeaders = Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"file\"")
            }
            append(key = "file_image", InputProvider { fileImage.openStream().asInput() }, imgHeaders)
        }

        is URI -> {
            val imgHeaders = Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"file\"")
            }
            append(key = "file_image", InputProvider { fileImage.toURL().openStream().asInput() }, imgHeaders)
        }
    }
}

internal actual fun checkFileImage(fileImage: Any) {
    when (fileImage) {
        is File -> {}
        is Path -> {}
        is URL -> {}
        is URI -> {}
        else -> {
            throw IllegalArgumentException("Unsupported fileImage type: $fileImage (${fileImage::class.java})")
        }
    }
}
