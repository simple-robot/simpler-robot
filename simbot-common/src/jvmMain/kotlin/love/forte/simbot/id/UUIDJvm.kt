/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.id

import java.util.UUID as JavaUUID

/**
 * 将 [UUID] 转化为 [Java UUID][JavaUUID]。
 *
 */
public inline val UUID.javaUUID: JavaUUID
    get() = JavaUUID(mostSignificantBits, leastSignificantBits)


/**
 * 将 [Java UUID][JavaUUID] 转化为 [UUID]。
 */
public inline val JavaUUID.simbotUUID: UUID
    get() = UUID.from(mostSignificantBits, leastSignificantBits)


/**
 * 通过 [java.util.Random] 构建随机的 [UUID]。
 *
 */
public fun randomUUID(random: java.util.Random): UUID {
    val data = ByteArray(16)
    random.nextBytes(data)
    return UUID.fromData(data)
}
