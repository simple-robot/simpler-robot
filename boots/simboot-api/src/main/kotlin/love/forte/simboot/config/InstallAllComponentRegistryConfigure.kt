package love.forte.simboot.config

import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.event.EventListenerManagerConfiguration

/**
 *
 * [ComponentRegistryConfigure] 的默认实现，
 * 当依赖环境中不存在任何 [ComponentRegistryConfigure] 的时候使用。
 *
 * @author ForteScarlet
 */
public object InstallAllComponentRegistryConfigure : ComponentRegistryConfigure() {
    @OptIn(ExperimentalSimbotApi::class)
    override fun registerComponent(eventListenerManagerConfiguration: EventListenerManagerConfiguration) {
        eventListenerManagerConfiguration.installAll()
    }
}