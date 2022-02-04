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

@file:JvmName("AnnotationParseUtil")

package love.forte.simboot.annotation

import love.forte.simboot.filter.FilterData
import love.forte.simboot.filter.FiltersData
import love.forte.simboot.filter.TargetFilterData
import love.forte.simboot.listener.ListenData
import love.forte.simboot.listener.ListenerData
import love.forte.simboot.listener.ListensData


public fun Filter.toData(source: Any? = null): FilterData {
    return FilterData(
        source = source,
        value = value,
        ifNullPass = ifNullPass,
        matchType = matchType,
        target = target.toData(source = source),
        and = and.toData(source = source),
        or = or.toData(source = source),
        processor = processor

    )
}

public fun TargetFilter.toData(source: Any? = null): TargetFilterData {
    return TargetFilterData(
        source = source,
        components = components.toList(),
        bots = bots.toList(),
        authors = authors.toList(),
        groups = groups.toList(),
        channels = channels.toList(),
        guilds = guilds.toList(),
        atBot = atBot
    )
}

public fun Filters.toData(source: Any? = null, valueList: List<FilterData>? = null): FiltersData {
    return FiltersData(
        source = source,
        value = valueList ?: value.map(Filter::toData),
        multiMatchType = multiMatchType,
        processor = processor,
    )
}


/**
 * 将 [Listen] 转化为 [ListenData].
 *
 */
public fun Listen.toData(): ListenData {
    return ListenData(value)
}

public fun Listens.toData(values: List<ListenData> = value.map(Listen::toData)): ListensData {
    return ListensData(values)
}

@JvmOverloads
public fun Listener.toData(listens: ListensData? = null): ListenerData {
    return ListenerData(
        id = id,
        priority = priority,
        async = async,
        listens = listens,
    )
}


public fun Listener.toData(listens: Listens): ListenerData {
    return toData(listens.toData())
}

