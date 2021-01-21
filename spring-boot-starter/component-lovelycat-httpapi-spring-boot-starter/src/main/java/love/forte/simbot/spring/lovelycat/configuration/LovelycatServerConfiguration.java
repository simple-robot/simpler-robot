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

import love.forte.simbot.component.lovelycat.configuration.LovelyCatServerProperties;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.http.configuration.HttpProperties;
import love.forte.simbot.spring.autoconfigure.properties.SimbotCompLovelycatServerProperties;
import love.forte.simbot.spring.lovelycat.server.LovelyCatServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author ForteScarlet
 */
@Configuration
@Import(value = {
        SimbotCompLovelycatServerProperties.class,
        LovelyCatServer.class
})
@ConditionalOnBean(SimbotContext.class)
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

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(SimbotContext simbotContext){
        HttpProperties httpProperties = simbotContext.get(HttpProperties.class);

        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(httpProperties.getConnectTimeout().intValue());
        simpleClientHttpRequestFactory.setReadTimeout(httpProperties.getRequestTimeout().intValue());

        return new RestTemplate(simpleClientHttpRequestFactory);
    }


}
