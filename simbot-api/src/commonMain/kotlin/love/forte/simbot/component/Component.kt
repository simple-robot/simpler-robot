/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.component

import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.application.ApplicationConfiguration
import love.forte.simbot.application.ApplicationEventRegistrar
import love.forte.simbot.bot.SerializableBotConfiguration
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.MergeableFactoriesConfigurator
import love.forte.simbot.common.function.MergeableFactory

/**
 * 一个 **组件**。
 *
 * 组件是针对某个平台的bot能力实现的标识单位。
 *
 * 在构建 [Application][love.forte.simbot.application.Application] 的过程中，
 * [Component] 是事件调度器之后最先被加载处理的内容。
 *
 * @author ForteScarlet
 */
public interface Component {
    /**
     * 一个组件的ID。
     * 组件id建议使用类似于Java包路径的格式，
     * 例如 `org.example.Sample` 并尽量避免重复。
     */
    public val id: String

    /**
     * 组件对外提供的统合所有所需的序列化信息。
     * 通常为 message 类型的序列化或文件配置类的序列化信息。
     */
    public val serializersModule: SerializersModule

    public companion object {
        /**
         * 建议使用的多组件多态序列化的 `classDiscriminator`。
         * 主要使用在配合 [SerializableBotConfiguration] 进行反序列化的情况下。
         *
         */
        public const val CLASS_DISCRIMINATOR: String = "component"

    }
}

/**
 * [Component] 的工厂函数，用于配置并预构建 [Component] 实例。
 *
 * @see Component
 * @param COM 目标组件类型
 * @param CONF 配置类型。配置类型应是一个可变类，以便于在 DSL 中进行动态配置。
 */
public interface ComponentFactory<COM : Component, CONF : Any> :
    MergeableFactory<ComponentFactory.Key, COM, CONF, ComponentConfigureContext> {
    /**
     * 用于 [ComponentFactory] 在内部整合时的标识类型。
     *
     * 更多说明参阅 [MergeableFactory.Key]。
     *
     * @see ComponentFactory.key
     * @see MergeableFactory.key
     */
    public interface Key : MergeableFactory.Key
}

/**
 * 一个 [Component] 的安装器接口，
 * 提供用于安装 [Component] 的能力。
 *
 */
public interface ComponentInstaller {
    /**
     * 注册安装一个组件类型，并为其添加对应的配置。
     */
    public fun <COM : Component, CONF : Any> install(
        componentFactory: ComponentFactory<COM, CONF>, configurer: ConfigurerFunction<CONF>
    )

    /**
     * 注册安装一个组件类型。
     */
    public fun <COM : Component, CONF : Any> install(componentFactory: ComponentFactory<COM, CONF>) {
        install(componentFactory) {}
    }
}


/**
 * 提供给 [ComponentFactoriesConfigurator] 用于配置 [Component] 的上下文信息。
 * 可以得到来自 [Application][love.forte.simbot.application.Application] 的初始化配置信息。
 */
public interface ComponentConfigureContext {
    /**
     * 构建 Application 的配置信息
     */
    public val applicationConfiguration: ApplicationConfiguration

    /**
     * Application 的阶段事件注册器。
     *
     */
    public val applicationEventRegistrar: ApplicationEventRegistrar
}


/**
 * 用于对 [ComponentFactory] 进行聚合组装的配置器。
 */
public class ComponentFactoriesConfigurator(
    configurators: Map<ComponentFactory.Key, ConfigurerFunction<Any>> = emptyMap(),
    factories: Map<ComponentFactory.Key, (ComponentConfigureContext) -> Component> = emptyMap(),
) : MergeableFactoriesConfigurator<ComponentConfigureContext, Component, ComponentFactory.Key>(configurators, factories)


// region Exceptions

/**
 * Component exception.
 */
public open class ComponentException : RuntimeException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * 没有符合条件的 Component 时
 */
public class NoSuchComponentException : ComponentException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * Component 已经存在时
 */
public class ComponentAlreadyExistsException : ComponentException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}
// endregion
