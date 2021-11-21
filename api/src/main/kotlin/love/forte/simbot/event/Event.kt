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

import kotlinx.serialization.Serializable
import love.forte.simbot.Bot
import love.forte.simbot.CharSequenceID
import love.forte.simbot.ID
import love.forte.simbot.definition.BotContainer
import love.forte.simbot.message.doSafeCast

/**
 *
 * [事件][Event] 的顶层接口。
 *
 * 对于任意一个继承此接口的事件类型（包括其他接口或抽象），
 * 其类型中必须存在一个实现了 [Event.Key] 的伴生对象，否则此事件将会被视为 **不可监听**。
 *
 * @author ForteScarlet
 */
public interface Event : BotContainer {
    /**
     * 与这个事件有关系的 [Bot].
     */
    override val bot: Bot

    /**
     * 这个事件的[元数据][Metadata]。
     */
    public val metadata: Metadata

    /**
     * 这个事件客观上的 [可见范围][VisibleScope]。
     */
    public val visibleScope: VisibleScope

    // 考虑事件环境，一般代表这个事件的发生地。例如群聊、私聊、系统消息等事件的根本环境都不同。
    // public val environment: environment

    /**
     * 得到当前事件所对应的类型key。
     */
    public val key: Key<Event>

    /**
     * 所有事件的根类型。
     */
    public companion object Root : Key<Event> {

        /**
         * Event根节点的唯一ID。
         */
        override val id: CharSequenceID = "api-root".ID

        /**
         * Event是所有事件的根，不可能是其他事件的子项.
         */
        override val parents: Set<Key<*>> get() = emptySet()


        override fun safeCast(value: Any): Event? = doSafeCast<Event>(value)
    }

    /**
     * 一个事件类型的Key。所有的计划对外的事件类型都必须通过 **伴生对象** 实现此类型并提供一个事件唯一的ID名称。
     * 在事件调度中，api直接提供基于反射 Class 的事件调度, 因此需要通过 [Key] 来判断事件类型与其之间的继承关系。
     *
     * 事件类型可以继承，且允许多继承，实现方可以通过 [isSubFrom] 来判断当前事件是否为某个类型的子类型。
     *
     * 比如
     * ```kotlin
     * val event: MessageEvent = ...
     * val isSubFrom = MessageEvent.Key isSubFrom Event.Key
     *
     * ```
     *
     */
    public interface Key<E : Event> {
        /**
         * 此事件的ID，需要是唯一的。假若在事件注册时出现了ID相同但不是同一个Key的情况将会导致异常。
         */
        public val id: CharSequenceID

        /**
         * 此事件所继承的所有父事件。
         */
        public val parents: Set<Key<*>>

        /**
         * 将一个提供的类型转化为当前的目标事件。
         * 如果得到null，则说明无法被转化。
         */
        public fun safeCast(value: Any): E?
    }


    /**
     * 事件的 [元数据][Metadata].
     *
     * 事件元数据记录这个事件较为原始的数据，例如其唯一ID、服务器时间等。
     *
     * 元数据中存在什么，完全由事件实现者决定。
     * 但是无论如何，元消息应当存在一个能够决定当前事件唯一性的 [id].
     *
     * 对于两个事件之间是否相同，即使用 [component] 和 [Metadata.id] 进行决定, 当同一个组件下的事件之间的 [Metadata.id] 的 [equals] 得到 `true`，
     * 则认为两个事件相同。
     *
     * [元数据][Metadata]应能够支持[序列化][Serializable].
     */
    public interface Metadata {
        /** 元数据唯一标识。 */
        public val id: ID
    }





    /**
     * 消息事件的可见范围类型。
     *
     */
    public enum class VisibleScope {

        /**
         * 公共的可见范围, 代表这个事件是可能在当前环境下（例如一个组织中）所有人都可见的事件，
         * 不仅是当前bot，可能同样会被其他任何人看到。
         *
         * 此类型的表现形式可以参考例如QQ群。
         *
         */
        PUBLIC,

        /**
         * 内部的可见范围，代表这个事件是可能在当前环境下（例如一个组织中）所有**相关内部人员**都可见的事件。
         * 所谓的相关内部人员，可能指的是有一定特殊条件的人员范围、一定权限的人员范围等。
         *
         * 这样的范围划分有可能是事件提供者所决定的，也有可能是由当前的事件环境所决定的。
         * 不一定必须要在事件环境（例如一个群消息）范围下的部分人可见才能作为内部消息，其从客观角度如果可以理解为 “内部”，那么也可以作为内部消息。
         *
         * 此类型的表现形式可以参考例如一个服务器中的某个只有管理员的频道、钉钉的内部群等等。
         *
         */
        INTERNAL,

        /**
         * 私有的可见范围，代表这个事件理论上来讲，应当只有bot自身和事件来源者能够了解到。
         *
         * 常见表现形式为例如私聊性质的消息事件、好友申请事件等。
         */
        PRIVATE

    }
}


/**
 * 判断当前类型是否为提供类型的子类型。
 *
 */
public infix fun Event.Key<*>.isSubFrom(parentMaybe: Event.Key<*>): Boolean {
    if (parentMaybe === Event) return true
    if (parentMaybe in parents) return true
    if (parents.isEmpty()) return false
    return parents.any {
        it isSubFrom parentMaybe
    }
}

/**
 * [Event.Key] 的基础抽象类，当一个事件仅来自于一个父级事件的时候可以使用此抽象类。
 */
public abstract class BaseEventKey<E : Event>(
    idValue: String,
    override val parents: Set<Event.Key<*>> = emptySet(),
) : Event.Key<E> {
    override val id: CharSequenceID= idValue.ID
}

