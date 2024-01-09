/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.spring.configuration.listener

import love.forte.simbot.ability.EventMentionAware
import love.forte.simbot.ability.MentionedTargetAware
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.common.PriorityConstant
import love.forte.simbot.common.attribute.MutableAttributeMap
import love.forte.simbot.common.attribute.mutableAttributeMapOf
import love.forte.simbot.common.attribute.set
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.definition.*
import love.forte.simbot.event.*
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.message.At
import love.forte.simbot.quantcat.annotations.*
import love.forte.simbot.quantcat.common.binder.*
import love.forte.simbot.quantcat.common.binder.impl.EmptyBinder
import love.forte.simbot.quantcat.common.binder.impl.MergedBinder
import love.forte.simbot.quantcat.common.filter.FilterMode
import love.forte.simbot.quantcat.common.filter.FilterProperties
import love.forte.simbot.quantcat.common.filter.FilterTargetsProperties
import love.forte.simbot.quantcat.common.filter.MultiFilterMatchType
import love.forte.simbot.quantcat.common.keyword.EmptyKeyword
import love.forte.simbot.quantcat.common.keyword.KeywordListAttribute
import love.forte.simbot.quantcat.common.keyword.SimpleKeyword
import love.forte.simbot.spring.MultipleIncompatibleTypesEventException
import love.forte.simbot.spring.utils.findMergedAnnotationSafely
import love.forte.simbot.spring.utils.findRepeatableMergedAnnotationSafely
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.ApplicationContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaMethod


private data class EventFilterData(
    val priority: Int,
    val matcher: MatcherFunc
)

private data class EventInterceptorData(
    val configurer: ConfigurerFunction<EventInterceptorRegistrationProperties>?,
    val interceptor: EventInterceptor
)


/**
 *
 * @author ForteScarlet
 */
internal class KFunctionEventListenerProcessor {
    private val instanceCache = ConcurrentHashMap<KClass<*>, Any>()

    fun process(
        beanName: String,
        function: KFunction<*>,
        listenerAnnotation: Listener,
        applyBinder: ApplyBinder?,
        applicationContext: ApplicationContext,
        binderManager: BinderManager
    ): SimbotEventListenerResolver {
        val id = listenerAnnotation.id
        val priority = listenerAnnotation.priority
        val listenTarget = function.listenTarget()
        val listenerAttributeMap = mutableAttributeMapOf(ConcurrentHashMap())
        val binders = function.resolveBinders(applicationContext, binderManager, applyBinder)

        // attributes
        listenerAttributeMap[KFunctionEventListener.RawFunctionAttribute] = function
        listenerAttributeMap[KFunctionEventListener.RawBindersAttribute] = binders.toList()
        listenerAttributeMap[KFunctionEventListener.RawListenTargetAttribute] = listenTarget

        val matchers = mutableListOf<MatcherFunc>()
        matchers.add { listenTarget.isInstance(it.event) }

        val filters = mutableListOf<EventFilterData>()
        val interceptors = mutableListOf<EventInterceptorData>()

        // filters & interceptors
        function.filtersAndInterceptorsByFilterAnnotations(
            listenerAttributeMap,
            onFilter = { filter ->
                filters.add(filter)
            },
            onInterceptor = { interceptor ->
                interceptors.add(interceptor)
            }
        )

        filters.sortBy { it.priority }
        filters.forEach {
            matchers.add(it.matcher)
        }

        // to array
        val finalMatchers = matchers.toTypedArray()



        return SimbotEventListenerResolver { application ->
            val instance = applicationContext.getBean(beanName)
            if (function.visibility != KVisibility.PUBLIC) {
                function.isAccessible = true
            }

            val listener = KFunctionEventListenerImpl(
                instance = instance,
                caller = function,
                binders = binders.toTypedArray(),
                attributes = listenerAttributeMap,
                matcher = { c -> finalMatchers.all { it.invoke(c) } },
            )

            application.eventDispatcher.register(
                {
                    this.priority = priority
                    interceptors.forEach { (config, interceptor) ->
                        addInterceptor(config, interceptor)
                    }
                }, listener
            )

            logger.debug("Registered listener {} (annotated id={}) from bean named {}", listener, id, beanName)
        }
    }

    @OptIn(InternalSimbotAPI::class)
    private fun KFunction<*>.resolveBinders(
        applicationContext: ApplicationContext,
        binderManager: BinderManager,
        applyBinder: ApplyBinder?
    ): List<ParameterBinder> {
        val binderFactories = binderManager.globals.toMutableList()
        if (applyBinder != null) {
            for (bid in applyBinder.value) {
                val b = binderManager[bid]
                if (b != null) {
                    binderFactories.add(b)
                } else {
                    logger.warn("Applied binder factory id [{}] not found, skip it.", bid)
                }
            }

            for (factoryType in applyBinder.factories) {
                val b = resolveBinderFactoryInstance(instanceCache, applicationContext, factoryType)
                binderFactories.add(b)
            }
        }

        return binderFactoriesToBinders(binderFactories.apply { sortBy { it.priority } })
    }

