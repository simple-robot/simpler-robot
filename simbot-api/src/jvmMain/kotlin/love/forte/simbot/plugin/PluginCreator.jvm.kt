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

package love.forte.simbot.plugin

import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy


public abstract class AbstractJPlugin(override val key: Key) : SimplePlugin {

    public interface Key : SimplePlugin.Key {
        override val name: String
    }

    /**
     * 应实现为单例。
     *
     */
    public abstract class Factory<P : AbstractJPlugin, CONF : Any>(name: String) : PluginFactory<P, CONF> {
        override val key: PluginFactory.Key = createKey(name)

        protected abstract fun createConfig(): CONF
        protected abstract fun create(context: PluginConfigureContext, configuration: CONF): P

        final override fun create(context: PluginConfigureContext, configurer: ConfigurerFunction<CONF>): P {
            val conf = createConfig().invokeBy(configurer)
            return create(context, conf)
        }
    }

    public companion object {
        private data class NamedKey(override val name: String) : Key

        @JvmStatic
        public fun createKey(name: String): Key = NamedKey(name)
    }
}


