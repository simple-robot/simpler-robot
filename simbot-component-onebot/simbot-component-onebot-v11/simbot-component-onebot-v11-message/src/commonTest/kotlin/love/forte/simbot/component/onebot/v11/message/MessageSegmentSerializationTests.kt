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

package love.forte.simbot.component.onebot.v11.message

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.component.onebot.v11.message.segment.*
import love.forte.simbot.message.messageElementPolymorphic
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 对消息段的序列化测试
 * @author ForteScarlet
 */
@Suppress("UNUSED_VARIABLE", "MaxLineLength", "HttpUrlsUsage")
class MessageSegmentSerializationTests {
    @OptIn(InternalSimbotAPI::class)
    private val defaultJson: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = false
        serializersModule = SerializersModule {
            messageElementPolymorphic {
                includeAllComponentMessageElementImpls()
            }
            polymorphic(OneBotMessageElement::class) {
                includeAllComponentMessageElementImpls()
            }
            polymorphic(OneBotMessageSegment::class) {
                includeAllOneBotSegmentImpls()
            }
        }
    }

    @Test
    fun obTextTest() {
        val jsonText = """{"type":"text","data":{"text":"纯文本内容"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotText.serializer(), jsonText)

        assertStringEquals("""纯文本内容""", segment.data.text)


    }


    @Test
    fun obFaceTest() {
        val jsonText = """{"type":"face","data":{"id":"123"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotFace.serializer(), jsonText)

        assertStringEquals("""123""", segment.data.id)


    }


    @Test
    fun obImageTest() {
        val jsonText = """{"type":"image","data":{"file":"http://baidu.com/1.jpg"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotImage.serializer(), jsonText)

        assertStringEquals("""http://baidu.com/1.jpg""", segment.data.file)


    }


    @Test
    fun obRecordTest() {
        val jsonText = """{"type":"record","data":{"file":"http://baidu.com/1.mp3"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotRecord.serializer(), jsonText)

        assertStringEquals("""http://baidu.com/1.mp3""", segment.data.file)


    }


    @Test
    fun obVideoTest() {
        val jsonText = """{"type":"video","data":{"file":"http://baidu.com/1.mp4"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotVideo.serializer(), jsonText)

        assertStringEquals("""http://baidu.com/1.mp4""", segment.data.file)


    }


    @Test
    fun obAtTest() {
        val jsonText = """{"type":"at","data":{"qq":"10001000"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotAt.serializer(), jsonText)

        assertStringEquals("""10001000""", segment.data.qq)


    }


    @Test
    fun obRpsTest() {
        val jsonText = """{"type":"rps","data":{}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        defaultJson.decodeFromString(OneBotRps.serializer(), jsonText)


    }


    @Test
    fun obDiceTest() {
        val jsonText = """{"type":"dice","data":{}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        defaultJson.decodeFromString(OneBotDice.serializer(), jsonText)


    }


    @Test
    fun obShakeTest() {
        val jsonText = """{"type":"shake","data":{}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        defaultJson.decodeFromString(OneBotShake.serializer(), jsonText)


    }


    @Test
    fun obPokeTest() {
        val jsonText = """{"type":"poke","data":{"type":"126","id":"2003"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotPoke.serializer(), jsonText)

        assertStringEquals("""126""", segment.data.type)
        assertStringEquals("""2003""", segment.data.id)


    }


    @Test
    fun obAnonymousTest() {
        val jsonText = """{"type":"anonymous","data":{}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        defaultJson.decodeFromString(OneBotAnonymous.serializer(), jsonText)


    }


    @Test
    fun obShareTest() {
        val jsonText = """{"type":"share","data":{"url":"http://baidu.com","title":"百度"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotShare.serializer(), jsonText)

        assertStringEquals("""http://baidu.com""", segment.data.url)
        assertStringEquals("""百度""", segment.data.title)


    }


    @Test
    fun obContactTest() {
        val jsonText = """{"type":"contact","data":{"type":"qq","id":"10001000"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotContact.serializer(), jsonText)

        assertStringEquals("""qq""", segment.data.type)
        assertStringEquals("""10001000""", segment.data.id)

    }

    @Test
    fun obContactGroupTest() {
        val jsonText = """{"type":"contact","data":{"type":"group","id":"100100"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotContact.serializer(), jsonText)

        assertStringEquals("""group""", segment.data.type)
        assertStringEquals("""100100""", segment.data.id)


    }


    @Test
    fun obLocationTest() {
        val jsonText = """{"type":"location","data":{"lat":"39.8969426","lon":"116.3109099"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotLocation.serializer(), jsonText)

        assertStringEquals("""39.8969426""", segment.data.lat)
        assertStringEquals("""116.3109099""", segment.data.lon)


    }


    @Test
    fun obMusicTest() {
        val jsonText = """{"type":"music","data":{"type":"163","id":"28949129"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotMusic.serializer(), jsonText)

        assertStringEquals("""163""", segment.data.type)
        assertStringEquals("""28949129""", segment.data.id)


    }


    @Test
    fun obMusicCustomTest() {
        val jsonText =
            """{"type":"music","data":{"type":"custom","url":"http://baidu.com","audio":"http://baidu.com/1.mp3","title":"音乐标题"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotMusic.serializer(), jsonText)

        assertStringEquals("""custom""", segment.data.type)
        assertStringEquals("""http://baidu.com""", segment.data.url)
        assertStringEquals("""http://baidu.com/1.mp3""", segment.data.audio)
        assertStringEquals("""音乐标题""", segment.data.title)


    }


    @Test
    fun obReplyTest() {
        val jsonText = """{"type":"reply","data":{"id":"123456"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotReply.serializer(), jsonText)

        assertStringEquals("""123456""", segment.data.id)


    }


    @Test
    fun obForwardTest() {
        val jsonText = """{"type":"forward","data":{"id":"123456"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotForward.serializer(), jsonText)

        assertStringEquals("""123456""", segment.data.id)


    }


    @Test
    fun obNodeIdTest() {
        val jsonText = """{"type":"node","data":{"id":"123456"}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotForwardNode.serializer(), jsonText)

        assertStringEquals("""123456""", segment.data.id)


    }


    @Test
    fun obNodeTest() {
        val jsonText =
            """{"type":"node","data":{"user_id":"10001000","nickname":"某人","content":[{"type":"face","data":{"id":"123"}},{"type":"text","data":{"text":"哈喽～"}}]}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotForwardNode.serializer(), jsonText)

        assertStringEquals("""10001000""", segment.data.userId)
        assertStringEquals("""某人""", segment.data.nickname)

    }


    @Test
    fun obXmlTest() {
        val jsonText = """{"type":"xml","data":{"data":"<?xml ..."}}"""
        val jsonObj = defaultJson.parseToJsonElement(jsonText).jsonObject
        val segment = defaultJson.decodeFromString(OneBotXml.serializer(), jsonText)

        assertStringEquals("""<?xml ...""", segment.data.data)


    }


}

private fun assertStringEquals(expected: String?, actual: Any?) {
    assertEquals(expected, actual?.toString())
}
