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
 */

package love.forte.simboot.core.filter

import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.Filters
import love.forte.simboot.annotation.TargetFilter
import love.forte.simboot.core.listener.FunctionalListenerProcessContext
import love.forte.simboot.filter.*
import love.forte.simbot.MutableAttributeMap
import love.forte.simbot.core.event.coreFilter
import love.forte.simbot.event.*
import love.forte.simbot.literal
import love.forte.simbot.message.At
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KAnnotatedElement

/**
 * Filter处理器。按照注解要求的预期规范进行处理。
 *
 */
private object CoreFilterAnnotationProcessor {
    fun process(filter: Filter, context: FiltersAnnotationProcessContext): EventFilter? {
        
        val value = filter.value
        val target = filter.target.box()
        val and = filter.and.takeIf { it.value.isNotEmpty() }
        val or = filter.or.takeIf { it.value.isNotEmpty() }
        
        if (value.isEmpty() && target == null && and == null && or == null) {
            return null
        }
        
        // 当前的普通过滤器
        val currentFilter = AnnotationFilter(
            target,
            value,
            filter.ifNullPass,
            filter.matchType
        ) { it.textContent } // selector
        
        // put keyword.
        context.listenerAttributes.computeIfAbsent(KeywordsAttribute) { CopyOnWriteArrayList() }
            .add(currentFilter.keyword)
        
        
        val andFilter = and?.process(context)
        val orFilter = or?.process(context)
        
        // 预解析
        when {
            andFilter == null && orFilter == null -> {
                return currentFilter
            }
            
            // both
            andFilter != null && orFilter != null -> {
                return coreFilter { filterContext ->
                    currentFilter.test(filterContext)
                            && andFilter.test(filterContext)
                            || orFilter.test(filterContext)
                }
            }
            // only and
            andFilter != null -> {
                return coreFilter { filterContext ->
                    currentFilter.test(filterContext) && andFilter.test(filterContext)
                }
            }
            // only or
            else -> {
                val orFilter0 = orFilter!!
                return coreFilter { filterContext ->
                    currentFilter.test(filterContext) || orFilter0.test(filterContext)
                }
            }
        }
    }
    
}

private class ListFilterRegistrar(val list: MutableList<EventFilter>) : EventFilterRegistrar {
    override fun register(filter: EventFilter) {
        list.add(filter)
    }
}

private fun Filters.process(context: FiltersAnnotationProcessContext): EventFilter? {
    return takeIf { it.value.isNotEmpty() }
        ?.let {
            val filters = CoreFiltersAnnotationProcessor.process(context)
            
            when {
                filters.isEmpty() -> null
                filters.size == 1 -> filters.first()
                else -> {
                    val matcherList: List<suspend (EventListenerProcessingContext) -> Boolean> = filters.apply {
                        sortedBy { f -> f.priority }
                    }.map { f -> f::test }
                    coreFilter { c ->
                        multiMatchType.match(c, matcherList)
                    }
                }
            }
        }
}


/**
 * @see TargetFilterData
 */
private data class FilterTarget(
    val components: Set<String>,
    val bots: Set<String>,
    val authors: Set<String>,
    val groups: Set<String>,
    val channels: Set<String>,
    val guilds: Set<String>,
    // 非
    val nonComponents: Set<String>,
    val nonBots: Set<String>,
    val nonAuthors: Set<String>,
    val nonGroups: Set<String>,
    val nonChannels: Set<String>,
    val nonGuilds: Set<String>,
    
    // at bot
    val atBot: Boolean,
)


private fun TargetFilter.box(): FilterTarget? {
    if (
        components.isEmpty() &&
        bots.isEmpty() &&
        authors.isEmpty() &&
        groups.isEmpty() &&
        channels.isEmpty() &&
        guilds.isEmpty() &&
        !atBot
    ) {
        return null
    }
    
    fun Array<String>.normals(): Set<String> {
        return filterTo(mutableSetOf()) { !it.startsWith(TargetFilter.NON_PREFIX) }
    }
    
    // 同时移除前缀
    fun Array<String>.nons(): Set<String> {
        return mapNotNullTo(mutableSetOf()) {
            if (it.startsWith(TargetFilter.NON_PREFIX)) {
                it.substringAfter(TargetFilter.NON_PREFIX)
            } else {
                null
            }
        }
    }
    
    return FilterTarget(
        components.normals(),
        bots.normals(),
        authors.normals(),
        groups.normals(),
        channels.normals(),
        guilds.normals(),
        // 非
        components.nons(),
        bots.nons(),
        authors.nons(),
        groups.nons(),
        channels.nons(),
        guilds.nons(),
        
        atBot
    )
    
}


private object AlwaysTrue : suspend (Event) -> Boolean {
    override suspend fun invoke(p1: Event): Boolean = true
}


