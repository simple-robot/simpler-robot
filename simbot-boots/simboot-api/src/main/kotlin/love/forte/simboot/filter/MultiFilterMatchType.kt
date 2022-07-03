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