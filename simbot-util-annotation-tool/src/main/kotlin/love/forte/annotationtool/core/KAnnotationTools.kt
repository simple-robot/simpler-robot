/*
 * Copyright (c) 2023 ForteScarlet <ForteScarlet@163.com>
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





