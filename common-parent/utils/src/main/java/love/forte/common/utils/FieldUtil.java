/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     FieldUtil.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * 获取字段的工具类
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class FieldUtil {

    /**
     * 获取某个类的所有字段
     * @param type 类
     * @param predicate 过滤器
     * @param withSuper 是否获取父类的字段
     * @return 字段列表
     */
    public static List<Field> getDeclaredFields(Class<?> type, Predicate<Field> predicate, boolean withSuper) {
        List<Field> list = new ArrayList<>();
        if (!withSuper) {
            final Field[] declaredFields = type.getDeclaredFields();
            for (Field field : declaredFields) {
                if (predicate.test(field)) {
                    list.add(field);
                }
            }
            return list;
        } else {
            return getDeclaredFields(type, list, predicate);
        }
    }

    /**
     * 获取某个类的所有字段
     * @param type 类
     * @param withSuper 是否获取父类的字段
     * @return 字段列表
     */
    public static List<Field> getDeclaredFields(Class<?> type, boolean withSuper) {
        List<Field> list = new ArrayList<>();
        if (!withSuper) {
            final Field[] declaredFields = type.getDeclaredFields();
            list.addAll(Arrays.asList(declaredFields));
            return list;
        } else {
            return getDeclaredFields(type, list, f -> true);
        }
    }


    private static List<Field> getDeclaredFields(Class<?> type, List<Field> fieldList, Predicate<Field> predicate) {
        final Field[] declaredFields = type.getDeclaredFields();
        for (Field field : declaredFields) {
            if (predicate.test(field)) {
                fieldList.add(field);
            }
        }
        final Class<?> superClass = type.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            return getDeclaredFields(superClass, fieldList, predicate);
        } else {
            return fieldList;
        }
    }


}
