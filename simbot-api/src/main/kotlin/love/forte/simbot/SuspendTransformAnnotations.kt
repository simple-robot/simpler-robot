/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */


package love.forte.simbot

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking


/**
 * 用于代表同时标记 [@JvmBlocking][JvmBlocking] 和 [@JvmAsync][JvmAsync] 的整合性注解。
 *
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
public annotation class JvmSuspendTrans(
    val blockingBaseName: String = "",
    val blockingSuffix: String = "Blocking",
    val blockingAsProperty: Boolean = false,
    
    val asyncBaseName: String = "",
    val asyncSuffix: String = "Async",
    val asyncAsProperty: Boolean = false,
)

/**
 * **J**vm **S**uspend **T**rans
 * @see JvmSuspendTrans
 */
@Suppress("SpellCheckingInspection")
internal typealias JST = JvmSuspendTrans


/**
 * 用于代表同时标记 [@JvmBlocking][JvmBlocking] 和 [@JvmAsync][JvmAsync] 的整合性注解。
 *
 * [JvmSuspendTransProperty] 默认转化为属性类型，且 blocking 的转化默认没有后缀。
 *
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
public annotation class JvmSuspendTransProperty(
    val blockingBaseName: String = "",
    val blockingSuffix: String = "", // nothing
    val blockingAsProperty: Boolean = true,
    
    val asyncBaseName: String = "",
    val asyncSuffix: String = "Async",
    val asyncAsProperty: Boolean = true,
)

/**
 * **J**vm **S**uspend **T**rans **P**roperty.
 * @see JvmSuspendTransProperty
 */
@Suppress("SpellCheckingInspection")
internal typealias JSTP = JvmSuspendTransProperty
