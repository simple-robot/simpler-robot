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

import love.forte.simboot.annotation.AnnotationEventFilter
import love.forte.simboot.annotation.AnnotationEventFilter.InitType.INDEPENDENT
import love.forte.simboot.annotation.AnnotationEventFilter.InitType.UNITED
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.Filters
import love.forte.simboot.annotation.TargetFilter
import love.forte.simboot.core.listener.FunctionalListenerProcessContext
import love.forte.simboot.filter.EmptyKeyword
import love.forte.simboot.filter.MatchType
import love.forte.simboot.filter.MultiFilterMatchType
import love.forte.simbot.*
import love.forte.simbot.core.event.coreFilter
import love.forte.simbot.event.*
import love.forte.simbot.message.At
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass

/**
 * Filter处理器。按照注解要求的预期规范进行处理。
 *
 */
public object CoreFilterAnnotationProcessor {
    private val logger = LoggerFactory.getLogger(CoreFilterAnnotationProcessor::class)
    public fun process(filters: Filters, filter: Filter, context: FiltersAnnotationProcessContext): EventFilter? {
        if (filter.by != AnnotationEventFilter::class) {
            return process(filter.by, filters, filter, context)
        }
        
        
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
        
        return currentFilter.processOrAnd(context, and, or)
    }
    
    
    private fun process(
        type: KClass<out AnnotationEventFilter>,
        filters: Filters,
        filter: Filter,
        context: FiltersAnnotationProcessContext,
    ): EventFilter? {
        val obj = type.objectInstance
        val annotationEventFilter: AnnotationEventFilter = obj ?: run {
            // from bean container
            val beanContainer = context.context.beanContainer
            val beanNames = beanContainer.getAll(type)
            
            val fromContainer = kotlin.runCatching {
                when {
                    beanNames.size > 1 -> {
                        throw SimbotIllegalStateException("There is more than 1 event filter handler of type [$type] in the bean container: $beanNames")
                    }
                    beanNames.isEmpty() -> {
                        return@runCatching null
                    }
                    else -> {
                        // get
                        beanContainer[beanNames.first()] as? AnnotationEventFilter
                    }
                }
            }.getOrElse { e ->
                logger.debug("Cannot get the instance of type [$type] in beanContainers by names [$beanNames]", e)
                null
            }
            
            if (fromContainer != null) {
                return@run fromContainer
            }
            
            // try to create instance.
            return@run kotlin.runCatching {
                val constructor = type.constructors.find { it.parameters.isEmpty() }
                    ?: throw NoSuchElementException("Public, no-argument constructor for type [$type]")
                
                constructor.call()
            }.getOrElse { e ->
                throw SimbotIllegalStateException("Cannot create instance for type [$type]", e)
            }
        }
        
        // init it.
        val initType = annotationEventFilter.init(context.listener, filter, filters)
        
        logger.debug("Init annotation event filter: [{}], init type: {}", annotationEventFilter, initType)
        
        return when (initType) {
            INDEPENDENT -> {
                // just return
                annotationEventFilter
            }
            UNITED -> {
                // andOr
                val and = filter.and.takeIf { it.value.isNotEmpty() }
                val or = filter.or.takeIf { it.value.isNotEmpty() }
                annotationEventFilter.processOrAnd(context, and, or)
            }
        }
        
    }
    
    private fun EventFilter.processOrAnd(
        context: FiltersAnnotationProcessContext,
        and: Filters?,
        or: Filters?,
    ): EventFilter {
        val andFilter = and?.process(context)
        val orFilter = or?.process(context)
        
        // 预解析
        when {
            andFilter == null && orFilter == null -> {
                return this
            }
            
            // both
            andFilter != null && orFilter != null -> {
                return coreFilter { filterContext ->
                    this.test(filterContext)
                            && andFilter.test(filterContext)
                            || orFilter.test(filterContext)
                }
            }
            // only and
            andFilter != null -> {
                return coreFilter { filterContext ->
                    this.test(filterContext) && andFilter.test(filterContext)
                }
            }
            // only or
            else -> {
                val orFilter0 = orFilter!!
                return coreFilter { filterContext ->
                    this.test(filterContext) || orFilter0.test(filterContext)
                }
            }
        }
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
 * @see TargetFilter
 */
private data class FilterTarget(
    val components: Set<CharSequenceID>,
    val bots: Set<String>,
    val authors: Set<String>,
    val groups: Set<String>,
    val channels: Set<String>,
    val guilds: Set<String>,
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
    
    return FilterTarget(
        components.map { it.ID }.toSet(),
        bots.toSet(),
        authors.toSet(),
        groups.toSet(),
        channels.toSet(),
        guilds.toSet(),
        atBot
    )
    
}


private class AnnotationFilter(
    target: FilterTarget?,
    val value: String,
    val ifNullPass: Boolean,
    val matchType: MatchType,
    val contentSelector: (EventListenerProcessingContext) -> String?,
) : EventFilter {
    val keyword = if (value.isEmpty()) EmptyKeyword else KeywordImpl(value)
    val targetMatch: suspend (Event) -> Boolean = target?.toMatcher() ?: { true }
    
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
        
        
        // maybe other match
        
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
            if (event.bot.id.literal !in bots) {
                return@M false
            }
        }
        
        if (authors.isNotEmpty()) {
            if (event is ChatRoomMessageEvent) {
                if (event.author().id.literal !in authors) {
                    return@M false
                }
            }
            if (event is ContactMessageEvent) {
                if (event.source().id.literal !in authors) {
                    return@M false
                }
            }
        }
        
        if (groups.isNotEmpty() && event is GroupEvent) {
            if (event.group().id.literal !in groups) {
                return@M false
            }
        }
        
        if (channels.isNotEmpty() && event is ChannelEvent) {
            if (event.channel().id.literal !in channels) {
                return@M false
            }
        }
        
        if (guilds.isNotEmpty() && event is GuildEvent) {
            if (event.guild().id.literal !in guilds) {
                return@M false
            }
        }
        
        // atBot
        if (atBot && event is ChatRoomMessageEvent) {
            if (event.messageContent.messages.none { it is At && !event.bot.isMe(it.target) }) {
                return@M false
            }
        }
        
        true
    }
    
}

public data class FiltersAnnotationProcessContext(
    val annotateElement: KAnnotatedElement,
    val filters: Filters,
    val listener: EventListener,
    val listenerAttributes: MutableAttributeMap,
    val context: FunctionalListenerProcessContext,
)

/**
 * boot-core模块中的对 [Filters] 注解的基础实现。
 *
 */
public object CoreFiltersAnnotationProcessor {
    public fun process(context: FiltersAnnotationProcessContext): List<EventFilter> {
        val filters = context.filters
        val allFilters = filters.value
        
        if (allFilters.isEmpty()) return emptyList()
        
        val eventFilterList = allFilters.mapNotNull { f ->
            CoreFilterAnnotationProcessor.process(filters, f, context)
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
