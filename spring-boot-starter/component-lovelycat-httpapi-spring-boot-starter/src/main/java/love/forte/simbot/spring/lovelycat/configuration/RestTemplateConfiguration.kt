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
package love.forte.simbot.spring.lovelycat.configuration

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

/**
 * @author ForteScarlet
 */
@Suppress("SpringFacetCodeInspection")
@Configuration
@Import(HttpProperties::class)
public open class RestTemplateConfiguration {
    @Bean
    @ConditionalOnMissingBean
    open fun restTemplate(httpProperties: HttpProperties): RestTemplate {
        val simpleClientHttpRequestFactory = SimpleClientHttpRequestFactory()
        simpleClientHttpRequestFactory.setConnectTimeout(httpProperties.connectTimeout.toInt())
        simpleClientHttpRequestFactory.setReadTimeout(httpProperties.requestTimeout.toInt())
        return RestTemplate(simpleClientHttpRequestFactory)
    }
}