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
import io.ktor.utils.io.core.*
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads

/**
 * 一个可以向 Ktor 表单请求中输入的文件信息。
 *
 * Tip: Create [InputFile] via static methods in `InputFiles` on JVM.
 *
 * @author ForteScarlet
 */
public interface InputFile {
    /**
     * Include this file to [FormBuilder].
     */
    @Throws(Exception::class)
    public fun includeTo(key: String, headers: Headers = Headers.Empty, formBuilder: FormBuilder)

    /**
     * To a [FormPart].
     */
    @Throws(Exception::class)
    public fun toFormPart(key: String, headers: Headers = Headers.Empty): FormPart<*>
}

/**
 * Create an instance of [InputFile] from [ByteArray].
 */
@JvmName("of")
@JvmOverloads
public fun InputFile(bytes: ByteArray, defaultHeaders: Headers = Headers.Empty): InputFile =
    ByteArrayInputFile(bytes, defaultHeaders)

private class ByteArrayInputFile(private val bytes: ByteArray, private val defaultHeaders: Headers) : InputFile {
    override fun includeTo(key: String, headers: Headers, formBuilder: FormBuilder) {
        formBuilder.append(key, bytes, defaultHeaders + headers)
    }

    override fun toFormPart(key: String, headers: Headers): FormPart<*> =
        FormPart(key, bytes, defaultHeaders + headers)
}

/**
 * Create an instance of [InputFile] from [InputProvider].
 */
@JvmName("of")
@JvmOverloads
public fun InputFile(input: InputProvider, defaultHeaders: Headers = Headers.Empty): InputFile =
    InputProviderInputFile(input, defaultHeaders)

private class InputProviderInputFile(private val input: InputProvider, private val defaultHeaders: Headers) :
    InputFile {
    override fun includeTo(key: String, headers: Headers, formBuilder: FormBuilder) {
        formBuilder.append(key, input, defaultHeaders + headers)
    }

    override fun toFormPart(key: String, headers: Headers): FormPart<*> =
        FormPart(key, input, defaultHeaders + headers)
}

/**
 * Create an instance of [InputFile] from [ChannelProvider].
 */
@JvmName("of")
@JvmOverloads
public fun InputFile(channel: ChannelProvider, defaultHeaders: Headers = Headers.Empty): InputFile =
    ChannelProviderInputFile(channel, defaultHeaders)

private class ChannelProviderInputFile(private val channel: ChannelProvider, private val defaultHeaders: Headers) :
    InputFile {
    override fun includeTo(key: String, headers: Headers, formBuilder: FormBuilder) {
        formBuilder.append(key, channel, defaultHeaders + headers)
    }

    override fun toFormPart(key: String, headers: Headers): FormPart<*> =
        FormPart(key, channel, defaultHeaders + headers)
}

/**
 * Create an instance of [InputFile] from [ByteReadPacket].
 */
@JvmName("of")
@JvmOverloads
public fun InputFile(byteReadPacket: ByteReadPacket, defaultHeaders: Headers = Headers.Empty): InputFile =
    ByteReadPacketInputFile(byteReadPacket, defaultHeaders)

private class ByteReadPacketInputFile(private val byteReadPacket: ByteReadPacket, private val defaultHeaders: Headers) :
    InputFile {
    override fun includeTo(key: String, headers: Headers, formBuilder: FormBuilder) {
        formBuilder.append(key, byteReadPacket, defaultHeaders + headers)
    }

    override fun toFormPart(key: String, headers: Headers): FormPart<*> =
        FormPart(key, byteReadPacket, defaultHeaders + headers)
}

internal operator fun Headers.plus(other: Headers): Headers {
    if (isEmpty()) return other
    if (other.isEmpty()) return this

    return headers {
        appendAll(this@plus)
        appendAll(other)
    }
}
