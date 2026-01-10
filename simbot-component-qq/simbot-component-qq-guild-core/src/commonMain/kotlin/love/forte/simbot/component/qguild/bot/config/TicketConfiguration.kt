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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 *
 * 用于配置 [QGBotFileConfiguration.Ticket] 信息的配置属性，
 * 提供多种可选的配置方案。
 *
 * ```json
 * {
 *   "ticket": {
 *     "type": "...",
 *     "prop1": "v1"
 *   }
 * }
 * ```
 *
 * @author ForteScarlet
 */
@UsedOnlyForConfigSerialization
@Serializable
public sealed class TicketConfiguration {

    /**
     * 得到 [QGBotFileConfiguration.Ticket] 结果
     */
    public abstract fun toTicket(): QGBotFileConfiguration.Ticket


    /**
     * 与 [QGBotFileConfiguration.Ticket] 一致的配置类型，
     * 也是 [TicketConfiguration] 的默认方案。
     *
     * ```json
     * {
     *   "ticket": {
     *     "type": "plain",
     *     "appId": "appId-value",
     *     "secret": "secret-value",
     *     "token": "token-value"
     *   }
     * }
     * ```
     *
     * 或
     *
     * ```json
     * {
     *   "ticket": {
     *     "appId": "appId-value",
     *     "secret": "secret-value",
     *     "token": "token-value"
     *   }
     * }
     * ```
     *
     * > Note: 默认 `type` 的支持需要 simbot-core 3.2.0+
     *
     */
    @Serializable
    @SerialName(Plain.TYPE)
    public data class Plain(
        /**
         * @see QGBotFileConfiguration.Ticket
         */
        val appId: String,

        /**
         * @see QGBotFileConfiguration.Ticket
         */
        val secret: String,

        /**
         * @see QGBotFileConfiguration.Ticket
         */
        val token: String
    ) : TicketConfiguration() {

        override fun toTicket(): QGBotFileConfiguration.Ticket =
            QGBotFileConfiguration.Ticket(appId, secret, token)

        public companion object {
            public const val TYPE: String = "plain"
        }
    }

    /**
     * 使用JVM参数或环境变量来读取 [QGBotFileConfiguration.Ticket] 中对应的配置类型。
     *
     * ```json
     * {
     *   "ticket": {
     *     "type": "env",
     *     "appId": "APP_ID",
     *     "secret": "SECRET",
     *     "token": "TOKEN",
     *     "plain": false
     *   }
     * }
     * ```
     *
     * [Env] 会首先尝试获取 JVM 参数，即运行时的 `-Dxxx=xxx` （也就是 `System.getProperty`），
     * 当不存在时会尝试通过环境变量获取（即 `System.getenv`）。
     *
     * ## 原始输入
     *
     * 当 [plain] 为 `true` 时（默认为 `false`），如果某属性通过上述流程无法获取到值，则会尝试直接使用原始输入值。
     *
     * 例如：
     * ```json
     * {
     *   "ticket": {
     *     "type": "env",
     *     "appId": "aaa",
     *     "secret": "MY_SECRET",
     *     "token": "MY_TOKEN",
     *     "plain": true
     *    }
     *  }
     *  ```
     *
     * 示例中的 `appId` 并没有找到名为 `aaa` 的 JVM 参数或环境变量，因此它会直接使用 `aaa` 作为 appId。
     * 而如果 [plain] 为 `false`，则会直接抛出 [IllegalStateException] 异常。
     *
     *
     * 当一个属性以 `PLAIN:` （区分大小写） 为前缀，则会直接使用原始输入值，不会尝试从环境变量中获取。
     *
     * 例如：
     * ```json
     * {
     *   "ticket": {
     *     "type": "env",
     *     "appId": "PLAIN:aaa",
     *     "secret": "MY_SECRET",
     *     "token": "MY_TOKEN",
     *     "plain": false
     *   }
     * }
     * ```
     *
     * 示例中 `appId` 会直接使用 `aaa` 作为 appId，而不会尝试从 JVM 参数或环境变量中获取。
     *
     *
     */
    @Serializable
    @SerialName(Env.TYPE)
    public data class Env(
        /**
         * @see QGBotFileConfiguration.Ticket
         */
        val appId: String,

        /**
         * @see QGBotFileConfiguration.Ticket
         */
        val secret: String,

        /**
         * @see QGBotFileConfiguration.Ticket
         */
        val token: String,

        /**
         * 是否允许在无法获取到对应的 JVM 参数或环境变量时，直接使用原始输入值。
         */
        val plain: Boolean = false,
    ) : TicketConfiguration() {

        /**
         * @throws IllegalStateException 当 [plain] 为 `false` 时，无法获取到对应的 JVM 参数或环境变量时。
         */
        override fun toTicket(): QGBotFileConfiguration.Ticket {
            val appIdValue = prop("appId", appId)
            val secretValue = prop("secret", secret)
            val tokenValue = prop("token", token)

            return QGBotFileConfiguration.Ticket(appIdValue, secretValue, tokenValue)
        }

        private fun prop(key: String, prop: String): String {
            if (prop.startsWith(PLAIN_PREFIX)) {
                return prop.substringAfter(PLAIN_PREFIX, missingDelimiterValue = "")
            }

            return systemProperty(prop)
                ?: systemEnv(prop)
                ?: if (plain) return prop
                else throw IllegalStateException("Cannot find property '$prop' for ticket '$key' in JVM or environment variables.")
        }

        public companion object {
            public const val TYPE: String = "env"
            private const val PLAIN_PREFIX = "PLAIN:"
        }
    }


    public companion object
}

internal expect fun systemProperty(name: String): String?
internal expect fun systemEnv(name: String): String?
