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
 *
 */


package love.forte.simbot.event

import love.forte.simbot.*
import love.forte.simbot.definition.BotContainer
import love.forte.simbot.definition.IDContainer
import love.forte.simbot.event.Event.Key.Companion.getKey
import love.forte.simbot.event.Event.Key.Companion.isSub
import love.forte.simbot.message.doSafeCast
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.safeCast

/**
 * ## 事件类型
 *
 * 所有 [事件][Event] 的顶层接口。
 *
 * ## Key
 * 在事件处理的时候，将不会根据其 `class` 进行直接的类型关系判断来决定事件监听，而是根据 [Event.key][Event.Key] 进行事件之间的继承关系的判断。
 * 对于任意一个继承此接口的事件类型（包括其他接口或抽象），
 * 其类型中必须存在一个实现了 [Event.Key] 的伴生对象或者通过 [EventKey] 注解指定 [Event.Key] 的实现，否则此事件将会被视为 **不可监听**。
 *
 *
 * ## 泛型事件类型
 *
 * 所有能够监听的事件中，**不建议**监听带有泛型信息的事件类型（例如 [ChangedEvent]）,
 * 虽然它们允许被监听，但是它们大多数都代表对其他事件的类型约束。
 *
 * 并且，在进行事件监听的时候，事件类型的判断 **不支持** 泛型判断，因此如果你需要监听这些携带泛型的事件类型，
 * 那么你必须使用kotlin中的 `*`，在Java中使用 `?` 或直接忽略它。否则会很容易导致出现异常。
 *
 *
 *
 *
 * @see Event.Key
 * @author ForteScarlet
 */
public interface Event : BotContainer, IDContainer, ComponentContainer {

    /**
     * 事件的唯一标识。
     */
    override val id: ID

    /**
     * 与这个事件有关系的 [Bot].
     */
    override val bot: Bot

    /**
     * 一个事件所属的组件。
     * 通常与 [bot] 的组件所属一致。
     */
    override val component: Component
        get() = bot.component

    /**
     * 此时间发生的时间戳。
     *
     * 如果相关组件支持，则为对应时间，如果不支持则一般为构建时的瞬时时间戳。
     */
    public val timestamp: Timestamp

    /**
     * 这个事件客观上的 [可见范围][VisibleScope]。
     *
     * 此属性意义不大，未来可能会移除。
     *
     */
    @Suppress("DEPRECATION")
    @Deprecated("此属性意义不大，未来可能会移除。", ReplaceWith("VisibleScope.PUBLIC", "love.forte.simbot.event.Event.VisibleScope"))
    public val visibleScope: VisibleScope get() = VisibleScope.PUBLIC


    /**
     * 得到当前事件所对应的类型key。
     */
    public val key: Key<out Event>

    /**
     * 所有事件的根类型。
     */
    public companion object Root : Key<Event> {

        @Suppress("MemberVisibilityCanBePrivate")
        public const val ID_VALUE: String = "api.root"

        /**
         * Event根节点的唯一ID。
         */
        override val id: CharSequenceID = ID_VALUE.ID

        /**
         * Event是所有事件的根，不可能是其他事件的子项.
         */
        override val parents: Set<Key<*>> get() = emptySet()


        override fun safeCast(value: Any): Event? = doSafeCast<Event>(value)

        override fun toString(): String {
            return "RootEvent(id=$ID_VALUE)"
        }
    }

