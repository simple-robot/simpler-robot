/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     SimbotAppConfiguration.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.spring.autoconfigure;

import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.annotation.SimbotResource;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Configuration
@Import({
        SpringEnvironmentConfiguration.class,
        SimbotAppProperties.class,
        SpringDependBeanFactory.class
})
@SimbotApplication({
        @SimbotResource(value = "def-empty.properties", orIgnore = true)
})
public class SimbotAppConfiguration {

    private final SpringEnvironmentConfiguration springEnvironmentConfiguration;
    private final SimbotAppProperties simbotAppProperties;
    private final SpringDependBeanFactory springDependBeanFactory;
    private final ApplicationArguments applicationArguments;


    public SimbotAppConfiguration(SpringEnvironmentConfiguration springEnvironmentConfiguration, SimbotAppProperties simbotAppProperties, SpringDependBeanFactory springDependBeanFactory, ApplicationArguments applicationArguments) {
        this.springEnvironmentConfiguration = springEnvironmentConfiguration;
        this.simbotAppProperties = simbotAppProperties;
        this.springDependBeanFactory = springDependBeanFactory;
        this.applicationArguments = applicationArguments;
    }

    @Bean
    public SimbotContext simbotApp() {
        Class<?> applicationClass = simbotAppProperties.getAppClass();
        if (applicationClass == null) {
            applicationClass = SimbotAppConfiguration.class;
        }

        final String[] sourceArgs = applicationArguments.getSourceArgs();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        // run simbot app.
        return SimbotApp.run(applicationClass, loader, springDependBeanFactory, springEnvironmentConfiguration, sourceArgs);
    }













}
