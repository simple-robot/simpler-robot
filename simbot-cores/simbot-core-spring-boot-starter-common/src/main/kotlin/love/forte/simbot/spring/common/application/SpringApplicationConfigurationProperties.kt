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

package love.forte.simbot.spring.common.application

import love.forte.simbot.application.Application
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.SerializableBotConfiguration


/**
 * 通过 Spring Boot 配置文件进行配置的各项属性。
 *
 * @author ForteScarlet
 */
public class SpringApplicationConfigurationProperties {
    /**
     * 与 bot 配置相关的属性。
     */
    public var bots: BotProperties = BotProperties()

    /**
     * 与 bot 配置相关的属性。
     */
    public class BotProperties {
        public companion object {
            internal const val DEFAULT_JSON_RESOURCE_PATTERN: String = "classpath:simbot-bots/*.bot.json"
        }

        /**
         * 需要加载的所有组件下它们对应的所有 JSON 格式 的 bot 配置文件。
         * 默认为 [`classpath:simbot-bots/\*.bot.json`][DEFAULT_JSON_RESOURCE_PATTERN]
         */
        public var configurationJsonResources: MutableSet<String> = mutableSetOf(DEFAULT_JSON_RESOURCE_PATTERN)

        // with types?

        /**
         * 当加载用于注册bot的配置文件出现错误时的处理策略。默认为 [BotConfigResourceLoadFailurePolicy.ERROR] 即抛出异常。
         */
        public var autoRegistrationResourceLoadFailurePolicy: BotConfigResourceLoadFailurePolicy =
            BotConfigResourceLoadFailurePolicy.ERROR

        /**
         * 当无法为某个 [SerializableBotConfiguration] 找到任何可供其注册的 [BotManager] 时的处理策略。
         * 默认为 [MismatchConfigurableBotManagerPolicy.ERROR_LOG] 输出 `error` 日志。
         */
        public var autoRegistrationMismatchConfigurableBotManagerPolicy: MismatchConfigurableBotManagerPolicy =
            MismatchConfigurableBotManagerPolicy.ERROR_LOG

        /**
         * 是否在 `Bot` 注册后使用 [Bot.start] 启动它们。
         */
        public var autoStartBots: Boolean = true

        /**
         * 当 [autoStartBots] 为 `true` 时，
         * 启动 bot 的方式。
         * 会先加载（注册）完所有 bot 后再启动。
         * 默认为 [BotAutoStartMode.ASYNC]。
         */
        public var autoStartMode: BotAutoStartMode = BotAutoStartMode.ASYNC

        /**
         * 当自动扫描的bot注册或启动失败时的处理策略。默认为直接异常以终止程序。
         */
        public var autoRegistrationFailurePolicy: BotRegistrationFailurePolicy = BotRegistrationFailurePolicy.ERROR
    }

    /**
     * 组件相关的配置信息。
     */
    public var components: ComponentProperties = ComponentProperties()

    /**
     * 组件相关的配置信息。
     */
    public class ComponentProperties {
        /**
         * 是否通过 SPI 自动加载所有可寻得的组件
         */
        public var autoInstallProviders: Boolean = true

        /**
         * 是否在加载 SPI providers 时候也同时加载它们的前置配置。
         * [autoInstallProviders] 为 `true` 时有效。
         */
        public var autoInstallProviderConfigurers: Boolean = true
    }

    /**
     * 插件相关的配置信息。
     */
    public var plugins: PluginProperties = PluginProperties()

    /**
     * 插件相关的配置信息。
     */
    public class PluginProperties {
        /**
         * 是否通过 SPI 自动加载所有可寻得的插件
         */
        public var autoInstallProviders: Boolean = true

        /**
         * 是否在加载 SPI providers 时候也同时加载它们的前置配置。
         * [autoInstallProviders] 为 `true` 时有效。
         */
        public var autoInstallProviderConfigurers: Boolean = true
    }

    /**
     * 与 [Application] 相关的配置。
     */
    public var application: ApplicationProperties = ApplicationProperties()

    /**
     * 与 [Application] 相关的配置。
     */
    public class ApplicationProperties {
        /**
         * 保持 [Application] （或者说整个程序活跃）的策略。默认为 [ApplicationLaunchMode.NONE]
         */
        public var applicationLaunchMode: ApplicationLaunchMode = ApplicationLaunchMode.NONE

    }
}


public enum class BotAutoStartMode {
    /**
     * 依次同步启动。
     */
    SYNC,

    /**
     * 每个 bot 独立地异步启动。
     * 如果 [SpringApplicationConfigurationProperties.BotProperties.autoRegistrationFailurePolicy] 为 [BotRegistrationFailurePolicy.ERROR],
     * 则异步中任意 bot 如果启动失败都会导致整体失败。
     */
    ASYNC
}

/**
 * 基于 [Application] 保活的策略
 */
public enum class ApplicationLaunchMode {
    /**
     * 使用一个独立的非守护线程来保持程序活跃。
     */
    THREAD,

    /**
     * 不进行任何行为，适用于环境中有其他可保证程序运行的内容，
     * 例如 spring-web。是默认选项
     */
    NONE
}


/**
 * 当自动扫描的bot注册或启动失败时的处理策略。
 */
public enum class BotRegistrationFailurePolicy {

    /**
     * 当bot注册或启动过程中出现异常或bot最终无法注册时都会抛出异常并终止程序。
     * 是建议的默认选择。
     */
    ERROR,

    /**
     * 当bot注册或启动过程中出现异常或bot最终无法注册时会输出带有异常信息的 `error` 日志。
     */
    ERROR_LOG,

    /**
     * 当bot注册或启动过程中出现异常或bot最终无法注册时会输出带有异常信息的 `warn` 日志。
     */
    WARN,

    /**
     * 当bot注册或启动过程中出现异常或bot最终无法注册时仅会输出 `debug` 调试日志。
     */
    IGNORE;
}

/**
 * 当自动扫描的bot的配置文件加载失败时候的策略（找不到文件、无法读取、无法解析为 [SerializableBotConfiguration] 等）。
 */
public enum class BotConfigResourceLoadFailurePolicy {

    /**
     * 当出现无法解析的资源文件时抛出异常来尝试中断整个处理流程。
     * 是建议的默认选择。
     */
    ERROR,

    /**
     * 当出现无法解析的资源文件时输出 `error` 级别的日志来尝试中断整个处理流程。
     */
    ERROR_LOG,

    /**
     * 当出现无法解析的资源文件时会输出 `warn` 日志。
     */
    WARN,

    /**
     * 当出现无法解析的资源文件时仅会输出 `debug` 调试日志。
     */
    IGNORE;
}


/**
 * 被加载的 [SerializableBotConfiguration] 无法找到任何可供注册的 [BotManager] 时的策略。
 */
public enum class MismatchConfigurableBotManagerPolicy {
    /**
     * 当无法找到任何可供注册的 [BotManager] 时抛出异常来尝试中断整个处理流程。
     */
    ERROR,

    /**
     * 当无法找到任何可供注册的 [BotManager] 时输出 `error` 级别的日志来尝试中断整个处理流程。
     * 是建议的默认选择。
     */
    ERROR_LOG,

    /**
     * 当无法找到任何可供注册的 [BotManager] 时输出 `warn` 级别的日志并尝试跳过。
     */
    WARN,

    /**
     * 当无法找到任何可供注册的 [BotManager] 时输出 `debug` 级别的日志并尝试跳过。
     */
    IGNORE;
}

