@file:Suppress("MemberVisibilityCanBePrivate", "unused", "RedundantVisibilityModifier")

package love.forte.common.utils

import java.util.concurrent.TimeUnit


/**
 * 时间内联类。[time]参数是 **毫秒** 值。
 * 你可以通过 中缀函数 [Number].[timeBy] ([TimeUnit]) 得到此实例，例如：
 * ```
 *  val second: Long = ...
 *  val time: Time = time timeBy TimeUnit.SECONDS
 * ```
 * 你也可以直接通过函数 [now] 来得到当前时间的 [Time] 值。
 */
public inline class Time(val time: Long){
    private val unit: TimeUnit get() = TimeUnit.MILLISECONDS
    fun toNanos(): Long = unit.toNanos(time)
    fun toMicros(): Long = unit.toMicros(time)
    fun toMillis(): Long = unit.toMillis(time)
    fun toSeconds(): Long = unit.toSeconds(time)
    fun toMinutes(): Long = unit.toMinutes(time)
    fun toHours(): Long = unit.toHours(time)
    fun toDays(): Long = unit.toDays(time)
}


/**
 * 将一个时间数值根据 [时间类型][timeUnit] 转化为一个内联时间类 [Time]
 */
public infix fun Number.timeBy(timeUnit: TimeUnit): Time = Time(timeUnit.toMillis(toLong()))

/**
 * 获取当前时间的 [Time] 类型
 */
public fun now(): Time = Time(System.currentTimeMillis())





