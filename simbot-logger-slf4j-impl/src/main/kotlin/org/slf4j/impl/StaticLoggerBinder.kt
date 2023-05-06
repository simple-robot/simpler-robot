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

package org.slf4j.impl

import love.forte.simbot.logger.slf4j.InternalSimbotLoggerProvider
import love.forte.simbot.logger.slf4j.SimbotLoggerConfiguration
import love.forte.simbot.logger.slf4j.SimbotLoggerProcessorsFactory
import org.slf4j.ILoggerFactory

/**
 * simbot-logger 下的日志工厂绑定器。
 *
 * <details><summary>说明</summary>
 *
 * `simbot-logger` 的所有日志处理均通过 [SimbotLoggerProcessorsFactory] 所得到的处理器列表进行链式处理。
 *
 * ## 配置文件
 *
 * 会读取配置文件 `simbot-logger-slf4j.properties` 文件，此文件优先寻找当前项目根目录，其次则会根据当前类加载器寻找资源目录，否则不读取。
 *
 * 可通过JVM属性 `simbot.logger.configFile.disable=true` 来直接关闭配置文件的读取。
 *
 * 可通过JVM属性 `simbot.logger.configFile.file` 来指定一个配置文件。此文件需要为 `properties` 格式。
 *
 * ## JVM属性
 *
 * 默认情况下，除了配置文件还会加载所有 [`'simbot.logger'`][SimbotLoggerConfiguration.JVM_PROPERTY_PREFIX] 为开头的JVM属性，
 * 并（在去除前缀之后）以高优先级加载为配置属性。
 *
 * 例如 `-Dsimbot.logger.level=DEBUG` 会被加载为 `level=DEBUG`
 *
 * </details>
 *
 * **Deprecated**
 *
 * 在 SLF4J 2.x 中被 [SimbotLoggerProvider][love.forte.simbot.logger.slf4j.SimbotLoggerProvider] 取代。
 *
 * @see love.forte.simbot.logger.slf4j.SimbotLoggerProvider
 *
 * @author forte
 */
@Suppress("DEPRECATION")
@Deprecated("Use SimbotLoggerProvider via ServiceLoader")
public object StaticLoggerBinder : org.slf4j.spi.LoggerFactoryBinder {
    private const val LOGGER_FACTORY_CLASS_STR = "love.forte.simbot.logger.SimbotLoggerFactory"

    @JvmStatic
    public fun getSingleton(): StaticLoggerBinder = this

    override fun getLoggerFactory(): ILoggerFactory = InternalSimbotLoggerProvider.loggerFactory
    override fun getLoggerFactoryClassStr(): String = LOGGER_FACTORY_CLASS_STR
}


