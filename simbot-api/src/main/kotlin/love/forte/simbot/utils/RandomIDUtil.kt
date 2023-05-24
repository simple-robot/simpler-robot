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

package love.forte.simbot.utils

import kotlin.random.Random

/**
 * 随机ID工具。
 *
 * 一般为内部使用。
 */
public object RandomIDUtil {

    /**
     * 生成一个32字节长度的随机字符串。
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

/**
 * 得到一个随机ID字符串。
 * @see RandomIDUtil.randomID
 */
@JvmSynthetic
public fun randomIdStr(random: Random = Random): String = RandomIDUtil.randomID(random)
