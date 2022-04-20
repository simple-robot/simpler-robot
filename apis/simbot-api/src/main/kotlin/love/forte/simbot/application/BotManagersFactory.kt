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
import love.forte.simbot.event.EventListenerManager
import java.io.InputStream


/**
 *
 * bot管理器配置。
 *
 * @author ForteScarlet
 */
public interface BotManagersFactory<Builder : BotManagersBuilder> {

    // TODO

    public fun create(configurator: Builder.() -> Unit): List<BotManager<*>>


}


public interface BotManagersBuilder {


    /**
     * 注册一个组件信息到当前事件管理器中。
     *
     * @param registrar 组件注册器。
     * @param config 配置函数
     */
    public fun <B : Bot, M : BotManager<B>, Config : BotManagerRegistrar.Configuration> install(
        registrar: BotManagerRegistrar<B, M, Config>,
        config: Config.() -> Unit = {},
    )

    /**
     * 构建并得到已安装的所有botManger。
     */
    public fun build(): List<BotManager<*>>


}


public interface BotManagerRegistrar<B : Bot, M : BotManager<B>, Config : BotManagerRegistrar.Configuration> {

    /**
     * 用于在通过 [BotManagersBuilder] 注册组件的时候进行唯一标记使用。
     *
     * 建议将 [key] 作为固定常量使用。
     */
    public val key: Attribute<M>

    /**
     * 提供注册函数，得到对应的组件实例。
     */
    public fun register(components: List<Component>, eventListenerManager: EventListenerManager, block: Config.() -> Unit): M


    /**
     * [BotManagerRegistrar] 的基础配置类类型。
     */
    public interface Configuration {

        /**
         * 预注册一个外部的Bot信息。
         */
        public fun register(type: BotInfoSupportType, inputStream: InputStream)

    }
}


@DslMarker
@Retention(AnnotationRetention.BINARY)
public annotation class BotManagersDsl