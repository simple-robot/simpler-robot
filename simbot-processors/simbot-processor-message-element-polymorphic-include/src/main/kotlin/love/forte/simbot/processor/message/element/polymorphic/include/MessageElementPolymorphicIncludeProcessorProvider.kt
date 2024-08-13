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

package love.forte.simbot.processor.message.element.polymorphic.include

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.properties.Properties


/**
 * Provide [MessageElementPolymorphicIncludeProcessor].
 *
 * @author ForteScarlet
 */
public open class MessageElementPolymorphicIncludeProcessorProvider : SymbolProcessorProvider {
    public object EmptySymbolProcessor : SymbolProcessor {
        override fun process(resolver: Resolver): List<KSAnnotated> = emptyList()
    }

    @OptIn(ExperimentalSerializationApi::class)
    protected open fun readConfiguration(
        environment: SymbolProcessorEnvironment
    ): MessageElementPolymorphicIncludeConfiguration {
        val serializer = Properties(EmptySerializersModule())

        val properties = environment.options
            .filterKeys { it.startsWith(MessageElementPolymorphicIncludeConfiguration.CONFIG_PREFIX) }
            .mapKeys { it.key.removePrefix(MessageElementPolymorphicIncludeConfiguration.CONFIG_PREFIX) }

        return serializer.decodeFromStringMap(MessageElementPolymorphicIncludeConfiguration.serializer(), properties)
    }

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val configuration = readConfiguration(environment)

        if (!configuration.enable) {
            return object : SymbolProcessor {
                override fun process(resolver: Resolver): List<KSAnnotated> = emptyList()
            }
        }

        return MessageElementPolymorphicIncludeProcessor(environment, configuration)
    }
}
