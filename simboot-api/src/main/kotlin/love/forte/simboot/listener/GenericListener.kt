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

package love.forte.simboot.listener

import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.event.EventResult
import love.forte.simbot.utils.runWithInterruptible

/**
 *
 * 通用监听函数接口，是在 boot-core 下解析生成的各类监听函数的统一标准接口。
 *
 * 所有通过扫描函数而得到的监听函数，其都建议使用可挂起的(`suspend`) 函数作为执行体。
 * 对于那些非可挂起的监听函数（普通函数、Java函数等），函数的执行逻辑 (KFunction.call(...)) 将会在 [runWithInterruptible] 中，默认以 [kotlinx.coroutines.Dispatchers.IO] 作为调度器被执行。
 *
 *
 *
 * @author ForteScarlet
 */
public interface GenericBootEventListener : EventListener {
    override fun isTarget(eventType: Event.Key<*>): Boolean
    override suspend fun match(context: EventListenerProcessingContext): Boolean
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult
}

