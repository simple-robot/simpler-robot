package love.forte.simbot.api.message

import love.forte.simbot.api.Component
import love.forte.simbot.api.ComponentContainer
import love.forte.simbot.api.SimbotComponent


/**
 * 消息。
 *
 * 消息的可能性是无限的。在有多种类型的消息的情况下，
 * 有些消息可能只能独立发送、有些消息需要组合发送，而有些消息既能组合，也能独立。
 * 同时，有时候可能在组合消息中，会存在多种类型之间的相互依赖或排斥。
 *
 * 考虑如下场景：
 * 有三种消息：文本、图片、视频。
 * 1. 文本能够与图片共存，文本能够与视频共存，但图片无法与视频共存。
 * 2. 三者均能够与其他内容一起发送，或单独发送。
 * 3. 三者均无法组合发送。
 *
 *
 *
 *
 * @see Messages
 * @see AbsoluteMessage
 */
public sealed interface Message : ComponentContainer {

    /**
     * 每个消息，都有一个所属的组件。组件之间不应出现消息交叉。
     */
    override val component: Component

    /**
     * 消息类型的唯一表示标识。
     *
     * 一般由伴生对象或对象实现。
     *
     */
    public interface Key<M : AbsoluteMessage> : ComponentContainer {
        /**
         * 任何消息都应由某个组件所提供。
         * 在检测冲突的前提是组件应当一致。
         */
        override val component: Component


        /**
         * 用于判断是否与检测目标的key相冲突。
         * 此函数不检测 [component], 通过顶层函数 [checkConflict] 来在冲突检测之前检测组件。
         *
         */
        public infix fun conflict(key: Key<*>): Boolean


        /**
         * 解决冲突的函数。
         *
         */
        public fun solve(current: @UnsafeVariance M, other: @UnsafeVariance M) : SolveStatus = SolveStatus.Overwrite

    }

}


/**
 * 冲突检测，并在冲突检测之前检测组件。
 * 如果 [allowSimbotComponent] 为 `true`, 则双方至少一个的组件为 [SimbotComponent]时, 视为相同组件。
 */
public fun Message.Key<*>.checkConflict(target: Message.Key<*>, allowSimbotComponent: Boolean = false): Boolean {
    val otherComponent = target.component
    val componentCheck = if (allowSimbotComponent) {
        if (component === SimbotComponent || otherComponent === SimbotComponent) {
            true
        } else {
            component == otherComponent
        }
    } else {
        component == otherComponent
    }

    if (!componentCheck) {
        return false
    }

    return conflict(target)
}


/**
 * [Message.Key] 的基础抽象类。
 *
 *
 */
public abstract class BaseMessageKey<M : AbsoluteMessage> : Message.Key<M> {
    /**
     * 如果 [equals] == `true`, 即代表冲突。
     */
    override fun conflict(key: Message.Key<*>): Boolean = this == key
}

/**
 * [Message.Key] 抽象类之一，指定冲突目标。
 *
 */
public abstract class TargetConflictMessageKey<M : AbsoluteMessage>(
    private val conflictTargets: Set<Message.Key<*>>,
) : Message.Key<M> {
    public constructor(vararg conflictTargets: Message.Key<*>) : this(conflictTargets.toSet())

    override fun conflict(key: Message.Key<*>): Boolean {
        return this != key && key !in conflictTargets
    }
}


/**
 * 一个独立的消息。此接口代表一个消息单元，可用于拼接至 [Messages] 中。
 *
 * 消息是否需要自排序（固定顺序与无序消息）
 *
 */
public sealed interface AbsoluteMessage : Message {
    public val key: Message.Key<*>
}










