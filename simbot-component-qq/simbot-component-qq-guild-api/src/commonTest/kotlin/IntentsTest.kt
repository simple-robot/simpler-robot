package test

import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.event.Intents
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


/**
 *
 * @author ForteScarlet
 */
class IntentsTest {

    @Test
    fun testIntentsFactory() {
        assertContains(
            Intents {
                audioAction()
            },
            EventIntents.AudioAction.intents,
        )

        assertEquals(0, Intents {}.value)

        Intents {
            audioAction()
            forumsEvent()
        }.also {
            assertContains(it, EventIntents.AudioAction.intents)
            assertContains(it, EventIntents.ForumsEvent.intents)
        }
    }

    private fun assertContains(actual: Intents, expect: Intents) {
        assertTrue(expect in actual)
    }

}
