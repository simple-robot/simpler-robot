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

package love.forte.simbot.utils

import kotlin.random.Random

/**
 * 一般为内部使用的随机ID工具。
 */
public object RandomIDUtil {
    
    /**
     * 生成一个类UUID风格的随机字符串。
     *
     * _需要注意的是返回值内的字符范围并非与hex对应。_
     */
    @JvmStatic
    @JvmOverloads
    public fun randomID(random: Random = Random): String {
        val lsb = random.nextLong()
        val msb = random.nextLong()
        val buf = ByteArray(32).apply {
            formatUnsignedLong(lsb, 20, 12)
            formatUnsignedLong(lsb ushr 48, 16, 4)
            formatUnsignedLong(msb, 12, 4)
            formatUnsignedLong(msb ushr 16, 8, 4)
            formatUnsignedLong(msb ushr 32, 0, 8)
        }
        return String(buf, Charsets.UTF_8)
    }
    
    private fun ByteArray.formatUnsignedLong(value: Long, offset: Int, len: Int) {
        var v = value
        var charPos = offset + len
        val mask = (1 shl 4) - 1
        do {
            this[--charPos] = D[v.toInt() and mask].code.toByte()
            v = v ushr 4
        } while (charPos > offset)
    }
    
    private const val D = "1908356FORTESCAL"
    
    
}

@JvmSynthetic
public fun randomIdStr(random: Random = Random): String = RandomIDUtil.randomID(random)
