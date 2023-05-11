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

import java.lang.annotation.RetentionPolicy
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Deprecated("")
public annotation class a

/**
 * Some Annotation metadata.
 *
 * @author ForteScarlet
 */
public interface KAnnotationMetadata<A : Annotation> {
    /**
     * Get annotation type, Just like `Annotation::class`.
     *
     * @return annotation type
     */
    public val annotationType: KClass<A>

    /**
     * Get [RetentionPolicy] of this annotation.
     *
     * @return [AnnotationRetention]
     * @see kotlin.annotation.Retention
     */
    public val retention: AnnotationRetention

    /**
     * Get Set<[AnnotationTarget]> of this annotation.
     *
     * @return Unmodifiable element types.
     * @see kotlin.annotation.Target
     */
    public val targets: Set<AnnotationTarget>

    /**
     * Detect whether there is a target type.
     *
     * @param type type
     * @return true if contains.
     */
    public operator fun contains(type: AnnotationTarget): Boolean


    /**
     * @see contains[AnnotationTarget]
     */
    public fun containsTarget(type: AnnotationTarget): Boolean = type in this

    /**
     * Check if [MustBeDocumented] is marked.
     *
     * @return True if marked [MustBeDocumented]
     */
    public val isMustBeDocumented: Boolean


    /**
     * Check if [Deprecated] is marked.
     *
     * @return True if marked [Deprecated]
     */
    public val isDeprecated: Boolean

    /**
     * The [Deprecated.message] if [isDeprecated] == true.
     */
    public val deprecatedMessage: String?

    /**
     * The [Deprecated.replaceWith].[expression][ReplaceWith.expression] if [isDeprecated] == true or null
     */
    public val deprecatedReplaceWithExpression: String?

    /**
     * The [Deprecated.replaceWith].[imports][ReplaceWith.imports] if [isDeprecated] == true or null.
     */
    public val deprecatedReplaceWithImports: Set<String>?

    /**
     * The [Deprecated.level] if [isDeprecated] == true or empty.
     */
    public val deprecatedLevel: DeprecationLevel?

    /**
     * Is a repeatable annotation.
     *
     *
     * return true when If this annotation contains [Repeatable].
     *
     *
     * If the current annotation has a <tt>value</tt> property of the [annotation array][Annotation] type,
     * and there is a <tt>@Repeatable</tt> annotation on this annotation type,
     * and the value of its value is equal to the current annotation
     *
     * @return repeatable if true.
    </pre> */
    public val isRepeatable: Boolean

    /**
     * Get current annotation type 's all property name.
     *
     * @return property names.
     */
    public val propertyNames: Set<String>

    /**
     * Determine whether a certain property exists.
     *
     * @param name property name.
     * @return exists if true
     */
    public operator fun contains(name: String): Boolean

    /**
     * @see contains[String]
     */
    public fun containsProperty(name: String): Boolean = name in this

    /**
     * Get current annotation type 's all properties with their type.
     *
     * @return properties with their type
     */
    public val propertyTypes: Map<String, KType>


    /**
     * Get the type of the property of the specified property name.
     *
     * @param property property name
     * @return property 's type, or null.
     */
    public fun getPropertyType(property: String): KType?

    /**
     * Get all properties with their default value (if it exists).
     *
     * @return properties with their default value.
     */
    public val propertyDefaultValues: Map<String, Any>

    /**
     * Get default value of property of the specified property name.
     *
     * @param property property name
     * @return the default value, or null
     */
    public fun getPropertyDefaultValue(property: String): Any?

    /**
     * Get properties of specified annotation instance.
     *
     * @param annotation annotation instance.
     * @return properties.
     */
    public fun getProperties(annotation: @UnsafeVariance A): Map<String, Any>

    /**
     * Get the property mappings for target type. The key is  [targetType] 's property name, value is current type's property.
     *
     * @param targetType mappings target.
     * @return mappings
     */
    public fun getPropertyNamingMaps(targetType: KClass<out Annotation>): Map<String, String>

    /**
     * Get the property mapping for target type.
     *
     * @param targetType         mapping target.
     * @param targetPropertyName property name of current.
     * @return target name, or null.
     */
    public fun getPropertyNamingMap(targetType: KClass<out Annotation>, targetPropertyName: String): String?

    /**
     * Get property value of specified annotation instance with property name.
     *
     * @param property   property name
     * @param annotation annotation instance
     * @return property value, or null
     */
    public fun getAnnotationValue(property: String, annotation: @UnsafeVariance A): Any?

    public companion object {

        /**
         * Get instance of [KAnnotationMetadata] by an annotation type.
         *
         * @param annotationType annotation type
         * @return instance of [KAnnotationMetadata]
         */
        public fun <A : Annotation> resolve(annotationType: KClass<A>): KAnnotationMetadata<A> {
            return FACTORY.getAnnotationMetadata(annotationType)
        }

        /**
         * An [KAnnotationMetadataFactory] instance.
         *
         *
         * *In the future, it may be adjusted to obtain dynamic instances through Service Loader loading or other methods.*
         */
        private val FACTORY: KAnnotationMetadataFactory = SimpleCacheableKAnnotationMetadataFactory()
    }
}

internal class SimpleCacheableKAnnotationMetadataFactory : KAnnotationMetadataFactory {
    private val cache: WeakHashMap<KClass<out Annotation>, KAnnotationMetadata<*>> =
        WeakHashMap<KClass<out Annotation>, KAnnotationMetadata<*>>()

    private operator fun <A : Annotation> get(annotationType: KClass<A>): KAnnotationMetadata<A>? {
        @Suppress("UNCHECKED_CAST") return cache[annotationType] as? KAnnotationMetadata<A>
    }

    private fun <A : Annotation> put(annotationType: KClass<A>): KAnnotationMetadata<A> {
        val metadata: SimpleKAnnotationMetadata<A> = SimpleKAnnotationMetadata(annotationType)
        cache[annotationType] = metadata
        return metadata
    }

    override fun <A : Annotation> getAnnotationMetadata(annotationType: KClass<A>): KAnnotationMetadata<A> {
        return get(annotationType) ?: synchronized(this) {
            return get(annotationType) ?: return put(annotationType)
        }
    }
}


public fun <A : Annotation> KClass<A>.metadata(): KAnnotationMetadata<A> = KAnnotationMetadata.resolve(this)
public inline fun <reified A : Annotation> metadata(): KAnnotationMetadata<A> = A::class.metadata()
