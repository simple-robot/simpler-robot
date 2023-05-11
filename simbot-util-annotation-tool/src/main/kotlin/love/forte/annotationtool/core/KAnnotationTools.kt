/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.annotationtool.core

import java.util.*
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass


/**
 * Get a default implementation of [KAnnotationTool].
 *
 * @param cacheMap Map of cached results. Use [java.util.WeakHashMap] by default.
 * @param nullCacheMap Map of cached null values. Use [mutableMapOf] by default.
 * @param converters Type converter for property mapping. Use [nonConverters] by default.
 *
 */
public fun KAnnotationTool(
    cacheMap: MutableMap<KAnnotatedElement, MutableMap<KClass<out Annotation>, Annotation>> = WeakHashMap(),
    nullCacheMap: MutableMap<KAnnotatedElement, MutableSet<KClass<out Annotation>>> = mutableMapOf(),
    converters: Converters = nonConverters()
): KAnnotationTool = SimpleKAnnotationTool(cacheMap, nullCacheMap, converters)


public inline fun <reified A : Annotation> KAnnotationTool.getAnnotation(
    element: KAnnotatedElement,
    exclude: Set<String> = emptySet()
): A? {
    return getAnnotation(element, A::class, exclude)
}

public inline fun <reified A : Annotation> KAnnotationTool.getAnnotations(
    element: KAnnotatedElement,
    exclude: Set<String> = emptySet()
): List<A> {
    return getAnnotations(element, A::class, exclude)
}

public inline fun <reified A : Annotation> KAnnotationTool.createAnnotation(
    properties: Map<String, Any> = emptyMap(),
    base: A? = null
): A {
    return createAnnotationInstance(A::class, properties, base)
}





