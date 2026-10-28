/*
 *     Copyright (c) 2022-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

package love.forte.simbot.qguild.common


/**
 * 一个仅服务于Java的API。对于Kotlin来讲通常有更优选择。
 */
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@RequiresOptIn("API marked for Java use, not recommended for Kotlin.")
@Deprecated(
    "Use Api4J instead",
    ReplaceWith(
        "Api4J",
        imports = ["love.forte.simbot.annotations.Api4J"]
    )
)
public annotation class QGApi4J

/**
 * 一个仅服务于JS的API。对于Kotlin来讲通常有更优选择。
 */
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@RequiresOptIn("API marked for JS use, not recommended for Kotlin.")
@Deprecated(
    "Use Api4Js instead",
    ReplaceWith(
        "Api4Js",
        imports = ["love.forte.simbot.annotations.Api4Js"]
    )
)
public annotation class QGApi4JS

// 好吧可能 OptIn 的 annotation 不能用 actual

/**
 * 一个内部使用的API。
 */
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@RequiresOptIn("Internal API")
public annotation class QGInternalApi

/**
 * A auto-generated API.
 */
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class Generated

/**
 * 一个**实验性**的与媒体资源相关的API，可能在未来发生变更或被移除。
 *
 * @since 4.1.1
 */
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@RequiresOptIn(
    "一个实验性的与媒体资源相关的API，可能在未来发生变更或被移除。",
)
public annotation class ExperimentalQGMediaApi

/**
 * 标记一个仅供QQ组件内部实现的接口或抽象类，对外部实现不保证任何兼容性或稳定性
 *
 * @since 5.0
 */
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn("仅供QQ组件内部实现的接口或抽象类，对外部实现不保证任何兼容性或稳定性")
@MustBeDocumented
public annotation class QGInternalInheritanceApi

/**
 * 标记一个不稳定的 QQ 组件第三方 API 属性，用于表示某些可能是来自猜测、实际 API 调用后人工核查而来，
 * 并且并未出现在官方文档中，因此而不稳定的属性。
 *
 * @since 5.0
 */
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    "标记一个不稳定的 QQ 组件第三方 API 属性，用于表示某些可能是来自猜测、实际 API 调用后人工核查而来，" +
        "并且并未出现在官方文档中，因此而不稳定的属性。",
)
@MustBeDocumented
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.VALUE_PARAMETER
)
public annotation class QGUnstableProperty


