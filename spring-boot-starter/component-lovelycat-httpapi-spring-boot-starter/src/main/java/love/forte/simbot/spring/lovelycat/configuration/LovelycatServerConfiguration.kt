/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     LovelycatServerConfiguration.java
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
package love.forte.simbot.spring.lovelycat.configuration

import love.forte.simbot.component.lovelycat.configuration.LovelyCatServerProperties
import love.forte.simbot.core.SimbotContext
import love.forte.simbot.spring.autoconfigure.properties.SimbotCompLovelycatServerProperties
import love.forte.simbot.spring.lovelycat.server.LovelyCatServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * @author ForteScarlet
 */
@Configuration
@Import(value = [RestTemplateConfiguration::class, SimbotCompLovelycatServerProperties::class, LovelyCatServer::class])
@ConditionalOnBean(SimbotContext::class)
public open class LovelycatServerConfiguration @Autowired constructor(private val simbotCompLovelycatServerProperties: SimbotCompLovelycatServerProperties) {
    @Bean
    open fun lovelyCatServerProperties(): LovelyCatServerProperties {
        val properties = LovelyCatServerProperties()
        properties.cors = true
        properties.enable = simbotCompLovelycatServerProperties.isEnable
        properties.port = simbotCompLovelycatServerProperties.port
        properties.path = simbotCompLovelycatServerProperties.path
        return properties
    }
}