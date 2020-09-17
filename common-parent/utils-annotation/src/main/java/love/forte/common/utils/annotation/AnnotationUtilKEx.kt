/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     AnnotationUtilKEx.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.utils.annotation

import java.lang.reflect.AnnotatedElement
import kotlin.reflect.*
import kotlin.reflect.jvm.javaConstructor
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.jvm.javaMethod


/**
 * 针对kotlin下的兼容
 */
public fun <T: Annotation> getAnnotation(callAble: KCallable<*>, type: KClass<T>) : T? {
    val annotatedElement: AnnotatedElement = when(callAble) {
        is KFunction -> callAble.javaMethod ?: callAble.javaConstructor ?: return null
        is KProperty -> callAble.javaField ?: callAble.javaGetter ?: return null
        else -> return null
    }
    return AnnotationUtil.getAnnotation(annotatedElement, type.java)
}

/**
 * 针对kotlin下的兼容
 */
public fun <T: Annotation> getAnnotation(kClass: KClass<*>, type: KClass<T>) : T? {
    return AnnotationUtil.getAnnotation(K2JAnnotatedElement(kClass), type.java)
}



private data class K2JAnnotatedElement(private val kAnnotated: KAnnotatedElement) : AnnotatedElement {
    /**
     * Returns this element's annotation for the specified type if
     * such an annotation is *present*, else null.
     *
     * @param <T> the type of the annotation to query for and return if present
     * @param annotationClass the Class object corresponding to the
     * annotation type
     * @return this element's annotation for the specified annotation type if
     * present on this element, else null
     * @throws NullPointerException if the given annotation class is null
     * @since 1.5
    </T> */
    override fun <T : Annotation> getAnnotation(annotationClass: Class<T>): T? {
        return kAnnotated.annotations.find {
            it::annotationClass.get().java == annotationClass
        } as? T
    }

    /**
     * Returns annotations that are *present* on this element.
     *
     * If there are no annotations *present* on this element, the return
     * value is an array of length 0.
     *
     * The caller of this method is free to modify the returned array; it will
     * have no effect on the arrays returned to other callers.
     *
     * @return annotations present on this element
     * @since 1.5
     */
    override fun getAnnotations(): Array<Annotation> {
        return kAnnotated.annotations.toTypedArray()
    }

    /**
     * Returns annotations that are *directly present* on this element.
     * This method ignores inherited annotations.
     *
     * If there are no annotations *directly present* on this element,
     * the return value is an array of length 0.
     *
     * The caller of this method is free to modify the returned array; it will
     * have no effect on the arrays returned to other callers.
     *
     * @return annotations directly present on this element
     * @since 1.5
     */
    override fun getDeclaredAnnotations(): Array<Annotation> = annotations

}
