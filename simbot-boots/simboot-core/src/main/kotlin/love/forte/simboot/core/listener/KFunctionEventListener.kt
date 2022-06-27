/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
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
    override val id: String,
    override val priority: Int,
    override val isAsync: Boolean,
    private val targets: Set<Event.Key<*>>,
    override val binders: Array<ParameterBinder>,
    private val attributeMap: AttributeMutableMap,
    matcher: suspend (EventListenerProcessingContext) -> Boolean,
    caller: KFunction<R>,
) : FunctionalBindableEventListener<R>(matcher, caller) {
    
    override fun toString(): String {
        return "KFunctionEventListener(id=$id, priority=$priority, isAsync=$isAsync, isSuspend=${caller.isSuspend}, targets=${
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