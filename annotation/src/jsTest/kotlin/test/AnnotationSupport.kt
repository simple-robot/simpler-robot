package test

import kotlin.reflect.KClass

/**
 *
 * @author ForteScarlet
 */
public actual interface AnnotationSupport {
    public actual val annotations: List<Annotation>
}

/**
 * 将一个 [KClass] 转化为一个 [AnnotationSupport].
 */
public actual fun KClass<*>.toAnnotationSupport(): AnnotationSupport =
    InvalidAnnotatedElement


actual fun <A : Annotation> AnnotationSupport.getAnnotation(type: KClass<A>): A? = null
actual fun <A : Annotation> AnnotationSupport.getAnnotationsByType(type: KClass<A>): List<A> = emptyList()