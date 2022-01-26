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

package love.forte.simbot

import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.Components.find
import love.forte.simbot.Components.get
import love.forte.simbot.definition.Container
import love.forte.simbot.definition.IDContainer
import love.forte.simbot.message.Messages
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Stream
import kotlin.collections.set
import kotlin.streams.asStream


/**
 * 组件标识。
 * 此标记用于向全局注册一个组件.
 *
 * 组件仅能够从 [Components.create] 处进行创建，其[ID][id]必须唯一，
 *
 * [Component] 的 [equals] 将会直接进行 `===` 匹配, 因此需要保证组件实例唯一性，开发者在 [Components] 中注册的时候，需要保证组件标识的ID唯一不变。
 *
 * 如果你想在对比两个 [Component] 的时候，允许其中任意一方为 [SimbotComponent], 那么你需要使用 [Component.like] 而不是 `equals`.
 *
 * @see Components
 * @see Component.like
 * @see ComponentAttributes
 */
@Suppress("MemberVisibilityCanBePrivate")
public sealed class Component : Scope {
    abstract override val id: CharSequenceID
    abstract override val name: String

    /**
     * 获取一个属性。
     */
    public abstract operator fun <T : Any> get(attribute: Attribute<T>): T?

    /**
     * 获得属性列表。
     */
    public abstract fun attributes(): AttributeMap

    /**
     * 根据 [attribute] 获取对应结果。
     */
    public open fun <T : Any> getAttribute(attribute: Attribute<T>): T? = attributes()[attribute]

    /**
     * 直接使用 === 进行比较。
     */
    override fun equals(other: Any?): Boolean = this === other

    /**
     * 目前组件没有嵌套关系。唯一的嵌套关系为 [SimbotComponent] 包含所有的组件。
     */
    override fun contains(scope: Scope): Boolean = false

    /**
     * hashcode. 等同于 [id] 的 hashcode.
     */
    override fun hashCode(): Int = id.hashCode()


}


/**
 * 由simbot实现的唯一顶层组件，一部分可能由simbot自身提供的通用内容会使用此组件。
 * simbot的顶层组件一般代表可以通用的组件，也应当是唯一一个允许组件交叉的组件。
 */
public object SimbotComponent : Component() {
    override val id: CharSequenceID = "simbot".ID
    override val name: String get() = "simbot"
    override fun <T : Any> get(attribute: Attribute<T>): T? = null
    override fun attributes(): AttributeMap = SimbotAttributes
    override fun toString(): String = "SimbotComponent"
    override fun hashCode(): Int = 0
    override fun contains(scope: Scope): Boolean = scope is Component

    private object SimbotAttributes : AttributeMap by AttributeMutableMap(
        mutableMapOf(
            ComponentAttributes.authors to Authors(
                Author(
                    id = "ForteScarlet".ID,
                    name = "ForteScarlet",
                    email = "ForteScarlet@163.com",
                    url = "forte.love",
                    roles = listOf("developer"),
                    timezone = 8
                )
            )
        )
    )

}

/**
 * 两个 [Component] 是否相似。
 * 即其中一方为 [SimbotComponent], 或者二者相等。
 */
public infix fun Component.like(other: Component): Boolean =
    this === SimbotComponent || other === SimbotComponent || this === other


/**
 *
 * 全局统一的 Component 管理器, 是 [Component] 构建实例的唯一合法途径。
 *
 * 通过 [Components.create] 对 [Component] 的实例进行统一管理，通过 [get]、[find] 或 [resolve] 来进行获取。
 *
 *
 * @see Component
 * @see SimbotComponent
 * @see ComponentAttributes
 *
 */
public object Components {
    private val comps: ConcurrentHashMap<ID, Component> = ConcurrentHashMap<ID, Component>().also {
        it[SimbotComponent.id] = SimbotComponent
    }

    init {
        fun toComponent(information: ComponentInformation): Comp {
            val id = information.id.toCharSequenceID()
            val name = information.name

            val attribute = AttributeMutableMap()
            information.configAttributes(attribute)

            val serializerModule = information.messageSerializersModule
            if (serializerModule != null) {
                Messages.mergeSerializersModule(serializerModule)
            }

            return create(id, name, attribute) as Comp
        }

        val registrarList = ServiceLoader.load(ComponentInformationRegistrar::class.java)
        // val waitingQueue = LinkedList<ComponentInformationRegistrar>()

        val waitingQueue = registrarList.toMutableList()
        var lastSize: Int = waitingQueue.size
        while (waitingQueue.isNotEmpty()) {
            val iter = waitingQueue.iterator()
            while (iter.hasNext()) {
                val next = iter.next()
                when (val result = next.informations()) {
                    is ComponentInformationRegistrar.Result.Skip -> {
                        iter.remove()
                    }
                    is ComponentInformationRegistrar.Result.Wait -> continue
                    is ComponentInformationRegistrar.Result.OK -> {
                        for (componentInformation in result.infoList) {
                            val comp = toComponent(componentInformation)
                            componentInformation.setComponent(comp)
                        }
                        iter.remove()
                    }
                }
            }
            val nowSize = waitingQueue.size
            if (nowSize != 0) {
                if (nowSize == lastSize) {
                    // lastTime
                    for (registrar in waitingQueue) {
                        when (val result = registrar.lastTimeInformations()) {
                            is ComponentInformationRegistrar.Result.Skip -> continue
                            is ComponentInformationRegistrar.Result.Wait -> {
                                throw IllegalStateException("The last time register cannot wait.")
                            }
                            is ComponentInformationRegistrar.Result.OK -> {
                                for (componentInformation in result.infoList) {
                                    val comp = toComponent(componentInformation)
                                    componentInformation.setComponent(comp)
                                }
                            }
                        }
                    }
                    break
                }
                lastSize = nowSize
            }
        }
    }