    private fun KFunction<*>.binderFactoriesToBinders(
        factories: List<ParameterBinderFactory>
    ): List<ParameterBinder> {
        val binders = parameters.map { parameter ->
            val bindList = mutableListOf<ParameterBinderResult.NotEmpty>()
            val bindSpareList = mutableListOf<ParameterBinderResult.NotEmpty>()

            val bindContext = ParameterBinderFactoryContextImpl(
                this,
                parameter
            )

            for (factory in factories) {
                when (val result = factory.resolveToBinder(bindContext)) {
                    is ParameterBinderResult.Empty -> continue
                    is ParameterBinderResult.NotEmpty -> {
                        // not empty.
                        when (result) {
                            is ParameterBinderResult.Normal -> {
                                if (bindList.isEmpty() || (bindList.first() !is ParameterBinderResult.Only)) {
                                    bindList.add(result)
                                }
                            }

                            is ParameterBinderResult.Only -> {
                                if (bindList.isNotEmpty() && bindList.first() is ParameterBinderResult.Only) {
                                    // 上一个也是Only.
                                    bindList[0] = result
                                } else {
                                    bindList.clear() // clear all
                                    bindList.add(result)
                                }
                            }

                            is ParameterBinderResult.Spare -> {
                                bindSpareList.add(result)
                            }
                        }
                    }
                }
            }
            bindList.sortBy { it.priority }
            bindSpareList.sortBy { it.priority }

            logger.trace(
                "There are actually {} normal binders bound to parameter [{}]. the binders: {}",
                bindList.size,
                parameter,
                bindList
            )
            logger.trace(
                "There are actually {} spare binders bound to parameter [{}]. the binders: {}",
                bindSpareList.size,
                parameter,
                bindSpareList
            )

            when {
                bindList.isEmpty() && bindSpareList.isEmpty() -> {
                    // no binder.
                    EmptyBinder(parameter)
                }

                bindList.isEmpty() -> {
                    // spare as normal.
                    MergedBinder(bindSpareList.map { it.binder }, emptyList(), parameter)
                }

                else -> {
                    MergedBinder(
                        bindList.map { it.binder },
                        bindSpareList.map { it.binder }.ifEmpty { emptyList() },
                        parameter
                    )
                }
            }
        }

        return binders
    }

    data class Data(val properties: FilterProperties, val matcher: MatcherFunc?)

    private inline fun KFunction<*>.filtersAndInterceptorsByFilterAnnotations(
        listenerAttributeMap: MutableAttributeMap,
        onFilter: (EventFilterData) -> Unit,
        onInterceptor: (EventInterceptorData) -> Unit,
    ) {
        // filter annotations
        val filterAnnotations = javaMethod?.findRepeatableMergedAnnotationSafely<Filter>() ?: return
        val multiFilter = javaMethod?.findMergedAnnotationSafely<MultiFilter>()
        val matchType = multiFilter?.matchType ?: MultiFilterMatchType.Default

        val grouped =
            filterAnnotations
                .groupingBy { f -> f.mode }
                .aggregate<Filter, FilterMode, MutableList<Data>> { _, accumulator, element, _ ->
                    val prop = element.toProperties()

                    val keywordMatchFunc = keywordMatcher(listenerAttributeMap, prop)
                    val targetsFilterFunc = prop.targets.firstOrNull()?.let(::targetFilterMatcher)

                    val finalMatcherFunc = when {
                        keywordMatchFunc == null && targetsFilterFunc == null -> null
                        targetsFilterFunc == null -> keywordMatchFunc
                        keywordMatchFunc == null -> targetsFilterFunc
                        else -> {
                            { c ->
                                targetsFilterFunc(c) && keywordMatchFunc(c)
                            }
                        }
                    }

                    val data = Data(prop, finalMatcherFunc)
                    val list: MutableList<Data> =
                        accumulator?.also { it.add(data) } ?: mutableListOf(data)

                    list
                }

        grouped[FilterMode.IN_LISTENER]?.mapNotNull {
            if (it.matcher == null) {
                return@mapNotNull null
            }

            EventFilterData(it.properties.priority, it.matcher)
        }?.also { filterMatchers ->
            when {
                filterMatchers.size == 1 -> onFilter(filterMatchers.first())
                filterMatchers.size > 1 -> merge(matchType, filterMatchers)
            }
        }

        grouped[FilterMode.INTERCEPTOR]?.forEach { (properties, matcher) ->
            if (matcher != null) {
                val priority = properties.priority
                val interceptor = EventInterceptor { c ->
                    if (matcher.invoke(c.eventListenerContext)) c.invoke() else EventResult.invalid
                }

                onInterceptor(EventInterceptorData({
                    this.priority = priority
                }, interceptor))
            }
        }

    }


