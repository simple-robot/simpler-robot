/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.kook.util

import io.ktor.http.*


/**
 * 通过 [URLBuilder] lambda 构建 [Url] 实例。
 */
public inline fun buildUrl(url: Url? = null, builder: URLBuilder.() -> Unit): Url {
    val urlBuilder = url?.let { URLBuilder(it) } ?: URLBuilder()
    return urlBuilder.apply(builder).build()
}

/**
 * 通过 [URLBuilder] lambda 构建 [Url] 实例。
 */
public inline fun buildUrl(urlString: String, builder: URLBuilder.() -> Unit): Url {
    return URLBuilder(urlString).apply(builder).build()
}

/**
 * 使用 [URLBuilder.parameters]
 */
public inline fun URLBuilder.parameters(block: ParametersBuilder.() -> Unit) {
    parameters.apply(block)
}

/**
 * 当 [value] 不为 null 时向 [ParametersBuilder] 中添加 [name] 与 [value]。
 */
public fun ParametersBuilder.appendIfNotNull(name: String, value: String?) {
    value?.also { append(name, it) }
}

/**
 * 当 [value] 不为 null 时向 [ParametersBuilder] 中添加 [name] 与 [value]。
 *
 * @param stringValue [value] 的字符串值。
 *
 */
public inline fun <T> ParametersBuilder.appendIfNotNull(name: String, value: T?, stringValue: (T) -> String) {
    value?.also { append(name, stringValue(it)) }
}

