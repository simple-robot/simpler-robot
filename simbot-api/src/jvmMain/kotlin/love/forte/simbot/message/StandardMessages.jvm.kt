/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

@file:JvmName("StandardMessages")
@file:JvmMultifileClass

package love.forte.simbot.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.message.OfflineFileImage.Companion.toOfflineFileImage
import love.forte.simbot.message.OfflinePathImage.Companion.toOfflinePathImage
import love.forte.simbot.message.OfflineURIImage.Companion.toOfflineImage
import love.forte.simbot.resource.*
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.io.path.readBytes

/**
 * 将 [Resource] 转化为 [OfflineResourceImage]。
 *
 * 如果 [Resource] 类型为 [FileResource]、[PathResource]、[URIResource]，
 * 则会分别对应地得到 [OfflineFileImage]、[OfflinePathImage]、[OfflineURIImage]，
 *
 * 否则将会使用 [SimpleOfflineResourceImage]。
 *
 */
public actual fun Resource.toOfflineResourceImage(): OfflineResourceImage {
    return when (this) {
        is FileResource -> toOfflineFileImage()
        is PathResource -> toOfflinePathImage()
        is URIResource -> toOfflineImage()
        else -> SimpleOfflineResourceImage(this)
    }
}

/**
 * 基于 [File] 的 [OfflineImage] 实现。
 *
 * [file] 的序列化会通过 [File.getName] 作为字符串进行。
 *
 */
@Serializable
@SerialName("m.std.img.offline.file")
public class OfflineFileImage private constructor(@Serializable(FileSerializer::class) public val file: File) :
    OfflineResourceImage {
    public companion object {
        /**
         * Converts a [File] to an [OfflineFileImage].
         *
         * @return The [OfflineFileImage] representation of the File.
         */
        @JvmStatic
        @JvmName("of")
        public fun File.toOfflineImage(): OfflineFileImage =
            OfflineFileImage(this)

        /**
         * Converts a [FileResource] to an [OfflineFileImage].
         *
         * @return The converted [OfflineFileImage].
         */
        @JvmStatic
        @JvmName("of")
        public fun FileResource.toOfflineFileImage(): OfflineFileImage =
            OfflineFileImage(file).also { image ->
                image._resource = this
            }
    }

    @Transient
    private var _resource: FileResource? = null

    override val resource: FileResource
        get() = _resource ?: file.toResource().also { _resource = it }

    @Throws(IOException::class)
    override fun data(): ByteArray = file.readBytes()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OfflineFileImage) return false

        if (file != other.file) return false

        return true
    }

    override fun hashCode(): Int {
        return file.hashCode()
    }

    override fun toString(): String {
        return "OfflineFileImage(file=$file)"
    }
}

internal object FileSerializer : KSerializer<File> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("File", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): File {
        val pathname = decoder.decodeString()
        return File(pathname)
    }

    override fun serialize(encoder: Encoder, value: File) {
        encoder.encodeString(value.name)
    }
}

/**
 * 基于 [Path] 的 [OfflineImage] 实现。
 *
 * [path] 的序列化会通过 [Path.pathString] 作为字符串进行。
 *
 */
@Serializable
@SerialName("m.std.img.offline.path")
public data class OfflinePathImage(@Serializable(PathSerializer::class) public val path: Path) : OfflineResourceImage {
    public companion object {
        /**
         * Converts a [Path] object to an [OfflinePathImage] object representing an offline image.
         *
         * @return An [OfflinePathImage] object representing the offline image.
         */
        @JvmStatic
        @JvmName("of")
        public fun Path.toOfflineImage(): OfflinePathImage = OfflinePathImage(this)

        /**
         * Converts a [PathResource] to an [OfflinePathImage].
         *
         * @return The converted [OfflinePathImage].
         */
        @JvmStatic
        @JvmName("of")
        public fun PathResource.toOfflinePathImage(): OfflinePathImage =
            OfflinePathImage(path).also { image ->
                image._resource = this
            }
    }

    @Transient
    private var _resource: PathResource? = null

    override val resource: PathResource
        get() = _resource ?: path.toResource().also {
            _resource = it
        }

    @Throws(IOException::class)
    override fun data(): ByteArray = path.readBytes()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OfflinePathImage) return false

        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }

    override fun toString(): String {
        return "OfflinePathImage(path=$path)"
    }
}

internal object PathSerializer : KSerializer<Path> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Path", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Path {
        val path = decoder.decodeString()
        return Path(path)
    }

    override fun serialize(encoder: Encoder, value: Path) {
        encoder.encodeString(value.pathString)
    }

}

/**
 * 基于 [URI] 的 [OfflineImage] 实现。
 *
 * [uri] 的序列化会通过 [URI.toString] 作为字符串进行。
 *
 */
@Serializable
@SerialName("m.std.img.offline.uri")
public data class OfflineURIImage(
    @Serializable(URISerializer::class) public val uri: URI
) : OfflineResourceImage {
    public companion object {

        /**
         * Converts the [URI] object to an [OfflineURIImage] object representing an offline image.
         *
         * @return An OfflineURLImage object representing the offline image.
         */
        @JvmStatic
        @JvmName("of")
        public fun URI.toOfflineImage(): OfflineURIImage = OfflineURIImage(this)

        @JvmStatic
        @JvmName("of")
        public fun URIResource.toOfflineImage(): OfflineURIImage =
            OfflineURIImage(uri).also { image ->
                image._resource = this
            }
    }

    @Transient
    private var _resource: URIResource? = null

    override val resource: URIResource
        get() = _resource ?: uri.toResource().also {
            _resource = it
        }

    /**
     * Read bytes from [uri].
     *
     * @throws MalformedURLException see [URI.toURL]
     * @throws IOException see [URL.readBytes]
     */
    @Throws(IOException::class)
    override fun data(): ByteArray = uri.toURL().readBytes()
}

internal object URISerializer : KSerializer<URI> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("URI", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): URI {
        val str = decoder.decodeString()
        return URI.create(str)
    }

    override fun serialize(encoder: Encoder, value: URI) {
        encoder.encodeString(value.toString())
    }
}
