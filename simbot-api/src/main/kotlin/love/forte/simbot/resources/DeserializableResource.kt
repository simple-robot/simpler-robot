/*
 * Copyright (c) 2022-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.simbot.resources

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import java.io.InputStream

/**
 * 代表为一个可以进行反序列化的 [Resource] 类型，提供了一个 [decode] 函数
 * 来将当前资源中的信息解析为目标类型。
 *
 * 此类型多为一个文本格式的资源。
 *
 * @author ForteScarlet
 */
public interface DeserializableResource : Resource {

    /**
     * 提供一个 [DeserializationStrategy], 将当前资源反序列化为目标类型。
     */
    public fun <T> decode(deserializer: DeserializationStrategy<T>): T


    public companion object
    
}


/**
 * 对 [DeserializableResource] 进行反序列化的解码器。
 */
public interface DeserializableResourceDecoder {

    /**
     * 提供数据输入流和[deserializer], 解析为目标类型。
     *
     * @param inputStream 提供的数据输入流。应当由调用者关闭。
     */
    public fun <T> decode(inputStream: InputStream, deserializer: DeserializationStrategy<T>): T


    public companion object {

        /**
         * 使用字符串解码器，例如 [kotlinx.serialization.json.Json].
         */
        @JvmStatic
        @JvmName("of")
        public fun <F : StringFormat> F.toDeserializableResourceDecoder(): SimpleStringFormatDeserializableResourceDecoder<F> {
            return SimpleStringFormatDeserializableResourceDecoder(this)
        }

        /**
         * 使用二进制解码器，例如 [kotlinx.serialization.protobuf.ProtoBuf].
         */
        @JvmStatic
        @JvmName("of")
        public fun <F : BinaryFormat> F.toDeserializableResourceDecoder(): SimpleBinaryFormatDeserializableResourceDecoder<F> {
            return SimpleBinaryFormatDeserializableResourceDecoder(this)
        }

    }

}

/**
 * 基于 kotlinx.serialization 的 [SerialFormat] 所实现的抽象 [DeserializableResourceDecoder].
 *
 * 通常作为其他抽象的上层。
 */
public abstract class SerialFormatDeserializableResourceDecoder : DeserializableResourceDecoder {


    /**
     * 使用的format。
     */
    public abstract val format: SerialFormat

}

/**
 * 基于 `kotlinx.serialization` 中的 [StringFormat] 所实现的抽象 [DeserializableResourceDecoder].
 */
public abstract class StringFormatDeserializableResourceDecoder : SerialFormatDeserializableResourceDecoder() {

    /**
     * 使用的format。
     */
    abstract override val format: StringFormat

    /**
     * 提前准备输入流，将其转化为 [String].
     */
    protected open fun InputStream.prepare(): String {
        return reader().readText()
    }

    override fun <T> decode(inputStream: InputStream, deserializer: DeserializationStrategy<T>): T {
        val value = inputStream.prepare()
        return format.decodeFromString(deserializer, value)
    }
}


/**
 * [StringFormatDeserializableResourceDecoder] 的基础实现，提供一个 [format] 作为解析器。
 */
public class SimpleStringFormatDeserializableResourceDecoder<F : StringFormat>(override val format: F) :
    StringFormatDeserializableResourceDecoder()


/**
 * 基于 `kotlinx.serialization` 中的 [StringFormat] 所实现的抽象 [DeserializableResourceDecoder].
 */
public abstract class BinaryFormatDeserializableResourceDecoder : SerialFormatDeserializableResourceDecoder() {

    /**
     * 使用的format。
     */
    abstract override val format: BinaryFormat

    /**
     * 提前准备输入流，将其转化为 [ByteArray].
     */
    protected open fun InputStream.prepare(): ByteArray {
        return readBytes()
    }

    override fun <T> decode(inputStream: InputStream, deserializer: DeserializationStrategy<T>): T {
        val value = inputStream.prepare()
        return format.decodeFromByteArray(deserializer, value)
    }
}


/**
 * [BinaryFormatDeserializableResourceDecoder] 的基础实现，提供一个 [format] 作为解析器。
 */
public class SimpleBinaryFormatDeserializableResourceDecoder<F : BinaryFormat>(override val format: F) :
    BinaryFormatDeserializableResourceDecoder()


