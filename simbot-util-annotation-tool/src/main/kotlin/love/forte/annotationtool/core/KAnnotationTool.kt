/*
 * Copyright (c) 2021-2023 ForteScarlet.
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

import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * KAnnotation tool interface.
 *
 *
 * The KAnnotation tool interface provides some abstract methods to define some operations for annotations,
 * such as [getting an annotation instance][.getAnnotation],
 * [getting the properties of an annotation][.getProperties]
 * or [directly constructing an annotation instance][createAnnotationInstance], etc.
 *
 *
 * You can implement this interface any way you like,
 * but of course, the library provides a default implementation, as well as an implementation of the instance provided by [KAnnotationTool]: [SimpleKAnnotationTool].
 *
 *
 * @author ForteScarlet
 * @see SimpleKAnnotationTool
 * @see KAnnotationTool
 */
public interface KAnnotationTool {
    /**
     * Get annotation instance from [KAnnotatedElement]. e.g. from [Function] or [KClass].
     *
     * @param fromElement    annotation fromElement instance.
     * @param annotationType annotation type.
     * @param excludes       excludes annotation class name. They will not be parsing.
     * @return The annotation instance, or null.
     */
    public fun <A : Annotation> getAnnotation(
        fromElement: KAnnotatedElement,
        annotationType: KClass<A>,
        excludes: Set<String>
    ): A?

    /**
     * Get annotation instance fromElement [KAnnotatedElement]. e.g. fromElement [Function] or [KClass].
     *
     */
    public fun <A : Annotation> getAnnotation(fromElement: KAnnotatedElement, annotationType: KClass<A>): A? {
        return getAnnotation(fromElement, annotationType, emptySet())
    }

    /**
     * Get annotation instance list from [KAnnotatedElement].
     *
     * @param element        annotation element instance. e.g. from [Function] or [KClass].
     * @param annotationType annotation type.
     * @param excludes       excludes annotation class name. will not be checked.
     * @return The annotation instance, or empty.
     */
    public fun <A : Annotation> getAnnotations(
        element: KAnnotatedElement,
        annotationType: KClass<A>,
        excludes: Set<String>
    ): List<A>

    /**
     * Get a repeatable annotation instance from [KAnnotatedElement]. e.g. from [Function] or [KClass].
     *
     * @param element        annotation element instance.
     * @param annotationType annotation type.
     * @return The annotation instance, or empty.
     */
    public fun <A : Annotation> getAnnotations(
        element: KAnnotatedElement,
        annotationType: KClass<A>
    ): List<A> {
        return getAnnotations(element, annotationType, emptySet())
    }

    /**
     * Get annotation values.
     *
     * @param annotation An annotation instance.
     * @return annotation property values.
     */
    public fun <A : Annotation> getAnnotationValues(annotation: A): Map<String, Any>

    /**
     * Get annotation property names.
     *
     * @param annotation An annotation instance.
     * @return property name set. Treat it as **immutable** plz.
     */
    public fun getPropertyNames(annotation: Annotation): Set<String>


    @Deprecated("Use 'getPropertyNames'", ReplaceWith("getPropertyNames(annotation)"))
    public fun getProperties(annotation: Annotation): Set<String> = getPropertyNames(annotation)

    /**
     * Get annotation type's value types.
     *
     * @param annotationType An annotation instance type.
     * @return name-type map. Treat it as **immutable** plz.
     */
    public fun getAnnotationPropertyTypes(annotationType: KClass<out Annotation>): Map<String, KType>

    /**
     * Create an annotation instance.
     *
     * @param annotationType annotation type.
     * @param properties     annotation's properties.
     * @param base           base annotation.
     * @return annotation proxy instance.
     */
    public fun <A : Annotation> createAnnotationInstance(
        annotationType: KClass<A>,
        properties: Map<String, Any>,
        base: A?
    ): A

    /**
     * Create an annotation proxy instance.
     *
     * @param annotationType annotation type.
     * @param properties     annotation's properties.
     * @return annotation proxy instance.
     */
    public fun <A : Annotation> createAnnotationInstance(
        annotationType: KClass<A>,
        properties: Map<String, Any>
    ): A {
        return createAnnotationInstance(annotationType, properties, null)
    }

    // /**
    //  * Create an annotation proxy instance.
    //  *
    //  * @param annotationType annotation type.
    //  * @param properties     annotation's properties.
    //  * @return annotation proxy instance.
    //  * @see .createAnnotationInstance
    //  */
    // public fun <A : Annotation> createAnnotationInstance(annotationType: KClass<A>, properties: Map<String, Any>): A {
    //     return createAnnotationInstance(annotationType, properties)
    // }

    /**
     * Create an annotation proxy instance.
     *
     * @param annotationType annotation type.
     * @return annotation proxy instance.
     * @see .createAnnotationInstance
     */
    public fun <A : Annotation> createAnnotationInstance(annotationType: KClass<A>): A {
        return createAnnotationInstance(annotationType, emptyMap())
    }

    /**
     * Clean internal annotation instance cache. (if exists.)
     */
    public fun clearCache()
}
