/*
 *     Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.logger.slf4j2

import love.forte.simbot.logger.LogLevel
import love.forte.simbot.logger.slf4j2.SimbotLoggerConfiguration.Companion.JVM_PROPERTY_PREFIX
import love.forte.simbot.logger.slf4j2.dispatcher.AsyncDispatcher
import love.forte.simbot.logger.slf4j2.dispatcher.DisruptorDispatcher
import love.forte.simbot.logger.slf4j2.dispatcher.LogDispatcherFactory
import love.forte.simbot.logger.slf4j2.dispatcher.SyncDispatcher


/**
 * simbot-logger的实现中进行传递的配置文件。
 *
 * 配置中存在一些全局性的配置，它们会在 [SimbotLoggerConfiguration]
 * 构建之初就被解析为各属性而存在。有关它们的说明可参考 [SimbotLoggerConfiguration]
 * 的各个属性，例如 [debug] 、[defaultLevel]、[dispatcherMode] 等。
 *
 * 配置中
 *
 * 配置中的属性可能会被所有的 [SimbotLoggerProcessor] 各取所需。
 * 对于默认的实现，可以参考它们的文档说明：
 * - [ConsoleSimbotLoggerProcessor]
 *
 *
 *
 * @author ForteScarlet
 */
public abstract class SimbotLoggerConfiguration {
    /**
     * 可读取到的所有配置文件属性。其中包括目标目录中的 `simbot-logger-slf4j.properties` 文件内容
     * 和以 `simbot.logger` 开头的JVM属性。
     *
     * 其中，JVM属性优先级高于文件读取到的属性。
     */
    public abstract val properties: Map<String, Property>

    /**
     * 通过指定的 [key] 寻找一个配置项属性。
     */
    public open operator fun get(key: String): Property? = properties[key]

    /**
     * 是否输出 `debug` 信息，
     * 属性键为 [`debug`][DEBUG_PROPERTY] 时对应的结果。
     *
     * 当设置为 `true` 时，日志相关内容在初始化、解析等环节可能会向控制台输出一些测试信息。
     *
     */
    public abstract val debug: Boolean

    /**
     * 全局的默认日志等级，
     * 属性键为 [`level`][DEFAULT_LEVEL_PROPERTY] 时对应的结果。
     *
     * 如果未找到此属性则得到null。
     */
    public abstract val defaultLevel: LogLevel?

    /**
     * 日志的调度模式（决定使用的调度器），
     * 属性键为 [`dispatcher`][DISPATCH_MODE_PROPERTY] 时对应的结果，
     * 元素与枚举 [DispatchMode] 中的元素对应。
     *
     * 如果未找到则得到null。
     */
    public abstract val dispatcherMode: DispatchMode?

    /**
     * 指定了前缀的等级配置，例如
     * ```properties
     * level.love.forte=DEBUG
     * level.org.example=DEBUG
     * ```
     *
     * 则当logger前缀为 `love.forte` 或 `org.example` 时，日志等级为 `DEBUG`。
     *
     */
    public abstract val prefixLevelList: List<PrefixLogLevel>

    public companion object {
        /**
         * 当JVM属性前缀为 `simbot.logger` 时，此值将会被解析至 [SimbotLoggerConfiguration.properties] 中。
         * (此前缀将会被移除)
         */
        public const val JVM_PROPERTY_PREFIX: String = "simbot.logger"

        /**
         * 是否标记为允许输出一些调试用的信息。
         *
         * ```
         * debug=true
         * ```
         */
        public const val DEBUG_PROPERTY: String = "debug"

        /**
         * 默认（全局）的日志等级配置属性键。此键对应的值必须为 [LogLevel] 中的元素名称。
         *
         * ```
         * level=DEBUG
         * ```
         *
         */
        public const val DEFAULT_LEVEL_PROPERTY: String = "level"

        /**
         * 日志的调度器，值为 [DispatchMode] 的元素名。
         *
         * ```
         * dispatcher=DISRUPTOR
         * ```
         *
         */
        public const val DISPATCH_MODE_PROPERTY: String = "dispatcher"
    }

    /**
     * 一个配置项的属性。
     */
    public interface Property {
        /**
         * 此属性的键。
         */
        public val key: String

