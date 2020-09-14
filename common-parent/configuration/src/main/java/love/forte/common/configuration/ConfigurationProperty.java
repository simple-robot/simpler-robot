/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Conf.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.configuration;

import java.lang.reflect.Type;

/**
 *
 * 代表一项配置信息。
 *
 * 配置信息总是以键值对的形式存在的，
 * 因此一项配置总是可以获取一个 {@code key} ({@link #getKey()})
 * 和某种类型的 {@code value}。
 *
 * 在实现的时候记得不要忘记实现 {@link #equals(Object)}、{@link #hashCode()}、{@link #hashCode()}
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface ConfigurationProperty {

    /**
     * 获取这个配置的 键。
     *
     * @return config key
     */
    String getKey();

    /**
     * 获取这个配置项的值。不会进行类型转化。
     *
     * @return 配置信息
     */
    Object getObject();

    /**
     * 获取这个配置项的值，并转化为指定的类型。
     *
     * 转化可参考 {@link love.forte.common.utils.convert.ConverterManager}
     *
     * @param type 转化类型
     * @return 配置信息
     */
    <T> T getObject(Class<T> type);

    /**
     * 获取这个配置项的值，并转化为指定的类型。
     *
     * 转化可参考 {@link love.forte.common.utils.convert.ConverterManager}
     *
     * @param type 类型。
     * @return
     */
    <T> T getObject(Type type);

    /**
     * 获取字符串格式的配置信息。
     *
     * @return 配置信息
     */
    String getString();


    /**
     * 得到int类型的基础数据类型的配置信息。
     *
     * @return 配置信息
     */
    int getInt();


    /**
     * 得到long类型的基础数据类型的配置信息。
     *
     * @return 配置信息
     */
    long getLong();


    /**
     * 得到boolean类型的基础数据类型的配置信息。
     *
     * @return 配置信息
     */
    boolean getBoolean();


}
