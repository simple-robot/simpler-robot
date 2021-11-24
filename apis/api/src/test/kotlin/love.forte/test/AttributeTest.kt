package love.forte.test

import love.forte.simbot.AttributeHashMap
import love.forte.simbot.attribute
import love.forte.simbot.set
import org.junit.Test


/**
 *
 * @author ForteScarlet
 */
class AttributeTest {

    private val map = AttributeHashMap()

    @Test
    fun test() {
        val attr1 = attribute<User1>("user")
        val attr2 = attribute<Foo>("foo")

        map[attr1] = User1("Forte")
        map[attr2] = Foo(2)

        println(map)
    }



}



data class User1(val name: String)
data class Foo(val age: Int)