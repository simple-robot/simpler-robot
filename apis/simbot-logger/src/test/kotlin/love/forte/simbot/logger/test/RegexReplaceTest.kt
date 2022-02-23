package love.forte.simbot.logger.test

import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class RegexReplaceTest {
    private val regex = Regex("\\{}")

    @Test
    fun test() {
        val msg = "Hello {}, My {}."

        var index = 0

        val replaced = regex.replace(msg) { result ->
            println(result)
            println(result.value)
            "{}"
        }

        println(replaced)

    }
}