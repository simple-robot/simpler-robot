/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.annotation

import love.forte.annotationtool.AnnotationMapper
import love.forte.simboot.annotation.Binder.Scope
import org.springframework.core.annotation.AliasFor

/**
 * 标记在一个函数上。假如这个函数不存在 [Listener],
 * 则说明这个函数是一个 [love.forte.simboot.listener.ParameterBinderFactory] 的函数体，
 * 此时函数有且只能有一个参数 [love.forte.simboot.listener.ParameterBinderFactory.Context],
 * 且返回值必须为 [love.forte.simboot.listener.ParameterBinderResult].
 *
 * 如果标记的函数上同样存在 [Listener], 则代表将指定ID的binder应用于当前监听函数。此时作用域只能为 [Binder.Scope.SPECIFY] 且必须指定所需id。
 *
 * @param value ID标识。当 [scope] 为 [Scope.SPECIFY] 时，指定对应ID. 指定id时，如果不是在 [Listener] 上，则值应当有且只有一个。
 * @param scope binder作用域。
 *
 * @see SpecifyBinder
 * @see CurrentBinder
 * @see GlobalBinder
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
public annotation class Binder(
    val value: Array<String> = [],
    val scope: Scope = Scope.DEFAULT,
) {
    
    /**
     * [Binder] 中的作用域。
     */
    public enum class Scope {
        
        /**
         * 默认的作用域。
         *
         * 当使用此类型的时候，应尝试根据获取的位置来自动填充一个默认的作用域。
         * - 当标记在一个同时存在 [Listener] 注解的函数上的时候，相当于使用了 [SPECIFY].
         * - 当标记在一个不存在 [Listener] 注解的函数上的时候，相当于使用了 [CURRENT].
         * - 当标记在了一个实现了 [love.forte.simboot.listener.ParameterBinderFactory] 接口的类上的时候，相当于使用了 [GLOBAL].
         */
        DEFAULT,
        
        /**
         * 仅用于ID指定的作用域。
         *
         * 此作用域下的binder不会自动作用于任何可动态绑定参数的监听函数中，
         * 必须在监听函数上通过 [Binder.value] 指定对应ID后才可生效。
         *
         * 使用此作用域时必须指定 [Binder.value], 如果指定多个值，则取第一个。
         *
         * 标记在监听函数上时必须使用此作用域。
         *
         */
        SPECIFY,
        
        /**
         * 直接作用域当前环境下的作用域，使用此作用域时，[Binder.value] 无效。
         *
         * 只能使用在函数类型的 binder 上，代表此binder应用于当前**类**中所有的listener。
         *
         * 此作用域暂不支持使用在顶层函数上。
         *
         */
        CURRENT,
        
        /**
         * 作用于所有的（可动态绑定的）监听函数的作用域，使用此作用域时，[Binder.value] 无效。
         *
         * 只能使用在类级别（实现了 [love.forte.simboot.listener.ParameterBinderFactory] ）的 binder 上, 代表此binder直接作用域所有的可动态绑定参数的监听函数中。
         *
         */
        GLOBAL
    }
}


public inline fun Binder.scopeIfDefault(block: () -> Scope): Scope =
    with(scope) { if (this == Scope.DEFAULT) block() else this }


/**
 * [Binder.scope] 为 [Binder.Scope.SPECIFY] 的 [Binder].
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Binder(scope = Scope.SPECIFY)
public annotation class SpecifyBinder(
    @get:AliasFor(annotation = Binder::class, attribute = "value")
    @get:AnnotationMapper.Property(target = Binder::class, value = "value")
    val value: Array<String>,
)

/**
 * [Binder.scope] 为 [Binder.Scope.CURRENT] 的 [Binder].
 */
@Target(AnnotationTarget.FUNCTION)
@Binder(scope = Scope.CURRENT)
public annotation class CurrentBinder

/**
 * [Binder.scope] 为 [Binder.Scope.GLOBAL] 的 [Binder].
 */
@Target(AnnotationTarget.CLASS)
@Binder(scope = Scope.GLOBAL)
public annotation class GlobalBinder
