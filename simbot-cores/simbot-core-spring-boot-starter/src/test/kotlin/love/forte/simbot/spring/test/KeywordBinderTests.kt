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

import io.mockk.*
import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.common.attribute.AttributeMapContainer
import love.forte.simbot.common.attribute.mutableAttributeMapOf
import love.forte.simbot.common.attribute.set
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerContext
import love.forte.simbot.event.MessageEvent
import love.forte.simbot.event.handleWith
import love.forte.simbot.quantcat.common.annotations.Filter
import love.forte.simbot.quantcat.common.annotations.FilterValue
import love.forte.simbot.quantcat.common.binder.ParameterBinderFactory
import love.forte.simbot.quantcat.common.binder.impl.KeywordBinderFactory
import love.forte.simbot.quantcat.common.convert.NonConverters
import love.forte.simbot.quantcat.common.keyword.KeywordListAttribute
import love.forte.simbot.quantcat.common.keyword.SimpleKeyword
import love.forte.simbot.spring.configuration.binder.DefaultBinderManagerProvidersConfiguration
import love.forte.simbot.spring.configuration.listener.KFunctionEventListenerImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


/**
 *
 * @author ForteScarlet
 */
@SpringBootTest(
    classes = [
        KeywordBinderTests::class,
        DefaultBinderManagerProvidersConfiguration::class,
    ]
)
@SpringBootConfiguration
open class KeywordBinderTests {

    @Suppress("MemberVisibilityCanBePrivate")
    fun doParam(value: Int) {
        println("param.value = $value")
    }

    @Filter("(?<value>\\d+)num")
    fun param(
        @FilterValue("value")
        value: Int
    ) {
        doParam(value)
    }

    @OptIn(InternalSimbotAPI::class)
    @Test
    fun keywordBinderTest(
        @Autowired factory: KeywordBinderFactory
    ) {
        mockkObject(NonConverters)

        val p = ::param.parameters.first()

        val context = mockk<ParameterBinderFactory.Context>()

        every { context.parameter } returns p

        val binderResult = factory.resolveToBinder(context)

        val binder0 = binderResult.binder
        assertNotNull(binder0)
        val binder = spyk(binder0, recordPrivateCalls = true)

        val binderContext = mockk<EventListenerContext>(relaxed = true)
        val event = mockk<MessageEvent>()
        val listener = mockk<EventListener>(
            moreInterfaces = arrayOf(AttributeMapContainer::class)
        )

        every { binderContext.listener } returns listener
        every { binderContext.event } returns event

        every {
            (listener as AttributeMapContainer).attributeMap[KeywordListAttribute]
        } returns mutableListOf(SimpleKeyword("(?<value>\\d+)num", isPlainText = false))

        every {
            binderContext.plainText
        } returns "123num"

        val bindResult = binder.arg(binderContext)

        verify { binder["convert"](any()) }
        verify { NonConverters.convert(any(), any()) }

        assertTrue(bindResult.isSuccess)
        assertEquals(123, bindResult.getOrNull())
    }


    @Test
    fun kFunctionListenerImplKeywordBinderTest(
        @Autowired factory: KeywordBinderFactory
    ) = runTest {
        val instance = spyk<KeywordBinderTests>()

        val func = instance::param
        val context = mockk<ParameterBinderFactory.Context>()
        every { context.parameter } returns func.parameters.first()
        val binderResult = factory.resolveToBinder(context)
        val binder = assertNotNull(binderResult.binder)

        val listener = KFunctionEventListenerImpl(
            instance = instance,
            caller = func,
            binders = arrayOf(binder),
            attributes = mutableAttributeMapOf().apply {
                set(
                    KeywordListAttribute,
                    mutableListOf(
                        SimpleKeyword("(?<value>\\d+)num", isPlainText = false)
                    )
                )
            },
            matcher = { true }
        )

        val ec = mockk<EventListenerContext>(
            relaxed = true
        )
        every { ec.listener } returns listener
        every { ec.plainText } returns "123num"

        listener.handleWith(ec)

        verify { instance.doParam(any()) }
    }

}