        /**
         * 此属性的字符串值。
         */
        public val stringValue: String
    }

    /**
     * 前缀日志级别信息。
     */
    public interface PrefixLogLevel {
        /**
         * 匹配前缀
         */
        public val prefix: String

        /**
         * 对应等级
         */
        public val level: LogLevel
    }
}

internal fun createSimbotLoggerConfiguration(
    loadJvmArgs: Boolean = true,
    initProperties: Map<String, String> = emptyMap()
): SimbotLoggerConfiguration {
    val properties = initProperties.toMutableMap()
    if (loadJvmArgs) {
        for (propertyName in System.getProperties().stringPropertyNames()) {
            if (!propertyName.startsWith("$JVM_PROPERTY_PREFIX.")) {
                continue
            }

            val value = System.getProperty(propertyName) ?: continue
            val key = propertyName.substringAfter("$JVM_PROPERTY_PREFIX.")

            properties[key] = value
        }
    }

    return SimbotLoggerConfigurationImpl(properties.mapValues { (k, v) -> PropertyImpl(k, v) })
}


/**
 * 日志的调度模式。
 */
public enum class DispatchMode(internal val factory: LogDispatcherFactory) {

    /**
     * 使用 [Disruptor][com.lmax.disruptor.dsl.Disruptor] 进行 **异步** 调度。
     *
     * 在内部会使用 [Disruptor][com.lmax.disruptor.dsl.Disruptor]
     * 作为调度器来对收集的日志进行异步调度。
     *
     * [DISRUPTOR] 是未配置时的默认选择。
     *
     * @see DisruptorDispatcher
     */
    DISRUPTOR(DisruptorDispatcher),

    /**
     * 同步地进行调度。
     *
     * @see SyncDispatcher
     */
    SYNC(SyncDispatcher),

    /**
     * 纯粹异步地进行调度。
     *
     * @see AsyncDispatcher
     */
    ASYNC(AsyncDispatcher),

    ;

    public companion object {

        /**
         * 根据名称寻找一个 [DispatchMode] 对应的元素。无法找到时得到null。
         *
         */
        @JvmStatic
        public fun find(name: String): DispatchMode? {
            try {
                return DispatchMode.valueOf(name)
            } catch (illArg: IllegalArgumentException) {
                // not found
            }

            val name0 = name.replace('-', '_').uppercase()

            try {
                return DispatchMode.valueOf(name0)
            } catch (illArg: IllegalArgumentException) {
                // also not found
            }

            return null
        }
    }
}


//// impls


private data class PropertyImpl(override val key: String, private val value: String) :
    SimbotLoggerConfiguration.Property {
    override val stringValue: String
        get() = value
}

private data class PrefixLogLevelImpl(override val prefix: String, override val level: LogLevel) :
    SimbotLoggerConfiguration.PrefixLogLevel

private data class SimbotLoggerConfigurationImpl(
    override val properties: Map<String, Property>
) : SimbotLoggerConfiguration() {
    @Transient
    override val debug: Boolean = properties[DEBUG_PROPERTY]?.stringValue.toBoolean()

    @Transient
    override val defaultLevel: LogLevel? = properties[DEFAULT_LEVEL_PROPERTY]?.stringValue?.let { LogLevel.valueOf(it) }

    @Transient
    override val dispatcherMode: DispatchMode? =
        properties[DISPATCH_MODE_PROPERTY]?.stringValue?.let { DispatchMode.find(it) }

    @Transient
    override val prefixLevelList: List<PrefixLogLevel>


    init {
        val prefixLevelList = mutableListOf<PrefixLogLevel>()
        properties.forEach { (k, v) ->
            if (k.length > DEFAULT_LEVEL_PROPERTY.length && k.startsWith(DEFAULT_LEVEL_PROPERTY)) {
                val prefix = k.substringAfter("$DEFAULT_LEVEL_PROPERTY.").takeIf { it.isNotBlank() } ?: return@forEach
                val level = LogLevel.valueOf(v.stringValue.uppercase())
                prefixLevelList.add(PrefixLogLevelImpl(prefix, level))
            }
        }

        this.prefixLevelList = prefixLevelList.toList()

    }
}
