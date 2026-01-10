/*
 * Copyright (c) 2025. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

@file:JvmName("Ed25519s")
@file:JvmMultifileClass

package love.forte.simbot.qguild.ed25519

import love.forte.simbot.logger.Logger
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.ed25519.annotations.InternalEd25519Api
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

internal val ed25519sLogger: Logger =
    LoggerFactory.getLogger("love.forte.simbot.qguild.ed25519.Ed25519s")

@InternalEd25519Api
public interface Ed25519KeyPairGenerator {
    public fun generate(seed: ByteArray): Ed25519KeyPair
}

/**
 * 根据当前平台情况和classpath环境选择一个合适的 [Ed25519KeyPairGenerator] 实例。
 *
 * 在非 JVM 平台中，会使用 [libsodium bindings](https://github.com/ionspin/kotlin-multiplatform-libsodium)。
 * 由于 libsodium bindings 初始化可能需要挂起，因此 [ed25519KeyPairGenerator] 在首次执行时也可能是挂起的。
 *
 * 在 JVM 中，会优先检测是否存在 [BouncyCastle](https://www.bouncycastle.org/) 依赖中所需使用的类,
 * 例如:
 *
 * - `org.bouncycastle.crypto.CipherParameters`
 * - `org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters`
 * - `org.bouncycastle.crypto.params.Ed25519PublicKeyParameters`
 * - `org.bouncycastle.crypto.signers.Ed25519Signer`
 *
 * 等等，详见
 * `love.forte.simbot.qguild.ed25519` 包下与 `BouncyCastle` 相关的类。
 *
 * 如果不存在则会使用 [ed25519-java](https://github.com/str4d/ed25519-java)，
 * 它更轻量级且不依赖于其他库，因此其依赖会默认被传递。
 *
 */
@InternalEd25519Api
public expect suspend fun ed25519KeyPairGenerator(): Ed25519KeyPairGenerator

@InternalEd25519Api
public interface Ed25519KeyPair {
    public val privateKey: Ed25519PrivateKey
    public val publicKey: Ed25519PublicKey

    @InternalEd25519Api
    public companion object {
        public const val CRYPTO_SIGN_BYTES: Int = 64
    }
}

@InternalEd25519Api
public operator fun Ed25519KeyPair.component1(): Ed25519PrivateKey = privateKey

@InternalEd25519Api
public operator fun Ed25519KeyPair.component2(): Ed25519PublicKey = publicKey

/**
 * Ed25519 private key.
 */
@InternalEd25519Api
public interface Ed25519PrivateKey {
    /**
     * seed + public key 的 64 位字节私钥
     */
    public val bytes: ByteArray

    /**
     * Sign data with this private key.
     */
    @Throws(Exception::class)
    public fun sign(data: ByteArray): Signature
}

/**
 * Ed25519 public key.
 */
@InternalEd25519Api
public interface Ed25519PublicKey {
    public val bytes: ByteArray

    /**
     * Verify the signature.
     */
    @Throws(Exception::class)
    public fun verify(signature: Signature, data: ByteArray): Boolean
}

@JvmInline
@InternalEd25519Api
public value class Signature(public val data: ByteArray)

/**
 * Repeat to `length` == 32
 */
@InternalEd25519Api
public fun String.paddingEd25519Seed(): String {
    return when {
        length == 32 || isEmpty() -> this
        length > 32 -> substring(32)
        else -> {
            buildString(32) {
                append(this@paddingEd25519Seed)
                while (length < 32) {
                    append(
                        this@paddingEd25519Seed,
                        0,
                        kotlin.math.min(32 - length, this@paddingEd25519Seed.length)
                    )
                }
            }

        }
    }
}
