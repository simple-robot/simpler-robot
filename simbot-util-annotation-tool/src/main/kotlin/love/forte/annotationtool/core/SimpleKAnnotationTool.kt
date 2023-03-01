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
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmName

/**
 * This is the default implementation of the library for the [KAnnotationTool] and is based on the kotlin reflect which implements the functionality required by the [KAnnotationTool].
 *
 *
 *
 * This implementation internally caches the final [Annotation] instance by default (except for the result of [createAnnotationInstance(...) ][createAnnotationInstance] ).
 * You can change this default by providing another Map, for example using [HashMap].
 *
 *
 * [SimpleKAnnotationTool] is **not thread-safe**.
 *
 * @author ForteScarlet
 */
internal class SimpleKAnnotationTool(
    private val cacheMap: MutableMap<KAnnotatedElement, MutableMap<KClass<out Annotation>, Annotation>>,
    private val nullCacheMap: MutableMap<KAnnotatedElement, MutableSet<KClass<out Annotation>>>,
    private val converters: Converters
) : KAnnotationTool {

    /**
     * 获取注解实例. 深度获取, 取第一个找到的.
     */
    override fun <A : Annotation> getAnnotation(
        fromElement: KAnnotatedElement,
        annotationType: KClass<A>,
        excludes: Set<String>
    ): A? {
        if (isExcluded(annotationType, DEFAULT_EXCLUDES)) {
            return fromElement.annotations.firstOrNull { a -> a.annotationClass == annotationType }?.let {
                annotationType.cast(it)
            }
        }
        val realExcludes = DEFAULT_EXCLUDES + excludes


        val directly: A? = fromElement.getAnnotationDirectly(annotationType)
        if (directly != null) return directly

        val annotations = fromElement.annotations

        // deep found.
        for (annotation in annotations) {
            if (isExcluded(annotation, realExcludes)) {
                continue
            }
            val foundFromAnnotation = getAnnotationFromAnnotation(
                annotation,
                annotationType,
                realExcludes + setOf(annotation.annotationClass.name)
            )
            if (foundFromAnnotation != null) {
                return foundFromAnnotation
            }
        }

        fromElement.setNull(annotationType)
        return null
    }


    /**
     * 直接获取注解，会尝试检测缓存，但是不会深层获取或映射。
     */
    private fun <A : Annotation> KAnnotatedElement.getAnnotationDirectly(
        annotationType: KClass<A>,
        doCache: Boolean = true
    ): A? {
        // find cache
        val cache = getCache(annotationType)
        if (cache != null) return cache

        // is null cache
        if (isNull(annotationType)) return null

        // find.
        val annotations = annotations

        // directly first
        val directlyGot = annotations.firstOrNull { a -> a.annotationClass == annotationType }
        if (directlyGot != null) {
            // just return?
            return if (doCache) setCache(annotationType, annotationType.cast(directlyGot))
            else annotationType.cast(directlyGot)
        }

        return null
    }

    private fun isExcluded(want: KClass<out Annotation>, excludes: Set<String>): Boolean {
        return want.name in excludes
    }

    private fun isExcluded(annotation: Annotation, excludes: Set<String>): Boolean {
        return isExcluded(annotation.annotationClass, excludes)
    }

    /**
     * 从一个注解上获取一个（可能是映射的）注解。
     */
    private fun <A : Annotation> getAnnotationFromAnnotation(
        fromAnnotation: Annotation,
        annotationType: KClass<A>,
        excludes: Set<String>,
        deep: Boolean = true
    ): A? {
        val fromAnnotationType = fromAnnotation.annotationClass

        val annotationDirectly = fromAnnotationType.getAnnotationDirectly(annotationType, false)
        if (annotationDirectly != null) return fromAnnotationType.setCache(
            annotationType,
            fromAnnotation.mapTo(annotationDirectly)
        )


        val tryMapped = mapping(fromAnnotation, fromAnnotationType, annotationType)
        if (tryMapped != null) return fromAnnotationType.setCache(annotationType, tryMapped)

        if (deep) {
            for (annotation in fromAnnotationType.annotations) {
                if (isExcluded(annotation, excludes)) {
                    continue
                }

                val currentAnnotationType = annotation.annotationClass
                val deepGet = getAnnotationFromAnnotation(
                    annotation,
                    annotationType,
                    excludes + setOf(fromAnnotationType.name, currentAnnotationType.name),
                    true
                )
                if (deepGet != null) return deepGet
            }
        }


        return null
    }


    @OptIn(ExperimentalStdlibApi::class)
    override fun <A : Annotation> getAnnotations(
        element: KAnnotatedElement,
        annotationType: KClass<A>,
        excludes: Set<String>
    ): List<A> {
        val realExcludes = DEFAULT_EXCLUDES + excludes
        val annotationList = mutableListOf<A>()

        element.includeAnnotations(annotationType, realExcludes, annotationList)

        return annotationList
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun <A : Annotation> KAnnotatedElement.includeAnnotations(
        annotationType: KClass<A>,
        excludes: Set<String>,
        annotationList: MutableList<A>
    ) {
        // 先直接通过类型find，这是直接查找的。
        val directAnnotations = findAnnotations(annotationType)
        annotationList.addAll(directAnnotations)

        // 先直接查询所有注解.
        val foundAnnotations = annotations

        for (annotation in foundAnnotations) {
            if (isExcluded(annotation, excludes)) {
                continue
            }
            val annotationClass = annotation.annotationClass
            // if (annotationClass == annotationType) {
            //     annotationList.add(annotationType.cast(annotation))
            // } else {
            // 接着寻找可映射的
            val currentExclude = excludes + setOf(annotationClass.name)
            val gotFromAnnotation = getAnnotationFromAnnotation(annotation, annotationType, currentExclude)
            if (gotFromAnnotation != null) {
                annotationList.add(gotFromAnnotation)
            } else {
                // cannot map, deep.
                annotationClass.includeAnnotations(
                    annotationType,
                    currentExclude,
                    annotationList
                )
            }

            // }

        }
    }


    //endregion
    //region Mapping
    /**
     * Try resolve an annotation to target type.
     *
     * @return target annotation.
     */
    private fun <F : Annotation, T : Annotation> mapping(
        sourceAnnotation: F,
        sourceAnnotationType: KClass<out Annotation>,
        targetType: KClass<T>
    ): T? {
        val sourceAnnotationMapper = sourceAnnotationType.findAnnotation<AnnotationMapper>() ?: return null

        val mapperTargets = sourceAnnotationMapper.value

        // can not map
        if (targetType in mapperTargets) {
            val targetMetadata: KAnnotationMetadata<T> = targetType.metadata()
            val targetPropertyTypes: Map<String, KType> = targetMetadata.propertyTypes


            // get source values.
            val sourceMetadata: KAnnotationMetadata<out Annotation> = sourceAnnotationType.metadata()
            val sourceAnnotationValues = getAnnotationValues(sourceAnnotation)
            val namingMaps: Map<String, String> = sourceMetadata.getPropertyNamingMaps(targetType)
            val targetValues = namingMaps.map { (targetKey: String, sourceKey: String) ->
                val targetValue: Any = converters.convert(
                    instance = sourceAnnotationValues[sourceKey]!!,
                    to = targetPropertyTypes[targetKey]!!.classifier as KClass<*>
                )
                targetKey to targetValue
            }.toMap()
            return createAnnotationInstance(targetType, targetValues)
        }

        // not contains, find from other mapper target.
        for (mapperTarget in mapperTargets) {
            val otherTarget = mapping(sourceAnnotation, sourceAnnotation.annotationClass, mapperTarget)
            if (otherTarget != null) {
                val findFromOtherMapper = mapping(otherTarget, otherTarget.annotationClass, targetType)
                if (findFromOtherMapper != null) {
                    return findFromOtherMapper
                }
            }
        }
        return null
    }

    /**
     * Map <tt>source</tt> to <tt>target</tt>.
     * 其中，<tt>target</tt> 注解实例是从 <tt>source</tt> 注解实例上获取到的。
     * 因此，在提供 <tt>target</tt> 的基础上，通过 <tt>source</tt> 对 <tt>target</tt> 进行映射。
     * 如果无法映射，即既未标注 [AnnotationMapper], 也没有直接标记目标注解，得到null。
     */
    private fun <F : Annotation, T : Annotation> F.mapTo(target: T): T {
        val fromSourceValues = getAnnotationValues(this)
        val targetSourceValues = getAnnotationValues(target)
        val targetAnnotationType = target.annotationClass

        @Suppress("UNCHECKED_CAST")
        val targetMetadata: KAnnotationMetadata<T> = targetAnnotationType.metadata() as KAnnotationMetadata<T>

        @Suppress("UNCHECKED_CAST")
        val sourceMetadata: KAnnotationMetadata<F> = annotationClass.metadata() as KAnnotationMetadata<F>

        // mappings.
        val propertyNamingMaps: Map<String, String> = sourceMetadata.getPropertyNamingMaps(targetAnnotationType)

        val targetNames = targetMetadata.propertyNames

        // target values
        val targetValues = targetNames.map { targetValueKey ->
            // 如果有映射，此为source value
            val sourceValueKey: String? = propertyNamingMaps[targetValueKey]

            if (sourceValueKey != null) {
                val sourceValue = fromSourceValues[sourceValueKey]!!
                // 得到目标数据类型
                val targetType = targetMetadata.getPropertyType(targetValueKey)!!
                val targetValue: Any = converters.convert(
                    instance = sourceValue,
                    to = targetType.classifier as KClass<*>
                )
                return@map targetValueKey to targetValue
            } else {
                // 没有映射，直接使用target原本的值
                val targetValue = targetSourceValues[targetValueKey]!!
                return@map targetValueKey to targetValue
            }


        }.toMap()

        return createAnnotationInstance(targetAnnotationType, targetValues)
    }


    override fun <A : Annotation> getAnnotationValues(annotation: A): Map<String, Any> {
        @Suppress("UNCHECKED_CAST")
        return (annotation.annotationClass.metadata() as KAnnotationMetadata<A>).getProperties(annotation)
    }

    override fun getPropertyNames(annotation: Annotation): Set<String> {
        return annotation.annotationClass.metadata().propertyNames
    }

    override fun getAnnotationPropertyTypes(annotationType: KClass<out Annotation>): Map<String, KType> {
        return annotationType.metadata().propertyTypes
    }

    override fun <A : Annotation> createAnnotationInstance(
        annotationType: KClass<A>,
        properties: Map<String, Any>,
        base: A?
    ): A {
        val primaryConstructor = annotationType.primaryConstructor!!
        val args = primaryConstructor.valueParameters.mapNotNull {
            var value = properties[it.name] ?: return@mapNotNull null

            @Suppress("UNCHECKED_CAST")
            val valueType: KClass<Any> = value::class as KClass<Any>
            val parameterType = it.type.classifier as KClass<*>
            if (!valueType.isSubclassOf(parameterType)) {
                value = converters.convert(valueType, valueType.cast(value), parameterType)
            }
            it to value
        }.toMap()

        return annotationType.newInstance(primaryConstructor, args)
    }

    override fun clearCache() {
        cacheMap.clear()
        nullCacheMap.clear()
    }

    private fun <A : Annotation> KAnnotatedElement.getCache(annotationType: KClass<A>): A? {
        return cacheMap[this]?.get(annotationType)?.let { a -> annotationType.cast(a) }
    }

    private fun <A : Annotation> KAnnotatedElement.setCache(annotationType: KClass<A>, instance: A): A {
        cacheMap.computeIfAbsent(this) { mutableMapOf() }[annotationType] = instance
        return instance
    }

    private fun KAnnotatedElement.isNull(annotationType: KClass<out Annotation>): Boolean {
        return nullCacheMap[this]?.contains(annotationType) == true
    }

    private fun KAnnotatedElement.setNull(annotationType: KClass<out Annotation>) {
        nullCacheMap.computeIfAbsent(this) { mutableSetOf() }.add(annotationType)
    }

    companion object {
        private val DEFAULT_EXCLUDES = setOf(
            // java meta-annotation
            "java.lang.annotation.Documented",
            "java.lang.annotation.Retention",
            "java.lang.annotation.Target",
            "java.lang.annotation.Deprecated",
            "java.lang.annotation.Inherited",
            "java.lang.annotation.Repeatable",
            // kotlin meta-annotation
            "kotlin.annotation.Target",
            "kotlin.annotation.Retention",
            "kotlin.annotation.MustBeDocumented",
            "kotlin.annotation.Repeatable",
            // javax inject meta-annotation
            "javax.inject.Qualifier",
            // spring aliasFor
            "org.springframework.core.annotation.AliasFor",

            // this lib's meta-annotation
            with(AnnotationMapper::class) { qualifiedName ?: jvmName },
            with(AnnotationMapper.Properties::class) { qualifiedName ?: jvmName },
            with(AnnotationMapper.Property::class) { qualifiedName ?: jvmName }
        )

    }


}


/**
 * Create An annotation instance by primaryConstructor.
 */
internal fun <A : Annotation> KClass<A>.newInstance(
    constructor: KFunction<A> = primaryConstructor!!,
    args: Map<KParameter, Any>
): A {
    return constructor.callBy(args)
}

private val KClass<*>.name: String get() = qualifiedName ?: jvmName
