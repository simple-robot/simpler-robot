/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
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
                is KClass<*> -> classifier.java
                else -> null
            }
            // not support for KParameter
            is KParameter -> null
            else -> null
        }
    }

