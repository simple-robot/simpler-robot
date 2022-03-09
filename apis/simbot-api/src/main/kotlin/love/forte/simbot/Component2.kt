package love.forte.simbot

import love.forte.simbot.definition.*
import love.forte.simbot.event.*


/**
 * 一个组件信息。
 *
 * 一个组件应该有一个[id]作为唯一标识，用于与其他组件进行区分。
 *
 */
public interface Comp : IDContainer


/**
 * 组件注册器。
 *
 * 如果希望组件能够支持 [EventListenerManagerConfiguration.installAll],
 * 则需要保证 [ComponentRegistrar] 为 `object` 类型或者提供无参构造，并通过 `Java SPI`
 * 机制注册其信息。
 *
 * @param C 此组件注册器所最终需要被注册的组件类型。
 * @param Config 此组件注册时的配置类类型
 */
public interface ComponentRegistrar<C : Comp, out Config : Any> {

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
    public fun config(block: Config.() -> Unit): C


    /**
     * 配置完成后的通知函数，也可以在此处对最终的manager进行操作。
     */
    public fun install(component: C, manager: EventListenerManager)


}
