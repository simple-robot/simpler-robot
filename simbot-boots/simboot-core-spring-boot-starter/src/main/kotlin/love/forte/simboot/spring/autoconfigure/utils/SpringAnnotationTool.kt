/*
 * Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.simboot.spring.autoconfigure.utils

import love.forte.annotationtool.core.KAnnotationTool
import love.forte.simboot.annotation.*
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.annotation.AnnotationUtils
import java.lang.reflect.AnnotatedElement
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaConstructor
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.jvm.javaMethod

/**
 * 通过 [AnnotationUtils] 实现 [KAnnotationTool] 的功能。
 *
 * 其中一部分功能仍然需要使用到原本的 [KAnnotationTool] 实例, 比如 在当提供的 [KAnnotatedElement] 不支持转化为 [AnnotatedElement] 的情况下，
 * 或者使用时出现了异常的情况下。
 *
 *
 */
public class SpringAnnotationTool(private val tool: KAnnotationTool = KAnnotationTool()) : KAnnotationTool {
    override fun clearCache() {
        tool.clearCache()
        AnnotationUtils.clearCache()
    }

    override fun <A : Annotation> createAnnotationInstance(
        annotationType: KClass<A>,
        properties: Map<String, Any>,
        base: A?,
    ): A {
        try {
            return AnnotationUtils.synthesizeAnnotation(properties, annotationType.java, null)
        } catch (ignore: Throwable) {
        }
        return tool.createAnnotationInstance(annotationType, properties, base)
    }

    override fun <A : Annotation> getAnnotation(
        fromElement: KAnnotatedElement,
        annotationType: KClass<A>,
        excludes: Set<String>,
    ): A? {
        try {
            val jae = fromElement.javaAnnotatedElement
            val javaType = annotationType.java
            if (jae != null) {
                return AnnotationUtils.findAnnotation(jae, javaType)
            }
        } catch (ignore: Throwable) {
        }

        return tool.getAnnotation(fromElement, annotationType, excludes)
    }


    override fun getAnnotationPropertyTypes(annotationType: KClass<out Annotation>): Map<String, KType> {
        return tool.getAnnotationPropertyTypes(annotationType)
    }

    override fun <A : Annotation> getAnnotationValues(annotation: A): Map<String, Any> {
        return AnnotationUtils.getAnnotationAttributes(annotation)
    }

    override fun <A : Annotation> getAnnotations(
        element: KAnnotatedElement,
        annotationType: KClass<A>,
        excludes: Set<String>,
    ): List<A> {
        try {
            val annotatedElement = element.javaAnnotatedElement
            if (annotatedElement != null) {
                val containerType = annotationType.findContainerType()
                val repeatedResult = AnnotatedElementUtils.findMergedRepeatableAnnotations(
                    annotatedElement,
                    annotationType.java,
                    containerType
                )
//                val directedResult = AnnotatedElementUtils.findAllMergedAnnotations(annotatedElement, annotationType.java)
                return repeatedResult.toList()
            }
        } catch (ignore: Throwable) {
        }
        return tool.getAnnotations(element, annotationType, excludes)
    }

    override fun getPropertyNames(annotation: Annotation): Set<String> {
        return AnnotationUtils.getAnnotationAttributes(annotation).keys
    }
}

private fun KClass<out Annotation>.findContainerType(): Class<out Annotation>? {
    return kotlin.runCatching {
        when (this) {
            Filter::class -> return Filters::class.java
            Interceptor::class -> Interceptors::class.java
            Listen::class -> Listens::class.java
            Preparer::class -> Preparers::class.java
            // known
            Filters::class, Listens::class, Listener::class,
            Binder::class, SpecifyBinder::class, CurrentBinder::class, GlobalBinder::class,
            FilterValue::class, Interceptors::class, Preparers::class,
            -> return null
        }

        // find repeatable annotation
        return this.findAnnotation<JvmRepeatable>()?.value?.java
    }.getOrNull()
}

private val KAnnotatedElement.javaAnnotatedElement: AnnotatedElement?
    get() {
        return when (this) {
            is KCallable<*> -> when (this) {
                is KFunction<*> -> javaMethod ?: javaConstructor
                is KProperty<*> -> javaField ?: javaGetter
                else -> null
            }

            is KClass<*> -> java
            is KType -> when (val classifier = classifier) {
                is KClass<*> -> classifier.javaAnnotatedElement
                else -> null
            }
            // not support for KParameter
            is KParameter -> null
            else -> null
        }
    }
