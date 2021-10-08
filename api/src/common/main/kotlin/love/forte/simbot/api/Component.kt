package love.forte.simbot.api

import love.forte.simbot.api.exception.SimbotRuntimeException


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

    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int {
        return id.hashCode()
    }
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
    }

    /**
     *
     * @throws ComponentAlreadyException 如果组件已经存在
     */
    public fun create(id: String, properties: Map<String, Any> = emptyMap()): Component {

        return TODO()
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
    public fun find(id: String): Component? = TODO()

}


public inline fun Components.resolve(id: String, propertiesBlock: () -> Map<String, Any> = { emptyMap() }): Component {
    return find(id) ?: create(id, propertiesBlock())
}


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