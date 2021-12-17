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