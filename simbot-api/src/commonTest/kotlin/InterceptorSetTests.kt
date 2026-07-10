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

import love.forte.simbot.common.collection.ExperimentalSimbotCollectionApi
import love.forte.simbot.common.collection.createConcurrentQueue
import love.forte.simbot.interceptor.Interceptor
import love.forte.simbot.interceptor.SimpleInterceptorSet
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

/**
 *
 * @author Forte Scarlet
 */
class InterceptorSetTests {

    private fun newIteratorInstance() = object : Interceptor<Interceptor.Context<Any?>, Any?> {
        override suspend fun intercept(context: Interceptor.Context<Any?>): Any? {
            TODO("Not yet implemented")
        }
    }

    @Test
    fun testIteratorEachWithModify() {
        val set = SimpleInterceptorSet()

        set.add(newIteratorInstance())
        set.add(newIteratorInstance())

        val iter = set.all().iterator()

        assertTrue(iter.hasNext())
        iter.next()

        set.add(newIteratorInstance())

        assertTrue(iter.hasNext())
        iter.next()
        assertTrue(iter.hasNext())
        iter.next()
        assertFalse(iter.hasNext())

    }

    @OptIn(ExperimentalSimbotCollectionApi::class)
    @Test
    fun testQueueEachWithModify() {
        val set = createConcurrentQueue<Interceptor<Interceptor.Context<Any?>, Any?>>()

        set.add(newIteratorInstance())
        set.add(newIteratorInstance())

        val iter = set.iterator()

        assertTrue(iter.hasNext())
        iter.next()

        set.add(newIteratorInstance())

        assertTrue(iter.hasNext())
        iter.next()
        assertTrue(iter.hasNext())
        iter.next()
        assertFalse(iter.hasNext())

    }

}
