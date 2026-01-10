package love.forte.simbot.component.qgguild.test

import love.forte.simbot.component.qguild.QQGuildComponent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for [QQGuildComponent] class.
 */
class QQGuildComponentTests {

    @Test
    fun testComponentId() {
        val component = QQGuildComponent()
        assertEquals(QQGuildComponent.ID_VALUE, component.id)
        assertEquals("simbot.qqguild", component.id)
    }

    @Test
    fun testComponentEquality() {
        val component1 = QQGuildComponent()
        val component2 = QQGuildComponent()

        assertTrue(component1 == component2)
        assertEquals(component1.hashCode(), component2.hashCode())
    }

    @Test
    fun testSerializersModule() {
        val component = QQGuildComponent()
        val module = component.serializersModule

        assertNotNull(module)
        // The module should be the same as the one in the companion object
        assertEquals(QQGuildComponent.messageSerializersModule, module)
    }

    @Test
    fun testToString() {
        val component = QQGuildComponent()
        assertEquals("QQGuildComponent(id=simbot.qqguild)", component.toString())
    }
}
