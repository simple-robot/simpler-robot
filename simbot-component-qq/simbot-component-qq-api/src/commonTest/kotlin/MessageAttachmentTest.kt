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

package test

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import love.forte.simbot.qguild.model.Message
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * 验证 QQ 附件字段的序列化及旧版属性表兼容性。
 */
class MessageAttachmentTest {
    private val json = Json

    @Suppress("DEPRECATION")
    @Test
    fun imageAttachmentRoundTrip() {
        val attachment = json.decodeFromString(
            Message.Attachment.serializer(),
            """{"url":"gchat.qpic.cn/image","filename":"image.png","width":236,"height":187,"size":44182,"content_type":"image/png","id":"2649174084"}"""
        )

        assertEquals("gchat.qpic.cn/image", attachment.url)
        assertEquals("image.png", attachment.filename)
        assertEquals(236, attachment.width)
        assertEquals(187, attachment.height)
        assertEquals(44182L, attachment.size)
        assertEquals("image/png", attachment.contentType)
        assertEquals("2649174084", attachment.id)
        assertNull(attachment.voiceWavUrl)
        assertEquals("44182", attachment.properties["size"])
        assertEquals(attachment, json.decodeFromString(Message.Attachment.serializer(), json.encodeToString(Message.Attachment.serializer(), attachment)))
    }

    @Test
    fun voiceAttachmentOptionalFields() {
        val attachment = json.decodeFromString(
            Message.Attachment.serializer(),
            """{"url":"https://example.com/voice.silk","content_type":"voice","voice_wav_url":"https://example.com/voice.wav","asr_refer_text":"你好"}"""
        )

        assertNull(attachment.id)
        assertNull(attachment.width)
        assertNull(attachment.height)
        assertEquals("voice", attachment.contentType)
        assertEquals("https://example.com/voice.wav", attachment.voiceWavUrl)
        assertEquals("你好", attachment.asrReferText)
    }

    @Suppress("DEPRECATION")
    @Test
    fun legacyPropertiesRemainAvailable() {
        val attachment = Message.Attachment(
            "https://example.com/image.png",
            mapOf("width" to "236", "size" to "44182", "id" to "attachment-id", "extra" to "value")
        )

        assertEquals(236, attachment.width)
        assertEquals(44182L, attachment.size)
        assertEquals("attachment-id", attachment.id)
        assertEquals("value", attachment.properties["extra"])
        assertEquals("236", attachment.properties["width"])
        assertEquals("44182", attachment.properties["size"])
        assertEquals("https://example.com/image.png", attachment.properties["url"])

        val encoded = json.encodeToString(Message.Attachment.serializer(), attachment)
        assertEquals(236L, json.parseToJsonElement(encoded).jsonObject.getValue("width").jsonPrimitive.long)
        assertEquals(44182L, json.parseToJsonElement(encoded).jsonObject.getValue("size").jsonPrimitive.long)
        assertEquals(attachment, json.decodeFromString(Message.Attachment.serializer(), encoded))
    }
}
