/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.core.listener

import love.forte.annotationtool.core.nonConverters
import love.forte.simboot.listener.FunctionalBindableEventListener
import love.forte.simboot.listener.ParameterBinder
import love.forte.simbot.Attribute
import love.forte.simbot.AttributeMutableMap
import love.forte.simbot.event.Event
import love.forte.simbot.event.Event.Key.Companion.isSub
import love.forte.simbot.event.EventListenerProcessingContext
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

/**
 * 使用 [KFunction] 并可提供 [binders] 的 [FunctionalBindableEventListener] 实现。
 */
public class KFunctionEventListener<R>(
    private val targets: Set<Event.Key<*>>,
    override val binders: Array<ParameterBinder>,
    private val attributeMap: AttributeMutableMap,
    matcher: suspend (EventListenerProcessingContext) -> Boolean,
    caller: KFunction<R>,
) : FunctionalBindableEventListener<R>(matcher, caller) {
    
    override fun toString(): String {
        return "KFunctionEventListener(isSuspend=${caller.isSuspend}, targets=${
            targets.takeIf { it.isNotEmpty() }?.joinToString(", ", "[", "]") ?: "[<ALL>]"
        }, binders=${binders.joinToString(separator = ", ", "[", "]")}, caller=$caller)"
    }
    
    private lateinit var targetCaches: MutableSet<Event.Key<*>>
    private lateinit var notTargetCaches: MutableSet<Event.Key<*>>
    
    init {
        // not empty, init it.
        if (targets.isNotEmpty()) {
            targetCaches = mutableSetOf()
            notTargetCaches = mutableSetOf()
        }
        
        // functionalEntrance = FunctionalListenerInterceptor.entrance(this, interceptors)
    }
    
    override fun isTarget(eventType: Event.Key<*>): Boolean {
        // 如果为空，视为监听全部
        if (targets.isEmpty()) return true
        
        if (eventType in notTargetCaches) return false
        if (eventType in targetCaches) return true
        if (eventType in targets) return true
        
        synchronized(targetCaches) {
            if (eventType in targetCaches) return true
            
            for (target in targets) {
                if (eventType isSub target) {
                    targetCaches.add(eventType)
                    return true
                }
            }
            notTargetCaches.add(eventType)
            return false
        }
    }
    
    override fun convertValue(value: Any?, parameter: KParameter): Any? {
        if (value == null) return null
        return nonConverters().convert(
            instance = value,
            to = parameter.type.classifier as KClass<*>
        )
    }
    
    
    override fun <T : Any> getAttribute(attribute: Attribute<T>): T? = attributeMap[attribute]
}
