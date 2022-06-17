package love.forte.simboot.core.filter

import love.forte.simboot.annotation.AnnotationEventFilterFactory
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.Filters
import love.forte.simboot.filter.EmptyKeyword
import love.forte.simboot.filter.MatchType
import love.forte.simbot.MutableAttributeMap
import love.forte.simbot.event.*
import love.forte.simbot.literal
import love.forte.simbot.message.At
import java.util.concurrent.CopyOnWriteArrayList

public object CoreAnnotationEventFilterFactory : AnnotationEventFilterFactory {
    override fun resolveFilter(
        listener: EventListener,
        listenerAttributes: MutableAttributeMap,
        filter: Filter,
        filters: Filters,
    ): EventFilter? {
        val value = filter.value
        val target = filter.target.box()
        
        if (value.isEmpty() && target == null) {
            return null
        }
        // 当前的普通过滤器
        val currentFilter = AnnotationFilter(
            target, value, filter.ifNullPass, filter.matchType
        ) { it.textContent } // selector
        
        // put keyword.
        listenerAttributes.computeIfAbsent(KeywordsAttribute) { CopyOnWriteArrayList() }.add(currentFilter.keyword)
        
        return currentFilter
    }
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
            if (event.component.id.literal !in components) {
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
            if (event.messageContent.messages.none { it is At && event.bot.isMe(it.target) }) {
                return@M false
            }
        }
        
        true
    }
    
}