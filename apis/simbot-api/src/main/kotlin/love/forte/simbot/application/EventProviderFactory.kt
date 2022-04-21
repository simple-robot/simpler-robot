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

import love.forte.simbot.*
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.utils.currentClassLoader
import java.util.*


/**
 * 事件提供者。
 *
 * 用于安装在 [Application] 中，通过 [EventProviderFactory] 向其提供一个 [事件处理器][EventProvider],
 * 使其能够向目标事件处理器提供(推送)事件。
 *
 * 事件提供者无所谓形式，可以是一个 [BotManager], 或是一个定时任务、一个http服务。
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
    public fun create(
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
@ApplicationBuildDsl
public fun ApplicationBuilder.installAllEventProviders(classLoader: ClassLoader = this.currentClassLoader) {
    val factories = ServiceLoader.load(EventProviderAutoRegistrarFactory::class.java, classLoader)
    factories.forEach {
        install(it.registrar)
    }

}
//
// internal inline val Any.currentClassLoader: ClassLoader
//     get() =
//         javaClass.classLoader
//             ?: Thread.currentThread().contextClassLoader
//             ?: ClassLoader.getSystemClassLoader()