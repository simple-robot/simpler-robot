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

@file:JvmName("InputFiles")
@file:JvmMultifileClass

package love.forte.simbot.common.ktor.inputfile

import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.nio.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.fileSize

/**
 * Create an instance of [InputFile] from [File].
 *
 * This [InputFile] Will use [File.length] in [InputFile.includeTo], [InputFile.toFormPart],
 * and use [File.readChannel] in [ChannelProvider].
 *
 */
@JvmName("of")
@JvmOverloads
public fun InputFile(file: File, defaultHeaders: Headers = Headers.Empty): InputFile =
    FileInputFile(file, defaultHeaders)

private class FileInputFile(private val file: File, private val defaultHeaders: Headers) : InputFile {
    override fun includeTo(key: String, headers: Headers, formBuilder: FormBuilder) {
        formBuilder.append(key, pathChannelProvider(), defaultHeaders + headers)
    }

    override fun toFormPart(key: String, headers: Headers): FormPart<*> =
        FormPart(key, pathChannelProvider(), defaultHeaders + headers)

    private fun pathChannelProvider() = ChannelProvider(file.length()) { file.readChannel() }
}

/**
 * Create an instance of [InputFile] from [Path].
 *
 * This [InputFile] will use [Path.fileSize] in [InputFile.includeTo], [InputFile.toFormPart],
 * and use [Files.newByteChannel], [asInput] in [ChannelProvider].
 *
 */
@JvmName("of")
@JvmOverloads
public fun InputFile(path: Path, defaultHeaders: Headers = Headers.Empty): InputFile =
    PathInputFile(path, defaultHeaders)

private class PathInputFile(private val path: Path, private val defaultHeaders: Headers) : InputFile {

    @Throws(IOException::class)
    override fun includeTo(key: String, headers: Headers, formBuilder: FormBuilder) {
        formBuilder.append(key, pathInput(), defaultHeaders + headers)
    }

    @Throws(IOException::class)
    override fun toFormPart(key: String, headers: Headers): FormPart<*> =
        FormPart(key, pathInput(), defaultHeaders + headers)

    @Throws(IOException::class)
    private fun pathInput(): InputProvider = InputProvider(path.fileSize()) { Files.newByteChannel(path).asInput() }
}
