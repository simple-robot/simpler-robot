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
import love.forte.simbot.ComponentRegistrar
import love.forte.simbot.ExperimentalSimbotApi


/**
 * 组件工厂。
 */
public interface ComponentsFactory<CBuilder : ComponentsBuilder> {

    /**
     * 提供配置函数, 构建所有的组件。
     */
    public fun create(configurator: CBuilder.() -> Unit): List<Component>

}




/**
 * 组件构建器。
 *
 * @see
 */
public interface ComponentsBuilder {

    /**
     * 注册一个组件信息到当前事件管理器中。
     *
     * @param registrar 组件注册器。
     * @param config 配置函数
     */
    public fun <C : Component, Config : Any> install(
        registrar: ComponentRegistrar<C, Config>,
        config: Config.() -> Unit = {}
    )



    /**
     * 尝试注册所有可寻的组件到当前配置中。
     *
     * 此函数需要对应的组件注册器支持 `Java SPI` 加载。
     *
     * @see ComponentRegistrar
     */
    @ExperimentalSimbotApi
    public fun installAll()

    /**
     * 执行构建并得到之前注册的所有组件信息。
     */
    public fun build(): List<Component>

}

/**
 * 用在 [ComponentsBuilder] 的实现上的DSL标记。
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
public annotation class ComponentBuildDsl