/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     NullConfigProperty.java
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
 * 针对于 {@link ConfigurationProperty} 的默认实现类，代表一个内容物为 null 的配置。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public final class NullConfigurationProperty implements ConfigurationProperty {

    private final String key;

    public NullConfigurationProperty(String key){
        Objects.requireNonNull(key, "key cannot be null.");
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        //noinspection AliControlFlowStatementWithoutBraces
        if (this == o) return true;
        if(o instanceof NullConfigurationProperty){
            return this.key.equals(((NullConfigurationProperty) o).key);
        }else if(o instanceof ConfigurationProperty){
            return ((ConfigurationProperty) o).getObject() == null && this.key.equals(((ConfigurationProperty) o).getKey());
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return key + "=";
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
        return null;
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
        return null;
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
        return null;
    }

    /**
     * 获取字符串格式的配置信息。
     *
     * @return 配置信息
     */
    @Override
    public String getString() {
        return null;
    }

    /**
     * 得到int类型的基础数据类型的配置信息。
     *
     * @return 配置信息
     */
    @Override
    public int getInt() {
        throw new ClassCastException("cannot cast null to int type.");
    }

    /**
     * 得到long类型的基础数据类型的配置信息。
     *
     * @return 配置信息
     */
    @Override
    public long getLong() {
        throw new ClassCastException("cannot cast null to long type.");
    }

    /**
     * 得到boolean类型的基础数据类型的配置信息。
     *
     * @return 配置信息
     */
    @Override
    public boolean getBoolean() {
        throw new ClassCastException("cannot cast null to boolean type.");
    }
}
