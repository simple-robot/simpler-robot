/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.quantcat.common.binder

import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.common.PriorityConstant
import kotlin.reflect.KFunction


/**
 *
 * JVM 平台中 `BaseParameterBinderFactory` 工厂类型的全平台兼容父类型。
 *
 * 工厂所需的 `KParameter` 仅支持 JVM 平台，因此只能在全平台中提供一个抽象的基础父类信息。
 *
 * 详细说明参考 [ParameterBinderFactory][love.forte.simbot.quantcat.common.binder.ParameterBinderFactory]
 *
 * @see love.forte.simbot.quantcat.common.binder.ParameterBinderFactory
 *
 * @author ForteScarlet
 */
@InternalSimbotAPI
public interface BaseParameterBinderFactory<C : BaseParameterBinderFactory.Context> {

    /**
     * 工厂优先级.
     */
    public val priority: Int get() = PriorityConstant.NORMAL

    /**
     * 根据 [Context] 提供的各项参数进行解析与预变异，并得到一个最终的 [ParameterBinder] 到对应的parameter中。
     * 如果返回 [ParameterBinderResult.Empty] ，则视为放弃对目标参数的匹配。
     *
     * 返回值最终会被整合，并按照 [ParameterBinderResult.priority] 的顺序作为此binder的执行顺序。
     *
     * 在监听函数被执行时将会通过解析的 [ParameterBinder] 对参数进行注入，
     * 会依次执行对应的binder取第一个执行成功的.
     *
     */
    public fun resolveToBinder(context: C): ParameterBinderResult


    /**
     * [BaseParameterBinderFactory] 进行参数处理时的可用参数内容，
     * 在 JVM 平台实现并额外提供有关 `KParameter` 的信息。
     *
     * 详细信息参考 [ParameterBinderFactory.Context][love.forte.simbot.quantcat.common.binder.ParameterBinderFactory.Context]
     *
     * @see love.forte.simbot.quantcat.common.binder.ParameterBinderFactory.Context
     */
    public interface Context {
        /**
         * 目标监听函数所对应的函数体。
         */
        public val source: KFunction<*>
    }
}