    /**
     * 一个事件类型的Key。所有的计划对外的事件类型都必须通过 **伴生对象** 实现此类型并提供一个事件**唯一**的ID名称。
     * 并非所有的事件接口 [Event] 的类型都能够允许被监听，有些事件类型可能仅用做标记或者由于其他原因无法/不允许被**直接**监听，
     * 因此在事件调度中，需要通过 [Key] 来判断事件类型与其之间的继承关系。
     *
     * 所有事件的 [Key.id] 必须尽可能保证唯一，因此建议对ID进行命名的时候使用较为特殊的命名方式以杜绝出现ID重复。
     * id重复不一定会出现异常提示，但是在使用 [isSub] 等方法的时候，很有可能会出现缓存内容混乱进而导致引发预期外的异常。
     *
     * 事件类型可以继承，且允许多继承，实现方可以通过 [isSub] 来判断当前事件是否为某个类型的子类型。
     *
     * 比如
     * ```kotlin
     * val event: MessageEvent = ...
     * val isSub: Boolean = MessageEvent.Key isSub Event.Key
     * ```
     * [Key] 的继承关系是单向传递的，因此你能够通过一个key找到它继承的所有父类型，但是无法反向查找。

     *
     * 当一个事件提供伴生Key的时候，[E] 建议且应当与当前事件类型**一致**, 因为在 [Key.getKey] 等通过类型获取Key的场景下，均默认为 [Key] 的类型与当前事件类型一致。
     * 如下所示：
     * ```kotlin
     * interface MyEvent : Event {
     *      companion object Key: Event.Key<MyEvent> {
     *          // ...
     *      }
     * }
     * ```
     *
     *
     *
     * @see getKey
     * @see EventKey
     * @see isSub
     */
    public interface Key<E : Event> {
        /**
         * 此事件的ID，需要是唯一的。假若在事件注册时出现了ID相同但不是同一个Key的情况将会导致异常。
         */
        public val id: CharSequenceID

        /**
         * 此事件所继承的所有父事件。
         * 此属性应当是不可变的，不应在运行期内发生变更。
         */
        public val parents: Set<Key<*>>

        /**
         * 将一个提供的类型转化为当前的目标事件。
         * 如果得到null，则说明无法被转化。
         */
        public fun safeCast(value: Any): E?


        public companion object {
            private val keyCache = ConcurrentHashMap<KClass<*>, Key<*>>()
            private val subCache = ConcurrentHashMap<String, ConcurrentSkipListSet<String>>()
            private val notSubCache = ConcurrentHashMap<String, ConcurrentSkipListSet<String>>()

            /**
             * 检测当前接收器是否为 [from] 的子类型。
             */
            @JvmStatic
            public infix fun Key<*>.isSub(from: Key<*>): Boolean {
                val target = this
                if (from === Event) return true
                if (from == target) return true
                if (from in target.parents) return true

                val tid = target.id.literal
                val fid = from.id.literal
                if (subCache.computeIfAbsent(tid) { ConcurrentSkipListSet() }.contains(fid)) {
                    return true
                }
                if (notSubCache.computeIfAbsent(tid) { ConcurrentSkipListSet() }.contains(fid)) {
                    return false
                }


                val isSub = target.parents.any {
                    it isSub from
                }
                if (isSub) {
                    subCache.computeIfAbsent(tid) { ConcurrentSkipListSet() }.add(fid)
                } else {
                    notSubCache.computeIfAbsent(tid) { ConcurrentSkipListSet() }.add(fid)
                }
                return isSub
            }

            /**
             * 尝试通过一个 [Event] 的 [KClass] 来得到一个其对应的 [Key].
             */
            @JvmSynthetic
            @OptIn(Api4J::class)
            public fun <E : Event> getKey(type: KClass<E>): Key<E> {
                val cached = keyCache[type]
                @Suppress("UNCHECKED_CAST")
                if (cached != null) return cached as Key<E>

                // companion try
                val companionObject = type.companionObjectInstance

                @Suppress("UNCHECKED_CAST")
                if (companionObject != null && companionObject is Key<*>) return companionObject as Key<E>



                @Suppress("UNCHECKED_CAST")
                return keyCache.computeIfAbsent(type) { k ->
                    // find EventKey annotation
                    k.findAnnotation<EventKey>()?.toKey<E>()
                        ?: throw NoSuchEventKeyDefineException("Unable to find event key in [$type] by companion object or @EventKey annotation")
                } as Key<E>
            }

            @Api4J
            public fun <T : Event> getKey(type: Class<T>): Key<T> = getKey(type.kotlin)


            @JvmSynthetic
            public inline fun <reified T : Event> getKey(): Key<T> = getKey(T::class)

        }

    }


    /**
     * 消息事件的可见范围类型。
     *
     * _Deprecated: 含义不明确，缺少应用场景，未来可能会移除_
     */
    @Deprecated("Ambiguous meaning, lack of application scenarios, may be removed in the future")
    public enum class VisibleScope {

