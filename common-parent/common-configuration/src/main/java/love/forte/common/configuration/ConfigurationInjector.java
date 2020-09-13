/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ConfigurationInjecter.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration;

import love.forte.common.configuration.impl.ConfigurationInjectorImpl4J;
import love.forte.common.utils.convert.ConverterManager;

/**
 *
 * 配置注入器, 向一个实例中注入配置信息。
 *
 * @see ConfigurationInjectorImpl4J
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface ConfigurationInjector {
    /**
     * 向一个配置类实例中注入配置信息。
     *
     * @see love.forte.common.configuration.annotation.AsConfig
     * @see love.forte.common.configuration.annotation.ConfigInject
     * @param configInstance 配置类实例
     * @param configuration 配置信息
     * @param converterManager 类型转化管理器，可能为null。
     *
     * @throws love.forte.common.configuration.exception.ConfigurationInjectException 如果注入的时候出现意外，则可能抛出此异常。
     */
    void inject(Object configInstance, Configuration configuration, ConverterManager converterManager);

    /**
     * 向一个配置类实例中注入配置信息。
     *
     * @see love.forte.common.configuration.annotation.AsConfig
     * @see love.forte.common.configuration.annotation.ConfigInject
     * @param configInstance 配置类实例
     * @param configuration 配置信息
     *
     * @throws love.forte.common.configuration.exception.ConfigurationInjectException 如果注入的时候出现意外，则可能抛出此异常。
     */
    default void inject(Object configInstance, Configuration configuration) {
        inject(configInstance, configuration, null);
    }
}
