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

package love.forte.simbot.logger.slf4j2

import org.slf4j.ILoggerFactory
import org.slf4j.IMarkerFactory
import org.slf4j.helpers.BasicMarkerFactory
import org.slf4j.helpers.NOPMDCAdapter
import org.slf4j.helpers.Util
import org.slf4j.spi.MDCAdapter
import org.slf4j.spi.SLF4JServiceProvider
import java.io.IOException
import java.util.*
import kotlin.io.path.*


/**
 * `simbot-logger` 的日志工厂 provider。
 *
 * `simbot-logger` 的所有日志处理均通过 [SimbotLoggerProcessorsFactory] 所得到的处理器列表进行链式处理。
 *
 * ## 配置文件
 *
 * 会读取配置文件 `simbot-logger-slf4j.properties` 文件，此文件优先寻找当前项目根目录，其次则会根据当前类加载器寻找资源目录，否则不读取。
 *
 * 有关配置文件的更多说明参考 [SimbotLoggerConfiguration]。
 *
 * ## JVM属性
 *
 * simbot-logger-slf4j-impl 提供了一些可选的 JVM 属性。
 *
 * - 可通过JVM属性 `simbot.logger.configFile.disable=true` 来直接关闭配置文件的读取。
 * - 可通过JVM属性 `simbot.logger.configFile.file` 来指定一个配置文件。此文件需要为 `properties` 格式。
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
public class SimbotLoggerProvider : SLF4JServiceProvider {
    override fun getLoggerFactory(): ILoggerFactory = InternalSimbotLoggerProvider.loggerFactory
    override fun getMarkerFactory(): IMarkerFactory = defaultMarkerFactory
    override fun getMDCAdapter(): MDCAdapter = defaultMdcAdapter
    override fun getRequestedApiVersion(): String = API_VERSION
    override fun initialize() {
        if (InternalSimbotLoggerProvider.configuration.debug) {
            DEBUG.print("SimbotLoggerProvider", "initialize")
        }
    }

    public companion object {
        private val defaultMarkerFactory = BasicMarkerFactory()
        private val defaultMdcAdapter = NOPMDCAdapter()

        // see NOPServiceProvider.REQUESTED_API_VERSION
        // field not be final
        @JvmStatic
        private var API_VERSION = SLF4JInformation.VERSION
    }
}



internal object InternalSimbotLoggerProvider {
    private const val DISABLE_CONFIG_FILE_LOAD = "simbot.logger.configFile.disable"
    private const val DISABLE_CONFIG_FROM_JVM = "simbot.logger.configJvm.disable"
    private const val CONFIG_FILE_FILEPATH = "simbot.logger.configFile.file"
    private const val DEFAULT_CONFIG_FILE_NAME = "simbot-logger-slf4j.properties"

    private val processFactory: SimbotLoggerProcessorsFactory by lazy {
        val loader = ServiceLoader.load(SimbotLoggerProcessorsFactory::class.java, javaClass.classLoader)
        val processorList = loader.toList()

        if (processorList.isEmpty()) {
            return@lazy DefaultSimbotLoggerProcessorsFactory
        }

        val firstProcessor = processorList.first()
        if (processorList.size > 1) {
            report(
                "There are multiple SimbotLoggerProcessorsFactory loaded. " +
                    "The firstProcessor [$firstProcessor] will be selected."
            )
            processorList.forEachIndexed { index, fac ->
                report("\t${index + 1}. $fac")
            }
        }

        firstProcessor
    }

    val configuration by lazy {
        initConfiguration()
    }

    val loggerFactory: SimbotLoggerFactory by lazy {
        SimbotLoggerFactory(processFactory.getProcessors(configuration), configuration)
    }


    private fun initConfiguration(): SimbotLoggerConfiguration {
        val initProperties = if (System.getProperty(DISABLE_CONFIG_FILE_LOAD).toBoolean()) {
            emptyMap()
        } else {
            loadFileProperties()
        }

        val loadJvmArgs = !System.getProperty(DISABLE_CONFIG_FROM_JVM).toBoolean()

        return createSimbotLoggerConfiguration(loadJvmArgs, initProperties)
    }

    @Suppress("PrintStackTrace")
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
