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
import love.forte.simbot.Components.find
import love.forte.simbot.Components.get
import love.forte.simbot.definition.Container
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
 * @see Components
 */
@Serializable
@Suppress("MemberVisibilityCanBePrivate")
public sealed class Component(public open val id: String) {

    /**
     * 获取一个属性。
     */
    public abstract operator fun get(propertyKey: String): String?

    /**
     * 获得属性列表。
     */
    public abstract fun properties(): Map<String, String>

    /**
     * 直接使用 === 进行比较。
     */
    override fun equals(other: Any?): Boolean = this === other


    override fun hashCode(): Int = id.hashCode()


    public interface Attribute<V> { // TODO
        public val name: String
    }
}


/**
 * 由simbot实现的唯一顶层组件，一部分可能由simbot自身提供的通用内容会使用此组件。
 * simbot的顶层组件一般代表可以通用的组件，也应当是唯一一个允许组件交叉的组件。
 */
@Serializable
@SerialName("simbotComponent")
public object SimbotComponent : Component("simbot") {
    override fun get(propertyKey: String): String? = null
    override fun properties(): Map<String, String> = emptyMap()
    override fun toString(): String = "Component(id=simbot)"
    override fun hashCode(): Int = 0
}

/**
 * 两个 [Component] 是否相似。
 * 即其中一方为 [SimbotComponent], 或者二者相等。
 */
public infix fun Component.like(other: Component): Boolean = this === SimbotComponent || other === SimbotComponent || this === other


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
    private val comps: MutableMap<String, Component> = ConcurrentHashMap()

    init {
        comps[SimbotComponent.id] = SimbotComponent
    }


    /**
     * 创建一个对应 [id] 的 [Component] 并记录。如果 [Component] 已经存在，则抛出 [ComponentAlreadyExistsException].
     *
     * 可以提供一个 [properties] 参数集作为当前组件的参数列表。[properties] 将会被直接被使用，不会进行任何装饰。
     * 因此如果你不希望他后续能够被修改，请自行进行处理，比如在JVM平台下使用 `Collections.unmodifiableMap` 等。当然，如果你希望它日后能够被修改，同理。
     *
     *
     * @throws ComponentAlreadyExistsException 如果组件已经存在
     */
    public fun create(id: String, properties: Map<String, String> = emptyMap()): Component {
        return comps.compute(id) { k, old ->
            if (old != null) {
                throw ComponentAlreadyExistsException("$k: $old")
            }
            Comp(k, properties)
        }!!


    }

    /**
     * 得到一个对应 [id] 的 [Component] 实例，如果不存在则抛出 [NoSuchComponentException].
     *
     * @throws NoSuchComponentException 如果没有对应的 component
     */
    public operator fun get(id: String): Component = find(id) ?: throw NoSuchComponentException(id)


    /**
     * 寻找对应 [id] 的 [Component] 实例，如果不存在则返回null。
     *
     */
    public fun find(id: String): Component? = comps[id]


    @SerialName("component")
    @Serializable
    internal data class Comp(@SerialName("comp_id") override val id: String, private val properties: Map<String, String>) : Component(id) {
        @Suppress("UNCHECKED_CAST")
        override fun get(propertyKey: String): String? = properties[propertyKey]
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int {
            return id.hashCode()
        }
        override fun properties(): Map<String, String> = properties.toMap()
    }
}


/**
 * 寻找对应的 [Component], 如果不存在，创建一个。
 *
 */
public inline fun Components.resolve(id: String, properties: () -> Map<String, String> = { emptyMap() }): Component {
    return find(id) ?: create(id, properties())
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
public infix fun ComponentContainer.sameComponentWith(other: ComponentContainer): Boolean = component === other.component






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