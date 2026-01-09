/*
 *     Copyright (c) 2025-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

package love.forte.simbot.qguild.ed25519

import com.ionspin.kotlin.crypto.getSodiumLoaded
import com.ionspin.kotlin.crypto.signature.InvalidSignatureException
import com.ionspin.kotlin.crypto.signature.Signature
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
    private val pair = Signature.seedKeypair(seed.asUByteArray())

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

    override fun sign(data: ByteArray): love.forte.simbot.qguild.ed25519.Signature {
        return Signature(
            Signature.sign(data.asUByteArray(), key).asByteArray()
        )
    }
}

@InternalEd25519Api
@OptIn(ExperimentalUnsignedTypes::class)
public class LibsodiumEd25519PublicKey(private val key: UByteArray) : Ed25519PublicKey {
    override val bytes: ByteArray
        get() = key.toByteArray()

    override fun verify(signature: love.forte.simbot.qguild.ed25519.Signature, data: ByteArray): Boolean {
        return try {
            Signature.verifyDetached(
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
    if (!getSodiumLoaded()) {
        ed25519sLogger.info("LibsodiumInitializer is not initialed yet, initializing...")
        JsSodiumLoader.load()
        ed25519sLogger.info("LibsodiumInitializer initialized")
    }
    // TODO
    //  TypeError: _sodium_init is not a function
    //  TypeError: _sodium_init is not a function
    //      at <global>.<unknown>(C:\home\ionspin\Projects\Future\kotlin-multiplatform-libsodium\multiplatform-crypto-libsodium-bindings\src\jsMain\kotlin\com\ionspin\kotlin\crypto\JsSodiumLoader.kt:33)
    //      at <global>.processTicksAndRejections(node:internal/process/task_queues:105)

    // if (!LibsodiumInitializer.isInitialized()) {
    //     ed25519sLogger.info("LibsodiumInitializer is not initialed yet, initializing...")
    //     LibsodiumInitializer.initialize()
    //     ed25519sLogger.info("LibsodiumInitializer initialized")
    // }
}
