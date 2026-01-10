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
import org.bouncycastle.crypto.CipherParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer

@InternalEd25519Api
public object BouncyCastleEd25519KeyPairGenerator : Ed25519KeyPairGenerator {
    override fun generate(seed: ByteArray): BouncyCastleEd25519KeyPair {
        return BouncyCastleEd25519KeyPair(seed)
    }
}

/**
 * Ed25519 key pair implementation based on BouncyCastle.
 *
 * @author ForteScarlet
 */
@InternalEd25519Api
public class BouncyCastleEd25519KeyPair(
    seed: ByteArray,
) : Ed25519KeyPair {
    override val privateKey: BouncyCastleEd25519PrivateKey =
        BouncyCastleEd25519PrivateKey(
            seed,
            Ed25519PrivateKeyParameters(seed, 0)
        )

    override val publicKey: BouncyCastleEd25519PublicKey =
        BouncyCastleEd25519PublicKey(
            privateKey.privateKey.generatePublicKey()
        )
}

@InternalEd25519Api
public class BouncyCastleEd25519PrivateKey(
    private val seed: ByteArray,
    internal val privateKey: Ed25519PrivateKeyParameters
) : Ed25519PrivateKey {
    private val _bytes64: ByteArray by lazy {
        val publicKey = privateKey.generatePublicKey()
        val publicKeyBytes = publicKey.encoded

        seed + publicKeyBytes
    }

    override val bytes: ByteArray
        get() = _bytes64.clone()

    override fun sign(data: ByteArray): Signature {
        return withSigner(true, privateKey) {
            update(data, 0, data.size)
            Signature(generateSignature())
        }
    }
}

@InternalEd25519Api
public class BouncyCastleEd25519PublicKey(
    private val publicKey: Ed25519PublicKeyParameters
) : Ed25519PublicKey {
    override val bytes: ByteArray
        get() = publicKey.encoded

    override fun verify(signature: Signature, data: ByteArray): Boolean {
        return withSigner(false, publicKey) {
            update(data, 0, data.size)
            verifySignature(signature.data)
        }
    }
}

private inline fun <R> withSigner(
    forSigning: Boolean,
    parameters: CipherParameters,
    block: Ed25519Signer.() -> R
): R {
    return Ed25519Signer().apply { init(forSigning, parameters) }.block()
}
