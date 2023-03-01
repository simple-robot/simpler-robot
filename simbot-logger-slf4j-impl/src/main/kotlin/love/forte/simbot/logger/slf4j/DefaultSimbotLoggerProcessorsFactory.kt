/*
 * Copyright (c) 2022-2023 ForteScarlet.
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


/**
 * 当无法加载任何 [SimbotLoggerProcessorsFactory] 时使用的默认工厂。
 * 默认工厂中只有一个用于控制台输出的 [ConsoleSimbotLoggerProcessor].
 *
 */
public object DefaultSimbotLoggerProcessorsFactory : SimbotLoggerProcessorsFactory {
    /**
     * 得到默认工厂中的处理器。
     */
    override fun getProcessors(configuration: SimbotLoggerConfiguration): List<SimbotLoggerProcessor> {
        return listOf(ConsoleSimbotLoggerProcessor(configuration))
    }
}
