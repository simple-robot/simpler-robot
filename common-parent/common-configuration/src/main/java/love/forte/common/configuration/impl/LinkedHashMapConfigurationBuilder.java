/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     LinkedHashMapConfigurationBuilder.java
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
 * {@link ConfigurationBuilder} 基于 {@link MapConfiguration} 的基础实现类。
 *
 * 需要提供一个类型转化器 {@link ConverterManager} 。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class LinkedHashMapConfigurationBuilder implements ConfigurationBuilder {


    /** 类型转化器 */
    private final ConverterManager converterManager;
    /**
     * config map.
     */
    private final Map<String, ConfigurationProperty> configMap = new HashMap<>();

    /**
     * 构造，需要提供一个类型转化器。
     * @param converterManager 类型转化器
     */
    public LinkedHashMapConfigurationBuilder(ConverterManager converterManager){
        this.converterManager = converterManager;
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
        configMap.put(key, new ConverterConfigurationProperty(key, value, converterManager));
        return this;
    }

    /**
     * 构建一个 {@link Configuration} 实例。
     *
     * @return {@link Configuration} 实例
     */
    @Override
    public Configuration build() {
        return new MapConfiguration(configMap);
    }
}
