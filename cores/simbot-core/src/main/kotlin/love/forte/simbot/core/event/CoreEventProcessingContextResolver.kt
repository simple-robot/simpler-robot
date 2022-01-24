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