package love.forte.simbot.message

import love.forte.simbot.Component
import love.forte.simbot.ComponentContainer
import love.forte.simbot.SimbotComponent
import love.forte.simbot.message.Message.Element
import kotlin.reflect.KClass


/**
 * 消息。
 *
 *
 * @see Element
 */
public sealed interface Message : ComponentContainer {

    /**
     * 每个消息，都有一个所属的组件。组件之间不应出现消息交叉。
     *
     * @see SimbotComponent
     */
    override val component: Component


    public fun componentEquals(component: Component, simbotCompAsTrue: Boolean = true): Boolean {
        if (simbotCompAsTrue) {
            if (component == SimbotComponent || this.component == SimbotComponent) {
                return true
            }
        }
        return this.component == component
    }


    /**
     * 一个 [消息][Message] 的 [元素][Element], 元素本身也是一种消息。
     *
     * 需尽量保证实现类是可序列化的。
     *
     * @see SingleOnlyMessage 约束一个消息列表中仅只能存在此一种消息元素的消息。
     */
    public interface Element<E : Element<E>> : Message, ComponentContainer {
        public val key: Key<E>
        override val component: Component get() = key.component
        override fun toString(): String
        override fun equals(other: Any?): Boolean
    }

    /**
     * 消息元素类型的唯一表示标识。
     *
     * 一般由伴生对象或对象实现。
     *
     */
    public interface Key<E : Element<E>> : ComponentContainer {
        /**
         * 任何消息都应由某个组件所提供。
         * 在检测冲突的前提是组件应当一致。
         */
        override val component: Component

        /**
         * 得到此元素的 [KClass].
         */
        public val elementType: KClass<E>

        /**
         * 将一个实例转化为 [E] 实例。 无法转化得到null。
         *
         * *Just like JVM KClass::safeCast.*
         */
        public fun safeCast(instance: Any?): E?


    }

}


@Suppress("unused")
public inline fun <reified E : Element<E>> Message.Key<E>.cast(value: Any?): E {
    if (value == null) throw NullPointerException("cast value")
    if (value !is E) throw ClassCastException("Value cannot be cast to ${E::class.qualifiedName ?: E::class.simpleName}")
    return value
}


/**
 * [Message.Key] 基础抽象类。
 */
public abstract class AbstractKey<E : Element<E>>(
    override val component: Component,
    private val castFunc: (Any?) -> E?,
) : Message.Key<E> {
    override fun safeCast(instance: Any?): E? = castFunc(instance)
}


internal inline fun <reified E : Element<E>> doSafeCast(value: Any?): E? = if (value is E) value else null







