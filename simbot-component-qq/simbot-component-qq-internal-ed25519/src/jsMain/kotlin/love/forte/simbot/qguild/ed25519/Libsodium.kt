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

@file:JsModule("libsodium-wrappers-sumo")
@file:JsNonModule

package love.forte.simbot.qguild.ed25519

import kotlin.js.Promise

@JsName("ready")
internal external val libsodiumReady: Promise<dynamic>

// @JsName("crypto_generichash")
// internal external fun cryptoGenerichash(hashLength: Int, inputMessage: Uint8Array): Uint8Array
//
// @JsName("crypto_hash_sha256")
// internal external fun cryptoHashSha256(message: Uint8Array): Uint8Array
//
// @JsName("crypto_hash_sha512")
// internal external fun cryptoHashSha512(message: Uint8Array): Uint8Array
//
// @JsName("crypto_hash_sha256_init")
// internal external fun cryptoHashSha256Init(): dynamic
