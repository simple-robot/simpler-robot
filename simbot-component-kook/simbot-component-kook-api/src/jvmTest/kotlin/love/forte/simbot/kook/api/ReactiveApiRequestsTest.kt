/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.kook.api

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.ExperimentalReactiveBridgingApi
import love.forte.simbot.kook.api.user.GetMeApi
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * 验证 KOOK 文本请求的订阅时机和 HTTP 错误传播。
 */
@OptIn(Api4J::class, ExperimentalReactiveBridgingApi::class)
class ReactiveApiRequestsTest {
    /**
     * 创建 Publisher 时请求计数为零，两次订阅各发起一次请求并收到对应的响应文本。
     */
    @Test
    fun requestTextRunsOnEachSubscription() = runTest {
        val requests = AtomicInteger()

        HttpClient(MockEngine { respond(requests.incrementAndGet().toString()) }).use { client ->
            val response = GetMeApi.requestTextReactive(client, "Bot test-token")

            assertEquals(0, requests.get())
            assertEquals("1", response.awaitSingle())
            assertEquals("2", response.awaitSingle())
        }
    }

    /**
     * HTTP 400 响应通过 Publisher 向订阅者报告 [ApiResponseException]。
     */
    @Test
    fun requestTextEmitsHttpError() = runTest {
        HttpClient(MockEngine { respond("bad request", HttpStatusCode.BadRequest) }).use { client ->
            val response = GetMeApi.requestTextReactive(client, "Bot test-token")

            assertFailsWith<ApiResponseException> { response.awaitSingle() }
        }
    }
}
