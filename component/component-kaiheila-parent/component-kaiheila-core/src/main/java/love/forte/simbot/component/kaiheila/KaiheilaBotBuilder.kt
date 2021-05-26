/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.component.kaiheila

import love.forte.simbot.builder.Builder
import love.forte.simbot.component.kaiheila.api.ApiConfiguration
import love.forte.simbot.component.kaiheila.api.ApiConfigurationBuilder
import love.forte.simbot.component.kaiheila.api.apiVersionNumber
import love.forte.simbot.component.kaiheila.api.authorizationTypeByBot
import love.forte.simbot.component.kaiheila.api.apiConfiguration as api_apiConfiguration


public interface KaiheilaBotBuilder<B : KaiheilaBot> : Builder<B> {
    var apiConfiguration: ApiConfiguration?
    var clientId: String?
    var token: String? // null able
    var clientSecret: String? // null able
}

/**
 * [WebsocketBot] 实例构建器。
 *
 * ```kotlin
 *
 * val botInfo = websocketBot {
 *     apiConfiguration {
 *         apiVersionNumber = 3
 *         authorizationTypeByBot()
 *         this.language = "zh_cn"
 *     }
 *     clientId = "clientId"
 *     token = "token"
 *     clientSecret = "secret"
 * }
 *
 * ```
 *
 * @see websocketBot
 *
 */
public class WebsocketBotBuilder : KaiheilaBotBuilder<WebsocketBot> {
    @KaiheilaBotBuilderDsl override var apiConfiguration: ApiConfiguration? = null
    @KaiheilaBotBuilderDsl override var clientId: String? = null
    @KaiheilaBotBuilderDsl override var token: String? = null // null able
    @KaiheilaBotBuilderDsl override var clientSecret: String? = null // null able


    override fun build(): WebsocketBot {
        return WebsocketBotImpl(
            apiConfiguration = requireNotNull(apiConfiguration) { "Require value 'apiConfiguration' was null." },
            clientId = requireNotNull(clientId) { "Require value 'clientId' was null." },
            token = requireNotNull(token) { "Require value 'token' was null." },
            clientSecret = requireNotNull(clientSecret) { "Require value 'clientSecret' was null." },
        )
    }
}

/**
 * [WebhookBot] 实例构建器。
 *
 * ```kotlin
 *
 * val botInfo = webhookBot {
 *     apiConfiguration {
 *         apiVersionNumber = 3
 *         authorizationTypeByBot()
 *         this.language = "zh_cn"
 *     }
 *     clientId = "clientId"
 *     token = "token"
 *     clientSecret = "secret"
 *     verifyToken = "verifyToken"
 *     encryptKey = null
 * }
 *
 * ```
 *
 * @see webhookBot
 */
public class WebhookBotBuilder : KaiheilaBotBuilder<WebhookBot> {
    @KaiheilaBotBuilderDsl override var apiConfiguration: ApiConfiguration? = null
    @KaiheilaBotBuilderDsl override var clientId: String? = null
    @KaiheilaBotBuilderDsl override var token: String? = null // null able
    @KaiheilaBotBuilderDsl override var clientSecret: String? = null // null able
    @KaiheilaBotBuilderDsl var verifyToken: String? = null
    @KaiheilaBotBuilderDsl var encryptKey: String? = null // null able
    override fun build(): WebhookBot {
        return WebhookBotImpl(
            apiConfiguration = requireNotNull(apiConfiguration) { "Require value 'apiConfiguration' was null." },
            clientId = requireNotNull(clientId) { "Require value 'clientId' was null." },
            token = requireNotNull(token) { "Require value 'token' was null." },
            clientSecret = requireNotNull(clientSecret) { "Require value 'clientSecret' was null." },
            verifyToken = requireNotNull(verifyToken) { "Require value 'verifyToken' was null." },
            encryptKey = encryptKey
        )
    }
}


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@DslMarker
public annotation class KaiheilaBotBuilderDsl


public inline fun websocketBot(block: WebsocketBotBuilder.() -> Unit): WebsocketBot {
    return WebsocketBotBuilder().apply(block).build()
}

public inline fun webhookBot(block: WebhookBotBuilder.() -> Unit): WebhookBot {
    return WebhookBotBuilder().apply(block).build()
}

@KaiheilaBotBuilderDsl
public inline fun <B : KaiheilaBot> KaiheilaBotBuilder<B>.apiConfiguration(block: ApiConfigurationBuilder.() -> Unit) {
    this.apiConfiguration = api_apiConfiguration(block)
}



private data class WebsocketBotImpl(
    override val apiConfiguration: ApiConfiguration,
    override val clientId: String,
    override var token: String,
    override var clientSecret: String
) : WebsocketBot

private data class WebhookBotImpl(
    override val apiConfiguration: ApiConfiguration,
    override val clientId: String,
    override var token: String,
    override var clientSecret: String,
    override var verifyToken: String,
    override var encryptKey: String?
) : WebhookBot


fun main() {
    val bot1 = websocketBot {
        apiConfiguration {
            apiVersionNumber = 3
            authorizationTypeByBot()
            this.language = "zh_cn"
        }
        clientId = "clientId"
        token = "token"
        clientSecret = "secret"
    }

    val bot2 = webhookBot {
        apiConfiguration {
            apiVersionNumber = 3
            authorizationTypeByBot()
            this.language = "zh_cn"
        }
        clientId = "clientId"
        token = "token"
        clientSecret = "secret"
        verifyToken = "verifyToken"
        encryptKey = null
    }
    println(bot1)
}