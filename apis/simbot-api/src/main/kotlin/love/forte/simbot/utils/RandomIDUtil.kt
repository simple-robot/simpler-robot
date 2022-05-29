/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
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

package love.forte.simbot.utils

import kotlin.random.Random

/**
 * 一般为内部使用的随机ID工具。
 */
public object RandomIDUtil {
    
    /**
     * 生成一个近似UUID的随机字符串。
     */
    @JvmStatic
    public fun randomID(): String {
        // 还不知道 kotlin.Random 和 ThreadLocalRandom 之间的性能差距
        val lsb = Random.nextLong()
        val msb = Random.nextLong()
        val buf = ByteArray(32)
        formatUnsignedLong(lsb, buf, 20, 12)
        formatUnsignedLong(lsb ushr 48, buf, 16, 4)
        formatUnsignedLong(msb, buf, 12, 4)
        formatUnsignedLong(msb ushr 16, buf, 8, 4)
        formatUnsignedLong(msb ushr 32, buf, 0, 8)
        return String(buf, Charsets.UTF_8)
    }

    private fun formatUnsignedLong(value: Long, buf: ByteArray, offset: Int, len: Int) {
        var v = value
        var charPos = offset + len
        val mask = (1 shl 4) - 1
        do {
            buf[--charPos] = D[v.toInt() and mask].code.toByte()
            v = v ushr 4
        } while (charPos > offset)
    }

    private val D = charArrayOf(
        '1',
        '9',
        '0',
        '8',
        '3',
        '5',
        '6',
        'F',
        'O',
        'R',
        'T',
        'E',
        'S',
        'C',
        'A',
        'L',
    )

}