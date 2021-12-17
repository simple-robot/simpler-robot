/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
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
     * 需要无成功
     */
    NONE({ t, r -> r.none { it(t) } }),

    ;

    public suspend fun match(
        target: EventListenerProcessingContext,
        rule: Collection<suspend (target: EventListenerProcessingContext) -> Boolean>
    ): Boolean = matcher(target, rule)

}