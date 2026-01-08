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
 * 在非 JVM 平台中，会使用 [libsodium bindings](https://github.com/ionspin/kotlin-multiplatform-libsodium)。
 *
 */
@InternalEd25519Api
public actual suspend fun ed25519KeyPairGenerator(): Ed25519KeyPairGenerator {
    initialLibsodiumIfNecessary()
    return LibsodiumEd25519KeyPairGenerator
}
