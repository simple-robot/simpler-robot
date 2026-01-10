/*
 * Copyright (c) 2022-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild


/**
 * 一个仅服务于Java的API。对于Kotlin来讲通常有更优选择。
 */
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@RequiresOptIn("API marked for Java use, not recommended for Kotlin.", level = RequiresOptIn.Level.WARNING)
public annotation class QGApi4J

/**
 * 一个仅服务于JS的API。对于Kotlin来讲通常有更优选择。
 */
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@RequiresOptIn("API marked for JS use, not recommended for Kotlin.", level = RequiresOptIn.Level.WARNING)
public annotation class QGApi4JS

// 好吧可能 OptIn 的 annotation 不能用 actual

/**
 * 一个内部使用的API。
 */
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@RequiresOptIn("Internal API", level = RequiresOptIn.Level.WARNING)
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
    level = RequiresOptIn.Level.WARNING
)
public annotation class ExperimentalQGMediaApi
