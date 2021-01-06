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

package love.forte.simbot.spring.lovelycat.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import love.forte.simbot.component.lovelycat.configuration.LovelyCatServerProperties;
import love.forte.simbot.spring.autoconfigure.properties.SimbotCompLovelycatServerProperties;
import love.forte.simbot.spring.lovelycat.server.LovelyCatServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

/**
 * @author ForteScarlet
 */
@Configuration
@Import(value = {
        SimbotCompLovelycatServerProperties.class,
        LovelyCatServer.class
})
public class LovelycatServerConfiguration {

    private final SimbotCompLovelycatServerProperties simbotCompLovelycatServerProperties;

    @Autowired
    public LovelycatServerConfiguration(SimbotCompLovelycatServerProperties simbotCompLovelycatServerProperties) {
        this.simbotCompLovelycatServerProperties = simbotCompLovelycatServerProperties;
    }

    @Bean
    public LovelyCatServerProperties lovelyCatServerProperties(){
        LovelyCatServerProperties properties = new LovelyCatServerProperties();
        properties.setCors(true);
        properties.setEnable(simbotCompLovelycatServerProperties.isEnable());
        properties.setPort(simbotCompLovelycatServerProperties.getPort());
        properties.setPath(simbotCompLovelycatServerProperties.getPath());
        return properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
