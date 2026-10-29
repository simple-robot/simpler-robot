/*
 *     Copyright (c) 2026. ForteScarlet.
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

import kotlinx.coroutines.test.runTest
import love.forte.simbot.qguild.ed25519.annotations.InternalEd25519Api
import kotlin.test.*

@OptIn(InternalEd25519Api::class)
class Ed25519WasmJsTests {
    @Test
    fun rfc8032EmptyMessageVector() = runTest {
        val seed = "9d61b19deffd5a60ba844af492ec2cc44449c5697b326919703bac031cae7f60".hexToByteArray()
        val publicKey = "d75a980182b10ab7d54bfed3c964073a0ee172f3daa62325af021a68f707511a".hexToByteArray()
        val expectedSignature = (
            "e5564300c360ac729086e2cc806e828a84877f1eb8e5d974d873e06522490155" +
                "5fb8821590a33bacc61e39701cf9b46bd25bf5f0595bbe24655141438e7a100b"
            ).hexToByteArray()
        val pair = ed25519KeyPairGenerator().generate(seed)
        val message = byteArrayOf()
        val signature = pair.privateKey.sign(message)

        assertContentEquals(publicKey, pair.publicKey.bytes)
        assertContentEquals(seed + publicKey, pair.privateKey.bytes)
        assertContentEquals(expectedSignature, signature.data)
        assertTrue(pair.publicKey.verify(signature, message))
    }

    @Test
    fun binaryMessageAndInvalidSignatures() = runTest {
        val pair = ed25519KeyPairGenerator().generate(ByteArray(32) { it.toByte() })
        val message = ByteArray(256) { it.toByte() }
        val signature = pair.privateKey.sign(message)

        assertEquals(64, signature.data.size)
        assertTrue(pair.publicKey.verify(signature, message))
        assertFalse(pair.publicKey.verify(signature, message.copyOf().apply { this[128] = 0 }))
        val tamperedSignature = signature.data.copyOf().apply {
            this[0] = (this[0].toInt() xor 1).toByte()
        }
        assertFalse(pair.publicKey.verify(Signature(tamperedSignature), message))
        assertFalse(pair.publicKey.verify(Signature(ByteArray(63)), message))
        assertFalse(pair.publicKey.verify(Signature(ByteArray(65)), message))
        assertContentEquals(signature.data, pair.privateKey.sign(message).data)
    }

    @Test
    fun rejectsInvalidSeedLength() = runTest {
        val generator = ed25519KeyPairGenerator()
        assertFailsWith<IllegalArgumentException> { generator.generate(ByteArray(31)) }
        assertFailsWith<IllegalArgumentException> { generator.generate(ByteArray(33)) }
    }
}
