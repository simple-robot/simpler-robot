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

import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.runTest
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.bot.Bot
import love.forte.simbot.common.PriorityConstant
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.event.Event
import love.forte.simbot.event.MessageEvent
import love.forte.simbot.event.StandardEventResult
import love.forte.simbot.message.*
import love.forte.simbot.quantcat.common.annotations.Filter
import love.forte.simbot.quantcat.common.annotations.FilterValue
import love.forte.simbot.quantcat.common.annotations.Listener
import love.forte.simbot.quantcat.common.binder.ParameterBinderFactory
import love.forte.simbot.quantcat.common.binder.SimpleBinderManager
import love.forte.simbot.spring.configuration.binder.DefaultBinderManagerProvidersConfiguration
import love.forte.simbot.spring.configuration.listener.KFunctionEventListenerProcessor
import love.forte.simbot.spring.utils.findRepeatableMergedAnnotationSafely
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.reflect.jvm.javaMethod
import kotlin.test.*


/**
 *
 * @author ForteScarlet
 */
@SpringBootTest(
    classes = [
        FindRepeatableFilterTests.TestHd::class,
        DefaultBinderManagerProvidersConfiguration::class,
    ]
)
@SpringBootConfiguration
open class FindRepeatableFilterTests {

    @Test
    fun findRepeatableFilterTest() {
        val filters = TestHd::func.javaMethod!!.findRepeatableMergedAnnotationSafely<Filter>()
        assertNotNull(filters)
        assertEquals(2, filters.size)
    }

    @OptIn(ExperimentalSimbotAPI::class)
    @Test
    fun processorTest(
        @Autowired context: ApplicationContext,
        @Autowired binders: List<ParameterBinderFactory>,
    ) = runTest {
        val processor = KFunctionEventListenerProcessor()
        val manager = SimpleBinderManager(
            globalBinderFactories = binders
        )

        val processedResolver = processor.process(
            "test_bean_hd",
            TestHd::func,
            Listener("", PriorityConstant.DEFAULT),
            null,
            context,
            manager
        )

        val app = launchSimpleApplication { }
        processedResolver.resolve(app)
        val listeners = app.eventDispatcher.listeners.toList()
        assertEquals(1, listeners.size)

        val list = app.eventDispatcher.push(NopEvent()).toList()
        list.forEach {
            assertIs<StandardEventResult.Invalid>(it)
        }

        val resume1: String = suspendCancellableCoroutine { continuation ->
            launch {
                val results = app.eventDispatcher.push(TestEvent("name1 Hello", continuation)).toList()
                results.forEach {
                    assertIs<StandardEventResult.Empty>(it)
                }
                assertTrue(continuation.isCompleted)
            }
        }

        assertEquals("Hello", resume1)

        val resume2: String = suspendCancellableCoroutine { continuation ->
            launch {
                val results = app.eventDispatcher.push(TestEvent("name2 hi", continuation)).toList()
                results.forEach {
                    assertIs<StandardEventResult.Empty>(it)
                }
                assertTrue(continuation.isCompleted)
            }
        }

        assertEquals("hi", resume2)

        val resume3: String = suspendCancellableCoroutine { continuation ->
            launch {
                val results = app.eventDispatcher.push(TestEvent("name1", continuation)).toList()
                results.forEach {
                    assertIs<StandardEventResult.Invalid>(it)
                }

                assertTrue(continuation.isActive)
                assertFalse(continuation.isCancelled)
                assertFalse(continuation.isCompleted)
                continuation.resume("NOTHING")
            }
        }

        assertEquals("NOTHING", resume3)
    }


    @Component("test_bean_hd")
    class TestHd {
        @Listener
        @Filter("name1 {{name}}")
        @Filter("name2 {{name}}")
        fun func(event: TestEvent, @FilterValue("name") name: String) {
            event.continuation.resume(name)
        }
    }


    class NopEvent : Event {
        override val id: ID = UUID.random()

        @OptIn(ExperimentalSimbotAPI::class)
        override val time: Timestamp = Timestamp.now()
    }

    class TestEvent(
        val plain: String,
        val continuation: Continuation<String>
    ) : MessageEvent {

        override suspend fun reply(text: String): MessageReceipt = object : MessageReceipt {
            override suspend fun delete(vararg options: DeleteOption) {
                // Nothing.
            }
        }

        override val authorId: ID = 42.ID
        override val messageContent: MessageContent = object : MessageContent {
            override val id: ID = 0.ID
            override val messages: Messages = listOf(plain.toText()).toMessages()
            override val plainText: String = plain

            override suspend fun delete(vararg options: DeleteOption) {
                // Nothing.
            }
        }
        override val bot: Bot = mockk()

        override suspend fun reply(message: Message): MessageReceipt = object : MessageReceipt {
            override suspend fun delete(vararg options: DeleteOption) {
                // Nothing.
            }
        }

        override suspend fun reply(messageContent: MessageContent): MessageReceipt = object : MessageReceipt {
            override suspend fun delete(vararg options: DeleteOption) {
                // Nothing.
            }
        }

        override val id: ID = 4.ID

        @OptIn(ExperimentalSimbotAPI::class)
        override val time: Timestamp = Timestamp.now()
    }
}
