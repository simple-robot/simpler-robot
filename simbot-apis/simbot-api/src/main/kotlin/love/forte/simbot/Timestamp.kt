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

/**
 * 一个 **时间戳** 。
 *
 * 通常情况下，[second] 或 [millisecond] 得到-1的值，那么就说明此时间戳并不是一个真正的时间戳，
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
     * 此时间戳是否是一个被支持的真实时间戳。
     * 如果得到false，则代表此时间戳本质上不存在，
     * 且上述 [second] 和 [millisecond] 最终结果应为-1。
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
        override fun isSupport(): Boolean = false
        override fun toString(): String = "0000-01-01T00:00:00Z"
        override fun compareTo(other: Timestamp): Int {
            return if (other === NotSupport) 0 else -1
        }
    }
    
    public companion object {
        /**
         * 得到一个 [Timestamp.millisecond] 始终为 -1 的 [Timestamp] 实例。
         */
        @JvmStatic
        public fun notSupport(): Timestamp = NotSupport
        
        /**
         * 通过 [Instant] 构建一个 [Timestamp] 实例。
         */
        @JvmStatic
        public fun byInstant(instant: Instant): Timestamp = InstantTimestamp(instant.epochSecond, instant.nano)
        
        /**
         * 根据当前时间瞬时构建一个 [Timestamp].
         */
        @JvmStatic
        public fun now(): Timestamp = byMillisecond(System.currentTimeMillis())
    
        /**
         * 根据一个秒级时间和它的nano偏移值来构建一个 [Timestamp]。
         * 如果 [epochSecond] 小于0，则视为无效并等同于使用 [notSupport]。
         *
         * @param epochSecond 秒级时间戳值。
         * @param nanoAdjustment 针对 [epochSecond] 的纳秒偏移。有关nano偏移的概念类似于 [Instant.nanos]，有关更多内容参考 [Instant] 相关实现。
         */
        @JvmStatic
        @JvmOverloads
        public fun bySecond(epochSecond: Long, nanoAdjustment: Int = 0): Timestamp {
            if (epochSecond < 0) {
                return notSupport()
            }
            return InstantTimestamp(epochSecond, nanoAdjustment)
        }
    
        /**
         * 根据一个毫秒时间戳来构建一个 [Timestamp] 实例。
         * 如果 [epochMilli] 小于 0，则相当于使用 [notSupport]。
         */
        @JvmStatic
        public fun byMillisecond(epochMilli: Long): Timestamp {
            if (epochMilli < 0) {
                return notSupport()
            }
            
            val secs = Math.floorDiv(epochMilli, 1000.toLong())
            val mos = Math.floorMod(epochMilli, 1000.toLong()).toInt()
            
            return bySecond(secs, mos * 1000_000)
        }
    }
}


@JvmSynthetic
public fun Instant.toTimestamp(): Timestamp = Timestamp.byInstant(this)


/**
 * 基本的 [Timestamp] 实现。
 *
 * 大部分实现方式参考了 [Instant], 但并没有直接使用 [Instant] 实例。至少目前没有。
 *
 * 如果希望作为一个毫秒时间戳进行序列化，请使用 [Timestamp] 的序列化器 [TimestampSerializer]。
 */
@Suppress("MemberVisibilityCanBePrivate")
@Serializable
@SerialName("tsi")
public class InstantTimestamp internal constructor(
    override val second: Long,
    public val nanos: Int,
) : Timestamp() {
    
    init {
        Simbot.require(second >= 0) { "'second' must >= 0, but $second" }
    }
    
    /**
     * 瞬时时间所代表的毫秒值。此值应始终不小于0。
     */
    override val millisecond: Long
        get() {
            val millis = Math.multiplyExact(second, 1000L)
            return Math.addExact(millis, nanos / 1000000L)
        }
    
    /**
     * 根据 [millisecond] 构建一个 [Instant] 实例。
     */
    public fun toInstant(): Instant {
        return Instant.ofEpochSecond(second, nanos.toLong())
    }
    
    override fun isSupport(): Boolean = true
    
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is InstantTimestamp) return false
        
        return millisecond == other.millisecond
    }
    
    override fun hashCode(): Int {
        return millisecond.hashCode()
    }
    
    override fun toString(): String {
        return "InstantTimestamp(millisecond=$millisecond)"
    }
    
    override fun compareTo(other: Timestamp): Int {
        if (other === NotSupport) return 1
        
        other as InstantTimestamp
        return millisecond.compareTo(other.millisecond)
    }
}


/**
 * 将当前时间戳实例转化为 [Instant]。
 * 如果 [Timestamp] 满足 [isSupport][Timestamp.isSupport] != true，则会得到 [Instant.EPOCH]。
 */
public inline val Timestamp.instantValue: Instant
    get() = if (this is InstantTimestamp) toInstant() else if (isSupport()) Instant.ofEpochMilli(millisecond) else Instant.EPOCH


/**
 * 是否不支持的 timestamp。等同于 [isSupport][Timestamp.isSupport] != true。
 */
public fun Timestamp.isNotSupport(): Boolean = !isSupport()


/**
 * 将 [Timestamp] 直接作为字面值进行序列化的序列化器。
 * 序列化的字面值为 [Timestamp.millisecond] 的值。
 */
public object TimestampSerializer : KSerializer<Timestamp> {
    override fun deserialize(decoder: Decoder): Timestamp {
        val millisecond = decoder.decodeLong()
        return Timestamp.byMillisecond(millisecond)
    }
    
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TimestampMillisecond", PrimitiveKind.LONG)
    
    override fun serialize(encoder: Encoder, value: Timestamp) {
        encoder.encodeLong(value.millisecond)
    }
    
}