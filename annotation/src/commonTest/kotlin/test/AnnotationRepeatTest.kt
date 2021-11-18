package test

import love.forte.simbot.annotation.Listen
import love.forte.simbot.annotation.Listens
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
@Listen(String::class)
@Listen(Int::class)
class AnnotationRepeatTest {

    @Test
    fun getRepeatAnnotation() {
        val type = AnnotationRepeatTest::class
        val support = type.toAnnotationSupport()

        with(support) {
            getAnnotation(Listen::class).also {
                println(it)
            }
            getAnnotation(Listens::class).also {
                println(it)
            }
            getAnnotationsByType(Listen::class).also {
                println(it)
            }
            getAnnotationsByType(Listens::class).also {
                println(it)
            }
        }

    }

}