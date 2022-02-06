/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simboot.filter

import love.forte.di.BeanContainer
import love.forte.simbot.MutableAttributeMap
import love.forte.simbot.event.EventFilter
import love.forte.simbot.event.EventListener
import kotlin.reflect.KClass


/**
 * Annotation data for [love.forte.simboot.annotation.Filter].
 *
 * @property value same as [love.forte.simboot.annotation.Filter].value
 * @property matchType same as [love.forte.simboot.annotation.Filter].matchType
 * @property target same as [love.forte.simboot.annotation.Filter].target
 * @property and same as [love.forte.simboot.annotation.Filter].and
 * @property or same as [love.forte.simboot.annotation.Filter].or
 * @property processor same as [love.forte.simboot.annotation.Filter].processor
 * @property source [FilterData] 注解的 *源*。大多数情况下代表此注解所在的 [kotlin.reflect.KAnnotatedElement] 或 [java.lang.reflect.AnnotatedElement]。
 * 并不一定存在真实的源，当 [FilterData] 是通过手动构建等情况得到的，则无法确定 [source] 为何。
 *
 * @see love.forte.simboot.annotation.Filter
 */
public data class FilterData(
    val source: Any?,
    val value: String,
    val ifNullPass: Boolean,
    val matchType: MatchType = MatchType.REGEX_MATCHES,
    val target: TargetFilterData = TargetFilterData(source = source),
    val and: FiltersData = FiltersData(source = source),
    val or: FiltersData = FiltersData(source = source),
    val processor: KClass<out FilterAnnotationProcessor> = FilterAnnotationProcessor::class,
)

/**
 * Annotation data for [love.forte.simboot.annotation.TargetFilter].
 *
 * @property components same as [love.forte.simboot.annotation.TargetFilter].components
 * @property bots same as [love.forte.simboot.annotation.TargetFilter].bots
 * @property authors same as [love.forte.simboot.annotation.TargetFilter].authors
 * @property groups same as [love.forte.simboot.annotation.TargetFilter].groups
 * @property channels same as [love.forte.simboot.annotation.TargetFilter].channels
 * @property guilds same as [love.forte.simboot.annotation.TargetFilter].guilds
 * @property atBot same as [love.forte.simboot.annotation.TargetFilter].atBot
 */
public data class TargetFilterData(
    val source: Any?,
    val components: List<String> = emptyList(),
    val bots: List<String> = emptyList(),
    val authors: List<String> = emptyList(),
    val groups: List<String> = emptyList(),
    val channels: List<String> = emptyList(),
    val guilds: List<String> = emptyList(),
    val atBot: Boolean = false
)

/**
 * Annotation data for [love.forte.simboot.annotation.Filters].
 *
 * @property value same as [love.forte.simboot.annotation.Filters].value
 * @property multiMatchType same as [love.forte.simboot.annotation.Filters].multiMatchType
 * @property processor same as [love.forte.simboot.annotation.Filters].processor
 *
 * @property source [FiltersData] 的*源*, 大多数情况下代表此注解所在的 [kotlin.reflect.KAnnotatedElement] 或 [java.lang.reflect.AnnotatedElement]。
 * 并不一定存在真实的源，当 [FiltersData] 是通过手动构建等情况得到的，则无法确定 [source] 为何。
 */
public data class FiltersData(
    val source: Any?,
    val value: List<FilterData> = emptyList(),
    val multiMatchType: MultiFilterMatchType = MultiFilterMatchType.ANY,
    val processor: KClass<out FiltersAnnotationProcessor> = FiltersAnnotationProcessor::class,
)



public interface FilterAnnotationProcessContext {
    public val filter: FilterData
    public val listener: EventListener
    public val listenerAttributes: MutableAttributeMap
    public val registrar: EventFilterRegistrar
    public val beanContainer: BeanContainer
}

public fun filterAnnotationProcessContext(
    filter: FilterData,
    listener: EventListener,
    listenerAttributes: MutableAttributeMap,
    registrar: EventFilterRegistrar,
    beanContainer: BeanContainer
): FilterAnnotationProcessContext = FilterAnnotationProcessContextImpl(
    filter, listener, listenerAttributes, registrar, beanContainer
)

public fun filterAnnotationProcessContext(
    filter: FilterData,
    context: FilterAnnotationProcessContext
): FilterAnnotationProcessContext = FilterAnnotationProcessContextImpl(
    filter, context.listener, context.listenerAttributes, context.registrar, context.beanContainer
)

public fun filterAnnotationProcessContext(
    filter: FilterData,
    context: FiltersAnnotationProcessContext
): FilterAnnotationProcessContext = FilterAnnotationProcessContextImpl(
    filter, context.listener, context.listenerAttributes, context.registrar, context.beanContainer
)


private class FilterAnnotationProcessContextImpl(
    override val filter: FilterData,
    override val listener: EventListener,
    override val listenerAttributes: MutableAttributeMap,
    override val registrar: EventFilterRegistrar,
    override val beanContainer: BeanContainer
) : FilterAnnotationProcessContext


/**
 *
 * 对 [FilterData] 进行解析的处理加工器接口。
 *
 * @author ForteScarlet
 */
public interface FilterAnnotationProcessor {
    /**
     * 尝试解析处理并得到 [EventFilter].
     *
     * 正常来讲应该由 [FiltersAnnotationProcessor] 进行注册，而不需要通过此函数注册过滤器, [FilterAnnotationProcessor] 应当通过 [process] 的返回值来表示需要进行注册的过滤器。
     */
    public fun process(context: FilterAnnotationProcessContext): EventFilter?
}


public interface FiltersAnnotationProcessContext {
    public val filters: FiltersData
    public val listener: EventListener
    public val listenerAttributes: MutableAttributeMap
    public val registrar: EventFilterRegistrar
    public val beanContainer: BeanContainer

}

public fun filtersAnnotationProcessContext(
    filter: FiltersData,
    listener: EventListener,
    attributeMap: MutableAttributeMap,
    registrar: EventFilterRegistrar,
    beanContainer: BeanContainer
): FiltersAnnotationProcessContext = FiltersAnnotationProcessContextImpl(
    filter, listener, attributeMap, registrar, beanContainer
)

public fun filtersAnnotationProcessContext(
    filter: FiltersData,
    registrar: EventFilterRegistrar,
    context: FilterAnnotationProcessContext
): FiltersAnnotationProcessContext = FiltersAnnotationProcessContextImpl(
    filter, context.listener, context.listenerAttributes, registrar, context.beanContainer
)

public fun filtersAnnotationProcessContext(
    filter: FiltersData,
    context: FiltersAnnotationProcessContext
): FiltersAnnotationProcessContext = FiltersAnnotationProcessContextImpl(
    filter, context.listener, context.listenerAttributes, context.registrar, context.beanContainer
)


private class FiltersAnnotationProcessContextImpl(
    override val filters: FiltersData,
    override val listener: EventListener,
    override val listenerAttributes: MutableAttributeMap,
    override val registrar: EventFilterRegistrar,
    override val beanContainer: BeanContainer
) : FiltersAnnotationProcessContext

/**
 * 对 [FiltersData] 进行解析的处理加工器接口。
 *
 */
public interface FiltersAnnotationProcessor {

    /**
     * 处理并注册多个最终的 [EventFilter] 实例。所有实例最终会注入到当前的目标监听函数中。
     *
     * @return 如果 [FiltersData.value] 为空或者因为其他原因导致没有有效的过滤器，则返回null.
     *
     */
    public fun process(context: FiltersAnnotationProcessContext)


}