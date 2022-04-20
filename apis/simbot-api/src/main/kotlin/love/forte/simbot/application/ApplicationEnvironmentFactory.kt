/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
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
 */

package love.forte.simbot.application

import love.forte.simbot.Component
import love.forte.simbot.application.Application.Environment
import love.forte.simbot.event.EventListenerManager


/**
 * 用于构建 [Application.Environment] 的工厂。
 *
 * @author ForteScarlet
 */
public interface ApplicationEnvironmentFactory<
        CBuilder : ComponentsBuilder,
        MConfig : Any,
        BMBuilder : BotManagersBuilder,
        out Env : Environment,
        AppBuilder : ApplicationEnvironmentBuilder<CBuilder, MConfig, BMBuilder, Env>,
        > {

    public fun create(configurator: AppBuilder.() -> Unit): Env

}


/**
 * [Environment] 的构建器.
 * @param CBuilder 组件构建器的实例。
 */
public interface ApplicationEnvironmentBuilder<
        CBuilder : ComponentsBuilder,
        MConfig : Any,
        BMBuilder : BotManagersBuilder,
        out Env : Environment,
        > {

    /**
     * 配置组件注册信息。
     */
    @ComponentBuildDsl
    public fun components(componentsFactory: ComponentsFactory<CBuilder>, configurator: CBuilder.() -> Unit = {})


    /**
     * 配置事件处理器的构建。
     */
    @EventListenerFactoryDsl
    public fun listenerManager(
        components: List<Component>,
        factory: EventListenerManagerFactory<*, MConfig>,
        configurator: MConfig.() -> Unit = {},
    )

    /**
     * 配置bot管理器的构建。
     */
    @BotManagersDsl
    public fun botManagers(
        components: List<Component>,
        eventListenerManager: EventListenerManager,
        factory: BotManagersFactory<BMBuilder>,
        configurator: BMBuilder.() -> Unit,
    ) // TODO


    public fun build(): Env


}
