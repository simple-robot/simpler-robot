/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.test

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import love.forte.simbot.DoubleID
import love.forte.simbot.Grouping
import love.forte.simbot.ID
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.Test

@Serializable
data class User(
    val id: DoubleID,
    val age: Int,
)


/**
 *
 * @author ForteScarlet
 */
// @Suppress("UNUSED_VARIABLE")
class IDTest {

    @Test
    fun test() {
        assert(123.ID.number == 123)
        assert(123456L.ID.number == 123456L)
        assert(123.456.ID.number == 123.456)
        assert(123.456F.ID.number == 123.456F)
        assert(BigDecimal("123.456").ID.value == BigDecimal("123.456"))
        assert(BigInteger.valueOf(123456).ID.value == BigInteger.valueOf(123456))

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
    fun groupingTest() {
        val group1 = Grouping("abc".ID, "好友")
        val j1 = Json.encodeToString(group1)
        assert(Json.decodeFromString<Grouping>(j1).id == "abc".ID)
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
private data class Role(
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    val id: ID,
    val name: String
)