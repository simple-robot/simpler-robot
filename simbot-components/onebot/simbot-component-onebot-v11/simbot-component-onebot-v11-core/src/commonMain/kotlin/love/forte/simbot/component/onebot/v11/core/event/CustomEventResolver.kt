/*
 *     Copyright (c) 2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

package love.forte.simbot.component.onebot.v11.core.event

import kotlinx.serialization.json.Json
import love.forte.simbot.component.onebot.v11.core.OneBot11
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.event.Event

/**
 * 自定义事件解析器。根据得到的初步解析结果，将事件内容解析为一个 [Event]。
 *
 * 当存在多个自定义解析器，则会在**首次**得到非 `null` 结果时终止处理。
 *
 * 如果处理链上得到的全部都为 `null`，则会在 [RawEventResolveResult.rawEvent]
 *  不为 `null` 的情况下尝试解析为标准事件。如果无法解析，则最终会被解析为
 *  [love.forte.simbot.component.onebot.v11.event.UnknownEvent] 和对应的
 *  [OneBotUnknownEvent]。
 *
 * ### 异常
 *
 * 如果 [resolve] 产生异常，此异常会被暂时记录，并继续尝试使用其他解析器。
 * 如果最终所有的 [CustomEventResolver] 都无法得到有效的结果，则：
 * - 如果 [RawEventResolveResult.rawEvent] 不为 `null`，则会尝试解析为标准事件。
 *   - 如果解析成功，则记录的异常会以 **异常日志** 的形式输出。
 *   - 如果解析失败，则会使用 [OneBotUnsupportedEvent] 进行包装，记录的异常会以 **异常日志** 的形式输出。
 * - 如果 [RawEventResolveResult.rawEvent] 为 `null`，则会使用 [OneBotUnknownEvent] 进行包装，
 * 记录的异常会以被整合并填充到 [love.forte.simbot.component.onebot.v11.event.UnknownEvent.reason] 中，
 * 并以 **异常日志** 的形式输出。
 *
 * @see CustomEventResolveException
 * @since 1.8.0
 * @author ForteScarlet
 */
@ExperimentalCustomEventResolverApi
public fun interface CustomEventResolver {

    /**
     * 根据提供的内容信息，解析得到一个 [Event]。
     *
     * @return 解析结果。如果跳过解析则返回 `null`。
     * @throws Exception 解析过程可能出现的异常。
     */
    @Throws(Exception::class)
    public fun resolve(context: Context): Event?

    /**
     * 提供给 [CustomEventResolver.resolve] 进行处理的内容。
     */
    public interface Context {
        /**
         * 当前的 [OneBotBot] 对象。
         */
        public val bot: OneBotBot

        /**
         * 可用于解析JSON的解析器。
         *
         * @see OneBot11.DefaultJson
         */
        public val json: Json

        /**
         * 对原始事件内容的初步解析结果。
         */
        public val rawEventResolveResult: RawEventResolveResult
    }
}
