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

import love.forte.simbot.logger.slf4j2.SimbotLoggerProvider;
import org.slf4j.spi.SLF4JServiceProvider;

module simbot.logger.slf4j2impl {
    requires kotlin.stdlib;
    requires transitive simbot.logger;
    requires com.lmax.disruptor;

    exports love.forte.simbot.logger.slf4j2;
    exports love.forte.simbot.logger.slf4j2.color;
    exports love.forte.simbot.logger.slf4j2.dispatcher;

    provides SLF4JServiceProvider with SimbotLoggerProvider;
}
