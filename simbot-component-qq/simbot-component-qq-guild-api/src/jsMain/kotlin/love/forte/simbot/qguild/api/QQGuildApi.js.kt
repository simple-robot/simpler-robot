/*
 * Copyright (c) 2023-2024. ForteScarlet.
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

package love.forte.simbot.qguild.api

import io.ktor.http.*

//
///**
// * 用于多平台实现的最小目标。
// *
// * 在JVM平台和JS平台中分别提供对应的 blocking/async 兼容函数。
// * 但是不应追加新的抽象函数。
// */
//@QGInternalApi
//public actual abstract class PlatformQQGuildApi<out R> actual constructor() {
//
//    /**
//     * 使用此api发起一次请求，并得到预期中的结果。
//     *
//     * @see love.forte.simbot.qguild.ErrInfo
//     *
//     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
//     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
//     */
//    public actual abstract suspend fun doRequest(
//        client: HttpClient,
//        server: Url,
//        token: String,
//        decoder: StringFormat
//    ): R
//
//
//    /**
//     * 使用此api发起一次请求，并得到预期中的结果。
//     *
//     * @see ErrInfo
//     *
//     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
//     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
//     */
//    public actual abstract suspend fun doRequestRaw(
//        client: HttpClient,
//        server: Url,
//        token: String,
//    ): String
//
//    /**
//     * 使用此api发起一次请求，并得到预期中的结果。
//     *
//     * @see doRequestRaw
//     */
//    public fun doRequestAsync(
//        client: HttpClient,
//        server: Url,
//        token: String,
//        decoder: StringFormat
//    ): Promise<R> {
//        return client.promise(COROUTINE_NAME) { doRequest(client, server, token, decoder) }
//    }
//
//    /**
//     * 使用此api发起一次请求，并得到预期中的结果。
//     *
//     * @see doRequestRaw
//     */
//    public fun doRequestRawAsync(
//        client: HttpClient,
//        server: Url,
//        token: String,
//    ): Promise<String> {
//        return client.promise(COROUTINE_NAME) { doRequestRaw(client, server, token) }
//    }
//
//    public actual companion object {
//        private val COROUTINE_NAME = CoroutineName("Api-Async-Scope")
//    }
//}

/**
 * 日志对齐
 */
@PublishedApi
internal actual val HttpMethod.logName: String
    get() = defaultForLogName()
