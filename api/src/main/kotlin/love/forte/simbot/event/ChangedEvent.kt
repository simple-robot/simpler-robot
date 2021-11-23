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

package love.forte.simbot.event

import love.forte.simbot.message.doSafeCast

/**
 * 一个与 **变更** 有关的事件。
 *
 * 这个变更可能是正在变更、计划变更，或者已经变更。
 *
 * [F] 与 [T] 作为变更状态前后的两个瞬态，应当伴随事件并作为属性直接提供。
 *
 * 变更前后的两个瞬态均无法准确定义其是否可空，因此对于可空情况由实现者自行约束。
 *
 * @param F 变更前的状态。
 * @param T 变更后的状态。
 *
 * @see ChangedEvent
 */
public interface ChangeEvent<F, T> : Event {
    public val before: F
    public val after: T

    public companion object Key : BaseEventKey<ChangeEvent<*, *>>("api-change") {
        override fun safeCast(value: Any): ChangeEvent<*, *>? = doSafeCast(value)
    }
}

/**
 * 一个与 **变更** 有关的事件。
 *
 * 一个[变更事件][ChangedEvent] 描述一个
 *
 * @author ForteScarlet
 */
public interface ChangedEvent<F, T> : ChangeEvent<F, T> {



    public companion object Key : BaseEventKey<ChangedEvent<*, *>>("api-changed", setOf(ChangeEvent)) {
        override fun safeCast(value: Any): ChangedEvent<*, *>? =
            doSafeCast(value)

    }
}