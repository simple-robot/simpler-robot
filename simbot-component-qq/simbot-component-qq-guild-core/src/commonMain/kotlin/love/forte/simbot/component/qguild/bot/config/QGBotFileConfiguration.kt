/*
 * Copyright (c) 2023-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.bot.config

import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.bot.SerializableBotConfiguration
import love.forte.simbot.bot.configuration.DispatcherConfiguration
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.qguild.QQGuild
import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.event.Signal
import love.forte.simbot.qguild.stdlib.Bot
import love.forte.simbot.qguild.stdlib.BotConfiguration

/**
 * 标记一个类型为**仅用于配置序列化**的类型。
 *
 * 被标记的类型在除了配置序列化场景以外的情况下均不保证兼容和稳定，
 * 并且序列化的稳定以 `JSON` 格式为主要目标。
 */
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class UsedOnlyForConfigSerialization

/**
 * bot配置文件所对应的配置类，
 *
 * 通过由配置文件读取而来的信息来对指定Bot进行信息配置。
 *
 * ```json
 * {
 *    "component": "simbot.qqguild",
 *    "ticket": {
 *      "appId": "appId-value",
 *      "secret": "secret-value",
 *      "token": "token-value",
 *    },
 *    "config": null
 * }
 * ```
 *
 */
@Suppress("MemberVisibilityCanBePrivate")
@Serializable
@SerialName(QQGuildComponent.ID_VALUE)
@UsedOnlyForConfigSerialization
public data class QGBotFileConfiguration(
    /**
     * bot相关的票据信息，必填。
     */
    val ticket: TicketConfiguration,

    /**
     * 其他配置信息。
     */
    val config: Config? = null
) : SerializableBotConfiguration() {

    /**
     * bot票据信息。是与 [Bot.Ticket] 相对应的映射类
     * @see Bot.Ticket
     */
    @Serializable
    @UsedOnlyForConfigSerialization
    public data class Ticket(
        /**
         * 用于识别一个机器人的 id
         */
        val appId: String,

        /**
         * 用于在 oauth 场景进行请求签名的密钥
         */
        val secret: String,

        /**
         * 机器人token，用于以机器人身份调用 openapi，格式为 `${app_id}.${random_str}`
         */
        val token: String
    )

    /**
     * 其他配置信息。
     *
     * ```json
     * {
     *   "config": { ... }
     * }
     * ```
     *
     */
    @Serializable
    @UsedOnlyForConfigSerialization
    public data class Config(
        /**
         * 目标服务器地址。默认为null，使用 [BotConfiguration] 的默认情况。
         *
         * 当 [serverUrl] 的值为特殊值：`"SANDBOX"` 时会选择使用 [QQGuild.SANDBOX_URL_STRING]
         *
         * 自定义服务器地址：
         *
         * ```json
         * {
         *   "config": {
         *     "serverUrl": "https://xxx.com"
         *   }
         * }
         * ```
         *
         * `"SANDBOX"` 特殊值：
         *
         * ```json
         * {
         *   "config": {
         *     "serverUrl": "SANDBOX"
         *   }
         * }
         * ```
         */
        val serverUrl: String? = null,

        /**
         * 分片信息配置，默认为 [ShardConfig.Full]
         *
         * ```json
         * {
         *   "config": {
         *     "shard": {...}
         *   }
         * }
         * ```
         *
         * @see ShardConfig
         *
         */
        @SerialName("shard") val shardConfig: ShardConfig = ShardConfig.Full,

        /**
         * 事件订阅配置。
         *
         * ```json
         * {
         *    "config": {
         *      "intents": { ... }
         *    }
         * }
         * ```
         *
         * 默认情况下订阅如下事件：
         * - [频道相关事件][EventIntents.Guilds]
         * - [频道成员相关事件][EventIntents.GuildMembers]
         * - [公域消息相关事件][EventIntents.PublicGuildMessages]
         *
         * @see IntentsConfig
         */
        @SerialName("intents") // TODO type: ALL?
        val intentsConfig: IntentsConfig = IntentsConfig.Raw(
            EventIntents.Guilds.intents + EventIntents.GuildMembers.intents + EventIntents.PublicGuildMessages.intents
        ),

        /**
         * 用作 [Signal.Identify.Data.properties] 中的参数。
         *
         * ```json
         * {
         *    "config": {
         *      "clientProperties": {
         *        "k1": "v1",
         *        "foo": "bar"
         *       }
         *    }
         * }
         * ```
         *
         *
         */
        public val clientProperties: Map<String, String>? = null,

        /**
         * 与部分超时相关的配置信息。
         *
         * ```json
         * {
         *   "config": {
         *     "timeout": {
         *       "apiHttpRequestTimeoutMillis": ...,
         *       "apiHttpConnectTimeoutMillis": ...,
         *       "apiHttpSocketTimeoutMillis": ...
         *     }
         *   }
         * }
         * ```
         *
         * @see BotConfiguration.apiHttpRequestTimeoutMillis
         * @see BotConfiguration.apiHttpConnectTimeoutMillis
         * @see BotConfiguration.apiHttpSocketTimeoutMillis
         *
         */
        @SerialName("timeout") public val timeoutConfig: TimeoutConfig? = null,

        /**
         * 缓存相关配置。
         *
         * ```json
         * {
         *   "config": {
         *     "cache": { ... }
         *   }
         * }
         * ```
         *
         * 默认会启用 [TransmitCacheConfig]
         *
         * @see CacheConfig
         *
         */
        @SerialName("cache") public val cacheConfig: CacheConfig? = DEFAULT.cacheConfig,

        /**
         * 调度器相关配置。
         *
         * @see DispatcherConfiguration
         */
        @SerialName("dispatcher") public val dispatcherConfiguration: DispatcherConfiguration? = null,

    ) {
        /**
         * 是否禁用 ws
         * @since 4.1.0
         */
        public var disableWs: Boolean? = null

        public companion object {
            internal const val SERVER_URL_SANDBOX_VALUE: String = "SANDBOX"
            private val DEFAULT = QGBotComponentConfiguration()
        }
    }

    /**
     * 与部分超时相关的配置信息。
     * ```json
     * {
     *   "config": {
     *     "timeout": {
     *       "apiHttpRequestTimeoutMillis": ...,
     *       "apiHttpConnectTimeoutMillis": ...,
     *       "apiHttpSocketTimeoutMillis": ...
     *     }
     *   }
     * }
     * ```
     *
     * @see BotConfiguration.apiHttpRequestTimeoutMillis
     * @see BotConfiguration.apiHttpConnectTimeoutMillis
     * @see BotConfiguration.apiHttpSocketTimeoutMillis
     */
    @Serializable
    public data class TimeoutConfig(
        /** @see BotConfiguration.apiHttpRequestTimeoutMillis */
        val apiHttpRequestTimeoutMillis: Long? = null,
        /** @see BotConfiguration.apiHttpConnectTimeoutMillis */
        val apiHttpConnectTimeoutMillis: Long? = null,
        /** @see BotConfiguration.apiHttpSocketTimeoutMillis */
        val apiHttpSocketTimeoutMillis: Long? = null,
    )

    internal fun includeConfig(cpConfiguration: QGBotComponentConfiguration) {
        cpConfiguration.botConfig {
            val configuration = this
            config?.apply {
                configuration.shard = shardConfig.shard
                configuration.intents = intentsConfig.intents
                clientProperties?.also { configuration.clientProperties = it }

                serverUrl?.also { su ->
                    if (su == Config.SERVER_URL_SANDBOX_VALUE) {
                        configuration.serverUrl = QQGuild.SANDBOX_URL
                    } else {
                        configuration.serverUrl = Url(su)
                    }
                }

                timeoutConfig?.also { timeout ->
                    configuration.apiHttpRequestTimeoutMillis = timeout.apiHttpRequestTimeoutMillis
                    configuration.apiHttpConnectTimeoutMillis = timeout.apiHttpConnectTimeoutMillis
                    configuration.apiHttpSocketTimeoutMillis = timeout.apiHttpSocketTimeoutMillis
                }

                dispatcherConfiguration?.dispatcher?.also { dispatcher ->
                    configuration.coroutineContext += dispatcher
                }

                disableWs?.also { disableWs -> configuration.disableWs = disableWs }
            }
        }
    }

    public companion object
}
