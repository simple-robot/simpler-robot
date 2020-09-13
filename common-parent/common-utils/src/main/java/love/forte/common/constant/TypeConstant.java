/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     TypeConstant.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.constant;

import love.forte.common.impl.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 提供一些常用的 Type 常量，例如 {@code List<Integer>} 等，可以快捷用在需要类型转化的地方。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@SuppressWarnings("unused")
public final class TypeConstant {

    /** {@code List<Integer>} 类型 */
    public static final Type LIST_INT = new ParameterizedTypeImpl(List.class, new Type[]{Integer.class});
    /** {@code List<Long>} 类型 */
    public static final Type LIST_LONG = new ParameterizedTypeImpl(List.class, new Type[]{Long.class});
    /** {@code List<String>} 类型 */
    public static final Type LIST_STR = new ParameterizedTypeImpl(List.class, new Type[]{String.class});
    /** {@code List<Boolean>} 类型 */
    public static final Type LIST_BOOL = new ParameterizedTypeImpl(List.class, new Type[]{Boolean.class});
    /** {@code List<Char>} 类型 */
    public static final Type LIST_CHAR = new ParameterizedTypeImpl(List.class, new Type[]{Character.class});

    /** {@code String[]} 类型 */
    public static final Type ARRAY_STR = String[].class;

    /** {@code int[]} 类型 */
    public static final Type ARRAY_INT = int[].class;

    /** {@code long[]} 类型 */
    public static final Type ARRAY_LONG = long[].class;

    /** {@code boolean[]} 类型 */
    public static final Type ARRAY_BOOL = boolean[].class;

    /** {@code char[]} 类型 */
    public static final Type ARRAY_CHAR = char[].class;

    /** {@code Integer[]} 类型 */
    public static final Type ARRAY_INT_BOX = Integer[].class;

    /** {@code Long[]} 类型 */
    public static final Type ARRAY_LONG_BOX = Long[].class;

    /** {@code Boolean[]} 类型 */
    public static final Type ARRAY_BOOL_BOX = Boolean[].class;

    /** {@code Character[]} 类型 */
    public static final Type ARRAY_CHAR_BOX = Character[].class;

}
