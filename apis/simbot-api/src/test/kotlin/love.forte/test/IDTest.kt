/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.test

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import love.forte.simbot.DoubleID
import love.forte.simbot.Grouping
import love.forte.simbot.ID
import love.forte.simbot.LongID
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.test.Test

@Serializable
data class User(
    val id: DoubleID,
    val age: Int,
)

@Serializable
data class User2(val id: LongID, val name: String)


/**
 *
 * @author ForteScarlet
 */
class IDTest {

    @Test
    fun test() {
        val intId = 123.ID
        val longId = 123456L.ID
        val doubleId = 123.456.ID
        val floatId = 123.456F.ID
        val bdId = BigDecimal("123.456").ID
        val biId = BigInteger.valueOf(123456).ID

    }

    @Test
    fun idSerializerTest() {
        // val user = User(100L.ID, 24)
        val user = User(5.2355.ID, 24)

        println(user)

        val json = Json
        println(json.encodeToString(user))


    }

    @Test
    fun idEqualsTest() {
        val id: ID = 123.ID
        val id1: ID = "123".ID
        assert(id == id1)
    }

    @Test
    fun bitMapTest() {
        val set = BitSet()
        set.set(4)
        set.set(44)
        set.set(444)
        set.set(4444)
        set.set(44444)
        println(set)
        println(set.cardinality())
        println(set.size())
    }


    @Test
    fun groupingTest() {
        val group1 = Grouping("abc".ID, "好友")

        val j1 = Json.encodeToString(group1)

        println(j1)

        println(Json.decodeFromString<Grouping>(j1))


    }

    @Test
    fun idSerializerTest2() {
        val id1 = 10.ID
        val id2 = 1.2.ID

        val role1 = Role(id1, "admin")
        val role2 = Role(id2, "user")

        println(role1)
        println(role2)
        println()

        val json = Json

        val j1 = json.encodeToString(role1)
        val j2 = json.encodeToString(role2)

        println(j1)
        println(j2)

        val r1 = json.decodeFromString<Role>(j1)
        val r2 = json.decodeFromString<Role>(j2)

        println()
        println(r1)
        println(r2)

        println(r1 == role1)
        println(r2 == role2)
    }

}

@Serializable
data class Role(
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    val id: ID,
    val name: String
)