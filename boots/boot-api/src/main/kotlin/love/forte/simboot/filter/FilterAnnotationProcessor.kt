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

import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.Filters
import love.forte.simbot.event.EventFilter
import kotlin.reflect.KClass


public interface FilterAnnotationProcessContext {
    public val filter: Filter
    public val filterProcessorFactory: (type: KClass<out FilterAnnotationProcessor>) -> FilterAnnotationProcessor?
    public val filtersProcessorFactory: (type: KClass<out FiltersAnnotationProcessor>) -> FiltersAnnotationProcessor?
}

public fun filterAnnotationProcessContext(
    filter: Filter,
    filterProcessorFactory: (type: KClass<out FilterAnnotationProcessor>) -> FilterAnnotationProcessor?,
    filtersProcessorFactory: (type: KClass<out FiltersAnnotationProcessor>) -> FiltersAnnotationProcessor?
): FilterAnnotationProcessContext = FilterAnnotationProcessContextImpl(
    filter, filterProcessorFactory, filtersProcessorFactory
)


private class FilterAnnotationProcessContextImpl(
    override val filter: Filter,
    override val filterProcessorFactory: (type: KClass<out FilterAnnotationProcessor>) -> FilterAnnotationProcessor?,
    override val filtersProcessorFactory: (type: KClass<out FiltersAnnotationProcessor>) -> FiltersAnnotationProcessor?
) : FilterAnnotationProcessContext


/**
 *
 * 对 [Filter] 注解进行解析的处理加工器接口。
 *
 * @author ForteScarlet
 */
public interface FilterAnnotationProcessor {
    public fun process(context: FilterAnnotationProcessContext): EventFilter
}


public interface FiltersAnnotationProcessContext {
    public val filter: Filters
    public val filterProcessorFactory: (type: KClass<out FilterAnnotationProcessor>) -> FilterAnnotationProcessor?
    public val filtersProcessorFactory: (type: KClass<out FiltersAnnotationProcessor>) -> FiltersAnnotationProcessor?
}

public fun filtersAnnotationProcessContext(
    filter: Filters,
    filterProcessorFactory: (type: KClass<out FilterAnnotationProcessor>) -> FilterAnnotationProcessor?,
    filtersProcessorFactory: (type: KClass<out FiltersAnnotationProcessor>) -> FiltersAnnotationProcessor?

): FiltersAnnotationProcessContext = FiltersAnnotationProcessContextImpl(
    filter, filterProcessorFactory, filtersProcessorFactory
)


private class FiltersAnnotationProcessContextImpl(
    override val filter: Filters,
    override val filterProcessorFactory: (type: KClass<out FilterAnnotationProcessor>) -> FilterAnnotationProcessor?,
    override val filtersProcessorFactory: (type: KClass<out FiltersAnnotationProcessor>) -> FiltersAnnotationProcessor?
) : FiltersAnnotationProcessContext

/**
 * 对 [Filters] 注解进行解析的处理加工器接口。
 *
 */
public interface FiltersAnnotationProcessor {

    /**
     * 处理得到一个最终的汇总 [EventFilter] 实例。
     */
    public fun process(context: FiltersAnnotationProcessContext): EventFilter


}