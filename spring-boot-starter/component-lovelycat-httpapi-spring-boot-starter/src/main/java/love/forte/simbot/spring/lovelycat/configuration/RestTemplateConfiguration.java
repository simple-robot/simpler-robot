/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     ResetTemplateConfiguration.java
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
@Import(HttpProperties.class)
public class RestTemplateConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(HttpProperties httpProperties){
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(httpProperties.getConnectTimeout().intValue());
        simpleClientHttpRequestFactory.setReadTimeout(httpProperties.getRequestTimeout().intValue());
        return new RestTemplate(simpleClientHttpRequestFactory);
    }
}
