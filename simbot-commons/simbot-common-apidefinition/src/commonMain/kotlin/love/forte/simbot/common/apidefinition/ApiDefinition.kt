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

package love.forte.simbot.common.apidefinition

import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.serialization.DeserializationStrategy


/**
 * 一个针对 HTTP API 的基本形式的同于约束定义。
 *
 * [ApiDefinition] 是一种针对某个 HTTP API 的定义，
 * 使其对外提供一个API所需的基本信息。
 *
 * [ApiDefinition] 中的信息主要基于 `kotlinx.serialization` 和 `Ktor`，
 * 但其仅描述信息，不提供功能，因此对其的请求并不一定限于 `Ktor`。
 *
 * ## 不可变
 *
 * [ApiDefinition] 的实现理应是 **不可变** 的。[ApiDefinition] 最终可能产生的请求应当在填入参数构造它的时候就可以确定，
 * 且不可中途变更。
 * [ApiDefinition] 内部也许会存在一些懒加载的属性。
 *
 * ## 构建方式
 *
 * 推荐在实现 [ApiDefinition] 的时候，隐藏其构造方法而使用工厂方法或构建器取而代之，例如：
 *
 * ```kotlin
 * class FooApi private contractor(private val name: String) : ApiDefinition<Foo> {
 *    companion object Factory {
 *        // 提供工厂
 *        fun create(name: String): FooApi = FooApi(name)
 *    }
 *
 *    // ...
 * }
 * ```
 *
 * ## 响应类型
 *
 * 预期响应结果类型 [R] 不应为 null，如果可能响应空内容，则使用 `object` 类型代替，
 * 例如 [Unit]。需要注意的是部分反序列化器可能无法自动将 `null` 解析为 `object`，
 * 因此在实现对 [ApiDefinition] 的请求于解析时可能需要额外的处理。
 *
 * @param R 预期的响应结果
 *
 * @author ForteScarlet
 */
public interface ApiDefinition<out R : Any> {

    /**
     * 此 API 的请求方式。
     */
    public val method: HttpMethod

    /**
     * api的请求目标地址。
     */
    public val url: Url

    /**
     * api 请求时需要携带的 headers。
     * 默认为 [Headers.Empty]。
     */
    public val headers: Headers
        get() = Headers.Empty

    /**
     * 此 API 请求时需要携带的 body。在 [method] 为 [HttpMethod.Post]、[HttpMethod.Put] 的时候可能有值。
     * 可能会是 `Ktor` 的特殊类型，例如 [OutgoingContent]。
     *
     * 除了特殊类型以外，[body] 的类型都应当是可序列化的。至少应支持 `kotlinx.serialization`。
     */
    public val body: Any?

    /**
     * API 响应结果的反序列化器。
     */
    public val resultDeserializationStrategy: DeserializationStrategy<R>
}



