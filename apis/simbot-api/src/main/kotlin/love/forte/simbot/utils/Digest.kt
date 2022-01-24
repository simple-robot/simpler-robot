/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.utils

import java.security.MessageDigest



public typealias Digest = MessageDigest



public fun digest(algorithm: String): Digest = Digest.getInstance(algorithm)


public inline fun digest(algorithm: String, block: Digest.() -> Unit): ByteArray =
    digest(algorithm).also(block).digest()


public inline fun md5(block: Digest.() -> Unit): ByteArray = digest("md5", block)










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