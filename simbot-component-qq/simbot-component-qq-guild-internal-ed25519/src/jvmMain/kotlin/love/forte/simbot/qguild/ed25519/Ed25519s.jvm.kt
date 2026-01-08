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

package love.forte.simbot.qguild.ed25519

import love.forte.simbot.qguild.ed25519.annotations.InternalEd25519Api

/**
 * 根据当前平台情况和classpath环境选择一个合适的 [Ed25519KeyPairGenerator] 实例。
 *
 * 在非 JVM 平台中，会使用 [libsodium bindings](https://github.com/ionspin/kotlin-multiplatform-libsodium),
 * 在 JVM 中，会优先检测是否存在 [BouncyCastle](https://www.bouncycastle.org/) 依赖中所需使用的类
 *
 * (`org.bouncycastle.crypto.CipherParameters`,
 * `org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters`,
 * `org.bouncycastle.crypto.params.Ed25519PublicKeyParameters`,
 * `org.bouncycastle.crypto.signers.Ed25519Signer`) 等，详见
 * `love.forte.simbot.qguild.ed25519` 包下与 `BouncyCastle` 相关的类。
 *
 * 如果不存在则会使用 [ed25519-java](https://github.com/str4d/ed25519-java)，
 * 它更轻量级且不依赖于其他库，因此其依赖会默认被传递。
 *
 */
@InternalEd25519Api
public actual suspend fun ed25519KeyPairGenerator(): Ed25519KeyPairGenerator {
    return if (bouncyCastleAccessible) {
        BouncyCastleEd25519KeyPairGenerator
    } else {
        EddsaEd25519KeyPairGenerator
    }
}


private val bouncyCastleRequiredClasses = arrayOf(
    "org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters",
    "org.bouncycastle.crypto.params.Ed25519PublicKeyParameters",
    "org.bouncycastle.crypto.signers.Ed25519Signer",
)

private class BouncyCastleInaccessibleException : Exception("BouncyCastle is inaccessible")

private val bouncyCastleAccessible: Boolean by lazy {
    val bouncyCastleInaccessible = BouncyCastleInaccessibleException()
    bouncyCastleRequiredClasses.all {
        runCatching {
            Class.forName(it)
            true
        }.getOrElse { e ->
            bouncyCastleInaccessible.addSuppressed(e)
            false
        }
    }.also {
        if (!it) {
            val msg = "BouncyCastle is inaccessible because of the classes {} are not found in classpath. " +
                    "`ed25519-java` will be used instead."

            ed25519sLogger.info(msg, bouncyCastleRequiredClasses.asList())
            ed25519sLogger.debug(msg, bouncyCastleRequiredClasses.asList(), bouncyCastleInaccessible)
        }

    }
}
