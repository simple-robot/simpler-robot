package love.forte.simbot.api

import love.forte.simbot.api.definition.Container
import love.forte.simbot.api.exception.SimbotRuntimeException
import love.forte.simbot.api.utils.*


/**
 * 组件标识。
 * 此标记用于向全局注册一个组件.
 *
 * 向 [Components] 中注册一个组件, 其[ID][id]必须唯一，且仅能够从 [Components.create] 处进行创建。
 *
 * [Component] 的 [equals] 将会直接进行 `===` 匹配, 因此需要保证组件实例唯一性，开发者在 [Components] 中注册的时候，需要保证组件标识的ID唯一不变。
 *
 * @see Components
 */
@Suppress("MemberVisibilityCanBePrivate")
public sealed class Component(public val id: String) {

    /**
     * 获取一个属性。
     */
    public abstract operator fun <T> get(propertyKey: String): T?

    /**
     * 获得属性列表。
     */
    public abstract fun properties(): Map<String, Any>
}


/**
 * 由内部实现的顶层组件，一部分可能由simbot自身提供的通用内容会使用此组件。
 */
public object SimbotComponent : Component("simbot") {
    override fun <T> get(propertyKey: String): T? = null
    override fun properties(): Map<String, Any> = emptyMap()
}


/**
 *
 * 全局统一的 Component 管理器, 应当保证组件唯一。
 *
 */
public object Components {
    internal class Comp(id: String, private val properties: Map<String, Any>) : Component(id) {
        @Suppress("UNCHECKED_CAST")
        override fun <T> get(propertyKey: String): T? = properties[propertyKey] as? T

        override fun equals(other: Any?): Boolean = this === other

        override fun hashCode(): Int {
            return id.hashCode()
        }

        override fun properties(): Map<String, Any> = properties.toMap()
    }

    private val comps: MutableMap<String, Component> = concurrentMap()
    init {
        comps[SimbotComponent.id] = SimbotComponent
    }


    /**
     *
     * @throws ComponentAlreadyException 如果组件已经存在
     */
    public fun create(id: String, properties: Map<String, Any> = emptyMap()): Component {
        return comps.compute(id) { k, old ->
            if (old != null) {
                throw ComponentAlreadyException("$k: $old")
            }
            Comp(k, properties)
        }!!


    }

    /**
     *
     * @throws NoSuchComponentException 如果没有对应的 component
     */
    public operator fun get(id: String): Component = find(id) ?: throw NoSuchComponentException(id)


    /**
     *
     *
     */
    public fun find(id: String): Component? = comps[id]

}


public inline fun Components.resolve(id: String, propertiesBlock: () -> Map<String, Any> = { emptyMap() }): Component {
    return find(id) ?: create(id, propertiesBlock())
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


public class ComponentAlreadyException : SimbotRuntimeException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}