private class AnnotationFilter(
    target: FilterTarget?,
    val value: String,
    val ifNullPass: Boolean,
    val matchType: MatchType,
    val contentSelector: (EventListenerProcessingContext) -> String?,
) : EventFilter {
    val keyword = if (value.isEmpty()) EmptyKeyword else KeywordImpl(value)
    val targetMatch: suspend (Event) -> Boolean = target?.toMatcher() ?: AlwaysTrue
    
    override suspend fun test(context: EventListenerProcessingContext): Boolean {
        val event = context.event
        
        // target
        if (!targetMatch(event)) {
            return false
        }
        
        val textContent = contentSelector(context) //.textContent
        
        // match
        // 存在匹配词, 尝试匹配
        if (keyword !== EmptyKeyword) {
            if (textContent != null) {
                if (!matchType.match(textContent, keyword)) {
                    return false
                }
            } else return ifNullPass
        }
        // 匹配关键词本身没有, 直接放行.
        // maybe other match..?
        
        
        return true
    }
}

private inline fun <T, C : Collection<T>> C.ifIsNotEmpty(block: (C) -> Unit) {
    if (isNotEmpty()) {
        block(this)
    }
}


private fun FilterTarget.toMatcher(): suspend (Event) -> Boolean {
    
    val matchers = buildList<suspend Event.() -> Boolean> {
        // components
        components.ifIsNotEmpty {
            add { component.id.literal in components }
        }
        nonComponents.ifIsNotEmpty {
            add { component.id.literal !in it }
        }
        // bots
        bots.ifIsNotEmpty {
            add { bot.id.literal in it }
        }
        nonBots.ifIsNotEmpty {
            add { bot.id.literal !in it }
        }
        
        // authors
        authors.ifIsNotEmpty {
            add {
                when (this) {
                    is ChatRoomMessageEvent -> author().id.literal in it
                    is ContactMessageEvent -> source().id.literal in it
                    else -> true
                }
            }
        }
        nonAuthors.ifIsNotEmpty {
            add {
                when (this) {
                    is ChatRoomMessageEvent -> author().id.literal !in it
                    is ContactMessageEvent -> source().id.literal !in it
                    else -> true
                }
            }
        }
        
        // groups
        groups.ifIsNotEmpty {
            add {
                if (this is GroupEvent) group().id.literal in it else true
            }
        }
        nonGroups.ifIsNotEmpty {
            add {
                if (this is GroupEvent) group().id.literal !in it else true
            }
        }
        
        // channels
        channels.ifIsNotEmpty {
            add {
                if (this is ChannelEvent) channel().id.literal in it else true
            }
        }
        nonChannels.ifIsNotEmpty {
            add {
                if (this is ChannelEvent) channel().id.literal !in it else true
            }
        }
        
        // guilds
        guilds.ifIsNotEmpty {
            add {
                if (this is GuildEvent) guild().id.literal in it else true
            }
        }
        nonGuilds.ifIsNotEmpty {
            add {
                if (this is GuildEvent) guild().id.literal !in it else true
            }
        }
        
        // atBot
        if (atBot) {
            add {
                if (this is ChatRoomMessageEvent) {
                    messageContent.messages.any { it is At && bot.isMe(it.target) }
                } else {
                    true
                }
            }
        }
    }
    
    if (matchers.isEmpty()) {
        return AlwaysTrue
    }
    
    val matchersArray = matchers.toTypedArray()
    
    return { event ->
        matchersArray.all { m -> event.m() }
    }
    
}

internal data class FiltersAnnotationProcessContext(
    val annotateElement: KAnnotatedElement,
    val filters: Filters?,
    val filterList: List<Filter>,
    val listener: EventListener,
    val listenerAttributes: MutableAttributeMap,
    val context: FunctionalListenerProcessContext,
)

/**
 * boot-core模块中的基础实现。
 *
 */
internal object CoreFiltersAnnotationProcessor {
    fun process(context: FiltersAnnotationProcessContext): List<EventFilter> {
        val filters = context.filters
        val filterList = context.filterList
        
        if (filters == null && filterList.isEmpty()) return emptyList()
        
        val eventFilterList = filterList.mapNotNull { f ->
            CoreFilterAnnotationProcessor.process(f, context)
        }
        
        // only 1
        if (eventFilterList.size == 1) {
            return listOf(eventFilterList.first())
        }
        
        // multi
        
        val multiMatchType = filters?.multiMatchType ?: MultiFilterMatchType.ANY
        
        @Suppress("SuspiciousCallableReferenceInLambda")
        val matcherList: List<suspend (EventListenerProcessingContext) -> Boolean> =
            eventFilterList.sortedBy { it.priority }.map { f -> f::test }
        
        val filter = coreFilter {
            multiMatchType.match(it, matcherList)
        }
        
        return listOf(filter)
    }
    
}
