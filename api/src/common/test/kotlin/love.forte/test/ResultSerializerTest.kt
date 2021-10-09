package love.forte.test

import love.forte.simbot.api.*
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class ResultSerializerTest {

    class MyBotManager : BaseBotManager<Bot>() {
        override val component: Component
            get() = Components.resolve("myBotManager")

        override fun get(id: String): Bot? = null
    }

    @Test
    fun test() {
        val b1 = MyBotManager()
        val b2 = MyBotManager()

        println(b1 == b2)
        println(b1 === b2)
        println(b1.component == b2.component)
        println(b1.component === b2.component)


    }

}