package love.forte.common.utils.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
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
                                          Map<String, Object> params) {

        return (T) Proxy.newProxyInstance(classLoader,
                new Class[]{annotationType},
                new AnnotationInvocationHandler(annotationType, params));
    }


    public static <T extends Annotation> T proxy(Class<T> annotationType, Map<String, Object> params) {
        return proxy(annotationType, annotationType.getClassLoader(), params);
    }





}
