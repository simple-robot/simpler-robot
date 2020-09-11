/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     AnnotationValueUtil.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.utils.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 操作注解值得工具类
 *
 * 需要注意的是，注解的实例仅存在一个，因此当你修改了注解的值，则全局生效。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class AnnotationValueUtil {

    private static final Map<Class<? extends Annotation>, Field> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取注解对应得value map。只有正常的注解才能够获取到。
     *
     * @param annotation 注解
     * @return value map
     * @see sun.reflect.annotation.AnnotationInvocationHandler#memberValues
     */
    @SuppressWarnings("JavadocReference")
    private static <T extends Annotation> Map<String, Object> getValueMap(T annotation) {
        InvocationHandler ih = Proxy.getInvocationHandler(annotation);
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        // field
        Field memberValuesField = FIELD_CACHE.computeIfAbsent(annotationType, k -> {
            try {
                return ih.getClass().getDeclaredField("memberValues");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });
        memberValuesField.setAccessible(true);

        Map<String, Object> memberValues;
        try {
            memberValues = (Map<String, Object>) memberValuesField.get(ih);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return memberValues;
    }

    /**
     * 修改Annotation的值
     *
     * @param annotation       注解
     * @param valueMapConsumer 注解的值
     */
    public static <T extends Annotation> void setValue(T annotation, Consumer<Map<String, Object>> valueMapConsumer) {
        valueMapConsumer.accept(getValueMap(annotation));
    }

    /**
     * 修改Annotation的值
     *
     * @param annotation 注解
     * @param key        注解的key
     * @param value      要修改的值
     */
    public static <T extends Annotation> void setValue(T annotation, String key, Object value) {
        getValueMap(annotation).put(key, value);
    }


}

