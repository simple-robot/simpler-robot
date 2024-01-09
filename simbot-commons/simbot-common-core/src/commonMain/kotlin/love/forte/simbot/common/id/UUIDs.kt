/*
 *     Copyright (c) 2023-2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.common.id

// 以下内容参考自JDK `java.util.UUID`
/*
 * Copyright (c) 2003, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/**
 * 尝试将字符串解析为UUID的高低64位整型。
 *
 * @throws IllegalArgumentException 如果无法解析
 */
public inline fun <T> String.toUUIDSigs(block: (most: Long, least: Long) -> T): T {
    require(length <= 36) { "UUID string too large" }

    val dash1: Int = indexOf('-', 0)
    val dash2: Int = indexOf('-', dash1 + 1)
    val dash3: Int = indexOf('-', dash2 + 1)
    val dash4: Int = indexOf('-', dash3 + 1)
    val dash5: Int = indexOf('-', dash4 + 1)


    // For any valid input, dash1 through dash4 will be positive and dash5
    // negative, but it's enough to check dash4 and dash5:
    // - if dash1 is -1, dash4 will be -1
    // - if dash1 is positive but dash2 is -1, dash4 will be -1
    // - if dash1 and dash2 is positive, dash3 will be -1, dash4 will be
    //   positive, but so will dash5
    require(!(dash4 < 0 || dash5 >= 0)) { "Invalid UUID string: $this" }

    var mostSigBits: Long = toLong(0, dash1, 16) and 0xffffffffL
    mostSigBits = mostSigBits shl 16
    mostSigBits = mostSigBits or (toLong(dash1 + 1, dash2, 16) and 0xffffL)
    mostSigBits = mostSigBits shl 16
    mostSigBits = mostSigBits or (toLong(dash2 + 1, dash3, 16) and 0xffffL)
    var leastSigBits: Long = toLong(dash3 + 1, dash4, 16) and 0xffffL
    leastSigBits = leastSigBits shl 48
    leastSigBits = leastSigBits or (toLong(dash4 + 1, length, 16) and 0xffffffffffffL)

    return block(mostSigBits, leastSigBits)
}

//// 以下内容参考自 `kotlin.text` 中相关实现
/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

/**
 * Checks whether the given [radix] is valid radix for string to number and number to string conversion.
 */
@PublishedApi
internal fun checkRadix(radix: Int): Int {
    if (radix !in 2..36) {
        throw IllegalArgumentException("radix $radix was not in valid range ${2..36}")
    }
    return radix
}

@PublishedApi
internal fun String.toLong(begin: Int, end: Int, radix: Int): Long {
    checkRadix(radix)

    val length = this.length
    if (length == 0) return 0

    val start: Int
    val isNegative: Boolean
    val limit: Long

    val firstChar = this[0]
    if (firstChar < '0') {  // Possible leading sign
        if (length == 1) return 0L  // non-digit (possible sign) only, no digits after

        start = begin + 1

        when (firstChar) {
            '-' -> {
                isNegative = true
                limit = Long.MIN_VALUE
            }

            '+' -> {
                isNegative = false
                limit = -Long.MAX_VALUE
            }

            else -> throw IllegalArgumentException("unknown sign '$firstChar'")
        }
    } else {
        start = begin
        isNegative = false
        limit = -Long.MAX_VALUE
    }


    val limitForMaxRadix = (-Long.MAX_VALUE) / 36

    var limitBeforeMul = limitForMaxRadix
    var result = 0L
    for (i in start until end) {
        val digit = digitOf(this[i], radix)

        if (digit < 0) return 0
        if (result < limitBeforeMul) {
            if (limitBeforeMul == limitForMaxRadix) {
                limitBeforeMul = limit / radix

                if (result < limitBeforeMul) {
                    throw IllegalArgumentException()
                }
            } else {
                throw IllegalArgumentException()
            }
        }

        result *= radix

        if (result < limit + digit) throw IllegalArgumentException()

        result -= digit
    }

    return if (isNegative) result else -result
}


