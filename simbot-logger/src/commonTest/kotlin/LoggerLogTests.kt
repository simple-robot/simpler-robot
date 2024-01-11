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

import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.logger.name
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 *
 * @author ForteScarlet
 */
class LoggerLogTests {

    @Test
    fun loggerLogTest() {
        val logger = LoggerFactory.logger<LoggerLogTests>()
        assertEquals("LoggerLogTests", logger.name)

        logger.info("Hello!")
        logger.info("Hello {}", "World")
        logger.debug("Hello!")
        logger.debug("Hello {}", "World")
        logger.error("Hello!")
        logger.error("Hello {}", "World")
        logger.warn("Hello!")
        logger.warn("Hello {}", "World")

        val ex = RuntimeException()

        logger.info("Hello!", ex)
        logger.info("Hello {}", "World", ex)
        logger.debug("Hello!", ex)
        logger.debug("Hello {}", "World", ex)
        logger.error("Hello!", ex)
        logger.error("Hello {}", "World", ex)
        logger.warn("Hello!", ex)
        logger.warn("Hello {}", "World", ex)
    }

}
