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

package love.forte.simbot.test.component

import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.component.Component
import love.forte.simbot.component.ComponentConfigureContext
import love.forte.simbot.component.ComponentFactory
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic


/**
 *
 * @author ForteScarlet
 */
public class TestComponent(public val configuration: TestComponentConfiguration) : Component {
    override val id: String
        get() = ID_VALUE

    override val serializersModule: SerializersModule
        get() = configuration.serializersModule

    public companion object Factory : ComponentFactory<TestComponent, TestComponentConfiguration> {
        public const val ID_VALUE: String = "simbot.test"
        override val key: ComponentFactory.Key = object : ComponentFactory.Key {}

        @get:JvmStatic
        @get:JvmName("serializersModule")
        public val serializersModule: SerializersModule = EmptySerializersModule()

        override fun create(
            context: ComponentConfigureContext,
            configurer: ConfigurerFunction<TestComponentConfiguration>
        ): TestComponent {
            val configuration = TestComponentConfiguration().invokeBy(configurer)
            return TestComponent(configuration)
        }
    }
}

/**
 * Configuration of [TestComponent]
 */
public open class TestComponentConfiguration {
    public open var serializersModule: SerializersModule = EmptySerializersModule()
}
