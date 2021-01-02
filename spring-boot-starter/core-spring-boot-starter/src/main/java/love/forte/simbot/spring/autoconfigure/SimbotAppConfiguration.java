/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

package love.forte.simbot.spring.autoconfigure;

import cn.hutool.core.convert.ConverterRegistry;
import love.forte.common.utils.convert.ConverterManager;
import love.forte.common.utils.convert.HutoolConverterManagerImpl;
import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.annotation.SimbotResource;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Configuration(proxyBeanMethods = false)
@Import({
        SimbotAppProperties.class,
        SpringDependBeanFactory.class
})
@SimbotApplication({
        @SimbotResource(value = "def-empty.properties", orIgnore = true)
})
public class SimbotAppConfiguration {

    private final SimbotAppProperties simbotAppProperties;
    private final SpringDependBeanFactory springDependBeanFactory;
    private final ApplicationArguments applicationArguments;


    public SimbotAppConfiguration(SimbotAppProperties simbotAppProperties,
                                  SpringDependBeanFactory springDependBeanFactory,
                                  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
                                          ApplicationArguments applicationArguments) {
        this.simbotAppProperties = simbotAppProperties;
        this.springDependBeanFactory = springDependBeanFactory;
        this.applicationArguments = applicationArguments;
    }

    /**
     * simbot configuration 类型转化器。
     */
    @Bean
    @ConditionalOnMissingBean(ConverterManager.class)
    public ConverterManager converterManager() {
        return new HutoolConverterManagerImpl(ConverterRegistry.getInstance());
    }

    @Bean("simbotContext")
    public SimbotContext simbotApp(ConfigurableEnvironment environment, ConverterManager converterManager) {
        Class<?> applicationClass = simbotAppProperties.getAppClass();
        if (applicationClass == null) {
            applicationClass = SimbotAppConfiguration.class;
        }

        final String[] sourceArgs = applicationArguments.getSourceArgs();
        // ClassLoader loader = ClassLoader.getSystemClassLoader();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = getClass().getClassLoader();
        }
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }


        SpringEnvironmentConfiguration springEnvironmentConfiguration = new SpringEnvironmentConfiguration(environment, converterManager);

        // run simbot app.
        return SimbotApp.run(applicationClass, loader, springDependBeanFactory, springEnvironmentConfiguration, sourceArgs);
    }


}
