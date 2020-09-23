/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     BeanDependRegistrar.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.ioc

import love.forte.common.utils.annotation.AnnotationUtil
import java.lang.reflect.AnnotatedElement


/**
 * 可以动态注入bean的接口。实现了此接口的bean最终不应被注入到依赖中心。
 */
public interface BeanDependRegistrar {
    fun registerBeanDepend(annotationHelper: AnnotationHelper, beanDependRegistry: BeanDependRegistry)
}


/**
 * [BeanDepend] 注册器
 */
public interface BeanDependRegistry {

    /**
     * 注册一个beanDepend. 会将其直接注册
     */
    fun register(beanDepend: BeanDepend<*>)

    /**
     * 注册一个type. 解析注解后注册
     */
    fun register(type: Class<*>)

}


/**
 * 注解获取助手
 */
public object AnnotationHelper {

    /**
     * 判断是否存在某个注解
     */
    fun containsAnnotation(from: AnnotatedElement, annotationType: Class<out Annotation>): Boolean {
        return AnnotationUtil.getAnnotation(from, annotationType) != null
    }

    /**
     * 获取注解
     */
    fun <T : Annotation> getAnnotation(from: AnnotatedElement, annotationType: Class<out T>): T? {
        return AnnotationUtil.getAnnotation(from, annotationType)
    }

    /**
     * 获取注解，如果获取不到，则通过此注解的默认值构建一个默认值代理。
     */
    fun <T : Annotation> getAnnotationOrDefault(from: AnnotatedElement, annotationType: Class<out T>, params: Map<String, Any?>): T {
        return getAnnotation(from, annotationType) ?: AnnotationUtil.getDefaultAnnotationProxy(annotationType, params)
    }

    /**
     * 获取注解，如果获取不到，则通过此注解的默认值构建一个默认值代理。
     */
    fun <T : Annotation> getAnnotationOrDefault(from: AnnotatedElement, annotationType: Class<out T>): T {
        return getAnnotation(from, annotationType) ?: AnnotationUtil.getDefaultAnnotationProxy(annotationType)
    }


}
