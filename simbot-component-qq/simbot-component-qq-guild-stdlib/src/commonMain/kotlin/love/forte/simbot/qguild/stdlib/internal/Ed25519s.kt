/*
 * Copyright (c) 2024-2025. ForteScarlet.
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

package love.forte.simbot.qguild.stdlib.internal

import love.forte.simbot.qguild.ed25519.Ed25519KeyPair
import love.forte.simbot.qguild.ed25519.annotations.InternalEd25519Api
import love.forte.simbot.qguild.ed25519.ed25519KeyPairGenerator

@OptIn(InternalEd25519Api::class)
internal suspend fun genEd25519Keypair(seed: ByteArray): Ed25519KeyPair {
    return ed25519KeyPairGenerator().generate(seed)
}
