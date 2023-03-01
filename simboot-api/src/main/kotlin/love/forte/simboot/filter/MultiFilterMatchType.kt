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

package love.forte.simboot.filter

import love.forte.simbot.event.EventListenerProcessingContext


/**
 *
 * 多值匹配，当可能存在多轮匹配时进行的取值策略。
 *
 * @author ForteScarlet
 */
public enum class MultiFilterMatchType(private val matcher: suspend (EventListenerProcessingContext, Collection<suspend (target: EventListenerProcessingContext) -> Boolean>) -> Boolean) {
    
    /**
     * 任意匹配成功即可
     */
    ANY({ t, r -> r.any { it(t) } }),
    
    /**
     * 需要全部匹配成功
     */
    ALL({ t, r -> r.all { it(t) } }),
    
    /**
     * 需要无匹配内容
     */
    NONE({ t, r -> r.none { it(t) } }),
    
    ;
    
    /**
     * 提供匹配目标和多个匹配函数来进行多值匹配。
     */
    public suspend fun match(
        target: EventListenerProcessingContext,
        rule: Collection<suspend (target: EventListenerProcessingContext) -> Boolean>,
    ): Boolean = matcher(target, rule)
    
}
