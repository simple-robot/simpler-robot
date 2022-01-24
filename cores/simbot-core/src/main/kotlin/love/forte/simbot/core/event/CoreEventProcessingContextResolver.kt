/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.core.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import love.forte.simbot.Attribute
import love.forte.simbot.AttributeMutableMap
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.MutableAttributeMap
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventProcessingContext
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.ScopeContext
import java.util.concurrent.ConcurrentHashMap

/**
 * 核心默认的事件上下文处理器。
 */
internal class CoreEventProcessingContextResolver(
    private val coroutineScope: CoroutineScope
) : EventProcessingContextResolver<CoreEventProcessingContext> {
    private val resumedListenerManager = ResumedListenerManager()

    @ExperimentalSimbotApi
    override val globalContext = GlobalScopeContext()

    @ExperimentalSimbotApi
    override val continuousSessionContext = CoreContinuousSessionContext(coroutineScope, resumedListenerManager)

    /**
     * 每一次的事件处理都应存在的属性内容。
     */
    @ExperimentalSimbotApi
    private val constMaps = mutableMapOf<Attribute<*>, Any>(
        EventProcessingContext.Scope.Global to globalContext,
        EventProcessingContext.Scope.ContinuousSession to continuousSessionContext
    )

    internal class GlobalScopeContext : ScopeContext, MutableAttributeMap by AttributeMutableMap(ConcurrentHashMap())
    private class InstantScopeContext : ScopeContext, MutableAttributeMap by AttributeMutableMap(ConcurrentHashMap())

    /**
     * 根据一个事件和当前事件对应的监听函数数量得到一个事件上下文实例。
     */
    @OptIn(ExperimentalSimbotApi::class)
    override suspend fun resolveEventToContext(event: Event, listenerSize: Int): CoreEventProcessingContext {
        val context = CoreEventProcessingContext(
            event, AttributeMutableMap(
                ConcurrentHashMap(
                    constMaps,
                ).apply { put(EventProcessingContext.Scope.Instant, InstantScopeContext()) }
            )
        ) {
            ArrayList(
                listenerSize
            )
        }

        coroutineScope.launch {
            resumedListenerManager.process(context, this)
        }

        return context
    }


    /**
     * 将一次事件结果拼接到当前上下文结果集中。
     */
    override suspend fun appendResultIntoContext(
        context: CoreEventProcessingContext,
        result: EventResult
    ): ListenerInvokeType {
        if (result != EventResult.Invalid) {
            context._results.add(result)
        }
        return if (result.isTruncated) ListenerInvokeType.TRUNCATED
        else ListenerInvokeType.CONTINUE
    }

    /**
     * 只要存在任意会话监听函数，则都需要进行监听事件推送。
     */
    override fun isProcessable(eventKey: Event.Key<*>): Boolean {
        return !resumedListenerManager.isEmpty()
    }

}