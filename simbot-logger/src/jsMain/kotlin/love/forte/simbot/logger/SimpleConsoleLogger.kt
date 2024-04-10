/*
 *     Copyright (c) 2023-2024. ForteScarlet.
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
        console.log("[trace]", "[$displayName]:", formattedLog)
        throwable?.stackTraceToString()?.also { console.log(it) }
    }

    override fun debug0(formattedLog: String, throwable: Throwable?) {
        console.log("[debug]", "[$displayName]:", formattedLog)
        throwable?.stackTraceToString()?.also { console.log(it) }
    }

    override fun info0(formattedLog: String, throwable: Throwable?) {
        console.info("[$displayName]:", formattedLog)
        throwable?.stackTraceToString()?.also { console.info(it) }
    }

    override fun warn0(formattedLog: String, throwable: Throwable?) {
        console.warn("[$displayName]:", formattedLog)
        throwable?.stackTraceToString()?.also { console.warn(it) }
    }

    override fun error0(formattedLog: String, throwable: Throwable?) {
        console.error("[$displayName]:", formattedLog)
        throwable?.stackTraceToString()?.also { console.error(it) }
    }
}
