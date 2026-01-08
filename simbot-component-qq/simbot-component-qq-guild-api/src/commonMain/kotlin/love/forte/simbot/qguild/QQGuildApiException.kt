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

import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

/**
 * 初始化 cause, 并得到自身（或结果）
 * 在 JVM 平台上生效。
 * 在其他平台会使用 [addSuppressed] 添加 [cause]。
 */
@QGInternalApi
public expect inline fun <reified T : Throwable> T.initCause0(cause: Throwable): T

/**
 * QQ频道API请求过程中出现的异常
 *
 */
@Suppress("MemberVisibilityCanBePrivate")
public open class QQGuildApiException : RuntimeException {
    public val info: ErrInfo?
    public val value: Int
    public val description: String

    public constructor(value: Int, description: String) : this(
        value = value,
        description = description,
        message = "$value: $description"
    )

    public constructor(
        info: ErrInfo?,
        value: Int,
        description: String,
    ) : this(value = value, description = description, message = "$value: $description; response info: $info")

    public constructor(value: Int, description: String, message: String) : super(message) {
        this.info = null
        this.value = value
        this.description = description
    }

    public constructor(
        info: ErrInfo?,
        value: Int,
        description: String,
        message: String = "$value: $description; response info: $info"
    ) : super(message) {
        this.info = info
        this.value = value
        this.description = description
    }
}

/**
 * 当 API 相应结果反序列化失败
 */
public open class QQGuildResultSerializationException(
    value: Int,
    description: String,
    message: String = "$value: $description"
) : QQGuildApiException(value, description, message)

/**
 * @suppress
 */
@Deprecated("use 'addStackTrace'", ReplaceWith("addStackTrace()"))
public fun QQGuildApiException.copyCurrent(): QQGuildApiException = addStackTrace()

/**
 *
 * @suppress internal API to add suppressed for api exception
 */
@QGInternalApi
public inline fun <E : QQGuildApiException> E.addStackTrace(block: () -> String? = { null }): E {
    addSuppressed(APIStack(block()))
    return this
}

/**
 *
 * @see addStackTrace
 */
@QGInternalApi
@PublishedApi
internal class APIStack @PublishedApi internal constructor(message: String? = null) : Exception(message)

/**
 * 判断 [QQGuildApiException.value] 的值是否为 `404`
 */
public inline val QQGuildApiException.isNotFound: Boolean get() = value == 404

/**
 * 判断 [QQGuildApiException.value] 的值是否为 `401`
 */
public inline val QQGuildApiException.isUnauthorized: Boolean get() = value == 401

/**
 * 如果 [QQGuildApiException.isNotFound] 为 `true`, 得到null，否则抛出此异常
 */
public inline fun <reified T> QQGuildApiException.ifNotFoundThenNull(throwCopy: Boolean = true): T? =
    if (isNotFound) null else if (throwCopy) throw this.addStackTrace() else throw this

/**
 * 如果 [QQGuildApiException.isNotFound] 为 `true`, 得到null，否则抛出此异常
 */
public inline fun <reified T> QQGuildApiException.ifNotFoundThen(throwCopy: Boolean = true, value: () -> T): T =
    if (isNotFound) value() else if (throwCopy) throw this.addStackTrace() else throw this


/**
 * 如果 [QQGuildApiException.isNotFound] 为 `true`, 得到null，否则抛出此异常
 */
public inline fun QQGuildApiException.ifNotFoundThenNoSuch(throwCopy: Boolean = true, value: () -> String): Nothing =
    if (isNotFound) {
        throw NoSuchElementException(value()).apply { initCause0(this) }
    } else {
        if (throwCopy) throw this.addStackTrace() else throw this
    }

/**
 * 提供 [ErrInfo] 和 [HttpStatusCode] ，构建一个 [QQGuildApiException] 并抛出。
 *
 * @throws QQGuildApiException 由 [ErrInfo] 和 [HttpStatusCode] 构建而来的异常
 */
public fun ErrInfo.err(code: HttpStatusCode): Nothing {
    throw QQGuildApiException(this, code.value, code.description)
}

/**
 * 如果返回值不是成功的响应码，[ErrInfo] 则为统一的错误码响应格式。
 *
 */
@Suppress("MemberVisibilityCanBePrivate")
@Serializable
public data class ErrInfo(val code: Int, val message: String, val data: JsonElement = JsonNull)

/**
 * 提供一个 [CloseReason]，构建为一个 [QQGuildApiException] 并抛出。
 *
 * @throws QQGuildApiException 由 [CloseReason] 构建而来的异常
 */
public fun CloseReason?.err(e: Throwable? = null): Nothing {
    if (this == null) {
        if (e != null) {
            throw QQGuildApiException(-1, "No reason").apply { initCause0(e) }
        } else {
            throw QQGuildApiException(-1, "No reason")
        }
    }
    val known = knownReason
    val message = message
    if (known != null) {
        if (e != null) {
            throw QQGuildApiException(
                known.code.toInt(),
                "${known.name}: $message"
            ).apply { initCause0(e) }
        } else {
            throw QQGuildApiException(known.code.toInt(), "${known.name}: $message")
        }
    } else {
        if (e != null) {
            throw QQGuildApiException(code.toInt(), message).apply { initCause0(e) }
        } else {
            throw QQGuildApiException(code.toInt(), message)
        }
    }
}
