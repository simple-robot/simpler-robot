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
