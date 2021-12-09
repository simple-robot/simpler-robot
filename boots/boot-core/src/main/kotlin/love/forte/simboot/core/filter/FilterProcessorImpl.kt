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

package love.forte.simboot.core.filter

import love.forte.simboot.filter.*
import love.forte.simbot.CharSequenceID
import love.forte.simbot.ID
import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.core.event.coreFilter
import love.forte.simbot.event.*
import java.util.*

/**
 * Boot所提供的默认的 [FilterAnnotationProcessor] 实现，按照注解邀请的预期规范进行处理。
 *
 */
public object BootFilterAnnotationProcessor : FilterAnnotationProcessor {
    override fun process(context: FilterAnnotationProcessContext): EventFilter {
        val filter = context.filter
        // 当前的普通过滤器
        val currentFilter = FilterViaAnnotation(
            UUID.randomUUID().ID,
            filter.target.box(),
            filter.value,
            filter.matchType
        )

        val and = filter.and
        val or = filter.or

        val andFilter = and.process(context)
        val orFilter = or.process(context)

        // 预解析，不在逻辑中做判断。
        when {
            andFilter == null && orFilter == null -> {
                return currentFilter
            }

            // both
            andFilter != null && orFilter != null -> {
                return coreFilter(currentFilter.id) { filterContext ->
                    currentFilter.test(filterContext)
                            && andFilter.test(filterContext)
                            || orFilter.test(filterContext)
                }
            }
            // only and
            andFilter != null -> {
                return coreFilter(currentFilter.id) { filterContext ->
                    currentFilter.test(filterContext) && andFilter.test(filterContext)
                }
            }
            // only or
            else -> {
                val orFilter0 = orFilter!!
                return coreFilter(currentFilter.id) { filterContext ->
                    currentFilter.test(filterContext) || orFilter0.test(filterContext)
                }
            }
        }

    }

}

private fun FiltersData.process(context: FilterAnnotationProcessContext): EventFilter? {
    return takeIf { it.value.isNotEmpty() }
        ?.let {
            val processor = context.filtersProcessorFactory(it.processor)
                ?: throw SimbotIllegalStateException("Cannot found processor: ${it.processor}")

            processor.process(filtersAnnotationProcessContext(it, context))
        }

}


/**
 * @see TargetFilterData
 */
private data class FilterTarget(
    val components: Set<CharSequenceID>,
    val bots: Set<String>,
    val authors: Set<String>,
    val groups: Set<String>,
    val channels: Set<String>,
    val guilds: Set<String>,
)

private fun TargetFilterData.box(): FilterTarget? {
    if (
        components.isEmpty() &&
        bots.isEmpty() &&
        authors.isEmpty() &&
        groups.isEmpty() &&
        channels.isEmpty() &&
        guilds.isEmpty()
    ) {
        return null
    }

    return FilterTarget(
        components.map { it.ID }.toSet(),
        bots.toSet(),
        authors.toSet(),
        groups.toSet(),
        channels.toSet(),
        guilds.toSet()
    )

}

private class FilterViaAnnotation(
    override val id: ID,
    target: FilterTarget?,
    val value: String,
    val matchType: MatchType
) : EventFilter {
    private val keyword = if (value.isEmpty()) EmptyKeyword else KeywordImpl(value)
    private val targetMatch: suspend (Event) -> Boolean = target?.toMatcher() ?: { true }

    override suspend fun test(context: EventProcessingContext): Boolean {
        val event = context.event

        // target
        if (!targetMatch(event)) {
            return false
        }

        // match
        if (event is MessageEvent) {
            val targetText = event.messageContent.plainText
            if (!matchType.match(targetText, keyword)) {
                return false
            }
        }


        return true
    }
}


private fun FilterTarget.toMatcher(): suspend (Event) -> Boolean {
    return M@{ event ->
        if (components.isNotEmpty()) {
            if (event.component.id !in components) {
                return@M false
            }
        }

        if (bots.isNotEmpty()) {
            if (event.bot.id.toString() !in bots) {
                return@M false
            }
        }

        if (authors.isNotEmpty()) {
            if (event is ChatroomMessageEvent) {
                if (event.author.id.toString() !in authors) {
                    return@M false
                }
            }
            if (event is ContactMessageEvent) {
                if (event.source.id.toString() !in authors) {
                    return@M false
                }
            }
        }

        if (groups.isNotEmpty() && event is GroupEvent) {
            if (event.group().id.toString() !in groups) {
                return@M false
            }
        }

        if (channels.isNotEmpty() && event is ChannelEvent) {
            if (event.channel().id.toString() !in channels) {
                return@M false
            }
        }

        if (guilds.isNotEmpty() && event is GuildEvent) {
            if (event.guild().id.toString() !in guilds) {
                return@M false
            }
        }

        true
    }

}


public object BootFiltersAnnotationProcessor : FiltersAnnotationProcessor {
    override fun process(context: FiltersAnnotationProcessContext): EventFilter? {
        val filters = context.filters

        val filtersValue = filters.value
        if (filtersValue.isEmpty()) {
            return null
        }

        val filterList = filtersValue.map { f ->
            val processor = context.filterProcessorFactory(f.processor)
                ?: throw SimbotIllegalStateException("Cannot found processor: ${f.processor}")

            processor.process(filterAnnotationProcessContext(f, context))
        }

        // only 1
        if (filterList.size == 1) {
            return filterList[0]
        }

        // multi

        val multiMatchType = filters.multiMatchType

        @Suppress("SuspiciousCallableReferenceInLambda")
        val matcherList: List<suspend (EventProcessingContext) -> Boolean> = filterList.map { it::test }


        return coreFilter(UUID.randomUUID().ID) {
            multiMatchType.match(it, matcherList)
        }
    }


}

