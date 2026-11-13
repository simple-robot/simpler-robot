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
package love.forte.simbot.core.event

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.attribute.mutableAttributeMapOf
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.core.event.impl.SimpleEventDispatcherConfigurationImpl
import love.forte.simbot.event.*
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * 调度拦截器替换的业务上下文必须传到最终监听器。
 */
@OptIn(ExperimentalSimbotAPI::class, FuzzyEventTypeImplementation::class)
class EventDispatchContextTests {
    /**
     * 替换后的事件、属性和上下文实例应完整保留。
     */
    @Test
    fun replacedContextReachesListener() = runTest {
        val original = TestEvent()
        val replacement = TestContext(TestEvent())
        val configuration = SimpleEventDispatcherConfigurationImpl().apply {
            addDispatchInterceptor { invoke(replacement) }
        }
        val dispatcher = createSimpleEventDispatcherImpl(configuration)
        var observed: EventContext? = null
        dispatcher.register(EventListener {
            observed = context
            EventResult.of(event.id)
        })

        val results = dispatcher.push(original).toList()

        assertSame(replacement, observed)
        assertSame(replacement.attributes, observed?.attributes)
        assertEquals(replacement.event.id, results.single().content)
    }

    /**
     * 多个调度拦截器连续替换时，最终监听器应使用最后一次替换。
     */
    @Test
    fun lastReplacementReachesListener() = runTest {
        val first = TestContext(TestEvent())
        val last = TestContext(TestEvent())
        val configuration = SimpleEventDispatcherConfigurationImpl().apply {
            addDispatchInterceptor({ priority = 0 }) { invoke(first) }
            addDispatchInterceptor({ priority = 1 }) {
                assertSame(first, eventContext)
                invoke(last)
            }
        }
        val dispatcher = createSimpleEventDispatcherImpl(configuration)
        var observed: EventContext? = null
        dispatcher.register(EventListener {
            observed = context
            EventResult.of(event.id)
        })

        val results = dispatcher.push(TestEvent()).toList()

        assertSame(last, observed)
        assertSame(last.attributes, observed?.attributes)
        assertEquals(last.event.id, results.single().content)
    }

    /**
     * 为替换测试提供独立属性集合。
     */
    private class TestContext(override val event: Event) : EventContext {
        override val coroutineContext = EmptyCoroutineContext
        override val attributes = mutableAttributeMapOf()
    }

    /**
     * 各实例使用不同标识，便于检测事件被错误复用。
     */
    private class TestEvent : Event {
        override val id: ID = UUID.random()
        override val time: Timestamp = Timestamp.now()
    }
}
