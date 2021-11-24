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

package love.forte.simbot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import love.forte.simbot.Components.find
import love.forte.simbot.Components.get
import love.forte.simbot.definition.Container
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set


/**
 * 组件标识。
 * 此标记用于向全局注册一个组件.
 *
 * 组件仅能够从 [Components.create] 处进行创建，其[ID][id]必须唯一，
 *
 * [Component] 的 [equals] 将会直接进行 `===` 匹配, 因此需要保证组件实例唯一性，开发者在 [Components] 中注册的时候，需要保证组件标识的ID唯一不变。
 *
 * [Component] 的序列化不会对 [properties] 进行序列化.
 *
 * @see Components
 */
@Serializable
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
    public abstract fun properties(): AttributeMap

    /**
     * 直接使用 === 进行比较。
     */
    override fun equals(other: Any?): Boolean = this === other

    /**
     * 目前组件没有潜逃关系。唯一的嵌套关系为 [SimbotComponent] 包含所有的组件。
     */
    override fun contains(scope: Scope): Boolean = false

    override fun hashCode(): Int = id.hashCode()


}


/**
 * 由simbot实现的唯一顶层组件，一部分可能由simbot自身提供的通用内容会使用此组件。
 * simbot的顶层组件一般代表可以通用的组件，也应当是唯一一个允许组件交叉的组件。
 */
@Serializable
@SerialName("root")
public object SimbotComponent : Component() {
    override val id: CharSequenceID = "simbot".ID
    override val name: String get() = "simbot"
    override fun <T : Any> get(attribute: Attribute<T>): T? = null
    override fun properties(): AttributeMap = AttributeMap.Empty // TODO include metadata
    override fun toString(): String = "Component(id=simbot)"
    override fun hashCode(): Int = 0
    override fun contains(scope: Scope): Boolean = true
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
 */
public object Components {
    private val comps: MutableMap<ID, Component> = ConcurrentHashMap<ID, Component>().also {
        it[SimbotComponent.id] = SimbotComponent
    }

    init {
        ServiceLoader.load(ComponentRegistrar::class.java).forEach { r ->
            val conf = ComponentConfiguration()
            r.registerComponent(conf)
            val comp = create(conf.id, conf.name) // properties todo
            r.setComponent(comp)
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
    ): Component {
        return comps.compute(id) { k, old ->
            if (old != null) {
                throw ComponentAlreadyExistsException("$k: $old")
            }
            Comp(k.toCharSequenceID(), name)
        }!!
    }

    internal fun create(id: String, name: String = id): Component {
        return create(id.ID, name)
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


    @SerialName("c")
    @Serializable
    internal data class Comp(
        override val id: CharSequenceID,
        override val name: String,
    ) : Component() {

        @Transient
        private val properties = AttributeHashMap()

        @Suppress("UNCHECKED_CAST")
        override fun <T : Any> get(attribute: Attribute<T>): T? = properties[attribute]
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int {
            return id.hashCode()
        }

        override fun properties(): AttributeHashMap = properties
    }
}

/**
 * 通过 Java SPI 注册一个组件。
 */
public interface ComponentRegistrar {
    /**
     * 提供组件注册信息。
     */
    public fun registerComponent(configuration: ComponentConfiguration)

    /**
     * 得到注册后的组件实例。
     */
    public fun setComponent(component: Component)
}


/**
 * 组件注册器，通过 [ComponentConfiguration] 向此注册器提供信息并最终注册为一个组件信息。
 */
public class ComponentConfiguration {
    /**
     * 组件ID
     */
    public lateinit var id: CharSequenceID
    public fun setId(id: String) {
        this.id = id.ID
    }

    /**
     * 组件名称
     */
    public lateinit var name: String
}


//
// /**
//  * 寻找对应的 [Component], 如果不存在，创建一个。
//  *
//  */
// public inline fun Components.resolve(
//     id: CharSequenceID,
//     name: String = id.toString(),
//     properties: () -> Map<String, String> = { emptyMap() }
// ): Component {
//     return find(id) ?: create(id, name, properties())
// }
//
// /**
//  * 寻找对应的 [Component], 如果不存在，创建一个。
//  *
//  */
// public inline fun Components.resolve(
//     id: String,
//     name: String = id,
//     properties: () -> Map<String, String> = { emptyMap() }
// ): Component {
//     return find(id) ?: create(id, name, properties())
// }


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