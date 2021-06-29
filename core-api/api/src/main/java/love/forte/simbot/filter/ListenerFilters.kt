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

@file:JvmName("ListenerFilters")

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
 */
public inline fun listenerFilter(
    priority: Int = PriorityConstant.LAST,
    filterValueGetter: (name: String, text: String) -> String? = { _, _ -> null },
    block: (data: FilterData) -> Boolean,
): ListenerFilter {

    TODO()
}

