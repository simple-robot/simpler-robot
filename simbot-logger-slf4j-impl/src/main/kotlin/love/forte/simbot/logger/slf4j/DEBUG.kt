/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.logger.slf4j

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * 用于进行一些 DEBUG 信息的输出。
 *
 */
internal object DEBUG {
    private val formatter =  DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

    fun SimbotLoggerConfiguration.debug(name: String, msg: String) {
        if (debug) {
            print(name, msg)
        }
    }

    inline fun SimbotLoggerConfiguration.debug(name: String, block: () -> String) {
        if (debug) {
            print(name, block())
        }
    }

    fun print(name: String, msg: String) {
        println(buildString(38 + name.length + msg.length) {
            append("[LOG DEBUG] [")
            formatter.formatTo(LocalDateTime.now(), this)
            append("] > ")
            append(name)
            append(": ")
            append(msg)
        })

    }

}
