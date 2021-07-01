/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:JvmName("ListenerFilterUtil")

package love.forte.simbot.filter

import love.forte.simbot.constant.PriorityConstant


/**
 * 合并两个filter，并得到一个新的实例。
 *
 * 进行过滤匹配的时候，提供一个 [MostMatchType], 默认情况下需要合并后的两个过滤器都为 `true` 才会得到true，即默认为 [MostMatchType.ALL][MostMatchType.ALL] [.filterMatcher][MostMatchType.filterMatcher]
 *
 *  @see plus
 */
@JvmOverloads
public fun merge(
    filter1: ListenerFilter,
    filter2: ListenerFilter,
    filterMatcher: MostMatcher<FilterData> = MostMatchType.ALL.filterMatcher,
): ListenerFilter = filter1.plus(filter2, filterMatcher)

public fun merge(
    filter1: ListenerFilter,
    filter2: ListenerFilter,
    filterMatcher: MostMatchType = MostMatchType.ALL,
): ListenerFilter = merge(filter1, filter2, filterMatcher.filterMatcher)


/**
 * 合并两个filter，并得到一个新的实例。
 *
 * 得到的新实例中的优先级以 **最左侧** 为准。 即当 `filter1 + filter2`, 则以 `filter1` 为准,
 * [ListenerFilter.getFilterValue] 也是优先以左侧的为准.
 *
 * ```kotlin
 *  val filter1 = listenerFilter { ... }
 *  val filter2 = buildListenerFilter { ... }
 *
 *  val merged1 = filter1 + filter2
 *
 *  val merged2 = filter1 + filter2 with MostMatchType.ANY
 *
 * ```
 *
 * 进行过滤匹配的时候，提供一个 [MostMatchType], 默认情况下需要合并后的两个过滤器都为 `true` 才会得到true，即默认为 [MostMatchType.ALL][MostMatchType.ALL] [.filterMatcher][MostMatchType.filterMatcher]
 *
 */
@JvmSynthetic
public operator fun ListenerFilter.plus(otherFilter: ListenerFilter): MergedListenerFilter =
    MergedListenerFilter(this, otherFilter)

/**
 * 合并两个filter，并得到一个新的实例，且提供一个过滤器匹配方式。
 */
@JvmSynthetic
public fun ListenerFilter.plus(
    otherFilter: ListenerFilter,
    filterMatcher: MostMatcher<FilterData>,
): MergedListenerFilter = MergedListenerFilter(this, otherFilter, filterMatcher)


@JvmSynthetic
public infix fun MergedListenerFilter.with(filterMatcher: MostMatcher<FilterData>): MergedListenerFilter =
    MergedListenerFilter(this.mainFilter, this.subFilter, filterMatcher)

@JvmSynthetic
public infix fun MergedListenerFilter.with(mostMatchType: MostMatchType): MergedListenerFilter =
    this with mostMatchType.filterMatcher


public class MergedListenerFilter(
    internal val mainFilter: ListenerFilter,
    internal val subFilter: ListenerFilter,
    private val filterMatcher: MostMatcher<FilterData> = MostMatchType.ALL.filterMatcher,
) :
    ListenerFilter {

    override fun getFilterValue(name: String, text: String): String? {
        return mainFilter.getFilterValue(name, text) ?: subFilter.getFilterValue(name, text)
    }

    override val priority: Int
        get() = mainFilter.priority

    private val filterIter = listOf(mainFilter, subFilter)

    override fun test(data: FilterData): Boolean {
        return filterMatcher.mostMatch(data, filterIter)
    }
}


/**
 * 得到一个简易的 [ListenerFilter] 实例。
 *
 * 忽略 [ListenerFilter.getFilterValue] 的实现:
 *
 * ```kotlin
 *     val filter = listenerFilter(priority = 2) { data ->
 *          // do?
 *          true
 *     }
 * ```
 *
 * 也忽略优先级的实现：
 * ```kotlin
 *     val filter = listenerFilter { data ->
 *          // do?
 *          true
 *     }
 * ```
 *
 * 所有参数都有的实现：
 * ```kotlin
 *
 *     val filter = listenerFilter(
 *          priority = 2,
 *          filterValueGetter = { name, text -> "value?" }
 *          ) { data ->
 *          // do?
 *          true
 *     }
 *
 * ```
 *
 */
public fun listenerFilter(
    priority: Int = PriorityConstant.LAST,
    filterValueGetter: (name: String, text: String) -> String? = { _, _ -> null },
    block: (data: FilterData) -> Boolean,
): ListenerFilter = SimpleListenerFilter(priority, filterValueGetter, block)


/**
 * [ListenerFilter]的基础实现.
 */
internal class SimpleListenerFilter(
    override val priority: Int,
    private val filterValueGetter: (name: String, text: String) -> String?,
    private val block: (data: FilterData) -> Boolean,
) : ListenerFilter {
    override fun test(data: FilterData): Boolean = block(data)
    override fun getFilterValue(name: String, text: String): String? = filterValueGetter(name, text)
}


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
@DslMarker
public annotation class ListenerFilterBuilderDSL


/**
 * 构建一个基于函数的 [ListenerFilter] 实例。
 *
 * Kotlin for example:
 *
 * ```kotlin
 *     val filter = buildListenerFilter {
 *          // or ignore
 *          priority = 2
 *
 *          // or ignore
 *          filterValueGetter { name, text -> null }
 *
 *          // must
 *          filter { data -> data.atDetection.atAll() }
 *      }
 * ```
 *
 */
public fun buildListenerFilter(builder: ListenerFilterBuilder.() -> Unit): ListenerFilter {
    return ListenerFilterBuilder().also(builder).build()
}


/**
 *
 * [ListenerFilter] 基于函数的构建器。
 *
 * @see buildListenerFilter
 *
 */
@ListenerFilterBuilderDSL
public class ListenerFilterBuilder {

    @ListenerFilterBuilderDSL
    var priority: Int = PriorityConstant.LAST

    private var filterValueGetter: (name: String, text: String) -> String? = { _, _ -> null }

    @ListenerFilterBuilderDSL
    fun filterValueGetter(getter: (name: String, text: String) -> String?) {
        filterValueGetter = getter
    }

    private var block: ((data: FilterData) -> Boolean)? = null

    @ListenerFilterBuilderDSL
    fun filter(block: (data: FilterData) -> Boolean) {
        this.block = block
    }

    fun build(): ListenerFilter = listenerFilter(
        priority = priority,
        filterValueGetter = filterValueGetter,
        block = requireNotNull(block) { "Required filter function was null. Maybe you need to use ListenerFilterBuilder.filter { data -> Boolean } for this builder. " }
    )

}
