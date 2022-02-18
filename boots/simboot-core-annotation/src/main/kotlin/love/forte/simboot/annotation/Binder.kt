/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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
 *
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
 * @param id 当 [scope] 为 [Scope.SPECIFY] 时，指定对应ID. 指定id时，如果不是在 [Listener] 上，则值应当有且只有一个。
 * @param scope binder作用域。
 *
 * @see SpecifyBinder
 * @see CurrentBinder
 * @see GlobalBinder
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
public annotation class Binder(
    val id: Array<String> = [],
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
@Binder(scope = Scope.SPECIFY)
public annotation class SpecifyBinder(
    @get:AliasFor(annotation = Binder::class, attribute = "id")
    @get:AnnotationMapper.Property(target = Binder::class, value = "id")
    val value: Array<String>
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