    companion object {
        private val logger = LoggerFactory.logger<KFunctionEventListenerProcessor>()
    }
}

private data class ParameterBinderFactoryContextImpl(
    override val source: KFunction<*>,
    override val parameter: KParameter
) : ParameterBinderFactory.Context


@Suppress("UNCHECKED_CAST")
@OptIn(InternalSimbotAPI::class)
private fun resolveBinderFactoryInstance(
    instanceCache: MutableMap<KClass<*>, Any>,
    applicationContext: ApplicationContext,
    type: KClass<out BaseParameterBinderFactory<*>>
): ParameterBinderFactory {
    if (!type.isSubclassOf(ParameterBinderFactory::class)) {
        throw IllegalArgumentException("The types in ApplyBinder.factories must be ParameterBinderFactory, but found: $type")
    }

    type as KClass<out ParameterBinderFactory>

    val instance = try {
        applicationContext.getBean(type.java)
    } catch (ignore: NoSuchBeanDefinitionException) {
        null
    }

    if (instance != null) {
        return instance
    }

    val objInstance = type.objectInstance
    if (objInstance != null) {
        return objInstance
    }

    // try to create instance
    return runCatching {
        instanceCache.computeIfAbsent(type) { it.createInstance() } as ParameterBinderFactory
    }.getOrElse { e ->
        throw IllegalArgumentException("Can't create instance for $type", e)
    }
}


/**
 * 解析此监听函数所期望监听的事件列表。
 */
@Suppress("UNCHECKED_CAST")
private fun KFunction<*>.listenTarget(): KClass<out Event> {
    val typeLink = mutableListOf<KClass<*>>()
    var minType: KClass<out Event>? = null

    parameters.asSequence()
        .filter { it.kind != KParameter.Kind.INSTANCE }
        .filter { (it.type.classifier as? KClass<*>)?.isSubclassOf(Event::class) == true }
        .forEach {
            val e = it.type.classifier as KClass<out Event>
            val m = minType
            when {
                m == null -> {
                    minType = e
                    typeLink.add(e)
                }

                e == m -> {
                    // do nothing.
                }

                e.isSubclassOf(m) -> {
                    minType = e
                    typeLink.add(e)
                }

                else -> {
                    throw MultipleIncompatibleTypesEventException(buildString {
                        append("Current Event types link of function [${this@listenTarget}] is: \n[")
                        typeLink.forEachIndexed { index, t ->
                            append("(")
                            append(t)
                            append(")")
                            if (index != typeLink.lastIndex) {
                                append(" -> ")
                            }
                        }
                        append("], \nbut now: ")
                        append(it.type.classifier).append("(").append(it)
                        append("), it !is ").append(typeLink.last())
                    })
                }
            }
        }

    return minType ?: Event::class
}


private fun keywordMatcher(listenerAttributeMap: MutableAttributeMap, fp: FilterProperties): MatcherFunc? {
    val value = fp.value

    if (value.isEmpty()) return null

    val matchType = fp.matchType
    val keyword = if (value.isEmpty()) EmptyKeyword else SimpleKeyword(value, matchType.isPlainText)

    listenerAttributeMap.computeIfAbsent(KeywordListAttribute) { CopyOnWriteArrayList() }.add(keyword)

    listenerAttributeMap[KeywordListAttribute]

    val ifNullPass = fp.ifNullPass

    return m@{ c ->
        val content = c.plainText

        // 存在匹配词, 尝试匹配
        if (keyword !== EmptyKeyword) {
            if (content != null) {
                if (!matchType.match(keyword, content)) {
                    return@m false
                }
            } else return@m ifNullPass
        }

        // 匹配关键词本身没有, 直接放行.
        true
    }
}


