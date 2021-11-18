package test

import kotlin.reflect.KClass


/**
 * 将一个 [KClass] 转化为一个 [AnnotationSupport].
 */
public expect fun KClass<*>.toAnnotationSupport(): AnnotationSupport


/**
 * 一个可以获取到注解元素的接口。
 *
 * @author ForteScarlet
 */
public expect interface AnnotationSupport {
    public val annotations: List<Annotation>
}


public expect fun <A : Annotation> AnnotationSupport.getAnnotation(type: KClass<A>): A?
public expect fun <A : Annotation> AnnotationSupport.getAnnotationsByType(type: KClass<A>): List<A>


internal object InvalidAnnotatedElement : AnnotationSupport {
    override val annotations: List<Annotation> get() = emptyList()
}