        /**
         * 公共的可见范围, 代表这个事件是可能在当前环境下（例如一个组织中）所有人都可见的事件，
         * 不仅是当前bot，可能同样会被其他任何人看到。
         *
         * 此类型的表现形式可以参考例如群聊、公开频道等。
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
         * 此类型的表现形式可以参考例如一个服务器中的某个只有管理员的频道、只有管理员能够看得到的申请等等。
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
 *
 * 通过注解标记一个 [Event] 类型所对应的 [Event.Key] 数据.
 *
 * 用于在无法实现伴生对象的情况下（例如Java）提供 [Event.Key] 信息.
 *
 * 此注解没有"继承"的特性，不可嵌套。
 *
 * 使用方式如：
 * ```java
 *  @EventKey(id = "example.test_event", type = MyTestEvent4J.class, parents = { MessageEvent.class, MessageEvent.class })
 *  public interface MyTestEvent4J extends MessageEvent, ChannelEvent {
 *  }
 * ```
 *
 * @param id 此事件的 [Event.Key.id]
 * @param type 被标记事件的类型
 * @param parents 此事件的 [Event.Key.parents]
 *
 * @see Event.Key
 */
@Api4J
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
public annotation class EventKey(
    val id: String,
    val type: KClass<out Event>,
    val parents: Array<KClass<out Event>>,
)

/**
 * [EventKey] to [Event.Key].
 *
 */
@Suppress("UNCHECKED_CAST", "RemoveRedundantQualifierName")
@OptIn(Api4J::class)
private fun <T : Event> EventKey.toKey(): Event.Key<T> =
    AnnotationEventKey(
        id,
        type as KClass<T>, //
        parents.mapNotNull { it.takeIf { t -> t != type }?.let { t -> Event.Key.getKey(t) } }.toSet()
    )


private class AnnotationEventKey<T : Event>(
    idValue: String,
    private val type: KClass<T>,
    override val parents: Set<Event.Key<*>>,
) : Event.Key<T> {
    override val id: CharSequenceID = idValue.ID
    override fun safeCast(value: Any): T? = type.safeCast(value)
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Event.Key<*>) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}


/**
 * 通过 [KClass] 获取其对应的 [Event.Key].
 */
@JvmSynthetic
@Suppress("RemoveRedundantQualifierName")
public fun <T : Event> KClass<T>.getKey(): Event.Key<T> = Event.Key.getKey(this)


/**
 * 判断当前类型是否为提供类型的子类型。
 *
 */
@Deprecated("Just use '... isSub ...'",
    ReplaceWith("this isSub parentMaybe", "love.forte.simbot.event.Event.Key.Companion.isSub"))
public infix fun Event.Key<*>.isSubFrom(parentMaybe: Event.Key<*>): Boolean {
    return this isSub parentMaybe
}


/**
 * 判断当前类型是否为提供类型的子类型。
 *
 */
public operator fun Event.Key<*>.contains(parentIdMaybe: String): Boolean {
    if (id.literal == parentIdMaybe) return true
    return parents.any { parents -> parentIdMaybe in parents }
}

/**
 * 判断当前类型是否为提供类型的子类型。
 *
 */
public operator fun Event.Key<*>.contains(parentIdMaybe: ID): Boolean {
    if (id == parentIdMaybe) return true
    return parents.any { parents -> parentIdMaybe in parents }
}

/**
 * [Event.Key] 的基础抽象类，当一个事件仅来自于一个父级事件的时候可以使用此抽象类。
 */
public abstract class BaseEventKey<E : Event>(
    idValue: String,
    override val parents: Set<Event.Key<*>> = emptySet(),
) : Event.Key<E> {
    public constructor(idValue: String, vararg parents: Event.Key<*>) : this(idValue, parents.toSet())

    override val id: CharSequenceID = idValue.ID
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Event.Key<*>) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String = "EventKey(id=$id)"
}


/////


/**
 * 没有定义 [Event.Key] 异常。
 *
 */
public class NoSuchEventKeyDefineException internal constructor(message: String?) : SimbotIllegalStateException(message)