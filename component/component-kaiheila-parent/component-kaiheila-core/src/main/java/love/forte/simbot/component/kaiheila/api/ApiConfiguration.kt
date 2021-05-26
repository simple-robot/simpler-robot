/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     ApiConfiguration.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

@file:JvmName("ApiConfigurations")
package love.forte.simbot.component.kaiheila.api


/**
 * API请求配置信息。
 *
 *
 * 参考 [开黑啦常规http接口规范][https://developer.kaiheila.cn/doc/reference#%E5%B8%B8%E8%A7%84%20http%20%E6%8E%A5%E5%8F%A3%E8%A7%84%E8%8C%83]
 *
 * @author ForteScarlet
 */
public interface ApiConfiguration {

    /** api版本信息。 */
    val apiVersion: ApiVersion


    /** 鉴权Token获取方式。 */
    val authorizationType: AuthorizationType


    /** 是否指定语言。 */
    val language: String?

    companion object {
        @JvmStatic
        fun builder(): ApiConfigurationBuilder = ApiConfigurationBuilder()
    }

}


/**
 * http鉴权类型。
 *
 * 参考 [http接口规范 - 鉴权][https://developer.kaiheila.cn/doc/reference#%E5%B8%B8%E8%A7%84%20http%20%E6%8E%A5%E5%8F%A3%E8%A7%84%E8%8C%83]
 *
 */
public enum class AuthorizationType(private val authorizationFunction: (token: String) -> String) {
    /** BOT鉴权类型 */
    BOT("BOT"),
    /** Oauth2鉴权类型 */
    OAUTH2("Bearer");

    constructor(headerPre: String): this({ token -> "$headerPre $token" })

    /**
     * Get token by [authorizationFunction].
     */
    fun getAuthorization(token: String) = authorizationFunction(token)

}


/**
 * [ApiConfiguration] 构建器。
 */
public class ApiConfigurationBuilder {
    @ApiConfigurationBuildDsl
    var apiVersion: ApiVersion? = null
    @ApiConfigurationBuildDsl
    var authorizationType: AuthorizationType? = null
    @ApiConfigurationBuildDsl
    var language: String? = null

    @Synchronized
    fun build(): ApiConfiguration {
        return ApiConfigurationImpl(
            requireNotNull(apiVersion) { "Required apiVersion was null." },
            requireNotNull(authorizationType) { "Required authorizationType was null." },
            language
        )
    }
}


private data class ApiConfigurationImpl(
    override val apiVersion: ApiVersion,
    override val authorizationType: AuthorizationType,
    override val language: String?
) : ApiConfiguration



@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@DslMarker
public annotation class ApiConfigurationBuildDsl


public inline fun apiConfiguration(configBlock: ApiConfigurationBuilder.() -> Unit): ApiConfiguration {
    return ApiConfigurationBuilder().apply(configBlock).build()
}


@ApiConfigurationBuildDsl
public inline var ApiConfigurationBuilder.apiVersionNumber: Int?
get() = apiVersion?.versionNumber
set(value) {
    apiVersion = value?.let { apiVersion { it } }
}








