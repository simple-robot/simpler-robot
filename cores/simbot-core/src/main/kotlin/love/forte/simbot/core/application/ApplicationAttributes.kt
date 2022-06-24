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

import love.forte.simbot.Attribute
import love.forte.simbot.application.Application
import love.forte.simbot.attribute
import love.forte.simbot.core.scope.SimpleScope
import love.forte.simbot.event.EventProcessingContext

/**
 * 提供与 [Application][love.forte.simbot.application.Application] 相关的约定属性。
 */
public object ApplicationAttributes {
    // 使用 object 提供"命名空间"
    
    /**
     * [Application] 的属性名。
     */
    private const val APPLICATION_NAME: String = "\$simple-application$"
    
    /**
     * 获取当前环境下的 [love.forte.simbot.application.Application]。
     * [ApplicationScope][Application] 会置于 [EventProcessingContext] 的 [全局作用域][SimpleScope.Global] 中。
     *
     * ```kotlin
     *  fun getGlobalAttribute(context: EventProcessingContext) {
     *      val application = context[SimpleScope.Global]?.get(Application) ?: error("not support")
     *  }
     * ```
     *
     * simbot所提供的默认实现中都会保证将 [love.forte.simbot.application.Application] 置于对应的全局作用域中,
     * 可参考
     * [SimpleApplication]、
     * [BootApplication][love.forte.simboot.core.application.BootApplication]、
     * [SpringBootApplication][love.forte.simboot.spring.autoconfigure.application.SpringBootApplication]
     * 的相关实现。
     *
     */
    @JvmField
    public val Application: Attribute<Application> = attribute(APPLICATION_NAME)
}


