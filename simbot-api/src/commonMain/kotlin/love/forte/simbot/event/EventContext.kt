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

package love.forte.simbot.event

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.common.attribute.MutableAttributeMap
import love.forte.simbot.message.MessageContent
import kotlin.coroutines.CoroutineContext

/**
 * 一个在事件处理流程中流转的上下文。
 * 用于承载本次事件处理前后的诸项信息。
 *
 * @author ForteScarlet
 */
public interface EventContext : CoroutineScope {
    /**
     * 当前事件调度的上下文作为 [CoroutineScope] 的协程上下文。
     * 通常与其所产生的调度器实现有关，例如与
     * [ApplicationConfiguration.coroutineContext][love.forte.simbot.application.ApplicationConfiguration.coroutineContext]
     * 有关。
     *
     * [EventContext] 的 [coroutineContext] 中不应出现 [kotlinx.coroutines.Job]。
     */
    override val coroutineContext: CoroutineContext

    /**
     * 本次事件处理流程中被处理的事件。
     */
    public val event: Event

    /**
     * 可以通过 [EventContext] 相互传递的 [MutableAttributeMap]。
     */
    public val attributes: MutableAttributeMap
}

/**
 * 一个在每一个事件处理器的独立处理流程中流转的上下文。
 * 用于承载本次事件处理前后的诸项信息。
 *
 * @author ForteScarlet
 */
public interface EventListenerContext {
    /**
     * 整个事件调度流程中的 [EventContext]。
     */
    public val context: EventContext

    /**
     * 获取 [context] 中的 [event][EventContext.event]
     *
     * @see EventContext.event
     */
    public val event: Event get() = context.event

    /**
     * 当前正在处理事件（所处的）事件处理器实例。
     */
    public val listener: EventListener

    /**
     * 本次事件处理器进行处理时，用于**匹配**的事件中消息文本内容。
     * 如果为 `null` 则可能说明事件类型不是 [MessageEvent] 或 [MessageContent.plainText] 本身为 `null`。
     * 建议在使用 [EventInterceptor] 或逻辑内有效性匹配时，如果需要对事件的文本内容进行处理、匹配，使用此处的 [plainText]
     * 而不是 [MessageContent.plainText]。
     * 一些自动生成、处理或流程化的处理逻辑（例如 `quantcat` 相关模块中的注解形式处理器）也会使用此处的 [plainText] 并可能对其值造成影响。
     *
     * [plainText] 是 **可修改** 的，其值的修改**不会**影响到事件原本的值。
     * 此值服务于流程化的拦截器以及匹配逻辑。
     */
    public var plainText: String?
}


