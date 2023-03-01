/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot

import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

/**
 *
 * @see java.time.Duration
 */
public typealias JavaDuration = java.time.Duration


/**
 * 将 [JavaDuration] 转化为 [Duration].
 *
 * 在无法优化的情况下，会将 [JavaDuration] 转化为nanos后作为 [Duration] 使用。
 *
 */
public val JavaDuration.kotlin: Duration
    @JvmSynthetic
    get() {
        if (this == JavaDuration.ZERO) {
            return Duration.ZERO
        }
        if (nano == 0) {
            return seconds.seconds
        }
        
        return toNanos().nanoseconds
    }

/**
 * 将 [Duration] 转化为 [JavaDuration].
 *
 * 在无法优化的情况下，会将 [Duration] 转化为nanos后作为 [JavaDuration] 使用。
 *
 * 如果 [Duration] 的值为 [Duration.INFINITE], 则会使用 [ifInfinite] 计算结果。
 * 默认情况下会抛出 [IllegalArgumentException] 异常。
 *
 * 如果希望无视 [Duration] 为无穷的情况而直接进行转化，请使用 [Duration.java]。
 *
 */
public inline fun Duration.java(
    ifInfinite: (duration: Duration) -> JavaDuration = {
        throw IllegalArgumentException(
            "Duration is infinite"
        )
    },
): JavaDuration {
    return javaOrNull ?: ifInfinite(this)
}

/**
 * 将 [Duration] 转化为 [JavaDuration].
 *
 * 在无法优化的情况下，会将 [Duration] 转化为nanos后作为 [JavaDuration] 使用。
 *
 * 如果 [Duration] 的值为 [Duration.INFINITE], 则会得到null。
 *
 * 如果希望在出现无穷时进行计算，请使用 [Duration.java]；
 * 如果希望无视 [Duration] 为无穷的情况而直接进行转化，请使用 [Duration.java]。
 *
 */
public val Duration.javaOrNull: JavaDuration?
    @JvmSynthetic
    get() {
        if (this.isInfinite()) {
            return null
        }
        
        return java
    }

/**
 * 将 [Duration] 转化为 [JavaDuration].
 *
 * 在无法优化的情况下，会将 [Duration] 转化为nanos后作为 [JavaDuration] 使用。
 * 不会判断 [Duration] 是否为无穷的情况。
 *
 * 如果希望在出现无穷时进行计算，请使用 [Duration.java]。
 *
 */
public val Duration.java: JavaDuration
    @JvmSynthetic
    get() {
        if (this == Duration.ZERO) {
            return JavaDuration.ZERO
        }
        
        return JavaDuration.ofNanos(inWholeNanoseconds)
    }
