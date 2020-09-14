/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ParameterizedTypeImpl.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.impl;

import java.io.Serializable;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * 针对于 {@link ParameterizedType} 的基础实现。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class ParameterizedTypeImpl implements ParameterizedType, Serializable {
    private static final long serialVersionUID = -3174271240031257246L;

    /**
     * 实际类型，例如泛型类型
     */
    private final Type[] actualTypeArguments;

    private final Class<?> rawType;

    private final Type ownerType;

    /**
     * 构造
     *
     * @param ownerType 拥有者类型, 可以是null
     * @param rawType 原始类型的class
     * @param actualTypeArguments 实际的泛型参数类型
     */
    public ParameterizedTypeImpl(Type ownerType, Class<?> rawType, Type[] actualTypeArguments){
        this.rawType = rawType;
        this.actualTypeArguments = actualTypeArguments;
        this.ownerType = ownerType != null ? ownerType : rawType.getDeclaringClass();
    }

    /**
     * 构造
     *
     * @param rawType 原始类型的class
     * @param actualTypeArguments 实际的泛型参数类型
     */
    public ParameterizedTypeImpl(Class<?> rawType, Type[] actualTypeArguments){
        this(null, rawType, actualTypeArguments);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        final Type useOwner = this.ownerType;
        final Class<?> raw = this.rawType;
        if (useOwner == null) {
            sb.append(raw.getName());
        } else {
            if (useOwner instanceof Class<?>) {
                sb.append(((Class<?>) useOwner).getName());
            } else {
                sb.append(useOwner.toString());
            }
            sb.append('$').append(raw.getSimpleName());
        }

        if(actualTypeArguments != null && actualTypeArguments.length > 0){
            sb.append('<');
            final int length = this.actualTypeArguments.length;
            for (int i = 0; i < length; i++) {
                Type actualType = actualTypeArguments[i];
                if(i > 0){
                    sb.append(", ");
                }
                if(actualType instanceof Class){
                    sb.append(((Class<?>) actualType).getName());
                } else {
                    sb.append(actualType);
                }
            }
            sb.append('>');
        }

        return sb.toString();
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParameterizedTypeImpl that = (ParameterizedTypeImpl) o;
        return Arrays.equals(actualTypeArguments, that.actualTypeArguments) &&
                Objects.equals(rawType, that.rawType) &&
                Objects.equals(ownerType, that.ownerType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(rawType, ownerType);
        result = 31 * result + Arrays.hashCode(actualTypeArguments);
        return result;
    }
}
