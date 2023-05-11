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

import love.forte.simbot.ID
import love.forte.simbot.JSTP
import love.forte.simbot.Timestamp
import love.forte.simbot.message.doSafeCast

/**
 * 一个与 **变更** 有关的事件。
 *
 * 这个变更可能是正在变更、计划变更，或者已经变更。
 *
 * [source] 是本次变更的载体，是 [before] 与 [after] 发生这样前后变化的舞台。
 * 举个例子，对于一种"用户名称变更事件"，用户即为载体 [source], [before] 则为变更前的名称，[after] 则为变更后的名称。
 *
 *
 * [before] 与 [after] 作为变更状态前后的两个瞬态，应当伴随事件并作为属性直接提供。
 *
 * 当然，一般情况下，根据事件语义，如果目标尚未发生改变，[after] 应当为非真实内容，[before] **可能是瞬时** 状态。
 * 如果变更已经发生，则 [after] 应当是当前状态（可能是瞬时），而 [before] 则为历史态。
 *
 * 变更前后的两个瞬态均无法准确定义其是否可空，因此对于可空情况由实现者自行约束。
 *
 *
 * @see ChangedEvent
 */
@BaseEvent
public interface ChangeEvent : Event {
    override val id: ID


    /**
     * 变更载体，或者说变更内容的源。
     *
     * [source] 代表了当前的变更事件中，变更内容所发生的地方。
     * 比如说，用户名变更，变更内容（[before], [after]）就是用户名，
     * 而变更源（[source]）就是这个用户。
     *
     */
    @JSTP
    public suspend fun source(): Any
    
    /**
     * 变更行为前的内容。
     */
    @JSTP
    public suspend fun before(): Any?


    /**
     * 变更行为后的内容。
     */
    @JSTP
    public suspend fun after(): Any?


    public companion object Key : BaseEventKey<ChangeEvent>("api.change") {
        override fun safeCast(value: Any): ChangeEvent? = doSafeCast(value)
    }
}

/**
 * 一个 **变更** 事件，代表一个已经变更结束的事件。
 * [ChangedEvent] 是 [ChangeEvent] 事件的 **事实** 描述，
 * 一般来讲代表了一个**已经**发生变更的事件。
 *
 * [source] 仍然为变更内容的载体，[before] 代表一个变更之前的瞬时状态， [after] 则代表为一个变更后的状态。
 *
 *
 * @see ChangeEvent
 * @author ForteScarlet
 */
@BaseEvent
public interface ChangedEvent : ChangeEvent {
    /**
     * 此事件所代表的变更发生的时间。
     */
    public val changedTime: Timestamp

    /**
     * 通常情况下，事件时间就相当于其[变更时][changedTime]的时间。
     */
    override val timestamp: Timestamp get() = changedTime

    public companion object Key : BaseEventKey<ChangedEvent>("api.changed", ChangeEvent) {
        override fun safeCast(value: Any): ChangedEvent? =
            doSafeCast(value)

    }
}
