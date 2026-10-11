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

import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.message.QGAttachmentMessage
import love.forte.simbot.component.qguild.message.QGAttachmentMessage.Companion.toMessage
import love.forte.simbot.qguild.common.QGUnstableProperty
import love.forte.simbot.qguild.model.Message
import kotlin.test.*
import love.forte.simbot.message.Message as SimbotMessage

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
        val defaultsJson = Json { encodeDefaults = true }
        val encodedWithDefaults = defaultsJson.encodeToString(QGAttachmentMessage.serializer(), message)
        assertFalse("properties" in defaultsJson.parseToJsonElement(encodedWithDefaults).jsonObject)
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

    /**
     * 旧属性表中的类型化字段与扩展属性可读取并再次保存。
     */
    @OptIn(QGUnstableProperty::class)
    @Suppress("DEPRECATION")
    @Test
    fun historicalPropertiesRestoreAndRoundTrip() {
        val payload = """{"properties":{"filename":"image.png","width":"236","height":"187","size":"44182","content_type":"image/png","voice_wav_url":"https://example.com/a.wav","asr_refer_text":"text","id":"attachment","extra":"kept"},"url":"https://example.com/a"}"""
        for (format in listOf(json, Json { encodeDefaults = true }, Json { ignoreUnknownKeys = true })) {
            val message = format.decodeFromString(QGAttachmentMessage.serializer(), payload)
            assertEquals("image.png", message.filename)
            assertEquals(236, message.width)
            assertEquals(187, message.height)
            assertEquals(44182L, message.size)
            assertEquals("image/png", message.contentType)
            assertEquals("https://example.com/a.wav", message.voiceWavUrl)
            assertEquals("text", message.asrReferText)
            assertEquals("attachment", message.attachmentId)
            assertEquals("kept", message.properties["extra"])
            val encoded = format.encodeToString(QGAttachmentMessage.serializer(), message)
            val restored = format.decodeFromString(QGAttachmentMessage.serializer(), encoded)
            assertEquals(message, restored)
            assertEquals(message.properties, restored.properties)
            assertEquals(236, restored.source.width)
        }
    }

    /**
     * 新格式的显式值及 null 优先于旧属性表，不依赖 JSON 字段顺序。
     */
    @Test
    fun explicitFieldsOverrideHistoricalDefaults() {
        val message = json.decodeFromString(
            QGAttachmentMessage.serializer(),
            """{"width":99,"size":null,"properties":{"width":"236","height":"187","size":"44182"},"url":"u"}"""
        )
        assertEquals(99, message.width)
        assertEquals(187, message.height)
        assertNull(message.size)
        val restored = json.decodeFromString(
            QGAttachmentMessage.serializer(),
            json.encodeToString(QGAttachmentMessage.serializer(), message)
        )
        assertEquals(99, restored.width)
        assertNull(restored.size)
    }

    /**
     * 旧表中的非法数值保留原文，类型化视图沿用旧构造器的空值语义。
     */
    @Suppress("DEPRECATION")
    @Test
    fun invalidHistoricalNumbersRemainInProperties() {
        val message = json.decodeFromString(
            QGAttachmentMessage.serializer(),
            """{"url":"u","properties":{"width":"invalid","size":"9223372036854775808"}}"""
        )
        assertNull(message.width)
        assertNull(message.size)
        assertEquals("invalid", message.properties["width"])
        assertEquals("9223372036854775808", message.properties["size"])
    }

    /**
     * 消息多态序列化入口同样兼容旧附件属性表。
     */
    @Suppress("DEPRECATION")
    @Test
    fun historicalAttachmentUsesComponentPolymorphicSerializer() {
        val format = Json { serializersModule = QQGuildComponent.messageSerializersModule }
        val serializer = PolymorphicSerializer(SimbotMessage.Element::class)
        val element = format.decodeFromString(
            serializer,
            """{"type":"qg.attachment","url":"u","properties":{"width":"236","extra":"kept"}}"""
        )
        val message = assertIs<QGAttachmentMessage>(element)
        assertEquals(236, message.width)
        val restored = assertIs<QGAttachmentMessage>(
            format.decodeFromString(serializer, format.encodeToString(serializer, message))
        )
        assertEquals(message, restored)
        assertEquals("kept", restored.properties["extra"])
    }
}
