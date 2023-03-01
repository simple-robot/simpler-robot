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

package love.forte.simbot.core.application

import love.forte.simbot.Attribute
import love.forte.simbot.application.Application
import love.forte.simbot.attribute
import love.forte.simbot.core.scope.SimpleScope
import love.forte.simbot.core.scope.SimpleScope.globalOrNull
import love.forte.simbot.event.EventProcessingContext

/**
 * 提供与 [Application][love.forte.simbot.application.Application] 相关的约定属性。
 */
public object ApplicationAttributes {
    // 使用 object 提供"命名空间"
    
    /**
     * [Application] 的属性名。
     */
    public const val APPLICATION_NAME: String = "\$simple-application$"
    
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
    
    /**
     * 尝试从 [EventProcessingContext] 的全局属性中获取 [Application]。
     *
     * 因为 [ApplicationAttributes.Application] 属性在simbot标准库的默认实现中均达成了约定，因此如果不使用第三方实现的话，
     * 理论上是可以保证能够得到 [Application] 的。
     *
     * 如果因为以外情况而导致 [EventProcessingContext.application] 无法取到 [Application] 的值，则会抛出 [NoSuchElementException] 异常。
     * 此情况在使用simbot标准库中所提供的各个实现中均不应会出现。
     *
     * @throws NoSuchElementException 当属性不存在时
     */
    @JvmStatic
    public val EventProcessingContext.application: Application
        get() = applicationOrNull
            ?: throw NoSuchElementException("""ApplicationAttributes.Application("$APPLICATION_NAME")""")
    
    
    /**
     * 尝试从 [EventProcessingContext] 的全局属性中获取 [Application]。
     *
     * 因为 [ApplicationAttributes.Application] 属性在simbot标准库的默认实现中均达成了约定，因此如果不使用第三方实现的话，
     * 理论上是可以保证能够得到 [Application] 的。
     *
     * 如果因为以外情况而导致 [EventProcessingContext.application] 无法取到 [Application] 的值，则会得到 null。
     * 此情况在使用simbot标准库中所提供的各个实现中均不应会出现。
     *
     */
    @JvmStatic
    public val EventProcessingContext.applicationOrNull: Application?
        get() = globalOrNull?.get(Application)
    
    
}


