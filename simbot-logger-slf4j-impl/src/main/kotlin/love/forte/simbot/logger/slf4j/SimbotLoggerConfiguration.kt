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

import love.forte.simbot.logger.LogLevel
import love.forte.simbot.logger.slf4j.SimbotLoggerConfiguration.Companion.JVM_PROPERTY_PREFIX


/**
 * simbot-logger的实现中进行传递的配置文件。
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
     * 属性键为 [DEFAULT_LEVEL_PROPERTY] 时对应的结果。如果为找到此属性则得到null。
     */
    public abstract val defaultLevel: LogLevel?

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
         * 默认（全局）的日志等级配置属性键。此键对应的值必须为 [LogLevel] 中的元素名称。
         *
         * ```
         * level=DEBUG
         * ```
         *
         */
        public const val DEFAULT_LEVEL_PROPERTY: String = "level"
    }


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
    override val defaultLevel: LogLevel? = properties[DEFAULT_LEVEL_PROPERTY]?.stringValue?.let { LogLevel.valueOf(it) }

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
