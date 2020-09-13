/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ConfigurationInjectorImpl.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration.impl;

import love.forte.common.configuration.Configuration;
import love.forte.common.configuration.ConfigurationInjector;
import love.forte.common.configuration.annotation.AsConfig;
import love.forte.common.configuration.exception.ConfigurationInjectException;
import love.forte.common.utils.annotation.AnnotationUtil;
import love.forte.common.utils.convert.ConverterManager;

/**
 *
 * 配置信息注入实现, 单例工具类
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class ConfigurationInjectorImpl4J implements ConfigurationInjector {
    public static final ConfigurationInjectorImpl4J INSTANCE;
    static {
        INSTANCE = new ConfigurationInjectorImpl4J();
    }
    private ConfigurationInjectorImpl4J(){}

    /**
     * 向一个配置类实例中注入配置信息。
     *
     * @see love.forte.common.configuration.annotation.AsConfig
     * @see love.forte.common.configuration.annotation.ConfigInject
     *
     * @param configInstance 配置类实例
     * @param configuration  配置信息
     * @throws ConfigurationInjectException 如果注入的时候出现意外，则可能抛出此异常。
     */
    @Override
    public void inject(Object configInstance, Configuration configuration, ConverterManager converterManager) {
        final Class<?> configClass = configInstance.getClass();
        final AsConfig asConfig = AnnotationUtil.getAnnotation(configClass, AsConfig.class);





    }
}
