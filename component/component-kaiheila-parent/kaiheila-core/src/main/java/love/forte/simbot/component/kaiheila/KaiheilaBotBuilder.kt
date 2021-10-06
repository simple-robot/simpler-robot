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

import love.forte.simbot.component.kaiheila.api.ApiConfiguration
import love.forte.simbot.component.kaiheila.api.ApiConfigurationBuilder
import love.forte.simbot.component.kaiheila.api.apiConfiguration as api_apiConfiguration


public interface KaiheilaBotBuilder<B : KhlBot>{
    var apiConfiguration: ApiConfiguration?
    var clientId: String?
    var token: String? // null able
    var clientSecret: String? // null able

    // companion object Builder {
    //     @JvmStatic fun websocketBotBuilder() = WebsocketBotBuilder()
    //     @JvmStatic fun webhookBotBuilder() = WebhookBotBuilder()
    // }

}

//
// /**
//  * [WebsocketBot] 实例构建器。
//  *
//  * ```kotlin
//  *
//  * val botInfo = websocketBot {
//  *     apiConfiguration {
//  *         apiVersionNumber = 3
//  *         authorizationTypeByBot()
//  *         this.language = "zh_cn"
//  *     }
//  *     clientId = "clientId"
//  *     token = "token"
//  *     clientSecret = "secret"
//  * }
//  *
//  * ```
//  *
//  * @see websocketBot
//  *
//  */
// public class WebsocketBotBuilder : KaiheilaBotBuilder<WebsocketBot> {
//     @KaiheilaBotBuilderDsl override var apiConfiguration: ApiConfiguration? = null
//     @KaiheilaBotBuilderDsl override var clientId: String? = null
//     @KaiheilaBotBuilderDsl override var token: String? = null // null able
//     @KaiheilaBotBuilderDsl override var clientSecret: String? = null // null able
//
//
//     fun build(): WebsocketBot {
//         return WebsocketBotImpl(
//             apiConfiguration = requireNotNull(apiConfiguration) { "Require value 'apiConfiguration' was null." },
//             clientId = requireNotNull(clientId) { "Require value 'clientId' was null." },
//             token = requireNotNull(token) { "Require value 'token' was null." },
//             clientSecret = requireNotNull(clientSecret) { "Require value 'clientSecret' was null." },
//         )
//     }
// }
//
// /**
//  * [WebhookBot] 实例构建器。
//  *
//  * ```kotlin
//  *
//  * val botInfo = webhookBot {
//  *     apiConfiguration {
//  *         apiVersionNumber = 3
//  *         authorizationTypeByBot()
//  *         this.language = "zh_cn"
//  *     }
//  *     clientId = "clientId"
//  *     token = "token"
//  *     clientSecret = "secret"
//  *     verifyToken = "verifyToken"
//  *     encryptKey = null
//  * }
//  *
//  * ```
//  *
//  * @see webhookBot
//  */
// public class WebhookBotBuilder : KaiheilaBotBuilder<WebhookBot> {
//     @KaiheilaBotBuilderDsl override var apiConfiguration: ApiConfiguration? = null
//     @KaiheilaBotBuilderDsl override var clientId: String? = null
//     @KaiheilaBotBuilderDsl override var token: String? = null // null able
//     @KaiheilaBotBuilderDsl override var clientSecret: String? = null // null able
//     @KaiheilaBotBuilderDsl var verifyToken: String? = null
//     @KaiheilaBotBuilderDsl var encryptKey: String? = null // null able
//     fun build(): WebhookBot {
//         return WebhookBotImpl(
//             apiConfiguration = requireNotNull(apiConfiguration) { "Require value 'apiConfiguration' was null." },
//             clientId = requireNotNull(clientId) { "Require value 'clientId' was null." },
//             token = requireNotNull(token) { "Require value 'token' was null." },
//             clientSecret = requireNotNull(clientSecret) { "Require value 'clientSecret' was null." },
//             verifyToken = requireNotNull(verifyToken) { "Require value 'verifyToken' was null." },
//             encryptKey = encryptKey
//         )
//     }
// }


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@DslMarker
public annotation class KaiheilaBotBuilderDsl


@KaiheilaBotBuilderDsl
public inline fun <B : KhlBot> KaiheilaBotBuilder<B>.apiConfiguration(block: ApiConfigurationBuilder.() -> Unit) {
    this.apiConfiguration = api_apiConfiguration(block)
}



