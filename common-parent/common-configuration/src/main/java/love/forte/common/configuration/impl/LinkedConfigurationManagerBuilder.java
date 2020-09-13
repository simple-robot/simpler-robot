/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     LinkedCOnfigurationManagerBuilder.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration.impl;

import love.forte.common.configuration.Configuration;
import love.forte.common.configuration.ConfigurationBuilder;
import love.forte.common.configuration.ConfigurationProperty;
import love.forte.common.utils.convert.ConverterManager;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 用于构建一个 {@link MapConfiguration} 实例。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class LinkedConfigurationManagerBuilder implements ConfigurationBuilder {

    private final ConverterManager converterManager;

    private final Map<String, ConfigurationProperty> properties;


    public LinkedConfigurationManagerBuilder(ConverterManager converterManager){
        this.converterManager = converterManager;
        this.properties = new HashMap<>(8);
    }

    
    /**
     * 追加 多项配置信息。
     *
     * @param configMap configMap
     * @return builder
     */
    @Override
    public ConfigurationBuilder append(Map<String, Object> configMap) {
        configMap.forEach(this::append);
        return this;
    }

    /**
     * 追加单项配置信息。
     *
     * @param key   key
     * @param value config value
     * @return this builder
     */
    @Override
    public ConfigurationBuilder append(String key, Object value) {
        ConfigurationProperty prop = new ConverterConfigurationProperty(key, value, converterManager);
        properties.put(key, prop);
        return this;
    }

    /**
     * 构建一个 {@link Configuration} 实例。
     *
     * @return {@link Configuration} 实例
     */
    @Override
    public Configuration build() {
        return new MapConfiguration(properties);
    }
}
