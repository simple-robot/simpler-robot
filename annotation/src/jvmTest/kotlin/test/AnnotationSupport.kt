package test

import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotations

public actual typealias AnnotationSupport = KAnnotatedElement

@OptIn(ExperimentalStdlibApi::class)
public actual fun <A : Annotation> AnnotationSupport.getAnnotation(type: KClass<A>): A? {
    return getAnnotationsByType(type).takeIf { it.size == 1 }?.first()
}

@OptIn(ExperimentalStdlibApi::class)
public actual fun <A : Annotation> AnnotationSupport.getAnnotationsByType(type: KClass<A>): List<A> {
    return findAnnotations(type)
}



/**
 * 将一个 [KClass] 转化为一个 [AnnotationSupport].
 */
public actual fun KClass<*>.toAnnotationSupport(): AnnotationSupport = this
