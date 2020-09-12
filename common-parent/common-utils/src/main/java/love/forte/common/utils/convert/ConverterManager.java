/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ConverterManager.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.utils.convert;

import java.lang.reflect.Type;

/**
 *
 * 类型转化管理器。
 *
 * 类中主要的目标转化参数为 {@link Type} 接口。
 *
 * 如果想要实现一些跟泛型有关系的转化，例如（String 转化为 List<Int>）则可以考虑使用java的 {@link java.lang.reflect.ParameterizedType} 接口。
 *
 * 其中，hutool工具类提供了一个默认的实现类 {@link cn.hutool.core.lang.ParameterizedTypeImpl}。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface ConverterManager {

    /**
     * 进行转化。将一个 value 值转化为指定的类型。如果无法转化则获取一个默认值。
     * @param target 目标类型
     * @param value 待转化值
     * @param defValue 默认值。或许也会作为判断类型的依据。
     *
     * @return 转化后的值或者默认值。
     */
    <T> T convert(Type target, Object value, T defValue);


    /**
     * 进行转化。将一个 value 值转化为指定的类型。如果无法转化则获取一个默认值。
     * @param target 目标类型
     * @param value 待转化值
     *
     * @return 转化后的值或者默认值。
     */
    default <T> T convert(Type target, Object value) {
        return convert(target, value, null);
    }

    /**
     * 进行转化。将一个 value 值转化为指定的类型。如果无法转化则获取一个默认值。
     * @param targetClass 目标类型
     * @param value 待转化值
     * @param defValue 默认值。或许也会作为判断类型的依据。
     *
     * @return 转化后的值或者默认值。
     */
    default <T> T convert(Class<T> targetClass, Object value, T defValue) {
        return convert((Type)targetClass, value, defValue);
    }


    /**
     * 进行转化。将一个 value 值转化为指定的类型。如果无法转化则获取一个默认值。
     * @param targetClass 目标类型
     * @param value 待转化值
     *
     * @return 转化后的值或者默认值。
     */
    default <T> T convert(Class<T> targetClass, Object value) {
        return convert(targetClass, value, null);
    }



    /**
     * 获取某目标的转化器。
     *
     * @param target 目标结果
     * @return 转化器
     */
    <T> Converter<T> getConverterByTarget(Type target);
}
