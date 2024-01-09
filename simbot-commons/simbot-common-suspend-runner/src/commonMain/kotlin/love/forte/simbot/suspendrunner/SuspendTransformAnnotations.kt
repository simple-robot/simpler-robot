/*
 *     Copyright (c) 2023-2024. ForteScarlet.
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

@file:Suppress("KDocUnresolvedReference")

package love.forte.simbot.suspendrunner


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
public annotation class SuspendTrans(
    val blockingBaseName: String = "",
    val blockingSuffix: String = "Blocking",
    val blockingAsProperty: Boolean = false,

    val asyncBaseName: String = "",
    val asyncSuffix: String = "Async",
    val asyncAsProperty: Boolean = false,

    val reserveBaseName: String = "",
    val reserveSuffix: String = "Reserve",
    val reserveAsProperty: Boolean = false,
)

/**
 *  **S**uspend **T**rans 的简写类型。
 *
 * @see SuspendTrans
 */
@Suppress("SpellCheckingInspection")
public typealias ST = SuspendTrans


/**
 * 用于代表同时标记
 * [@JvmBlocking][love.forte.plugin.suspendtrans.annotation.JvmBlocking] 和
 * [@JvmAsync][love.forte.plugin.suspendtrans.annotation.JvmAsync]
 * 的整合性注解。
 *
 * [SuspendTransProperty] 默认转化为属性类型，且 blocking 的转化默认没有后缀。
 *
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
public annotation class SuspendTransProperty(
    // for Java
    val blockingBaseName: String = "",
    val blockingSuffix: String = "", // nothing
    val blockingAsProperty: Boolean = true,

    val asyncBaseName: String = "",
    val asyncSuffix: String = "Async",
    val asyncAsProperty: Boolean = true,

    val reserveBaseName: String = "",
    val reserveSuffix: String = "Reserve",
    val reserveAsProperty: Boolean = false,

    // for JS
    val jsPromiseBaseName: String = "",
    val jsPromiseSuffix: String = "Async",
    val jsPromiseAsProperty: Boolean = false,
)

/**
 * **S**uspend **T**rans **P**roperty 的简写类型。
 *
 * @see SuspendTransProperty
 */
@Suppress("SpellCheckingInspection")
public typealias STP = SuspendTransProperty

