/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
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
 * 如果标记的函数上同样存在 [Listener], 则代表将指定ID的binder应用于当前监听函数。此时作用域只能为 [Binder.Scope.SPECIFY] 且必须指定id。
 *
 * @property id 当 [scope] 为 [Scope.SPECIFY] 时，指定对应ID。
 * @property scope binder作用域。
 *
 * @see SpecifyBinder
 * @see CurrentBinder
 * @see GlobalBinder
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
public annotation class Binder(
    val id: String = "",
    val scope: Scope = Scope.SPECIFY
) {

    /**
     * [Binder] 中的作用域。
     */
    public enum class Scope {
        /**
         * 仅用于ID指定的作用域。
         *
         * 此作用域下的binder不会自动作用于任何可动态绑定参数的监听函数中，
         * 必须在监听函数上通过 [Binder.id] 指定对应ID后才可生效。
         *
         * 使用此作用域时必须指定 [Binder.id].
         *
         * 标记在监听函数上时必须使用此作用域。
         *
         */
        SPECIFY,

        /**
         * 直接作用域当前环境下的作用域，使用此作用域时，[Binder.id] 无效。
         *
         * 只能使用在函数类型的 binder 上，代表此binder应用于当前类中所有的listener。
         *
         */
        CURRENT,

        /**
         * 作用于所有的（可动态绑定的）监听函数的作用域，使用此作用域时，[Binder.id] 无效。
         *
         * 只能使用在类级别（实现了 [love.forte.simboot.listener.ParameterBinderFactory] ）的 binder 上, 代表此binder直接作用域所有的可动态绑定参数的监听函数中。
         *
         */
        GLOBAL
    }
}

/**
 * [Binder.scope] 为 [Binder.Scope.SPECIFY] 的 [Binder].
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Binder(scope = Binder.Scope.SPECIFY)
public annotation class SpecifyBinder(
    @get:AliasFor(annotation = Binder::class, attribute = "id")
    @get:AnnotationMapper.Property(target = Binder::class, value = "id")
    val value: String
)

/**
 * [Binder.scope] 为 [Binder.Scope.CURRENT] 的 [Binder].
 */
@Target(AnnotationTarget.FUNCTION)
@Binder(scope = Binder.Scope.CURRENT)
public annotation class CurrentBinder

/**
 * [Binder.scope] 为 [Binder.Scope.GLOBAL] 的 [Binder].
 */
@Target(AnnotationTarget.CLASS)
@Binder(scope = Binder.Scope.GLOBAL)
public annotation class GlobalBinder