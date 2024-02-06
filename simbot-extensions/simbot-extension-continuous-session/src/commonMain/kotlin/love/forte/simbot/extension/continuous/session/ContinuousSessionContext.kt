/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.extension.continuous.session

import love.forte.simbot.event.Event
import love.forte.simbot.event.EventResult


public fun interface InSession<T, R> {
    public suspend fun ContinuousSessionReceiver<T, R>.invoke()
}

/**
 *
 * @author ForteScarlet
 */
public interface ContinuousSessionContext<T, R> {

    /**
     * 创建，冲突
     */
    public fun session(key: Any, strategy: ConflictStrategy = ConflictStrategy.FAILURE, inSession: InSession<T, R>): ContinuousSessionProvider<T, R>

    public operator fun get(key: Any): ContinuousSessionProvider<T, R>?

    public fun remove(key: Any): ContinuousSessionProvider<T, R>?

    /**
     * 创建时的冲突策略
     */
    public enum class ConflictStrategy {
        // 报错
        FAILURE,
        // 关闭旧的
        REPLACE,
        // 获取旧的
        OLD // TODO name
    }
}

/**
 * 以事件为中心的 [ContinuousSessionContext] 子类型。
 */
public interface EventContinuousSessionContext : ContinuousSessionContext<Event, EventResult>
