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

package love.forte.simbot.event

import love.forte.simbot.JSTP
import love.forte.simbot.message.doSafeCast


/**
 * 一个 **起点** 事件。
 *
 * [StartPointEvent] 是一个变化的起始变化，通常情况下其代表在变化后变化体开始存在，
 * 因此在 [StartPointEvent] 中 [before] 通常为 null。
 *
 * Note: _[StartPointEvent] 主要为 [IncreaseEvent] 事件提供上层语义, 很少有直接使用此事件的情况。_
 */
@BaseEvent
public interface StartPointEvent : ChangedEvent {
    /**
     * 开端事件, before通常为null。
     */
    @JSTP
    override suspend fun before(): Any? = null



    public companion object Key : BaseEventKey<StartPointEvent>("api.start_point", ChangedEvent) {
        override fun safeCast(value: Any): StartPointEvent? = doSafeCast(value)
    }
}


/**
 * 一个 **终点** 事件。
 *
 *  [EndPointEvent] 是一个变化的最终变化，通常情况下其代表在变化后变化体则不再存在，
 * 因此在 [EndPointEvent] 中，[after] 应为null。
 *
 * Note: _[EndPointEvent] 主要为 [DecreaseEvent] 事件提供上层语义, 很少有直接使用此事件的情况。_
 *
 */
@BaseEvent
public interface EndPointEvent : ChangedEvent {

    /**
     * 终端事件，[after] 通常为null。
     */
    @JSTP
    override suspend fun after(): Any? = null


    public companion object Key : BaseEventKey<EndPointEvent>("api.end_point", ChangedEvent) {
        override fun safeCast(value: Any): EndPointEvent? = doSafeCast(value)
    }

}


/**
 * 一个 **增加** 事件，代表某种 _事物_ ([变更后属性][after]) 被增加到了一个 [源][source] 中。
 *
 */
@BaseEvent
public interface IncreaseEvent : StartPointEvent {
    public companion object Key : BaseEventKey<IncreaseEvent>("api.increase", StartPointEvent) {
        override fun safeCast(value: Any): IncreaseEvent? = doSafeCast(value)
    }
}


/**
 * 一个 **减少** 事件，代表某种 _事物_ ([变更前属性][before]) 被从一个 [源][source] 中移除。
 *
 */
@BaseEvent
public interface DecreaseEvent : EndPointEvent {
    public companion object Key : BaseEventKey<DecreaseEvent>("api.decrease", EndPointEvent) {
        override fun safeCast(value: Any): DecreaseEvent? = doSafeCast(value)
    }
}
