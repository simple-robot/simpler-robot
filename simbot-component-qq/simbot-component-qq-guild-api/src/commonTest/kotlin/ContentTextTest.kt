package test

import love.forte.simbot.qguild.message.ContentTextDecoder
import love.forte.simbot.qguild.message.ContentTextEncoder
import kotlin.test.Test
import kotlin.test.assertEquals

class ContentTextTest {

    @Test
    fun encodeTest() {
        fun assertEncode(expected: String, actual: String) {
            assertEquals(expected, ContentTextEncoder.encode(actual))
        }
        assertEncode("123", "123")
        assertEncode("&lt;@!user_id&gt;", "<@!user_id>")
        assertEncode("&lt;&amp;&gt;", "<&>")
        assertEncode("&amp;&lt;&gt;&amp;", "&<>&")
        assertEncode("&amp;amp;", "&amp;")
        assertEncode("&amp;", "&")
        assertEncode("&amp;&amp;", "&&")
        assertEncode("", "")
    }

    @Test
    fun decodeTest() {
        fun assertDecode(expected: String, actual: String) {
            assertEquals(expected, ContentTextDecoder.decode(actual))
        }
        assertDecode("", "")
        assertDecode("123", "123")
        assertDecode("<@!user_id>", "&lt;@!user_id&gt;")
        assertDecode("<&>", "&lt;&amp;&gt;")
        assertDecode("&<>&", "&amp;&lt;&gt;&amp;")
        assertDecode("&amp;", "&amp;amp;")
        assertDecode("&&", "&amp;&amp;")
        assertDecode("&", "&amp;")
        assertDecode(
            "<@1919810>你好！欢迎来到QQ开发者社区，请到<#114154> 认领身份组哦！认领完成后才可解锁对应子频道哦！",
            "&lt;@1919810&gt;你好！欢迎来到QQ开发者社区，请到&lt;#114154&gt; 认领身份组哦！认领完成后才可解锁对应子频道哦！"
        )
    }

    @Test
    fun decodeToTest() {
        fun assertDecode(expected: String, actual: String) {
           assertEquals(expected, ContentTextDecoder.decodeTo("6666${actual}9999", 4, actual.length + 4, StringBuilder()).toString())
           assertEquals(expected, ContentTextDecoder.decodeTo("6666${actual}", 4, actual.length + 4, StringBuilder()).toString())
           assertEquals(expected, ContentTextDecoder.decodeTo("${actual}9999", 0, actual.length, StringBuilder()).toString())
           assertEquals(expected, ContentTextDecoder.decodeTo(actual, 0, actual.length, StringBuilder()).toString())
        }
//        assertDecode("", "")
        assertDecode("123", "123")
        assertDecode("<@!user_id>", "&lt;@!user_id&gt;")
        assertDecode("<&>", "&lt;&amp;&gt;")
        assertDecode("&<>&", "&amp;&lt;&gt;&amp;")
        assertDecode("&amp;", "&amp;amp;")
        assertDecode("&&", "&amp;&amp;")
        assertDecode("&", "&amp;")
        assertDecode(
            "<@1919810>你好！欢迎来到QQ开发者社区，请到<#114154> 认领身份组哦！认领完成后才可解锁对应子频道哦！",
            "&lt;@1919810&gt;你好！欢迎来到QQ开发者社区，请到&lt;#114154&gt; 认领身份组哦！认领完成后才可解锁对应子频道哦！"
        )
    }


}
