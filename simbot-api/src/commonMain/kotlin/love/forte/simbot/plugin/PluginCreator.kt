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

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy


// TODO

public interface SimplePlugin : Plugin {
    public val key: Key

    public interface Key : PluginFactory.Key {
        public val name: String
    }
}

private data class PluginKey(override val name: String) : SimplePlugin.Key

private data class SimplePluginImpl<CONF>(override val key: SimplePlugin.Key, val configuration: CONF) : SimplePlugin

@PublishedApi
internal fun nameBasedPluginKey(name: String): SimplePlugin.Key = PluginKey(name)

@PublishedApi
internal fun <CONF : Any> simplePlugin(key: SimplePlugin.Key, conf: CONF): SimplePlugin = SimplePluginImpl(key, conf)

@ExperimentalSimbotAPI
public inline fun <CONF : Any> createPlugin(
    name: String,
    crossinline configCreator: () -> CONF,
    crossinline creator: PluginConfigureContext.(CONF) -> Unit
): PluginFactory<Plugin, CONF> {
    val key = nameBasedPluginKey(name)

    return object : PluginFactory<Plugin, CONF> {
        override val key: PluginFactory.Key = key
        override fun create(context: PluginConfigureContext, configurer: ConfigurerFunction<CONF>): Plugin {
            val conf = configCreator().invokeBy(configurer)
            context.creator(conf)
            return simplePlugin(key, conf)
        }
    }
}


@ExperimentalSimbotAPI
public inline fun createPlugin(
    name: String,
    crossinline creator: PluginConfigureContext.(Unit) -> Unit
): PluginFactory<Plugin, Unit> = createPlugin(name, { }, creator)




