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


package love.forte.simbot.quantcat.common.annotations

import love.forte.simbot.quantcat.common.annotations.Binder.Scope
import love.forte.simbot.quantcat.common.binder.BaseParameterBinderFactory
import kotlin.reflect.KClass

/**
 * 用于注册绑定器的注解。
 * 标记在一个 `ParameterBinderFactory` 类型上，
 * 用于标记其 binder 的部分额外信息。
 */
@Target(AnnotationTarget.CLASS) // AnnotationTarget.FUNCTION
public annotation class Binder(
    /**
     * 注册的binder的id
     */
    val id: String = "",

    /**
     * 注册的binder的作用域
     * @see Scope
     */
    val scope: Scope = Scope.DEFAULT
) {

    /**
     * [Binder] 中的作用域。
     */
    public enum class Scope {

        /**
         * 默认的作用域。会根据不同情况选择一个合适的作用域或应用环境。
         * 一般来讲，当 [Binder.id] 不为空则视为 [SPECIFY]，
         * 否则视为 [GLOBAL]。
         *
         * 默认行为的规则未来可能发生改变。
         */
        DEFAULT,

        /**
         * 仅用于ID指定的作用域。
         *
         * 此作用域下的binder不会自动作用于任何可动态绑定参数的监听函数中，
         * 必须在监听函数上通过 [Binder.id] 指定对应ID后才可生效。
         *
         * 使用此作用域时必须指定 [Binder.id] 且其值不可为空。
         *
         */
        SPECIFY,

        // CURRENT,

        /**
         * 作用于所有的（可动态绑定的）监听函数的作用域，使用此作用域时，[Binder.value] 无效。
         *
         * 只能使用在类级别（实现了 `ParameterBinderFactory` ）的 binder 上, 代表此binder直接作用域所有的可动态绑定参数的监听函数中。
         *
         */
        GLOBAL
    }
}

/**
 * 用于针对个事件处理器或所需处应用某些绑定器的标记注解。
 * 全局范围的绑定器无需应用，自动生效。
 *
 */
@Target(AnnotationTarget.FUNCTION)
public annotation class ApplyBinder(
    /**
     * 需要应用的绑定器的id。参考 [Binder.id]。
     */
    vararg val value: String,

    /**
     * 额外添加的基于类型的 [BaseParameterBinderFactory] 数组。
     * 实际类型必须是 [ParameterBinderFactory][love.forte.simbot.quantcat.common.binder.ParameterBinderFactory].
     *
     * 在部分支持依赖注入的环境下（例如 Spring），会尝试从依赖中获取。当依赖中没有对应类型的结果时会尝试实例化，
     * 此时类型必须是一个具有 **空构造** 的普通类或 `object` 类型。
     */
    @OptIn(love.forte.simbot.annotations.InternalSimbotAPI::class)
    val factories: Array<KClass<out BaseParameterBinderFactory<*>>> = []
)
