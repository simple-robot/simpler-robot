package love.forte.simbot

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.ID.*
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * 唯一标识 [ID].
 * ID一般存在与各个元数据中，作为元数据的唯一标识。
 *
 * 一个 [ID]，其代表了两种数据：ID的类型，以及ID具体的值。
 * [ID] 的类型即为其自身，不同类型的ID无论如何也不应相同。而 [ID] 中具体的值千变万化，且不一定仅存一个，由实现者自行决定。
 *
 * 假若一个 [ID] 中实际存储的值仅有一个，则它的 [toString] 应当就是它的字面值。
 *
 * [ID] 应当支持序列化。
 *
 * @see StringID
 * @see LongID
 * @see ComplexID
 *
 *
 * @author ForteScarlet
 */
@Serializable
public sealed class ID {

    /**
     * 以 [Long] 作为字面值的 [ID].
     *
     * ```kt
     * // Kotlin
     * val id = 123.ID
     * ```
     *
     * ```java
     * // Java
     * LongID id = ID.by(123);
     * ```
     *
     * 序列化的时候，如果需要将 [LongID] 字段作为数值字面量序列化，可以使用 [LongID.PrimitiveSerialSerializer].
     *
     * @see ID.by
     * @see Number.ID
     */
    @SerialName("id.n")
    @Serializable
    public data class LongID internal constructor(val value: Long) : ID() {
        override fun toString(): String = value.toString()

        public companion object {
            public fun primitiveSerialSerializer(): KSerializer<LongID> = PrimitiveSerialSerializer
        }

        /**
         * [LongID] 的字面值序列化器。
         */
        public object PrimitiveSerialSerializer : KSerializer<LongID> {
            override fun deserialize(decoder: Decoder): LongID = LongID(decoder.decodeLong())
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("simbot.LongIDAsLong", PrimitiveKind.LONG)

            override fun serialize(encoder: Encoder, value: LongID) {
                encoder.encodeLong(value.value)
            }
        }
    }

    /**
     * 以 [String] 作为字面值的 [ID].
     *
     * ```kt
     * // Kotlin
     * val id = "ID".ID
     * ```
     *
     * ```java
     * // Java
     * StringID id = ID.by("ID");
     * ```
     *
     * 序列化的时候，如果需要将 [StringID] 字段作为字符串字面量序列化，可以使用 [StringID.PrimitiveSerialSerializer].
     *
     * @see ID.by
     * @see String.ID
     */
    @SerialName("id.s")
    @Serializable
    public data class StringID internal constructor(val value: String) : ID() {
        override fun toString(): String = value

        public companion object {
            public fun primitiveSerialSerializer(): KSerializer<StringID> = PrimitiveSerialSerializer
        }

        /**
         * [StringID] 的字面值序列化器。
         */
        public object PrimitiveSerialSerializer : KSerializer<StringID> {
            override fun deserialize(decoder: Decoder): StringID = StringID(decoder.decodeString())
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("simbot.StringIDAsString", PrimitiveKind.STRING)

            override fun serialize(encoder: Encoder, value: StringID) {
                encoder.encodeString(value.value)
            }
        }
    }


    /**
     * 如果是一个复杂ID, 即无法通过 [LongID] 或 [StringID] 进行表示的，
     * 则实现此抽象类。
     */
    @Serializable
    public abstract class ComplexID : ID()


    @Suppress("FunctionName")
    public companion object {
        @JvmStatic
        @JsName("byNumber")
        public fun by(n: Number): LongID = n.ID


        @JvmStatic
        @JsName("byString")
        public fun by(s: String): StringID = s.ID
    }
}


public val Number.ID: LongID get() = LongID(this.toLong())
public val String.ID: StringID get() = StringID(this)