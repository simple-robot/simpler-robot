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

@file:JvmName("Resources")
@file:JvmMultifileClass

package love.forte.simbot.resource

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * 一个**资源**。
 *
 * 用于描述一个可以被读取字节数据（[data]）的资源。
 * [Resource] 用于代表一些二进制数据或**本地**文件资源。
 *
 * JVM 中的部分扩展、辅助API通过静态类 `Resources` 提供，
 * 例如 `Resources.valueOf(...)`。
 *
 * ## 序列化
 *
 * [Resource] 提供了一个基于 [Base64] 进行序列化操作的 [ResourceBase64Serializer]。
 *
 * ## 第三方实现不稳定
 *
 * [Resource] 主要由内部实现，不保证对第三方实现的稳定与兼容
 *
 * @see ByteArrayResource
 * @see SourceResource
 *
 * @author ForteScarlet
 */
public interface Resource {
    // TODO become `sealed` for ByteArrayResource and SourceResource.

    /**
     * 读取此资源的字节数据。
     *
     */
    @Throws(Exception::class)
    public fun data(): ByteArray
}

/**
 * 通过提供的 [ByteArray] 直接构建一个 [Resource]。
 *
 * @return Resource object representing the ByteArray data.
 */
@JvmName("valueOf")
public fun ByteArray.toResource(): ByteArrayResource = ByteArrayResourceImpl(this)

/**
 * 直接使用 [ByteArray] 作为 [data] 结果的 [Resource] 实现。
 *
 * @author forte
 */
public interface ByteArrayResource : Resource {
    /**
     * 获取到字节数组结果。
     */
    override fun data(): ByteArray
}

/**
 * 基于 [Base64] 的 [Resource] 序列化器。
 *
 * 它会将任何 [Resource] 都根据 [Resource.data] 序列化为 Base64 数据，
 * 并将任意序列化后的数据反序列化为 [ByteArrayResource]。
 *
 * 也因此，这会导致：
 * - 序列化时会读取数据、产生读取开销。
 * - 反序列化后的类型可能与原本的类型不一致。
 */
@ExperimentalEncodingApi
public object ResourceBase64Serializer : KSerializer<Resource> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("B64Resource", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Resource {
        val b64 = decoder.decodeString()
        return Base64.decode(b64).toResource()
    }

    override fun serialize(encoder: Encoder, value: Resource) {
        encoder.encodeString(Base64.encode(value.data()))
    }
}

/**
 * 直接基于 [ByteArray] 的 [Resource] 实现。
 *
 * 被包装使用的 [ByteArray] 不会发生拷贝，因此请避免修改原始的数组或 [data] 得到的数组。
 *
 */
private data class ByteArrayResourceImpl(private val raw: ByteArray) : ByteArrayResource {
    override fun data(): ByteArray = raw

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ByteArrayResourceImpl) return false

        if (!raw.contentEquals(other.raw)) return false

        return true
    }

    override fun hashCode(): Int {
        return raw.contentHashCode()
    }

    override fun toString(): String = buildString {
        append("ByteArrayResource(raw=")
        raw.joinTo(buffer = this, separator = ", ", prefix = "[", postfix = "]", limit = 8)
        append(")")
    }
}

/**
 * 一个可以读取到 [String] 内容物的拓展类型。
 * 是其他 [Resource] 类型的附加能力，但不属于一个标准的 [Resource] 类型。
 */
public interface StringReadableResource : Resource {
    /**
     * 读取此资源的 [String] 内容。
     */
    @Throws(Exception::class)
    public fun string(): String
}

/**
 * 直接使用 [String] 作为内容的 [Resource]。
 */
public interface StringResource : StringReadableResource {
    /**
     * 读取此资源的 [String] 内容。
     */
    override fun string(): String
}

/**
 * 通过提供的 [String] 直接构建一个 [StringResource]。
 */
@JvmName("valueOf")
public fun String.toStringResource(): StringResource = StringResourceImpl(this)


private data class StringResourceImpl(private val string: String) : StringResource {
    override fun string(): String = string
    override fun data(): ByteArray = string().encodeToByteArray()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StringResourceImpl) return false

        if (string != other.string) return false

        return true
    }

    override fun hashCode(): Int {
        return string.hashCode()
    }

    override fun toString(): String = "StringResource(string=\"$string\")"
}
