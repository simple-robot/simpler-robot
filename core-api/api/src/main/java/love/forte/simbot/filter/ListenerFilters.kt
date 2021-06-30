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
 * @see plus
 */
public fun merge(filter1: ListenerFilter, filter2: ListenerFilter): ListenerFilter = filter1 + filter2


/**
 * 合并两个filter，并得到一个新的实例。
 *
 * 得到的新实例中的优先级以 **最左侧** 为准。 即当 `filter1 + filter2`, 则以 `filter1` 为准,
 * [ListenerFilter.getFilterValue] 也是优先以左侧的为准.
 *
 */
@JvmSynthetic
public operator fun ListenerFilter.plus(otherFilter: ListenerFilter): ListenerFilter {
    TODO()
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
 *
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
