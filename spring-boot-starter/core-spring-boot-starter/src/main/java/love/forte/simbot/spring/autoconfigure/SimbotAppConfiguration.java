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

package love.forte.simbot.spring.autoconfigure;

import cn.hutool.core.convert.ConverterRegistry;
import love.forte.common.utils.convert.ConverterManager;
import love.forte.common.utils.convert.HutoolConverterManagerImpl;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Import({
        SimbotAppPropertiesConfiguration.class,
        SpringDependBeanFactory.class
})
@Configuration
@AutoConfigureAfter(SimbotAppPropertiesConfiguration.class)
public class SimbotAppConfiguration {

    private final SimbotAppProperties simbotAppProperties;
    private final SpringDependBeanFactory springDependBeanFactory;
    // private final ConfigurableEnvironment environment;

    public SimbotAppConfiguration(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
                                          SimbotAppProperties simbotAppProperties,
                                  SpringDependBeanFactory springDependBeanFactory,
                                  ConfigurableEnvironment environment) {
        this.simbotAppProperties = simbotAppProperties;
        this.springDependBeanFactory = springDependBeanFactory;
        // this.environment = environment;
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
        System.out.println("Simbot context run!");
        Class<?> applicationClass = simbotAppProperties.getAppClass();
        if (applicationClass == null) {
            applicationClass = SimbotAppConfiguration.class;
        }

        // final String[] sourceArgs = applicationArguments.getSourceArgs();
        final String[] sourceArgs = new String[0]; //applicationArguments.getSourceArgs();
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


    // @Override
    // public void run(ApplicationArguments args) {
    //     applicationArguments = args;
    // }
}
