/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.logger

import love.forte.simbot.logger.internal.AbstractSimpleLogger
import love.forte.simbot.logger.internal.toDisplayName
import web.console.console

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
        if (throwable == null) {
            console.trace("[$displayName]:", formattedLog)
        } else {
            console.trace("[$displayName]:", formattedLog, throwable.stackTraceToString())
        }
    }

    override fun debug0(formattedLog: String, throwable: Throwable?) {
        if (throwable == null) {
            console.debug("[$displayName]:", formattedLog)
        } else {
            console.debug("[$displayName]:", formattedLog, throwable.stackTraceToString())
        }
    }

    override fun info0(formattedLog: String, throwable: Throwable?) {
        if (throwable == null) {
            console.info("[$displayName]:", formattedLog)
        } else {
            console.info("[$displayName]:", formattedLog, throwable.stackTraceToString())
        }
    }

    override fun warn0(formattedLog: String, throwable: Throwable?) {
        if (throwable == null) {
            console.warn("[$displayName]:", formattedLog)
        } else {
            console.warn("[$displayName]:", formattedLog, throwable.stackTraceToString())
        }
    }

    override fun error0(formattedLog: String, throwable: Throwable?) {
        if (throwable == null) {
            console.error("[$displayName]:", formattedLog)
        } else {
            console.error("[$displayName]:", formattedLog, throwable.stackTraceToString())
        }
    }
}
