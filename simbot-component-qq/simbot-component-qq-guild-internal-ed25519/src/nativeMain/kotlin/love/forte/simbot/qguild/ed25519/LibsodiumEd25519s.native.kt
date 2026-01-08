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

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.signature.InvalidSignatureException
import kotlinx.coroutines.CompletableDeferred
import love.forte.simbot.qguild.ed25519.annotations.InternalEd25519Api


@InternalEd25519Api
public object LibsodiumEd25519KeyPairGenerator : Ed25519KeyPairGenerator {
    override fun generate(seed: ByteArray): Ed25519KeyPair {
        return LibsodiumEd25519KeyPair(seed)
    }
}

@InternalEd25519Api
@OptIn(ExperimentalUnsignedTypes::class)
public class LibsodiumEd25519KeyPair(seed: ByteArray) : Ed25519KeyPair {
    private val pair = com.ionspin.kotlin.crypto.signature.Signature.seedKeypair(seed.asUByteArray())

    override val privateKey: Ed25519PrivateKey =
        LibsodiumEd25519PrivateKey(pair.secretKey)

    override val publicKey: Ed25519PublicKey =
        LibsodiumEd25519PublicKey(pair.publicKey)
}

@InternalEd25519Api
@OptIn(ExperimentalUnsignedTypes::class)
public class LibsodiumEd25519PrivateKey(private val key: UByteArray) : Ed25519PrivateKey {
    override val bytes: ByteArray
        get() = key.toByteArray()

    override fun sign(data: ByteArray): Signature {
        return Signature(
            com.ionspin.kotlin.crypto.signature.Signature.sign(data.asUByteArray(), key).asByteArray()
        )
    }
}

@InternalEd25519Api
@OptIn(ExperimentalUnsignedTypes::class)
public class LibsodiumEd25519PublicKey(private val key: UByteArray) : Ed25519PublicKey {
    override val bytes: ByteArray
        get() = key.toByteArray()

    override fun verify(signature: Signature, data: ByteArray): Boolean {
        return try {
            com.ionspin.kotlin.crypto.signature.Signature.verifyDetached(
                signature.data.asUByteArray(),
                data.asUByteArray(),
                key
            )

            true
        } catch (ise: InvalidSignatureException) {
            false
        }
    }
}


internal suspend fun initialLibsodiumIfNecessary() {
    initialLibsodium // Init by lazy
    return initialDeferred.await()
}

private val initialDeferred = CompletableDeferred(Unit)

/**
 * Initialed libsodium by lazy.
 */
private val initialLibsodium: Unit by lazy(
    mode = LazyThreadSafetyMode.SYNCHRONIZED
) {
    if (!LibsodiumInitializer.isInitialized()) {
        ed25519sLogger.info("LibsodiumInitializer is not initialed yet, initializing...")

        LibsodiumInitializer.initializeWithCallback {
            initialDeferred.complete(Unit)
            ed25519sLogger.info("LibsodiumInitializer initialized")
        }
    }
    Unit
}
