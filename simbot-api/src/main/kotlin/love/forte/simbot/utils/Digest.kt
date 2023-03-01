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


/**
 * @see java.security.MessageDigest
 */
public typealias Digest = java.security.MessageDigest


/**
 * 根据一个算法名称得到一个对应的信息摘要算法.
 *
 * @throws java.security.NoSuchAlgorithmException – if no Provider supports a MessageDigestSpi implementation for the specified algorithm.
 *
 * @see java.security.MessageDigest.getInstance
 */
public fun digest(algorithm: String): Digest = Digest.getInstance(algorithm)

/**
 * 根据一个算法名称得到一个对应的信息摘要算法, 并在 [block] 中使用它, 然后得到摘要结果。
 *
 * @throws java.security.NoSuchAlgorithmException – if no Provider supports a MessageDigestSpi implementation for the specified algorithm.
 *
 * @see digest
 */
public inline fun digest(algorithm: String, block: Digest.() -> Unit): ByteArray =
    digest(algorithm).also(block).digest()


/**
 * 尝试获取 `md5` 摘要算法，并使用它得到摘要结果。
 */
public inline fun md5(block: Digest.() -> Unit): ByteArray = digest("md5", block)

/**
 * 将一个 [ByteArray] 转为16进制的字符串。
 */
public fun ByteArray.toHex(): String {
    return buildString {
        this@toHex.forEach { b ->
            val str = (b.toInt() and 0xff).toString(16)
            if (str.length == 1) {
                append('0')
            }
            append(str)
        }
    }
}

/**
 * 将一个16进制的字符串转化为字节数组。
 */
public fun String.toHex(): ByteArray {
    val result = ByteArray(length / 2)
    for (idx in result.indices) {
        val srcIdx = idx * 2
        val high = this[srcIdx].toString().toInt(16) shl 4
        val low = this[srcIdx + 1].toString().toInt(16)
        result[idx] = (high or low).toByte()
    }
    
    return result
}
