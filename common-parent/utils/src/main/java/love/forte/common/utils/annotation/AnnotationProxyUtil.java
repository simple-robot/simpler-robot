/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     AnnotationProxyUtil.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.utils.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 注解代理工具，用于动态构建一个注解实例。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class AnnotationProxyUtil {


    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T proxy(Class<T> annotationType,
                                          ClassLoader classLoader,
                                          Annotation baseAnnotation,
                                          Map<String, Object> params) {

        return (T) Proxy.newProxyInstance(classLoader,
                new Class[]{annotationType},
                new AnnotationInvocationHandler(annotationType, params, baseAnnotation));
    }

    public static <T extends Annotation> T proxy(Class<T> annotationType,
                                          ClassLoader classLoader,
                                          Map<String, Object> params) {

        return proxy(annotationType, classLoader, null, params);
    }


    public static <T extends Annotation> T proxy(Class<T> annotationType, Map<String, Object> params) {
        return proxy(annotationType, annotationType.getClassLoader(), params);
    }

    public static <T extends Annotation> T proxy(Class<T> annotationType, Annotation baseAnnotation, Map<String, Object> params) {
        return proxy(annotationType, annotationType.getClassLoader(), baseAnnotation, params);
    }





}