private fun digitOf(char: Char, radix: Int): Int = when {
    char in '0'..'9' -> char - '0'
    char in 'A'..'Z' -> char - 'A' + 10
    char in 'a'..'z' -> char - 'a' + 10
    char < '\u0080' -> -1
    char in '\uFF21'..'\uFF3A' -> char - '\uFF21' + 10 // full-width latin capital letter
    char in '\uFF41'..'\uFF5A' -> char - '\uFF41' + 10 // full-width latin small letter
    else -> char.digitToIntImpl()
}.let { if (it >= radix) -1 else it }

private object Digit {
    val rangeStart = intArrayOf(
        0x0030,
        0x0660,
        0x06f0,
        0x07c0,
        0x0966,
        0x09e6,
        0x0a66,
        0x0ae6,
        0x0b66,
        0x0be6,
        0x0c66,
        0x0ce6,
        0x0d66,
        0x0de6,
        0x0e50,
        0x0ed0,
        0x0f20,
        0x1040,
        0x1090,
        0x17e0,
        0x1810,
        0x1946,
        0x19d0,
        0x1a80,
        0x1a90,
        0x1b50,
        0x1bb0,
        0x1c40,
        0x1c50,
        0xa620,
        0xa8d0,
        0xa900,
        0xa9d0,
        0xa9f0,
        0xaa50,
        0xabf0,
        0xff10,
    )
}

/**
 * Returns the index of the largest element in [array] smaller or equal to the specified [needle],
 * or -1 if [needle] is smaller than the smallest element in [array].
 */
private fun binarySearchRange(array: IntArray, needle: Int): Int {
    var bottom = 0
    var top = array.size - 1
    var middle = -1
    var value = 0
    while (bottom <= top) {
        middle = (bottom + top) / 2
        value = array[middle]
        if (needle > value)
            bottom = middle + 1
        else if (needle == value)
            return middle
        else
            top = middle - 1
    }
    return middle - (if (needle < value) 1 else 0)
}

/**
 * Returns an integer from 0..9 indicating the digit this character represents,
 * or -1 if this character is not a digit.
 */
private fun Char.digitToIntImpl(): Int {
    val ch = this.code
    val index = binarySearchRange(Digit.rangeStart, ch)
    val diff = ch - Digit.rangeStart[index]
    return if (diff < 10) diff else -1
}


//// 下述代码参考自 JDK `java.lang.Long`
/*
 * Copyright (c) 1994, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

private const val DIGITS = "0123456789abcdef"

/**
 * the log2 of the base to format in (4 for hex, 3 for octal, 1 for binary)
 */
private const val SHIFT = 4
private const val MASK = 15

/**
 * Format a long (treated as unsigned) into a character buffer. If
 * `len` exceeds the formatted ASCII representation of `val`,
 * `buf` will be padded with leading zeroes.
 *
 * @param value the unsigned long to format
 * @param buf the character buffer to write to
 * @param offset the offset in the destination buffer to start at
 * @param len the number of characters to write
 */
/* byte[]/LATIN1 version */
private fun formatUnsignedLong0(value: Long, buf: ByteArray, offset: Int, len: Int) {
    var v = value
    var charPos = offset + len
    do {
        buf[--charPos] = DIGITS[v.toInt() and MASK].code.toByte()
        v = v ushr SHIFT
    } while (charPos > offset)
}


internal fun uuidString(msb: Long, lsb: Long): String {
    val buf = ByteArray(36)
    formatUnsignedLong0(lsb, buf, 24, 12)
    formatUnsignedLong0(lsb ushr 48, buf, 19, 4)
    formatUnsignedLong0(msb, buf, 14, 4)
    formatUnsignedLong0(msb ushr 16, buf, 9, 4)
    formatUnsignedLong0(msb ushr 32, buf, 0, 8)

    buf[23] = '-'.code.toByte()
    buf[18] = '-'.code.toByte()
    buf[13] = '-'.code.toByte()
    buf[8] = '-'.code.toByte()

    return buf.decodeToString()
}

/*
    public String toString() {
        return (digits(mostSigBits >> 32, 8) + "-" +
                digits(mostSigBits >> 16, 4) + "-" +
                digits(mostSigBits, 4) + "-" +
                digits(leastSigBits >> 48, 4) + "-" +
                digits(leastSigBits, 12));
    }

    /** Returns val represented by the specified number of hex digits. */
    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }
 */
