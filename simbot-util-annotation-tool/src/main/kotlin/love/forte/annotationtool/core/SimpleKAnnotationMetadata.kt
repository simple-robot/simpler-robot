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

import love.forte.annotationtool.AnnotationMapper
import java.util.*
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter

/**
 * Simple implement for [KAnnotationMetadata]
 *
 * @author ForteScarlet
 */
@Suppress("RedundantModalityModifier")
internal class SimpleKAnnotationMetadata<A : Annotation>(override val annotationType: KClass<A>) :
    KAnnotationMetadata<A>, java.io.Serializable {

    /*
        Warn: Property must be initialized, be final, or be abstract. This warning will become an error in future releases.
        因此属性添加 final ，但是加 final 又会提示 final 冗余，所以再加一个 @Suppress
     */

    @Transient
    final override val retention: AnnotationRetention

    @Transient
    final override val targets: Set<AnnotationTarget>

    // repeatable | deprecated | mustDocumented
    @Transient
    private val marks: Byte

    @Transient
    final override val deprecatedMessage: String?

    @Transient
    final override val deprecatedReplaceWithExpression: String?

    @Transient
    final override val deprecatedReplaceWithImports: Set<String>?

    @Transient
    final override val deprecatedLevel: DeprecationLevel?

    @Transient
    final override val propertyDefaultValues: Map<String, Any>

    @Transient
    private val propertiesMap: Map<String, KProperty1<A, Any>>

    @Transient
    final override val propertyTypes: Map<String, KType> // get() = propertiesMap.mapValues { e -> e.value.returnType }

    @Transient
    private val namingMaps: MutableMap<KClass<out Annotation>, MutableMap<String, String>>

    init {
        // repeatable
        val repeatable = annotationType.hasAnnotation<Repeatable>()

        val deprecated = getDeprecated(annotationType)
        deprecatedMessage = deprecated?.message
        deprecatedLevel = deprecated?.level
        with(deprecated?.replaceWith) {
            deprecatedReplaceWithExpression = this?.expression
            deprecatedReplaceWithImports = this?.imports?.toSet()
        }

        marks = getMarks0(annotationType, deprecated != null, repeatable)
        retention = annotationType.retentionPolicy
        targets = annotationType.targets

        @Suppress("UNCHECKED_CAST")
        val properties = annotationType.memberProperties.map { it as KProperty1<A, Any> }
        propertiesMap = properties.associateBy { it.name }
        propertyTypes = propertiesMap.mapValues { e -> e.value.returnType }
        propertyDefaultValues = propertiesMap.mapNotNull { entry ->
            kotlin.runCatching {
                val def: Any? = entry.value.javaGetter?.defaultValue
                if (def != null) entry.key to def else null
            }.getOrNull()
        }.toMap()

        val namingMaps: MutableMap<KClass<out Annotation>, MutableMap<String, String>> = WeakHashMap()
        val mapper: AnnotationMapper? = annotationType.findAnnotation()

        for (property in properties) {
            val defaultMapType: KClass<out Annotation>? = mapper?.value?.takeIf { it.size == 1 }?.first()

            // namingMap
            resolveNamingMaps(property, defaultMapType, namingMaps)
        }

        this.namingMaps = namingMaps
    }


    //endregion


    // repeatable | deprecated | mustDocumented
    override val isDeprecated: Boolean
        get() = marks and DEPRECATED_BYTE != ZERO_BYTE

    override val isMustBeDocumented: Boolean
        get() = marks and MUST_DOCUMENTED_BYTE != ZERO_BYTE

    override val isRepeatable: Boolean
        get() = marks and REPEATABLE_BYTE != ZERO_BYTE

    override val propertyNames: Set<String>
        get() = propertyTypes.keys

    override fun getPropertyType(property: String): KType? = propertyTypes[property]

    override fun getPropertyDefaultValue(property: String): Any? {
        val def = propertyDefaultValues[property] ?: return null
        return if (def is Array<*>) def.copyOf() else def
    }

    override fun getAnnotationValue(property: String, annotation: A): Any? {
        return propertiesMap[property]?.get(annotation)
    }

    override fun getProperties(annotation: A): Map<String, Any> {
        return propertiesMap.mapValues { (_, value) -> value.get(annotation) }
    }

    // contains annotation target
    override fun contains(type: AnnotationTarget): Boolean = type in targets

    // contains property name
    override fun contains(name: String): Boolean = name in propertyNames

    override fun getPropertyNamingMaps(targetType: KClass<out Annotation>): Map<String, String> {
        val namingMap = namingMaps[targetType] ?: namingMaps[Annotation::class] ?: emptyMap()

        val targetMetadata: KAnnotationMetadata<out Annotation> = KAnnotationMetadata.resolve(targetType)
        val targetNames: Set<String> = targetMetadata.propertyNames

        val namingMap0: MutableMap<String, String> = namingMap.toMutableMap()
        for (targetName in targetNames) {
            if (targetName !in namingMap0 && targetName in propertyTypes) {
                namingMap0[targetName] = targetName
            }
        }
        return namingMap0
    }

    override fun getPropertyNamingMap(targetType: KClass<out Annotation>, targetPropertyName: String): String? {
        val targetMapping = namingMaps[targetType]?.get(targetPropertyName)
        if (targetMapping != null) {
            return targetMapping
        }

        return if (containsProperty(targetType, targetPropertyName) && targetPropertyName in this) {
            targetPropertyName
        } else null

    }

    private fun containsProperty(targetType: KClass<out Annotation>, property: String): Boolean {
        val targetMetadata: KAnnotationMetadata<out Annotation> = KAnnotationMetadata.resolve(targetType)
        return property in targetMetadata
    }

    override fun toString(): String {
        return "KAnnotationMetadata(annotationType=${annotationType.qualifiedName})"
    }

    companion object {
        // repeatable | deprecated | mustDocumented
        private const val ZERO_BYTE: Byte = 0
        private const val MUST_DOCUMENTED_BYTE: Byte = 1
        private const val DEPRECATED_BYTE: Byte = 2
        private const val REPEATABLE_BYTE: Byte = 4

        private fun <A : Annotation> resolveNamingMaps(
            property: KProperty1<A, *>,
            defaultMapType: KClass<out Annotation>?,
            namingMaps: MutableMap<KClass<out Annotation>, MutableMap<String, String>>
        ) {
            val name = property.name
            val properties: List<AnnotationMapper.Property> =
                property.findAnnotations<AnnotationMapper.Property>().ifEmpty {
                    property.getter.findAnnotations()
                }

            if (properties.isNotEmpty()) {
                for (mapperProperty in properties) {
                    var target: KClass<out Annotation> = mapperProperty.target
                    if (target == Annotation::class) {
                        target = defaultMapType ?: // 无法确定属性的默认映射目标
                                throw IllegalStateException("Unable to determine the default mapping target of the mapperProperty.")
                    }
                    val targetName: String = mapperProperty.value
                    namingMaps.computeIfAbsent(target) { mutableMapOf() }
                        .merge(targetName, name) { v1: String, v2: String ->
                            throw IllegalStateException(
                                "Duplicate mapping target: $v1 -> $targetName vs $v2 -> $targetName"
                            )
                        }
                }
            }
        }

        // repeatable | deprecated | mustDocumented
        private fun getMarks0(
            annotationType: KClass<out Annotation>,
            deprecated: Boolean,
            repeatable: Boolean
        ): Byte {
            var marks: Byte = 0
            if (annotationType.hasAnnotation<MustBeDocumented>()) {
                marks = marks or MUST_DOCUMENTED_BYTE
            }
            if (deprecated) {
                marks = marks or DEPRECATED_BYTE
            }
            if (repeatable) {
                marks = marks or REPEATABLE_BYTE
            }
            return marks
        }
    }
}


private val KClass<out Annotation>.retentionPolicy: AnnotationRetention
    get() = findAnnotation<Retention>()?.value ?: AnnotationRetention.RUNTIME

private val KClass<out Annotation>.targets: Set<AnnotationTarget>
    get() = findAnnotation<Target>()?.allowedTargets?.toSet() ?: emptySet()

private fun getDeprecated(annotationType: KClass<out Annotation>): Deprecated? = annotationType.findAnnotation()

