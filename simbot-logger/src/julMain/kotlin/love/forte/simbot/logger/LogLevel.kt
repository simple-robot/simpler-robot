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

package love.forte.simbot.logger

import java.util.logging.Level

/**
 * 日志级别。
 *
 * 与 [JUL Level][Level] 对应并可通过属性 [level]
 * 直接获取 JUL Level.
 *
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
public actual enum class LogLevel(public val level: Level) {
    /**
     * @see Level.SEVERE
     */
    ERROR(Level.SEVERE),

    /**
     * @see Level.WARNING
     */
    WARN(Level.WARNING),

    /**
     * @see Level.INFO
     */
    INFO(Level.INFO),

    /**
     * A `DEBUG` level similar to [Level.CONFIG], named `DEBUG` .
     *
     * @see Level.CONFIG
     */
    DEBUG(SimbotLevel("DEBUG", Level.CONFIG.intValue(), Level.CONFIG.resourceBundleName)),

    /**
     * @see Level.FINE
     */
    FINE(Level.FINE),

    /**
     * @see Level.FINER
     */
    FINER(Level.FINER),

    /**
     * A `TRACE` level similar to [Level.FINEST], named `TRACE` .
     */
    TRACE(SimbotLevel("TRACE", Level.FINEST.intValue(), Level.FINEST.resourceBundleName)),
}

/**
 * internal [Level] implementation for simbot-logger.
 */
private class SimbotLevel(name: String, value: Int, resourceBundleName: String? = null) :
    Level(name, value, resourceBundleName)

