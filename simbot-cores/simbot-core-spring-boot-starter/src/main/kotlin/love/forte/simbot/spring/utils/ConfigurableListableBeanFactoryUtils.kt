/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.spring.utils

import org.springframework.aop.framework.autoproxy.AutoProxyUtils
import org.springframework.aop.scope.ScopedObject
import org.springframework.aop.scope.ScopedProxyUtils
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.core.MethodIntrospector
import org.springframework.core.annotation.AnnotatedElementUtils
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction


internal fun ConfigurableListableBeanFactory.getTargetTypeSafely(beanName: String): Class<*>? {
    val type = kotlin.runCatching { AutoProxyUtils.determineTargetClass(this, beanName) }.getOrNull() ?: return null

    return if (ScopedObject::class.java.isAssignableFrom(type)) {
        return kotlin.runCatching {
            AutoProxyUtils.determineTargetClass(this, ScopedProxyUtils.getTargetBeanName(beanName))
        }.getOrElse { type }
    } else {
        type
    }
}

internal fun Method.getKotlinFunctionSafely(): KFunction<*>? {
    return kotlin.runCatching { kotlinFunction }.getOrNull()
}

internal inline fun <reified A : Annotation> Class<*>.selectMethodsSafely(): Map<Method, A>? {
    return runCatching {
        MethodIntrospector.selectMethods(this, MethodIntrospector.MetadataLookup { method ->
            AnnotatedElementUtils.findMergedAnnotation(method, A::class.java)
        })
    }.getOrNull()
}

internal inline fun <reified A : Annotation> AnnotatedElement.findMergedAnnotationSafely(): A? {
    return runCatching {
        AnnotatedElementUtils.findMergedAnnotation(this, A::class.java)
    }.getOrNull()
}

internal inline fun <reified A : Annotation> AnnotatedElement.findRepeatableMergedAnnotationSafely(): Set<A>? {
    return runCatching {
        AnnotatedElementUtils.findMergedRepeatableAnnotations(this, A::class.java)
    }.getOrNull()
}
