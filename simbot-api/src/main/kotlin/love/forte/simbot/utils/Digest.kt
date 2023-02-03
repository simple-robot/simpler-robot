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
