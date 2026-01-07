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

@file:JvmName("AssetApis")
@file:JvmMultifileClass

package love.forte.simbot.kook.api.asset

import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.nio.*
import io.ktor.utils.io.streams.*
import java.io.File
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.name

/**
 * 提供文件的 [File] 作为上传API。
 *
 * 文件在上传时会通过 [File.readChannel] 转化为 [ByteReadChannel]。
 *
 * [File] 不会被立即读取，而是在实际发起请求时被读取。
 * API 进行请求时，可能会因 [File] 而导致产生各种异常，比如 [IOException][java.io.IOException]。
 *
 * @param file 文件
 * @param filename 使用在表单数据中 [HttpHeaders.ContentDisposition] 的 `filename` 属性，
 * 如果为 `null` 则会使用 [File.getName] 获取文件名称
 *
 * @see File.readChannel
 */
@JvmOverloads
@JvmName("create")
public fun createCreateAssetApi(file: File, filename: String? = null): CreateAssetApi =
    CreateAssetApi.create(ChannelProvider { file.readChannel() }, filename = filename ?: file.name)

/**
 * 提供文件的 [Path] 作为上传API。
 *
 * 文件在上传时会通过 [FileChannel.open] 将 [Path] 打开并通过 [FileChannel.asInput] 转化为 [Input]。
 *
 * [Path] 不会被立即读取，而是在实际发起请求时被读取。
 * API 进行请求时，可能会因 [Path] 而导致产生各种异常，比如 [IOException][java.io.IOException]。
 *
 * @param path 文件
 * @param filename 使用在表单数据中 [HttpHeaders.ContentDisposition] 的 `filename` 属性，
 * 如果为 `null` 则会使用 [Path.name] 获取文件名称
 *
 * @see FileChannel.open
 */
@JvmOverloads
@JvmName("create")
public fun createCreateAssetApi(path: Path, filename: String? = null): CreateAssetApi =
    CreateAssetApi.create(
        InputProvider { FileChannel.open(path, StandardOpenOption.READ).asInput() },
        filename = filename ?: path.name
    )

/**
 * 提供文件的 [URL] 作为上传API。
 *
 * 文件在上传时会通过 [URL.openStream] 打开并通过 [InputStream.asInput] 转化为 [Input]。
 *
 * [URL] 不会被立即读取，而是在实际发起请求时被读取。
 * API 进行请求时，可能会因 [URL] 而导致产生各种异常，比如 [IOException][java.io.IOException]。
 *
 * @param fileURL 文件 [URL]
 * @param filename 使用在表单数据中 [HttpHeaders.ContentDisposition] 的 `filename` 属性，
 * 如果为 `null` 则会提供一个默认的文件名称 `unknown-file`。
 *
 * @see URL.openStream
 */
@JvmOverloads
@JvmName("create")
public fun createCreateAssetApi(fileURL: URL, filename: String? = null): CreateAssetApi =
    CreateAssetApi.create(InputProvider { fileURL.openStream().asInput() }, filename = filename)

/**
 * 提供文件的 [URI] 作为上传API。
 *
 * 文件在上传时会通过 [URL.openStream] 打开并通过 [InputStream.asInput] 转化为 [Input]。
 *
 * [URL] 不会被立即读取，而是在实际发起请求时被读取。
 * API 进行请求时，可能会因 [URL] 而导致产生各种异常，比如 [IOException][java.io.IOException]。
 *
 * @param fileURI 文件 [URI]
 * @param filename 使用在表单数据中 [HttpHeaders.ContentDisposition] 的 `filename` 属性，
 * 如果为 `null` 则会提供一个默认的文件名称 `unknown-file`。
 *
 * @throws IllegalArgumentException see [URI.toURL]
 * @throws java.net.MalformedURLException see [URI.toURL]
 * @see URI.toURL
 */
@JvmOverloads
@JvmName("create")
@Throws(MalformedURLException::class)
public fun createCreateAssetApi(fileURI: URI, filename: String? = null): CreateAssetApi =
    createCreateAssetApi(fileURI.toURL(), filename = filename)

