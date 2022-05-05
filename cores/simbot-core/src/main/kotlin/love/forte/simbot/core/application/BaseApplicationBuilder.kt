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

package love.forte.simbot.core.application

import love.forte.simbot.*
import love.forte.simbot.ability.CompletionPerceivable
import love.forte.simbot.application.*
import love.forte.simbot.application.BotRegistrar
import love.forte.simbot.event.EventProcessor
import java.util.concurrent.ConcurrentLinkedQueue


/**
 *
 * 实现 [ApplicationBuilder] 并实现通用的组件与提供者的配置。
 *
 * @author ForteScarlet
 */
public abstract class BaseApplicationBuilder<A : Application> : ApplicationBuilder<A> {
    private val componentConfigurations = mutableMapOf<Attribute<*>, Any.() -> Unit>()
    private val componentFactories = mutableMapOf<Attribute<*>, () -> Component>()
    private val botRegisters = mutableListOf<BotRegistrar.() -> Unit>()

    /**
     * 事件提供者配置。
     */
    private val eventProviderConfigurations =
        mutableMapOf<Attribute<*>, Any.() -> Unit>()

    /**
     * 事件提供者工厂。
     */
    private val eventProviderFactories =
        mutableMapOf<Attribute<*>, (EventProcessor, List<Component>, ApplicationConfiguration) -> EventProvider>()

    /**
     * 注册一个 [组件][Component].
     */
    override fun <C : Component, Config : Any> install(
        componentFactory: ComponentFactory<C, Config>,
        configurator: Config.(perceivable: CompletionPerceivable<A>) -> Unit,
    ) {
        val key = componentFactory.key
        val newConfig: Any.() -> Unit = newConfigurator(key, eventProviderConfigurations, configurator)
        eventProviderConfigurations[key] = newConfig

        if (key in componentFactories) return

        componentFactories[key] = {
            val configuration = componentConfigurations[key]!!
            componentFactory.create(configuration)
        }
    }


    /**
     * 注册一个 [事件提供者][EventProvider].
     */
    override fun <P : EventProvider, Config : Any> install(
        eventProviderFactory: EventProviderFactory<P, Config>,
        configurator: Config.(perceivable: CompletionPerceivable<A>) -> Unit,
    ) {
        val key = eventProviderFactory.key
        val newConfig: Any.() -> Unit = newConfigurator(key, eventProviderConfigurations, configurator)
        eventProviderConfigurations[key] = newConfig

        if (key in eventProviderFactories) return

        eventProviderFactories[key] = { eventProcessor, components, applicationConfiguration ->
            val configuration = eventProviderConfigurations[key]!!
            eventProviderFactory.create(eventProcessor, components, applicationConfiguration, configuration)
        }

    }

    /**
     * 添加一个bot注册函数。
     */
    override fun bots(registrar: BotRegistrar.() -> Unit) {
        botRegisters.add(registrar)
    }


    private fun <Config : Any> newConfigurator(
        key: Attribute<*>,
        configurations: Map<Attribute<*>, Any.() -> Unit>,
        configurator: Config.(builder: ApplicationBuilder<A>) -> Unit,
    ): (Any.() -> Unit) {
        val oldConfig = configurations[key]
        @Suppress("UNCHECKED_CAST")
        return if (oldConfig != null) {
            {
                oldConfig.invoke(this)
                (this as Config).configurator(this@BaseApplicationBuilder)
            }
        } else {
            {
                (this as Config).configurator(this@BaseApplicationBuilder)
            }
        }
    }


    protected fun buildComponents(): List<Component> {
        return componentFactories.values.map { it() }
    }


    protected fun buildProviders(
        eventProcessor: EventProcessor,
        components: List<Component>,
        applicationConfiguration: ApplicationConfiguration
    ): List<EventProvider> {
        return eventProviderFactories.values.map { it(eventProcessor, components, applicationConfiguration) }
    }

    /**
     * 提供botManager列表并执行它们的注册逻辑。
     *
     * @return 被注册的bot数量
     */
    protected fun registerBots(botManagers: List<love.forte.simbot.BotRegistrar>): List<Bot> {
        val registrar = BotRegistrarImpl(botManagers)
        botRegisters.forEach {
            it(registrar)
        }
        return registrar.bots
    }


    /**
     * 当构建完成时统一执行的函数列表。
     */
    private val onCompletions = ConcurrentLinkedQueue<(A) -> Unit>()


    override fun onCompletion(handle: (application: A) -> Unit) {
        onCompletions.add(handle)
    }


    /**
     * 当 [Application] 构建完毕，则执行此函数来执行所有的 `onCompletion` 回调函数。
     */
    protected fun complete(application: A) {
        onCompletions.forEach { it(application) }
    }


    private class BotRegistrarImpl(private val registrars: List<love.forte.simbot.BotRegistrar>) : BotRegistrar {
        val bots = mutableListOf<Bot>()

        override fun register(botVerifyInfo: BotVerifyInfo): Bot? {
            logger.info("Registering bot with verify info [{}]", botVerifyInfo)
            for (manager in registrars) {
                try {
                    return manager.register(botVerifyInfo).also { bot ->
                        logger.debug(
                            "Bot verify info [{}] is registered as [{}] via manager [{}]",
                            botVerifyInfo,
                            bot,
                            manager
                        )
                        bots.add(bot)
                    }
                } catch (ignore: ComponentMismatchException) {
                    logger.debug("Bot verify info [{}] is not matched by manager {}, try next.", botVerifyInfo, manager)
                    // ignore this.
                }
            }
            return null
        }

        override fun toString(): String {
            return "BotRegistrarImpl(registrars=$registrars)"
        }

        private companion object {
            private val logger =
                LoggerFactory.getLogger("love.forte.simbot.core.application.BaseApplicationBuilder.BotRegistrarImpl")
        }
    }
}