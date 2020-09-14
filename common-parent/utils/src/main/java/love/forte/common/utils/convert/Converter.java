/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Converter.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.utils.convert;

/**
 *
 * 类型转化器接口
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface Converter<T> {
    /**
     * 将一个 任意类型转化为 {@code T}(Target)类型>
     * @param o 被转化的类型
     * @return 转化后的结果
     */
    T convert(Object o);
}
