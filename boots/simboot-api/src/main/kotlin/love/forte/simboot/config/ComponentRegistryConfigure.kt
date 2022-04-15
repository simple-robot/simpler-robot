package love.forte.simboot.config

import love.forte.simbot.event.EventListenerManagerConfiguration

/**
 * 提供用于针对组件的注册配置类。
 *
 * @author ForteScarlet
 */
public abstract class ComponentRegistryConfigure {

    /**
     * 提供 [EventListenerManagerConfiguration] 并注册组件。
     */
    public abstract fun registerComponent(eventListenerManagerConfiguration: EventListenerManagerConfiguration)
}