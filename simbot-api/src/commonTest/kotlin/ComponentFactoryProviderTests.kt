/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.component.*
import kotlin.test.Test
import kotlin.test.assertEquals

/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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
 * @author ForteScarlet
 */
class ComponentFactoryProviderTests {

    private class Fac : ComponentFactory<Component, Any> {
        override val key: ComponentFactory.Key = object : ComponentFactory.Key {}
        override fun create(context: ComponentConfigureContext, configurer: ConfigurerFunction<Any>): Component = object : Component {
            override val id: String = "simbot.localTest"
            override val serializersModule: SerializersModule = EmptySerializersModule()
        }
    }

    private class FacP : ComponentFactoryProvider<Any> {
        override fun provide(): ComponentFactory<*, Any> = Fac()
        override fun loadConfigures(): Sequence<ComponentFactoryConfigurerProvider<Any>>? = null
    }

    @Test
    fun addAndGet() {
        addProvider { FacP() }
        addProvider { FacP() }
        addProvider { FacP() }

        val list = loadComponentProviders().toList()
        assertEquals(3, list.size)

        clearProviders()
    }

    @Test
    fun addAsyncAndGet() = runTest {
        coroutineScope {
            withContext(Dispatchers.Default) {
                launch {
                    repeat(100) {
                        addProvider { FacP() }
                    }
                }
                launch {
                    repeat(100) {
                        addProvider { FacP() }
                    }
                }
                launch {
                    repeat(100) {
                        addProvider { FacP() }
                    }
                }
            }
        }

        val list = loadComponentProviders().toList()
        assertEquals(300, list.size)
        clearProviders()
    }

}
