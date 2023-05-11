/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

@file:JvmName("EventProviders")

package love.forte.simbot.application

import love.forte.simbot.Attribute
import love.forte.simbot.Component
import love.forte.simbot.ComponentAutoRegistrarFactory
import love.forte.simbot.ability.Survivable
import love.forte.simbot.bot.BotManager
import love.forte.simbot.event.EventProcessor
import java.util.*


/**
 * 事件提供者。
 *
 * 用于安装在 [Application] 中，通过 [EventProviderFactory] 向其提供一个 [事件处理器][EventProvider],
 * 使其能够向目标事件处理器提供(推送)事件。
 *
 * 事件提供者无所谓形式，可以是一个 [BotManager], 或是一个定时任务、一个http服务, 或者其他任何什么。
 *
 * @see BotManager
 *
 */
public interface EventProvider : Survivable


/**
 * [EventProvider] 工厂，用于在 [Application] 的过程中构建 [EventProvider].
 *
 * @author ForteScarlet
 */
public interface EventProviderFactory<P : EventProvider, Config : Any> {

    /**
     * 此工厂的唯一属性。
     */
    public val key: Attribute<P>

    /**
     * 提供所需属性，构建一个 [EventProvider].
     */
    public suspend fun create(
        eventProcessor: EventProcessor,
        components: List<Component>,
        applicationConfiguration: ApplicationConfiguration,
        configurator: Config.() -> Unit,
    ): P

}


/**
 * 实现自动注册的配置类。通过 Java SPI 机制加载。
 */
public interface EventProviderAutoRegistrarFactory<P : EventProvider, Config : Any> {

    /**
     * 得到 [EventProviderFactory] 实例。
     */
    public val registrar: EventProviderFactory<P, Config>

}


/**
 * 尝试加载所有的 [ComponentAutoRegistrarFactory] 并注册到 [ApplicationBuilder] 中。
 */
@ApplicationBuilderDsl
public fun <A : Application> ApplicationBuilder<A>.installAllEventProviders(classLoader: ClassLoader = EventProviderAutoRegistrarFactory::class.java.classLoader) {
    val factories = ServiceLoader.load(EventProviderAutoRegistrarFactory::class.java, classLoader)
    factories.forEach {
        install(it.registrar)
    }
}

