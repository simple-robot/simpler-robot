package love.forte.simbot

import kotlinx.serialization.modules.*
import love.forte.simbot.definition.*
import love.forte.simbot.event.*


/**
 * 一个组件信息。
 *
 * 一个组件应该有一个[id]作为唯一标识，用于与其他组件进行区分。
 *
 */
public interface Component : IDContainer {

    /**
     * 此组件所提供的所有消息序列化信息。
     *
     * 除了通过 [Component.componentSerializersModule] 获取之外，
     * 通常情况下组件的实现会提供其他的全局方式来或者这些序列化信息。
     *
     */
    public val componentSerializersModule: SerializersModule


}


/**
 * 用于支持 [EventListenerManagerConfiguration.installAll] 进行自动加载的工厂类型定义，
 * 实现 [registrar] 并返回一个 [ComponentRegistrar] 实例。
 *
 * 此类型的实现必须存在无参构造。
 */
public interface ComponentRegistrarFactory<C : Component, out Config : Any> {

    /**
     * 得到 [ComponentRegistrar] 实例。
     */
    public val registrar: ComponentRegistrar<C, Config>
}


/**
 * 组件注册器。
 *
 * 如果希望组件能够支持 [EventListenerManagerConfiguration.installAll],
 * 则需要提供 [ComponentRegistrarFactory] 用于通过 `Java SPI` 机制注册 [ComponentRegistrar] 信息。
 *
 * 当提供 配置类型[Config]的时候，应尽可能保持其所有内容均存在默认值。
 *
 * @param C 此组件注册器所最终需要被注册的组件类型。
 * @param Config 此组件注册时的配置类类型。
 */
public interface ComponentRegistrar<C : Component, out Config : Any> {

    /**
     * 用于在通过 [EventListenerManager] 注册组件的时候进行唯一标记使用。
     *
     * 例如:
     * ```kotlin
     * key = attribute(COMPONENT_ID)
     * ```
     *
     * 建议将 [key] 作为固定常量使用。
     *
     */
    public val key: Attribute<C>

    /**
     * 提供注册函数，得到对应的组件实例。
     */
    public fun register(block: Config.() -> Unit): C

}


/**
 * 一个组件的容器, 标记其实现需要存在一个 [组件][component] 实例。
 *
 */
public interface ComponentContainer : Container {
    /**
     * 获取当前内容所属组件。
     */
    public val component: Component
}


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

