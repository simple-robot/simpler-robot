package love.forte.test

import love.forte.simbot.api.message.MessagesBuilder
import love.forte.simbot.api.message.Text
import love.forte.simbot.api.message.toText
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class TextTest {

    // @Test TODO
    fun textTest() {
        val t1 = Text()
        val t2 = Text { "Abc" }
        val t3 = "23333".toText()

        println(t1 + t2)
        println(t2 + t3)
        println(t1 + t2 + t3 + ", Hello World")

        val messages = MessagesBuilder().append(t2).append(t2).append(t3)
            .build()

        println(messages.size)

        for (m in messages) {
            println(m)
        }

    }

}