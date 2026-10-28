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

@file:OptIn(ExperimentalWasmJsInterop::class)

package love.forte.simbot.qguild.ed25519

import kotlinx.coroutines.await
import love.forte.simbot.qguild.ed25519.annotations.InternalEd25519Api
import kotlin.js.Promise

@InternalEd25519Api
public actual suspend fun ed25519KeyPairGenerator(): Ed25519KeyPairGenerator {
    sodium.ready.await()
    return WasmJsEd25519KeyPairGenerator
}

@OptIn(InternalEd25519Api::class)
private object WasmJsEd25519KeyPairGenerator : Ed25519KeyPairGenerator {
    override fun generate(seed: ByteArray): Ed25519KeyPair {
        require(seed.size == 32) { "Ed25519 seed must contain 32 bytes" }
        val pair = sodium.crypto_sign_seed_keypair(seed.toUint8Array())
        return WasmJsEd25519KeyPair(pair)
    }
}

@OptIn(InternalEd25519Api::class)
private class WasmJsEd25519KeyPair(pair: SodiumKeyPair) : Ed25519KeyPair {
    override val privateKey: Ed25519PrivateKey = WasmJsEd25519PrivateKey(pair.privateKey)
    override val publicKey: Ed25519PublicKey = WasmJsEd25519PublicKey(pair.publicKey)
}

@OptIn(InternalEd25519Api::class)
private class WasmJsEd25519PrivateKey(private val key: Uint8Array) : Ed25519PrivateKey {
    override val bytes: ByteArray
        get() = key.toByteArray()

    override fun sign(data: ByteArray): Signature =
        Signature(sodium.crypto_sign_detached(data.toUint8Array(), key).toByteArray())
}

@OptIn(InternalEd25519Api::class)
private class WasmJsEd25519PublicKey(private val key: Uint8Array) : Ed25519PublicKey {
    override val bytes: ByteArray
        get() = key.toByteArray()

    override fun verify(signature: Signature, data: ByteArray): Boolean {
        if (signature.data.size != Ed25519KeyPair.CRYPTO_SIGN_BYTES) {
            return false
        }
        return sodium.crypto_sign_verify_detached(signature.data.toUint8Array(), data.toUint8Array(), key)
    }
}

// Use the default export: libsodium installs its crypto functions after ready resolves.
@JsModule("libsodium-wrappers-sumo")
private external object LibsodiumModule : JsAny {
    val default: Libsodium
}

private val sodium: Libsodium
    get() = LibsodiumModule.default

@Suppress("FunctionNaming")
private external interface Libsodium : JsAny {
    val ready: Promise<JsAny?>
    fun crypto_sign_seed_keypair(seed: Uint8Array): SodiumKeyPair
    fun crypto_sign_detached(message: Uint8Array, privateKey: Uint8Array): Uint8Array
    fun crypto_sign_verify_detached(signature: Uint8Array, message: Uint8Array, publicKey: Uint8Array): Boolean
}

private external interface SodiumKeyPair : JsAny {
    val privateKey: Uint8Array
    val publicKey: Uint8Array
}

private external class Uint8Array(length: Int) : JsAny {
    val length: Int
}

private fun ByteArray.toUint8Array(): Uint8Array =
    Uint8Array(size).also { array ->
        for (index in indices) {
            setByte(array, index, this[index].toInt() and 0xff)
        }
    }

private fun Uint8Array.toByteArray(): ByteArray =
    ByteArray(length) { index -> getByte(this, index).toByte() }

private fun getByte(array: Uint8Array, index: Int): Int = js("array[index]")

private fun setByte(array: Uint8Array, index: Int, value: Int): Unit = js("{ array[index] = value; }")
