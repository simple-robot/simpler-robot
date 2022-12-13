/*
 * Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package org.slf4j.impl

import love.forte.simbot.logger.slf4j.*
import org.slf4j.ILoggerFactory
import org.slf4j.helpers.Util
import org.slf4j.spi.LoggerFactoryBinder
import java.io.IOException
import java.util.*
import kotlin.io.path.*

/**
 * simbot-logger 下的日志工厂绑定器。
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
 * @see SimbotLoggerFactory
 * @see SimbotLoggerProcessorsFactory
 *
 * @author forte
 */
public object StaticLoggerBinder : LoggerFactoryBinder {
    private const val DISABLE_CONFIG_FILE_LOAD = "simbot.logger.configFile.disable"
    private const val DISABLE_CONFIG_FROM_JVM = "simbot.logger.configJvm.disable"
    private const val CONFIG_FILE_FILEPATH = "simbot.logger.configFile.file"
    private const val DEFAULT_CONFIG_FILE_NAME = "simbot-logger-slf4j.properties"

    private val processFactory by lazy {
        val loader = ServiceLoader.load(SimbotLoggerProcessorsFactory::class.java, javaClass.classLoader)
        val processorList = loader.toList()

        if (processorList.isEmpty()) {
            return@lazy DefaultSimbotLoggerProcessorsFactory
        }

        val firstProcessor = processorList.first()
        if (processorList.size > 1) {
            report("There are multiple SimbotLoggerProcessorsFactory loaded. The firstProcessor [$firstProcessor] will be selected.")
            processorList.forEachIndexed { index, fac ->
                report("\t${index + 1}. $fac")
            }
        }

        firstProcessor
    }

    private val configuration by lazy {
        initConfiguration()
    }

    private val loggerFactory by lazy {
        SimbotLoggerFactory(processFactory.getProcessors(configuration))
    }

    private const val LOGGER_FACTORY_CLASS_STR = "love.forte.simbot.logger.SimbotLoggerFactory"

    @JvmStatic
    public fun getSingleton(): StaticLoggerBinder = this

    override fun getLoggerFactory(): ILoggerFactory = loggerFactory
    override fun getLoggerFactoryClassStr(): String = LOGGER_FACTORY_CLASS_STR


    private fun initConfiguration(): SimbotLoggerConfiguration {
        val initProperties = if (System.getProperty(DISABLE_CONFIG_FILE_LOAD).toBoolean()) {
            emptyMap()
        } else {
            loadFileProperties()
        }

        val loadJvmArgs = !System.getProperty(DISABLE_CONFIG_FROM_JVM).toBoolean()

        return createSimbotLoggerConfiguration(loadJvmArgs, initProperties)
    }

    private fun loadFileProperties(): Map<String, String> {
        val fileName = configFileName()
        val file = Path(fileName)
        val properties = Properties()
        if (file.exists() && !file.isDirectory() && file.isReadable()) {
            try {
                file.bufferedReader().use { reader ->
                    properties.load(reader)
                }
            } catch (e: IOException) {
                System.err.println("Logger config file $file load failure. Reason: $e")
                e.printStackTrace() // print, but do not break
            }
        } else {
            try {
                val classLoader = javaClass.classLoader ?: ClassLoader.getSystemClassLoader()
                classLoader.getResourceAsStream(fileName)?.use { inp ->
                    properties.load(inp)
                }
            } catch (e: IOException) {
                System.err.println("Logger config file $file load failure. Reason: $e")
                e.printStackTrace() // print, but do not break
            }
        }

        val map = mutableMapOf<String, String>()
        for (name in properties.stringPropertyNames()) {
            val value = properties.getProperty(name) ?: continue
            map[name] = value
        }

        return map
    }

    private fun configFileName(): String {
        return System.getProperty(CONFIG_FILE_FILEPATH, DEFAULT_CONFIG_FILE_NAME)
    }
}

/**
 * 类似于 [Util.report].
 */
@Suppress("SameParameterValue")
private fun report(msg: String) {
    System.err.println("SIMBOT-LOGGER: $msg")
}


