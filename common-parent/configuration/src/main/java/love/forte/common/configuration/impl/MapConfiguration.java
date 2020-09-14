/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     LinkedHashMapConfiguration.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration.impl;

import love.forte.common.configuration.Configuration;
import love.forte.common.configuration.ConfigurationProperty;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 *
 * 基于 HashMap的 {@link Configuration} 基础实现类
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class MapConfiguration extends LinkedHashMap<String, ConfigurationProperty> implements Configuration {
    private static final long serialVersionUID = -6358293354735674011L;

    public MapConfiguration(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    public MapConfiguration(int initialCapacity) {
        super(initialCapacity);
    }
    public MapConfiguration() {
    }
    public MapConfiguration(Map<? extends String, ? extends ConfigurationProperty> m) {
        super(m);
    }
    public MapConfiguration(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    /**
     * 获取一项配置信息。
     *
     * @param key 这项配置的键。
     * @return 得到的信息
     */
    @Override
    public ConfigurationProperty getConfig(String key) {
        return get(key);
    }

    /**
     * 添加一项配置信息。
     * <p>
     * 如果想要使某个配置项为null，可以尝试使用 {@link NullConfigurationProperty}。
     *
     * @param key    键
     * @param config 配置信息
     * @return 如果有旧的配置被覆盖了，则此为旧配置信息。
     */
    @Override
    public ConfigurationProperty setConfig(String key, ConfigurationProperty config) {
        return put(key, config);
    }

    /**
     * 如果key对应的配置不存在，则存入一个配置信息。
     * 默认情况下不允许出现null值。
     *
     * @param key           键
     * @param value         要存入的配置信息
     * @param mergeFunction merge function
     * @return 存入的新值。
     */
    @Override
    public ConfigurationProperty mergeConfig(String key, ConfigurationProperty value, BiFunction<? super ConfigurationProperty, ? super ConfigurationProperty, ? extends ConfigurationProperty> mergeFunction) {
        return merge(key, value, mergeFunction);
    }

    /**
     * 判断是否存在某个键。
     *
     * @param key 配置键。
     * @return 是否存在。
     */
    @Override
    public boolean containsKey(String key) {
        return containsKey((Object) key);
    }

    /**
     * 获取所有的配置信息。
     *
     * @return config list.
     */
    @Override
    public Collection<ConfigurationProperty> getConfigProperties() {
        return values();
    }

    /**
     * 根据筛选条件获取所有的配置信息。
     *
     * @param testPredicate 筛选条件。
     * @return config list.
     */
    @Override
    public Collection<ConfigurationProperty> getConfigProperties(Predicate<ConfigurationProperty> testPredicate) {
        List<ConfigurationProperty> list =new ArrayList<>();
        final Collection<ConfigurationProperty> values = values();
        for (ConfigurationProperty value : values) {
            if(testPredicate.test(value)){
                list.add(value);
            }
        }
        return list;
    }
}