private fun targetFilterMatcher(target: FilterTargetsProperties): MatcherFunc? {
    val matchers = mutableListOf<MatcherFunc>()

    // components
    with(target.components.toSet()) {
        if (isNotEmpty()) {
            matchers.add { c ->
                val event = c.context.event
                event !is ComponentEvent || contains(event.component.id)
            }
        }
    }

    // bots
    with(target.bots.map { it.ID }) {
        if (isNotEmpty()) {
            matchers.add { c ->
                val event = c.context.event
                event !is BotEvent || all { b -> event.bot.isMe(b) }
            }
        }
    }

    // authors
    with(target.authors.toSet()) {
        if (isNotEmpty()) {
            matchers.add { c ->
                val event = c.context.event
                event !is MessageEvent || contains(event.authorId.literal)
            }
        }
    }

    //region actors
    val actors = target.actors.toSet()
    val chatRooms = target.chatRooms.toSet()
    val organizations = target.organizations.toSet()
    val groups = target.groups.toSet()
    val guilds = target.guilds.toSet()
    val contacts = target.contacts.toSet()

    if (
        actors.isNotEmpty() ||
        chatRooms.isNotEmpty() ||
        organizations.isNotEmpty() ||
        groups.isNotEmpty() ||
        guilds.isNotEmpty() ||
        contacts.isNotEmpty()
    ) {
        matchers.add { c ->
            val event = c.event
            val helper = ContentEventMatchHelper(event)

            with(actors) {
                if (isNotEmpty()) {
                    helper.content<Actor>()?.also { ac ->
                        if (!contains(ac.id.literal)) return@add false
                    }
                }
            }

            with(chatRooms) {
                if (isNotEmpty()) {
                    helper.content<ChatRoom>()?.also { cr ->
                        if (!contains(cr.id.literal)) return@add false
                    }
                }
            }

            with(organizations) {
                if (isNotEmpty()) {
                    helper.org<Organization>()?.also { o ->
                        if (!contains(o.id.literal)) return@add false
                    }
                }
            }

            with(groups) {
                if (isNotEmpty()) {
                    helper.org<ChatGroup>()?.also { g ->
                        if (!contains(g.id.literal)) return@add false
                    }
                }
            }

            with(guilds) {
                if (isNotEmpty()) {
                    helper.org<Guild>()?.also { g ->
                        if (!contains(g.id.literal)) return@add false
                    }
                }
            }

            with(contacts) {
                if (isNotEmpty()) {
                    helper.content<Contact>()?.also { c ->
                        if (!contains(c.id.literal)) return@add false
                    }
                }
            }

            true
        }
    }
    //endregion

    // ats
    with(target.ats) {
        if (isNotEmpty()) {
            val strIdSet = toSet()
            val idArray = map { it.ID }.toTypedArray()

            matchers.add { c ->
                val event = c.event
                if (event !is MessageEvent) {
                    return@add false
                }

                val content = event.messageContent
                if (content is MentionedTargetAware) {
                    return@add idArray.all { t -> content.isMentioned(t) }
                }

                content.messages.any {
                    it is At && strIdSet.contains(it.target.literal)
                }
            }
        }
    }

    // at bot
    if (target.atBot) {
        matchers.add { c ->
            val event = c.event
            if (event !is BotEvent) return@add true

            val bot = event.bot
            if (bot is EventMentionAware) {
                bot.isMention(event)
            } else {
                if (event !is MessageEvent) {
                    return@add true
                }

                event.messageContent.messages.any {
                    it is At && bot.isMe(it.target)
                }
            }
        }

    }

    if (matchers.isEmpty()) return null
    if (matchers.size == 1) return matchers.first()

    val array = matchers.toTypedArray()
    return { context ->
        array.all { it.invoke(context) }
    }
}

private class ContentEventMatchHelper(private val event: Event) {
    /**
     * 如果事件是 [ContentEvent], 可初始化
     */
    var content: Any? = null

    /**
     * 如果事件是 [OrganizationAwareEvent] 或 [OrganizationEvent], 可初始化
     */
    var org: Organization? = null

    suspend inline fun <reified T> content(): T? {
        if (content == null) {
            if (event !is ContentEvent) {
                return null
            }

            content = event.content().also { c ->
                if (org == null && event is OrganizationEvent) {
                    org = c as Organization
                }
            }
        }

        return content as? T
    }

    suspend inline fun <reified T : Organization> org(): T? {
        if (org == null) {
            if (content is Organization) {
                org = content as Organization
                return org as? T
            }

            if (event is OrganizationEvent) {
                org = event.content().also {
                    if (content == null) {
                        content = org
                    }
                }
            }
            if (event is OrganizationAwareEvent) {
                org = event.organization()
            }
        }

        return org as? T
    }
}

private fun merge(type: MultiFilterMatchType, func: Collection<EventFilterData>): EventFilterData {
    val funcArray = func.sortedBy { it.priority }.map { it.matcher }.toTypedArray()
    return when (type) {
        MultiFilterMatchType.ANY -> EventFilterData(PriorityConstant.NORMAL) { c ->
            funcArray.any { m -> m.invoke(c) }
        }

        MultiFilterMatchType.ALL -> EventFilterData(PriorityConstant.NORMAL) { c ->
            funcArray.all { m -> m.invoke(c) }
        }

        MultiFilterMatchType.NONE -> EventFilterData(PriorityConstant.NORMAL) { c ->
            funcArray.none { m -> m.invoke(c) }
        }
    }
}

private typealias MatcherFunc = suspend (EventListenerContext) -> Boolean
