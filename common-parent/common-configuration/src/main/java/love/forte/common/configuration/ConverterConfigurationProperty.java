/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     BaseConfigurationProperty.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration;

import love.forte.common.utils.convert.ConverterManager;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 *
 * {@link ConfigurationProperty} 的转化器实现类，提供基础功能的实现。
 *
 * 对于类型的转化基于 {@link ConverterManager} 实现，因此需要提供一个 {@link ConverterManager} 实例。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class ConverterConfigurationProperty implements ConfigurationProperty {

    private final String key;
    private final Object value;
    private final ConverterManager converterManager;

    /**
     * 构造
     * @param key key
     * @param value value
     * @param converterManager 转化器
     */
    public ConverterConfigurationProperty(String key, Object value, ConverterManager converterManager){
        this.key = key;
        this.value = value;
        this.converterManager = converterManager;
    }


    @Override
    public String toString() {
        return key + "=" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConverterConfigurationProperty that = (ConverterConfigurationProperty) o;
        return key.equals(that.key) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    /**
     * 获取这个配置的 键。
     *
     * @return config key
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * 获取这个配置项的值。不会进行类型转化。
     *
     * @return 配置信息
     */
    @Override
    public Object getObject() {
        return value;
    }

    /**
     * 获取这个配置项的值，并转化为指定的类型。
     * <p>
     * 转化可参考 {@link ConverterManager}
     *
     * @param type 转化类型
     * @return 配置信息
     */
    @Override
    public <T> T getObject(Class<T> type) {
        return converterManager.convert(type, value);
    }

    /**
     * 获取这个配置项的值，并转化为指定的类型。
     * <p>
     * 转化可参考 {@link ConverterManager}
     *
     * @param type 类型。
     * @return
     */
    @Override
    public <T> T getObject(Type type) {
        return converterManager.convert(type, value);
    }

    /**
     * 获取字符串格式的配置信息。
     *
     * @return 配置信息
     */
    @Override
    public String getString() {
        return converterManager.convert(String.class, value);
    }

    /**
     * 得到int类型的基础数据类型的配置信息。
     *
     * @return 配置信息
     */
    @Override
    public int getInt() {
        return converterManager.convert(int.class, value);
    }

    /**
     * 得到long类型的基础数据类型的配置信息。
     *
     * @return 配置信息
     */
    @Override
    public long getLong() {
        return converterManager.convert(long.class, value);
    }

    /**
     * 得到boolean类型的基础数据类型的配置信息。
     *
     * @return 配置信息
     */
    @Override
    public boolean getBoolean() {
        return converterManager.convert(boolean.class, value);
    }
}
