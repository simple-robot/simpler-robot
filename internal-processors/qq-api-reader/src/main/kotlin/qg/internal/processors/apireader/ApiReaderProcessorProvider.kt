/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package qg.internal.processors.apireader

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated

private object NonProcessor : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> = emptyList()
}

private const val ENABLE_OPTION = "qg.api.reader.enable"

/**
 *
 * @author ForteScarlet
 */
class ApiReaderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        if (environment.options[ENABLE_OPTION].toBoolean())
            ApiReaderProcessor(environment)
        else NonProcessor
}

/**
 *
 * @author ForteScarlet
 */
class EventReaderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        if (environment.options[ENABLE_OPTION].toBoolean())
            EventReaderProcessor(environment)
        else NonProcessor
}
