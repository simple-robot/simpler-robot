/*
 *     Copyright (c) 2023-2024. ForteScarlet.
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

@file:JvmName("Identifies")
@file:JvmMultifileClass

package love.forte.simbot.common.id

import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.common.id.LongID.Companion.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.UIntID.Companion.ID
import love.forte.simbot.common.id.ULongID.Companion.ID
import love.forte.simbot.common.id.UUID.Companion.UUID
import kotlin.random.Random
import kotlin.random.asKotlinRandom

/**
 * 一个设计为仅供 Java 用户使用的API
 */
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    message = "一个设计为仅供 Java 用户使用的ID API",
    level = RequiresOptIn.Level.ERROR
)
@MustBeDocumented
public annotation class ID4J

/**
 *
 * @see Long.ID
 */
@JvmName("of")
@ID4J
public fun longIDOf(value: Long): LongID = value.ID

/**
 * @throws NumberFormatException if the string is not a valid representation of a number.
 * @see ULong.ID
 */
@JvmName("ofULong")
@ID4J
public fun uLongIDOf(value: String): ULongID = value.toULong().ID

/**
 *
 * @see IntID.ID
 */
@JvmName("of")
@ID4J
public fun intIDOf(value: Int): IntID = value.ID

/**
 *
 * @see UInt.ID
 */
@JvmName("ofUInt")
@ID4J
public fun uIntIDOf(value: UInt): UIntID = value.ID

/**
 *
 * @throws NumberFormatException if the string is not a valid representation of a number.
 * @see UInt.ID
 */
@JvmName("ofUInt")
@ID4J
public fun uIntIDOf(value: String): UIntID = value.toUInt().ID

/**
 *
 * @see String.ID
 */
@JvmName("of")
@ID4J
public fun stringIDOf(value: String): StringID = value.ID

/**
 *
 * @see CharSequence.ID
 */
@JvmName("of")
@ID4J
public fun stringIDOf(value: CharSequence): StringID = value.ID

// UUIDs

/**
 *
 * @see UUID.random
 */
@JvmName("uuid")
@JvmOverloads
@ID4J
public fun uuidOf(random: Random? = null): UUID =
    if (random == null) UUID.random() else UUID.random(random)

/**
 * 将 [java.util.UUID] 转化为 [UUID]。
 *
 * @see java.util.UUID.simbotUUID
 */
@JvmName("uuid")
@ID4J
public fun uuidOf(javaUuid: java.util.UUID): UUID = javaUuid.simbotUUID

/**
 * 使用 [java.util.Random] 得到一个随机的 [UUID]。
 * @see java.util.UUID.simbotUUID
 */
@JvmName("uuid")
@ID4J
public fun uuidOf(javaRandom: java.util.Random?): UUID =
    uuidOf(javaRandom?.asKotlinRandom())

/**
 * 将 [UUID] 转化为 [java.util.UUID]。
 *
 * @since 4.6.1
 */
@ID4J
public fun UUID.toJava(): java.util.UUID =
    java.util.UUID(mostSignificantBits, leastSignificantBits)
