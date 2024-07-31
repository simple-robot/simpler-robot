/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.spring.test

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import love.forte.simbot.application.Application
import love.forte.simbot.event.*
import love.forte.simbot.quantcat.common.annotations.Filter
import love.forte.simbot.quantcat.common.annotations.FilterValue
import love.forte.simbot.quantcat.common.annotations.Listener
import love.forte.simbot.spring.EnableSimbot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull


/**
 * 测试来源：https://github.com/simple-robot/simpler-robot/issues/895
 *
 * @author ForteScarlet
 */
@SpringBootTest(
    classes = [
        DefaultBinderTests::class,
        TestListenerContainer::class,
    ]
)
@EnableSimbot
open class DefaultBinderTests {

    @Test
    fun binderTest1(
        @Autowired application: Application
    ) {
        fun Event.push(): List<EventResult> {
            return runBlocking {
                application.eventDispatcher.push(this@push)
                    .throwIfError()
                    .filterNotInvalid()
                    .toList()
            }
        }

        // test1: 应当得到 page=1, 因为匹配内容 page 实际不存在，使用默认值
        mockk<MessageEvent>(relaxed = true) {
            every { messageContent.plainText } returns "test_1 1"
            assertEquals(1, push().first().content)
        }

        // test2(1): 符合匹配结果，应当得到 1
        mockk<MessageEvent>(relaxed = true) {
            every { messageContent.plainText } returns "test_2 1"
            assertEquals("1", push().first().content)
        }

        // test2(2): 不符合匹配结果、不是required=false，理应报错
        mockk<MessageEvent>(relaxed = true) {
            every { messageContent.plainText } returns "test_2"
            assertFails { push() }
        }

        // test3(1): 不符合匹配结果、不是required=false，但参数是可选的，使用默认值，即得到 null
        mockk<MessageEvent>(relaxed = true) {
            every { messageContent.plainText } returns "test_3"
            assertNull(push().first().content)
        }

        // test3(2): 符合匹配结果、得到 "1"
        mockk<MessageEvent>(relaxed = true) {
            every { messageContent.plainText } returns "test_3 1"
            assertEquals("1", push().first().content)
        }

        // test4(1): 不符合匹配结果、是required=false，参数是可选的，使用默认值，即得到 null
        mockk<MessageEvent>(relaxed = true) {
            every { messageContent.plainText } returns "test_4"
            assertNull(push().first().content)
        }

        // test4(2): 符合匹配结果、得到 1
        mockk<MessageEvent>(relaxed = true) {
            every { messageContent.plainText } returns "test_4 1"
            assertEquals(1, push().first().content)
        }

        // test5(1): 不符合匹配结果、不是required=false，参数是可选的，使用默认值，即得到 1
        mockk<MessageEvent>(relaxed = true) {
            every { messageContent.plainText } returns "test_5"
            assertEquals(1, push().first().content)
        }

        // test5(2): 符合匹配结果、得到 2
        mockk<MessageEvent>(relaxed = true) {
            every { messageContent.plainText } returns "test_5 2"
            assertEquals(2, push().first().content)
        }

        // test6(1): 不符合匹配结果、是required=false，参数是可选的，但是参数是可以为null的，因此会填充 null 而不是默认值
        mockk<MessageEvent>(relaxed = true) {
            every { messageContent.plainText } returns "test_6"
            assertNull(push().first().content)
        }

        // test6(2): 符合匹配结果、得到 2
        mockk<MessageEvent>(relaxed = true) {
            every { messageContent.plainText } returns "test_6 2"
            assertEquals(2, push().first().content)
        }
    }

}

@Component
class TestListenerContainer {
    @Listener
    @Filter("^test_1(\\s+(?<page>\\d+))?")
    fun MessageEvent.handle1(
        @FilterValue("page", false) page: Int = 1
    ): Int = page

    @Listener
    @Filter("^test_2(\\s+(?<page>\\d+))?")
    fun MessageEvent.handle2(
        @FilterValue("page") page: String?
    ): String? = page

    @Listener
    @Filter("^test_3(\\s+(?<page>\\d+))?")
    fun MessageEvent.handle3(
        @FilterValue("page") page: String? = null
    ): String? = page

    @Listener
    @Filter("^test_4(\\s+(?<page>\\d+))?")
    fun MessageEvent.handle4(
        @FilterValue("page", false) page: Int? = null
    ): Int? = page

    @Listener
    @Filter("^test_5(\\s+(?<page>\\d+))?")
    fun MessageEvent.handle5(
        @FilterValue("page", false) page: Int = 1
    ): Int = page

    @Listener
    @Filter("^test_6(\\s+(?<page>\\d+))?")
    fun MessageEvent.handle6(
        @FilterValue("page", false) page: Int? = 1
    ): Int? = page
}
