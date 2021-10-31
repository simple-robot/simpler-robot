package love.forte.simbot.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.StringFormat
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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
     */
    public sealed interface Element<E : Element<E>> : Message, ComponentContainer {
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
         */
        public fun case(instance: Any): E?

        /**
         * 检测是否对某一类型相互排斥。
         *
         * 当被拼接到消息列中的时候，会与其他以存元素的key进行比较。如果存在冲突，则抛弃原有消息链。
         *
         */
        public fun isReject(otherKeys: Set<Key<*>>): Boolean

    }

}


/**
 * [Message.Key] 基础抽象类。
 */
public abstract class AbstractKey<E : Element<E>>(
    override val component: Component,
    private val rejects: Set<Message.Key<*>> = emptySet(),
    private val caseFunc: (Any) -> E?,
) : Message.Key<E> {
    override fun isReject(otherKeys: Set<Message.Key<*>>): Boolean = otherKeys.any { k -> k in rejects }
    override fun case(instance: Any): E? = caseFunc(instance)

}









