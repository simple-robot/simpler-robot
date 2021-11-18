package love.forte

import org.junit.Test

/**
 *
 * @author ForteScarlet
 */
class JTest {
    @Test
    fun a() {
        var i1 = 10
        var i = i1++
        i = ++i1
        println(i)
    }
}