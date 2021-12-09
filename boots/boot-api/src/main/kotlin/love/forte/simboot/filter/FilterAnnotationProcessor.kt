/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.filter

import love.forte.simbot.event.EventFilter
import kotlin.reflect.KClass


/**
 * Annotation data for `@Filter`
 */
public data class FilterData(
    val value: String,
    val matchType: MatchType = MatchType.REGEX_MATCHES,
    val target: TargetFilterData = TargetFilterData(),
    val and: FiltersData = FiltersData(),
    val or: FiltersData = FiltersData(),
    val processor: KClass<out FilterAnnotationProcessor> = FilterAnnotationProcessor::class
)

/**
 * Annotation data for `@TargetFilter`
 */
public data class TargetFilterData(
    val components: List<String> = emptyList(),
    val bots: List<String> = emptyList(),
    val authors: List<String> = emptyList(),
    val groups: List<String> = emptyList(),
    val channels: List<String> = emptyList(),
    val guilds: List<String> = emptyList()
)

/**
 * Annotation data for `@Filters`
 */
public data class FiltersData(
    val value: List<FilterData> = emptyList(),
    val multiMatchType: MultiFilterMatchType = MultiFilterMatchType.ALL,
    val processor: KClass<out FiltersAnnotationProcessor> = FiltersAnnotationProcessor::class
)


public interface FilterAnnotationProcessContext {
    public val filter: FilterData
    public val filterProcessorFactory: (type: KClass<out FilterAnnotationProcessor>) -> FilterAnnotationProcessor?
    public val filtersProcessorFactory: (type: KClass<out FiltersAnnotationProcessor>) -> FiltersAnnotationProcessor?
}

public fun filterAnnotationProcessContext(
    filter: FilterData,
    filterProcessorFactory: (type: KClass<out FilterAnnotationProcessor>) -> FilterAnnotationProcessor?,
    filtersProcessorFactory: (type: KClass<out FiltersAnnotationProcessor>) -> FiltersAnnotationProcessor?
): FilterAnnotationProcessContext = FilterAnnotationProcessContextImpl(
    filter, filterProcessorFactory, filtersProcessorFactory
)

public fun filterAnnotationProcessContext(
    filter: FilterData,
    context: FilterAnnotationProcessContext
): FilterAnnotationProcessContext = FilterAnnotationProcessContextImpl(
    filter, context.filterProcessorFactory, context.filtersProcessorFactory
)

public fun filterAnnotationProcessContext(
    filter: FilterData,
    context: FiltersAnnotationProcessContext
): FilterAnnotationProcessContext = FilterAnnotationProcessContextImpl(
    filter, context.filterProcessorFactory, context.filtersProcessorFactory
)


private class FilterAnnotationProcessContextImpl(
    override val filter: FilterData,
    override val filterProcessorFactory: (type: KClass<out FilterAnnotationProcessor>) -> FilterAnnotationProcessor?,
    override val filtersProcessorFactory: (type: KClass<out FiltersAnnotationProcessor>) -> FiltersAnnotationProcessor?
) : FilterAnnotationProcessContext


/**
 *
 * 对 [FilterData] 进行解析的处理加工器接口。
 *
 * @author ForteScarlet
 */
public interface FilterAnnotationProcessor {
    public fun process(context: FilterAnnotationProcessContext): EventFilter
}


public interface FiltersAnnotationProcessContext {
    public val filters: FiltersData
    public val filterProcessorFactory: (type: KClass<out FilterAnnotationProcessor>) -> FilterAnnotationProcessor?
    public val filtersProcessorFactory: (type: KClass<out FiltersAnnotationProcessor>) -> FiltersAnnotationProcessor?
}

public fun filtersAnnotationProcessContext(
    filter: FiltersData,
    filterProcessorFactory: (type: KClass<out FilterAnnotationProcessor>) -> FilterAnnotationProcessor?,
    filtersProcessorFactory: (type: KClass<out FiltersAnnotationProcessor>) -> FiltersAnnotationProcessor?

): FiltersAnnotationProcessContext = FiltersAnnotationProcessContextImpl(
    filter, filterProcessorFactory, filtersProcessorFactory
)

public fun filtersAnnotationProcessContext(
    filter: FiltersData,
    context: FilterAnnotationProcessContext

): FiltersAnnotationProcessContext = FiltersAnnotationProcessContextImpl(
    filter, context.filterProcessorFactory, context.filtersProcessorFactory
)

public fun filtersAnnotationProcessContext(
    filter: FiltersData,
    context: FiltersAnnotationProcessContext

): FiltersAnnotationProcessContext = FiltersAnnotationProcessContextImpl(
    filter, context.filterProcessorFactory, context.filtersProcessorFactory
)


private class FiltersAnnotationProcessContextImpl(
    override val filters: FiltersData,
    override val filterProcessorFactory: (type: KClass<out FilterAnnotationProcessor>) -> FilterAnnotationProcessor?,
    override val filtersProcessorFactory: (type: KClass<out FiltersAnnotationProcessor>) -> FiltersAnnotationProcessor?
) : FiltersAnnotationProcessContext

/**
 * 对 [FiltersData] 进行解析的处理加工器接口。
 *
 */
public interface FiltersAnnotationProcessor {

    /**
     * 处理得到一个最终的汇总 [EventFilter] 实例。
     *
     * @return 如果 [FiltersData.value] 为空或者因为其他原因导致没有有效的过滤器，则返回null.
     *
     */
    public fun process(context: FiltersAnnotationProcessContext): EventFilter?


}