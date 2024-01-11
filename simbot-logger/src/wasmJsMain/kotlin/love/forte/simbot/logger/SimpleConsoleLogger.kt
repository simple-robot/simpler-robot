/*
 *     Copyright (c) 2023-2024. ForteScarlet.
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

package love.forte.simbot.logger

import love.forte.simbot.logger.internal.AbstractSimpleLogger
import love.forte.simbot.logger.internal.toDisplayName


/**
 *
 * @author ForteScarlet
 */
internal class SimpleConsoleLogger(
    private val name: String,
    override val displayName: String = name.toDisplayName(),
    override val level: LogLevel,
) : AbstractSimpleLogger() {
    override fun getName(): String = name

    override fun trace0(formattedLog: String, throwable: Throwable?) {
        consoleTrace(displayName, formattedLog)
        throwable?.stackTraceToString()?.also { consoleTrace(it) }
    }

    override fun debug0(formattedLog: String, throwable: Throwable?) {
        consoleDebug(displayName, formattedLog)
        throwable?.stackTraceToString()?.also { consoleDebug(it) }
    }

    override fun info0(formattedLog: String, throwable: Throwable?) {
        consoleInfo(displayName, formattedLog)
        throwable?.stackTraceToString()?.also { consoleInfo(it) }
    }

    override fun warn0(formattedLog: String, throwable: Throwable?) {
        consoleWarn(displayName, formattedLog)
        throwable?.stackTraceToString()?.also { consoleWarn(it) }
    }

    override fun error0(formattedLog: String, throwable: Throwable?) {
        consoleError(displayName, formattedLog)
        throwable?.stackTraceToString()?.also { consoleError(it) }
    }
}

@Suppress("UNUSED_PARAMETER")
private fun consoleTrace(displayName: String, value: String) {
    js("console.log('[trace][' + displayName + ']:', value)")
}

@Suppress("UNUSED_PARAMETER")
private fun consoleTrace(value: String) {
    js("console.log('[trace]:', value)")
}

@Suppress("UNUSED_PARAMETER")
private fun consoleDebug(displayName: String, value: String) {
    js("console.log('[debug][' + displayName + ']:', value)")
}

@Suppress("UNUSED_PARAMETER")
private fun consoleDebug(value: String) {
    js("console.log('[debug]:', value)")
}

@Suppress("UNUSED_PARAMETER")
private fun consoleLog(displayName: String, value: String) {
    js("console.log('[' + displayName + ']:', value)")
}

@Suppress("UNUSED_PARAMETER")
private fun consoleLog(value: String) {
    js("console.log(value)")
}

@Suppress("UNUSED_PARAMETER")
private fun consoleInfo(displayName: String, value: String) {
    js("console.info('['+displayName+']:', value)")
}

@Suppress("UNUSED_PARAMETER")
private fun consoleInfo(value: String) {
    js("console.info(value)")
}

@Suppress("UNUSED_PARAMETER")
private fun consoleWarn(displayName: String, value: String) {
    js("console.warn('[' + displayName + ']:', value)")
}

@Suppress("UNUSED_PARAMETER")
private fun consoleWarn(value: String) {
    js("console.warn(value)")
}

@Suppress("UNUSED_PARAMETER")
private fun consoleError(displayName: String, value: String) {
    js("console.error('[' + displayName + ']:', value)")
}

@Suppress("UNUSED_PARAMETER")
private fun consoleError(value: String) {
    js("console.error(value)")
}
