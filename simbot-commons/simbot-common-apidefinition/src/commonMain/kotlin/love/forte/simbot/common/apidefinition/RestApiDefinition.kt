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
import kotlin.concurrent.Volatile


/**
 *
 * 基于 [ApiDefinition] 针对常见的4种 `method` 提供扩展实现类型的 [ApiDefinition] 子类型接口。
 *
 * @see ApiDefinition
 *
 * @author ForteScarlet
 */
public abstract class RestApiDefinition<out R : Any> : ApiDefinition<R>

/**
 * 使用 [HttpMethod.Get] 进行请求的 [RestApiDefinition]. `body` 默认为 `null`。
 */
public abstract class GetApiDefinition<out R : Any> : RestApiDefinition<R>() {
    override val method: HttpMethod
        get() = HttpMethod.Get

    override val body: Any?
        get() = null
}

/**
 * 使用 [HttpMethod.Delete] 进行请求的 [RestApiDefinition]. `body` 默认为 `null`。
 */
public abstract class DeleteApiDefinition<out R : Any> : RestApiDefinition<R>() {
    override val method: HttpMethod
        get() = HttpMethod.Delete

    override val body: Any?
        get() = null
}

/**
 * 提供一个抽象方法允许计算并缓存（ [body] 最终值懒计算并唯一）的 [RestApiDefinition] 抽象实现。
 *
 */
public abstract class BodyComputableApiDefinition<out R : Any> : RestApiDefinition<R>() {
    @Volatile
    private lateinit var _body: Any

    /**
     * 用于请求的body实体。
     *
     * [body] 内部懒加载，
     * 但不保证任何时间得到的结果始终如一 （无锁）
     *
     * 可通过重写 [body] 来改变此行为
     *
     */
    override val body: Any?
        get() = if (::_body.isInitialized) _body.takeIf { it !is NULL } else {
            createBody().also {
                _body = it ?: NULL
            }
        }

    /**
     * 用于懒计算 [body] 的构建函数。
     *
     * 可能会被调用多次（因为 [body] 的懒计算是无锁的），
     * 因此请避免在 [createBody] 中使用会产生副作用的逻辑。
     *
     * 可通过重写 [body] 来改变此行为
     */
    protected abstract fun createBody(): Any?

    private object NULL
}

/**
 * 使用 [HttpMethod.Post] 进行请求的 [RestApiDefinition].
 *
 * @see BodyComputableApiDefinition
 */
public abstract class PostApiDefinition<out R : Any> : BodyComputableApiDefinition<R>() {
    override val method: HttpMethod
        get() = HttpMethod.Post
}

/**
 * 使用 [HttpMethod.Put] 进行请求的 [RestApiDefinition].
 *
 * @see BodyComputableApiDefinition
 */
public abstract class PutApiDefinition<out R : Any> : BodyComputableApiDefinition<R>() {
    override val method: HttpMethod
        get() = HttpMethod.Put
}