    /**
     * 创建一个对应 [id] 的 [Component] 并记录。如果 [Component] 已经存在，则抛出 [ComponentAlreadyExistsException].
     *
     * @throws ComponentAlreadyExistsException 如果组件已经存在
     */
    internal fun create(
        id: CharSequenceID,
        name: String = id.toString(),
        attributes: AttributeMutableMap
    ): Component {
        return comps.compute(id) { k, old ->
            if (old != null) {
                throw ComponentAlreadyExistsException("$k: $old")
            }
            Comp(k.toCharSequenceID(), name, attributes)
        }!!
    }


    /**
     * 得到一个对应 [id] 的 [Component] 实例，如果不存在则抛出 [NoSuchComponentException].
     *
     * @throws NoSuchComponentException 如果没有对应的 component
     */
    public operator fun get(id: ID): Component = find(id) ?: throw NoSuchComponentException(id.toString())
    public operator fun get(id: String): Component = this[id.ID]


    /**
     * 寻找对应 [id] 的 [Component] 实例，如果不存在则返回null。
     *
     */
    public fun find(id: String): Component? = find(id.ID)

    /**
     * 寻找对应 [id] 的 [Component] 实例，如果不存在则返回null。
     *
     */
    public fun find(id: ID): Component? = comps[id.toCharSequenceID()]


    @Suppress("MemberVisibilityCanBePrivate")
    public val all: Sequence<Component>
        get() = comps.values.asSequence()

    @Api4J
    public fun all(): Stream<Component> = all.asStream()


    internal data class Comp(
        override val id: CharSequenceID,
        override val name: String,
        internal val attributes: AttributeMutableMap
    ) : Component() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : Any> get(attribute: Attribute<T>): T? = attributes[attribute]
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int {
            return id.hashCode()
        }

        override fun attributes(): AttributeMap = attributes
    }
}

/**
 * 向 Component 提供一个或多个组件信息。
 */
public interface ComponentInformationRegistrar {

    /**
     *
     * 返回本次需要注册的组件信息，或者选择跳过/等待。
     *
     * @see Result.ok
     * @see Result.waiting
     * @see Result.skip
     */
    public fun informations(): Result

    /**
     * 如果出现了所有组件注册器在一轮注册时一直在等待的情况，会通过 [lastTimeInformations] 进行最后一轮注册。
     * 如果本轮也返回了 [Result.waiting], 则会抛出异常。
     */
    public fun lastTimeInformations(): Result = informations()


    public sealed class Result {
        public companion object {
            /**
             * 跳过，不再注册。
             */
            @get:JvmName("skip")
            public val skip: Result
                get() = Skip

            /**
             * 需要等待下一轮注册。
             */
            @get:JvmName("waiting")
            public val waiting: Result
                get() = Wait

            /**
             * 完成本次注册。
             */
            public fun ok(infoList: List<ComponentInformation>): Result = OK(infoList)
        }

        internal class OK(val infoList: List<ComponentInformation>) : Result()
        internal object Skip : Result()
        internal object Wait : Result()

    }
}

/**
 * 通过 Java SPI 注册一个组件。
 */
public interface ComponentInformation : IDContainer {

    /**
     * 组件ID。一般建议用类似全限定名称来定义。
     *
     */
    override val id: ID

    /**
     * 组件名称。
     */
    public val name: String

    /**
     * 可以向 [Messages] 提供一个 [SerializersModule],
     * 会将此内容整合到 [Messages.serializersModule] 属性中。
     */
    public val messageSerializersModule: SerializersModule? get() = null

    /**
     * 得到一个 attributs, 并对其进行配置。
     */
    public fun configAttributes(attributes: MutableAttributeMap)


    /**
     * 得到注册后的组件实例。
     */
    public fun setComponent(component: Component)
}


/**
 * 一个组件的容器, 标记其实现需要存在一个 [组件][component] 实例。
 *
 */
public interface ComponentContainer : Container {
    public val component: Component
}

/**
 * 判断两个组件容器之间的组件是否相同。
 *
 */
public infix fun ComponentContainer.sameComponentWith(other: ComponentContainer): Boolean =
    component === other.component


//////////////////////// Exceptions ////////////////////////////


public class NoSuchComponentException : SimbotRuntimeException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}


public class ComponentAlreadyExistsException : SimbotRuntimeException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}






