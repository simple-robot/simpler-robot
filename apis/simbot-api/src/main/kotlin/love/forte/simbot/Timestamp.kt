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

/**
 * 一个 **时间戳** 。
 *
 * 在不同组件中，可能对于一个"时间点"的概念不太一致，
 * 比如时间戳可能是一个秒级时间戳（常见），也可能是一个毫秒级时间戳，
 * 也有可能组件根本不支持对时间的获取，而返回了一个-1。
 *
 * 但是对于用户，如果你不去阅读文档或注释，可能无法一时间猜到其时间点的类型到底是什么。
 *
 * 此类型旨在消除用户对于时间戳的使用混乱，统一时间类型。
 *
 * 通常情况下，[second] 或 [millisecond] 得到小于等于0的值，那么就说明此时间戳并不是一个真正的时间戳，
 * 而是一个不被支持的默认值，但是通过 [Timestamp] 你可以直接通过 [isSupport] 对支持情况进行判断。
 *
 * @author ForteScarlet
 */
@SerialName("TP")
@Serializable
public sealed class Timestamp {

    /**
     * 此时间戳对应的秒。
     */
    public abstract val second: Long

    /**
     * 此时间戳对应的毫秒值。
     */
    public abstract val millisecond: Long


    /**
     * 此时间戳是否是一个被支持的真实时间戳。
     * 如果得到false，则代表此时间戳本质上不存在。
     */
    public abstract fun isSupport(): Boolean


    @Serializable
    public object NotSupport : Timestamp() {
        override val second: Long get() = -1
        override val millisecond: Long get() = -1
        override fun isSupport(): Boolean = false
        override fun toString(): String = "0000-01-01T00:00:00Z"
    }

    public companion object {
        @JvmStatic
        public fun byInstant(instant: Instant): Timestamp = InstantTimestamp(instant)

        @JvmStatic
        public fun now(): Timestamp = byInstant(Instant.now())

        @JvmStatic
        public fun bySecond(epochSecond: Long): Timestamp = byInstant(Instant.ofEpochSecond(epochSecond))

        @JvmStatic
        public fun bySecond(epochSecond: Long, nanoAdjustment: Long): Timestamp =
            byInstant(Instant.ofEpochSecond(epochSecond, nanoAdjustment))

        @JvmStatic
        public fun byMillisecond(epochMilli: Long): Timestamp = byInstant(Instant.ofEpochMilli(epochMilli))
    }
}


@JvmSynthetic
public fun Instant.toTimestamp(): Timestamp = Timestamp.byInstant(this)



@SerialName("TPI")
@Serializable
public data class InstantTimestamp(
    @Serializable(with = InstantSerializer::class)
    private val instant: Instant
) : Timestamp(), Temporal by instant {
    override val second: Long get() = instant.epochSecond
    override val millisecond: Long get() = instant.toEpochMilli()
    override fun isSupport(): Boolean = true
    override fun toString(): String {
        return instant.toString()
    }
}


public object InstantSerializer : KSerializer<Instant> {
    override fun deserialize(decoder: Decoder): Instant {
        return Instant.ofEpochMilli(decoder.decodeLong())
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.toEpochMilli())
    }
}