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
import net.i2p.crypto.eddsa.EdDSAEngine
import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.EdDSAPublicKey
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec


@InternalEd25519Api
public object EddsaEd25519KeyPairGenerator : Ed25519KeyPairGenerator {
    override fun generate(seed: ByteArray): Ed25519KeyPair {
        return EddsaEd25519KeyPair(seed)
    }
}

@InternalEd25519Api
public class EddsaEd25519KeyPair(
    seed: ByteArray,
) : Ed25519KeyPair {
    override val privateKey: EddsaEd25519PrivateKey =
        EddsaEd25519PrivateKey(
            seed,
            EdDSAPrivateKey(
                EdDSAPrivateKeySpec(
                    seed,
                    EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519)
                )
            )
        )

    override val publicKey: EddsaEd25519PublicKey =
        EddsaEd25519PublicKey(
            EdDSAPublicKey(
                EdDSAPublicKeySpec(
                    privateKey.privateKey.abyte,
                    EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519)
                )
            )
        )
}

@InternalEd25519Api
public class EddsaEd25519PrivateKey(
    private val seed: ByteArray,
    internal val privateKey: EdDSAPrivateKey,
) : Ed25519PrivateKey {
    private val _bytes64: ByteArray by lazy {
        seed + privateKey.abyte
    }

    override val bytes: ByteArray
        get() = _bytes64.clone()

    @Throws(Exception::class)
    override fun sign(data: ByteArray): Signature {
        return withEdDSAEngine {
            initSign(privateKey)
            Signature(signOneShot(data))
        }
    }
}

@InternalEd25519Api
public class EddsaEd25519PublicKey(
    private val publicKey: EdDSAPublicKey,
) : Ed25519PublicKey {
    override val bytes: ByteArray
        get() = publicKey.abyte

    override fun verify(signature: Signature, data: ByteArray): Boolean {
        return withEdDSAEngine {
            initVerify(publicKey)
            update(data)
            verify(signature.data)
        }
    }
}

private inline fun <R> withEdDSAEngine(
    block: EdDSAEngine.() -> R,
): R {
    return EdDSAEngine().block()
}
