/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.core.filter

import love.forte.simboot.annotation.AnnotationEventFilterFactory
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.Filters
import love.forte.simboot.core.listener.FunctionalListenerProcessContext
import love.forte.simbot.MutableAttributeMap
import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.core.event.simpleFilter
import love.forte.simbot.event.EventFilter
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass

/**
 * Filter处理器。按照注解要求的预期规范进行处理。
 *
 */
public object CoreFilterAnnotationProcessor {
    private val logger = LoggerFactory.logger<CoreFilterAnnotationProcessor>()
    public fun process(filters: Filters, filter: Filter, context: FiltersAnnotationProcessContext): EventFilter? {
        if (filter.by != AnnotationEventFilterFactory::class) {
            return process(filter.by, filters, filter, context)
        }


        return CoreAnnotationEventFilterFactory.resolveFilter(
            context.listener, context.listenerAttributes, filter, filters
        )
    }


    private fun process(
        type: KClass<out AnnotationEventFilterFactory>,
        filters: Filters,
        filter: Filter,
        context: FiltersAnnotationProcessContext,
    ): EventFilter? {
        val obj = type.objectInstance
        val annotationEventFilterFactory: AnnotationEventFilterFactory = obj ?: run {
            // from bean container
            val beanContainer = context.context.beanContainer
            val beanNames = beanContainer.getAll(type)

            val fromContainer: AnnotationEventFilterFactory? =
                when {
                    beanNames.size > 1 -> {
                        throw SimbotIllegalStateException("There is more than 1 event filter handler of type [$type] in the bean container: $beanNames")
                    }
                    beanNames.isEmpty() -> {
                        null
                    }
                    else -> {
                        beanContainer.getOrNull(beanNames.first()) as? AnnotationEventFilterFactory
                    }
                }
//            }.getOrElse { e ->
//                logger.error("Cannot get the instance of type [$type] in beanContainers by names [$beanNames]", e)
//                null
//            }

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
        val eventFilter =
            annotationEventFilterFactory.resolveFilter(context.listener, context.listenerAttributes, filter, filters)

        logger.debug("Init annotation event filter: [{}]", eventFilter)

        return eventFilter
    }

}


/**
 * @see Filter.Targets
 */
internal data class FilterTarget(
    val components: Set<String>,
    val bots: Set<String>,
    val authors: Set<String>,
    val groups: Set<String>,
    val channels: Set<String>,
    val guilds: Set<String>,
    val atBot: Boolean,
)

internal fun Filter.Targets.box(): FilterTarget? {
    if (components.isEmpty() && bots.isEmpty() && authors.isEmpty() && groups.isEmpty() && channels.isEmpty() && guilds.isEmpty() && !atBot) {
        return null
    }

    return FilterTarget(
        components.toSet(), bots.toSet(), authors.toSet(), groups.toSet(), channels.toSet(), guilds.toSet(), atBot
    )

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

        val multiMatchType = filters.multiMatchType

        @Suppress("SuspiciousCallableReferenceInLambda") val matcherList: List<suspend (EventListenerProcessingContext) -> Boolean> =
            eventFilterList.sortedBy { it.priority }.map { f -> f::test }

        val filter = simpleFilter {
            multiMatchType.match(it, matcherList)
        }

        return listOf(filter)
    }

}
