package test

import love.forte.simbot.utils.view
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class ListViewTest {

    @Test
    fun viewTest() {
        val list = mutableListOf(1,2,3)
        val view = list.view()

        println(view)

        list.addAll(listOf(5,6,7,8))

        println(view)

    }

}