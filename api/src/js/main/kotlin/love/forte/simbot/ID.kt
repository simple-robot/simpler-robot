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

package love.forte.simbot

/**
 * JS平台下的无效实现。
 * 将会直接抛出 [NoSuchIDTypeException].
 */
@Suppress("FunctionName")
public actual fun <N: Number> N.ID(): ArbitraryNumericalID<N>  = throw NoSuchIDTypeException(this::class.toString())


//
// /**
//  * JS平台下无 [ArbitraryNumericalID] 实例。
//  */
// @Suppress("CanBeParameter")
// @SerialName("ID.N.A")
// @Serializable
// public actual sealed class ArbitraryNumericalID<N : Number> : NumericalID<N>()