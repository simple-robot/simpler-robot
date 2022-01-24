/*
 *  Copyright (c) 2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.utils

import kotlin.random.Random

public object RandomIDUtil {

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