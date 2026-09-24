/*
 *     Copyright (c) 2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.component.qgguild.test

import kotlinx.serialization.json.Json
import love.forte.simbot.component.qguild.message.QGAttachmentMessage
import love.forte.simbot.component.qguild.message.QGAttachmentMessage.Companion.toMessage
import love.forte.simbot.qguild.common.QGUnstableProperty
import love.forte.simbot.qguild.model.Message
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * 验证附件消息的类型化字段与旧版属性表兼容性。
 */
class QGAttachmentMessageTest {
    private val json = Json

    @OptIn(QGUnstableProperty::class)
    @Suppress("DEPRECATION")
    @Test
    fun receivedAttachmentRetainsTypedFieldsAfterSerialization() {
        val raw = json.decodeFromString(
            Message.Attachment.serializer(),
            """{"url":"gchat.qpic.cn/image","filename":"image.png","width":236,"height":187,"size":44182,"content_type":"image/png","id":"2649174084"}"""
        )
        val message = raw.toMessage()

        assertSame(raw, message.source)
        assertEquals("https://gchat.qpic.cn/image", message.url)
        assertEquals("image.png", message.filename)
        assertEquals(236, message.width)
        assertEquals(187, message.height)
        assertEquals(44182L, message.size)
        assertEquals("image/png", message.contentType)
        assertEquals("2649174084", message.attachmentId)
        assertEquals("44182", message.properties["size"])

        val restored = json.decodeFromString(
            QGAttachmentMessage.serializer(),
            json.encodeToString(QGAttachmentMessage.serializer(), message)
        )
        assertEquals(message, restored)
        assertEquals("2649174084", restored.source.id)
        assertEquals(44182L, restored.source.size)
    }

    @Suppress("DEPRECATION")
    @Test
    fun legacyConstructionAndCopyRemainAvailable() {
        val message = QGAttachmentMessage(
            "https://example.com/image.png",
            mapOf("width" to "236", "size" to "44182", "extra" to "value")
        )

        assertEquals(236, message.width)
        assertEquals(44182L, message.size)
        assertEquals("value", message.properties["extra"])
        assertEquals(message.properties, message.component2())
        assertEquals("https://example.com/image.png", message.component1())
        assertEquals(message, message.copy())
        assertEquals(44182L, message.source.size)
    }
}
