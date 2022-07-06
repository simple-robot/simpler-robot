/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (即 simple robot的v3版本，因此亦可称为 simple-robot v3 、simbot v3 等) 的一部分。
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
