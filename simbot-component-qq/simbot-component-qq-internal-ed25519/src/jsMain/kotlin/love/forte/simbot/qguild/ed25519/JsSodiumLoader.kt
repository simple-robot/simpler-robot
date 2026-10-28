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

import com.ionspin.kotlin.crypto.getSodiumLoaded
import com.ionspin.kotlin.crypto.sodiumLoaded
import kotlin.coroutines.suspendCoroutine

internal object JsSodiumLoader {
    suspend fun load() = suspendCoroutine { continuation ->
        if (!getSodiumLoaded()) {
            libsodiumReady.then<dynamic> {
                // TypeError: _sodium_init is not a function
                // TypeError: _sodium_init is not a function
                //     at <global>.<unknown>(C:\home\ionspin\Projects\Future\kotlin-multiplatform-libsodium\multiplatform-crypto-libsodium-bindings\src\jsMain\kotlin\com\ionspin\kotlin\crypto\JsSodiumLoader.kt:33)
                //     at <global>.processTicksAndRejections(node:internal/process/task_queues:105)

                // sodium_init()
                sodiumLoaded = true
                continuation.resumeWith(Result.success(Unit))
            }.catch { e ->
                continuation.resumeWith(Result.failure(e))
            }
        } else {
            continuation.resumeWith(Result.success(Unit))
        }
    }

    fun loadWithCallback(doneCallback: () -> (Unit)) {
        if (!getSodiumLoaded()) {
            libsodiumReady.then<dynamic> {
                // TypeError: _sodium_init is not a function
                // TypeError: _sodium_init is not a function
                //     at <global>.<unknown>(C:\home\ionspin\Projects\Future\kotlin-multiplatform-libsodium\multiplatform-crypto-libsodium-bindings\src\jsMain\kotlin\com\ionspin\kotlin\crypto\JsSodiumLoader.kt:33)
                //     at <global>.processTicksAndRejections(node:internal/process/task_queues:105)

                // sodium_init()
                sodiumLoaded = true
                doneCallback.invoke()
            }
        } else {
            doneCallback.invoke()
        }
    }
}
