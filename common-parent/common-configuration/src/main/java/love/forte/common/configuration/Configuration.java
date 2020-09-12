/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Configuration.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 *
 * 配置信息接口。
 *
 * 配置信息总是以 <b>键值对</b> 的格式存在的。
 *
 * 作为一个<b>键</b>，它的格式应该是字母与点的连接，例如 {@code user.name}。
 *
 * 正常情况下是不允许存在 {@link ConfigurationProperty} 为 {@code null} 的配置信息的。
 *
 * 如果想要使某个配置项为null，可以尝试使用 {@link NullConfigurationProperty}。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface Configuration {

    /**
     * 获取一项配置信息。
     *
     * @param key 这项配置的键。
     * @return 得到的信息
     */
    ConfigurationProperty getConfig(String key);

    /**
     * 添加一项配置信息。
     *
     * 如果想要使某个配置项为null，可以尝试使用 {@link NullConfigurationProperty}。
     *
     * @param key 键
     * @param config 配置信息
     *
     * @return 如果有旧的配置被覆盖了，则此为旧配置信息。
     */
    ConfigurationProperty setConfig(String key, ConfigurationProperty config);


    /**
     * 如果key对应的配置不存在，则存入一个配置信息。
     * 默认情况下不允许出现null值。
     *
     * @param key 键
     * @param value 要存入的配置信息
     * @param mergeFunction merge function
     * @return 存入的新值。
     */
    default ConfigurationProperty mergeConfig(String key, ConfigurationProperty value,
                                              BiFunction<? super ConfigurationProperty, ? super ConfigurationProperty, ? extends ConfigurationProperty> mergeFunction) {
        Objects.requireNonNull(key, "key(arg0) cannot be null.");
        Objects.requireNonNull(mergeFunction, "mergeFunction(arg2) cannot be null.");
        Objects.requireNonNull(value, "mergeFunction(arg1) cannot be null.");

        ConfigurationProperty oldValue = getConfig(key);
        ConfigurationProperty newValue = (oldValue == null) ? value :
                mergeFunction.apply(oldValue, value);
        if(newValue == null) {
            throw new NullPointerException("new config property connot be null.");
        } else {
            setConfig(key, newValue);
        }
        return newValue;
    }


    /**
     * 判断是否存在某个键。
     *
     * @param key 配置键。
     * @return 是否存在。
     */
    boolean containsKey(String key);


    /**
     * 获取配置的数量。
     * @return 数量
     */
    int size();


    /**
     * 判断是否存在配置内容。
     *
     * @return 是否存在配置内容
     */
    default boolean isEmpty() {
        return size() == 0;
    }


    /**
     * 获取所有的配置信息。
     * @return config list.
     */
    Collection<ConfigurationProperty> getConfigProperties();


    /**
     * 根据筛选条件获取所有的配置信息。
     *
     * @param testPredicate 筛选条件。
     * @return config list.
     */
    Collection<ConfigurationProperty> getConfigProperties(Predicate<ConfigurationProperty> testPredicate);


    /**
     * 遍历所有的配置信息。
     * @param forEachConsumer consumer.
     */
    void forEach(BiConsumer<? super String, ? super ConfigurationProperty> forEachConsumer);



}
