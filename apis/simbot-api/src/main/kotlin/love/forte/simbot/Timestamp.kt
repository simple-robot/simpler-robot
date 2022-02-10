/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.temporal.Temporal
import java.time.temporal.TemporalAdjuster

/**
 * 一个 **时间戳** 。
 *
 * 通常情况下，[second] 或 [millisecond] 得到小于等于0的值，那么就说明此时间戳并不是一个真正的时间戳，
 * 而是一个不被支持的默认值。
 * 通过 [Timestamp] 你可以直接通过 [isSupport] 对支持情况进行判断。
 *
 * @see InstantTimestamp
 * @author ForteScarlet
 */
@Serializable(TimestampSerializer::class)
@SerialName("ts")
public sealed class Timestamp : Comparable<Timestamp> {

    /**
     * 此时间戳对应的秒。
     */
    public abstract val second: Long

    /**
     * 此时间戳对应的毫秒值。
     */
    public abstract val millisecond: Long

    /**
     * 此时间戳中秒后的nano修正值。
     */
    public abstract val nano: Int


    /**
     * 此时间戳是否是一个被支持的真实时间戳。
     * 如果得到false，则代表此时间戳本质上不存在。
     */
    public abstract fun isSupport(): Boolean


    /**
     * 一个代表"不支持"的时间戳类型.
     */
    @Serializable
    @SerialName("tsn")
    public object NotSupport : Timestamp() {
        override val second: Long get() = -1
        override val millisecond: Long get() = -1
        override val nano: Int get() = 0
        override fun isSupport(): Boolean = false
        override fun toString(): String = "0000-01-01T00:00:00Z"
        override fun compareTo(other: Timestamp): Int {
            return if (other === NotSupport) 0 else -1
        }
    }

    public companion object {
        @JvmStatic
        public fun notSupport(): Timestamp = NotSupport

        @JvmStatic
        public fun byInstant(instant: Instant): Timestamp = InstantTimestamp(instant)

        @JvmStatic
        public fun now(): Timestamp = byInstant(Instant.now())

        @JvmStatic
        @JvmOverloads
        public fun bySecond(epochSecond: Long, nanoAdjustment: Long = 0): Timestamp =
            byInstant(Instant.ofEpochSecond(epochSecond, nanoAdjustment))

        @JvmStatic
        public fun byMillisecond(epochMilli: Long): Timestamp = byInstant(Instant.ofEpochMilli(epochMilli))
    }
}


@JvmSynthetic
public fun Instant.toTimestamp(): Timestamp = Timestamp.byInstant(this)


/**
 * 基于 [Instant] 的 [Timestamp] 实现。
 * 如果希望作为一个毫秒时间戳进行序列号，请使用 [Timestamp] 的序列化器。
 */
@Serializable
@SerialName("tsi")
public data class InstantTimestamp(
    @Serializable(with = InstantSerializer::class) val instant: Instant
) : Timestamp(),
    Temporal by instant, TemporalAdjuster by instant {
    override val second: Long get() = instant.epochSecond
    override val millisecond: Long get() = instant.toEpochMilli()
    override val nano: Int get() = instant.nano
    override fun isSupport(): Boolean = true
    override fun toString(): String {
        return instant.toString()
    }

    override fun compareTo(other: Timestamp): Int {
        if (other === NotSupport) return 1

        other as InstantTimestamp
        return instant.compareTo(other.instant)
    }
}


/**
 * 如果 [Timestamp] 类型为 [InstantTimestamp], 获取 [instant][InstantTimestamp.instant] 实例。
 */
public inline val Timestamp.instantValue: Instant? get() = takeIf { it is InstantTimestamp }?.let { (it as InstantTimestamp).instant }


public object InstantSerializer : KSerializer<Instant> {
    override fun deserialize(decoder: Decoder): Instant {
        return Instant.ofEpochMilli(decoder.decodeLong())
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.toEpochMilli())
    }
}

public object TimestampSerializer : KSerializer<Timestamp> {
    override fun deserialize(decoder: Decoder): Timestamp {
        val instant = decoder.decodeLong()
        return if (instant < 0) Timestamp.NotSupport else Instant.ofEpochMilli(instant).toTimestamp()
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Timestamp", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Timestamp) {
        encoder.encodeLong(value.millisecond)
    }

}