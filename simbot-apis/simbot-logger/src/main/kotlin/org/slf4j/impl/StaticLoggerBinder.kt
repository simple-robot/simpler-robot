/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package org.slf4j.impl

import love.forte.simbot.logger.DefaultSimbotLoggerProcessorsFactory
import love.forte.simbot.logger.SimbotLoggerFactory
import love.forte.simbot.logger.SimbotLoggerProcessorsFactory
import org.slf4j.ILoggerFactory
import org.slf4j.helpers.Util
import org.slf4j.spi.LoggerFactoryBinder
import java.util.*

/**
 * simbot-logger 下的日志工厂绑定器。
 *
 * `simbot-logger` 的所有日志处理均通过 [SimbotLoggerProcessorsFactory] 所得到的处理器列表进行链式处理。
 *
 * @see SimbotLoggerFactory
 * @see SimbotLoggerProcessorsFactory
 *
 * @author forte
 */
public object StaticLoggerBinder : LoggerFactoryBinder {
    private val processFactory by lazy {
        val loader = ServiceLoader.load(SimbotLoggerProcessorsFactory::class.java, javaClass.classLoader)
        val processorList = loader.toList()

        if (processorList.isEmpty()) {
            return@lazy DefaultSimbotLoggerProcessorsFactory
        }

        val firstProcessor = processorList.first()
        if (processorList.size > 1) {
            report("There are multiple SimbotLoggerProcessorsFactory loaded. The [$firstProcessor] will be selected.")
        }

        firstProcessor
    }

    private val loggerFactory by lazy {
        SimbotLoggerFactory(processFactory.getProcessors())
    }

    private const val LOGGER_FACTORY_CLASS_STR = "love.forte.simbot.logger.SimbotLoggerFactory"
    @JvmStatic
    public fun getSingleton(): StaticLoggerBinder = this

    override fun getLoggerFactory(): ILoggerFactory = loggerFactory
    override fun getLoggerFactoryClassStr(): String = LOGGER_FACTORY_CLASS_STR
}

/**
 * 类似于 [Util.report].
 */
@Suppress("SameParameterValue")
private fun report(msg: String) {
    System.err.println("SIMBOT-LOGGER: $msg")
}



