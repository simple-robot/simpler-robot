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

@file:OptIn(love.forte.simbot.annotations.Api4J::class, love.forte.simbot.annotations.ExperimentalReactiveBridgingApi::class)

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.test.runTest
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.QQGuildResultSerializationException
import love.forte.simbot.qguild.api.QQGuildApi
import love.forte.simbot.qguild.api.group.GetGroupJoinApprovalStrategyListApi
import love.forte.simbot.qguild.api.requestDataReactive
import love.forte.simbot.qguild.api.requestReactive
import love.forte.simbot.qguild.api.requestTextReactive
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ApiRequestsReactiveTests {
    private val response = """{"strategies":[],"next_cursor":""}"""

    @Test
    fun requestsAreLazyRepeatableAndPreserveOptions() = runTest {
        var count = 0
        val client = HttpClient(MockEngine { request ->
            count++
            assertEquals("example.test", request.url.host)
            assertEquals("app-id", request.headers["X-Union-Appid"])
            respond(response, HttpStatusCode.OK)
        })
        try {
            val api = GetGroupJoinApprovalStrategyListApi.create()
            val data = api.requestDataReactive(client, "token", Url("https://example.test"), "app-id")
            assertEquals(0, count)
            assertTrue(data.awaitSingle().strategies.isEmpty())
            assertTrue(data.awaitSingle().strategies.isEmpty())
            assertEquals(response, api.requestTextReactive(client, "token", Url("https://example.test"), "app-id").awaitSingle())
            val raw = api.requestReactive(client, "token", Url("https://example.test"), "app-id").awaitSingle()
            assertEquals(HttpStatusCode.OK, raw.status)
            assertEquals(response, raw.bodyAsText())
            assertEquals(4, count)
        } finally { client.close() }
    }

    @Test
    fun errorsAreDeliveredToSubscriber() = runTest {
        var malformed = false
        val client = HttpClient(MockEngine {
            if (malformed) respond("not json", HttpStatusCode.OK)
            else respond("""{"code":11253,"message":"no permission"}""", HttpStatusCode.Forbidden)
        })
        try {
            val api = GetGroupJoinApprovalStrategyListApi.create()
            assertEquals(HttpStatusCode.Forbidden, api.requestReactive(client, "token").awaitSingle().status)
            assertEquals("""{"code":11253,"message":"no permission"}""", api.requestTextReactive(client, "token").awaitSingle())
            assertEquals(11253, assertFailsWith<QQGuildApiException> { api.requestDataReactive(client, "token").awaitSingle() }.info?.code)
            malformed = true
            assertFailsWith<QQGuildResultSerializationException> { api.requestDataReactive(client, "token").awaitSingle() }
        } finally { client.close() }
    }

    @Test
    fun cancellingSubscriptionCancelsRequest() = runTest {
        val started = CompletableDeferred<Unit>()
        val cancelled = CompletableDeferred<Unit>()
        val client = HttpClient(MockEngine {
            started.complete(Unit)
            try { awaitCancellation() } finally { cancelled.complete(Unit) }
        })
        try {
            val subscription = CompletableDeferred<Subscription>()
            GetGroupJoinApprovalStrategyListApi.create().requestDataReactive(client, "token").subscribe(object : Subscriber<Any> {
                override fun onSubscribe(s: Subscription) { subscription.complete(s); s.request(1) }
                override fun onNext(item: Any) = Unit
                override fun onError(error: Throwable) = Unit
                override fun onComplete() = Unit
            })
            withContext(Dispatchers.Default) {
                withTimeout(5_000) { started.await(); subscription.await().cancel(); cancelled.await() }
            }
        } finally { client.close() }
    }

    @Test
    fun javaStaticOverloadsAreAvailable() {
        val requests = Class.forName("love.forte.simbot.qguild.api.ApiRequests")
        for (name in listOf("requestReactive", "requestTextReactive", "requestDataReactive")) {
            val short = requests.getMethod(name, QQGuildApi::class.java, HttpClient::class.java, String::class.java)
            val full = requests.getMethod(name, QQGuildApi::class.java, HttpClient::class.java, String::class.java, Url::class.java, String::class.java)
            assertEquals(Publisher::class.java, short.returnType)
            assertEquals(Publisher::class.java, full.returnType)
        }
    }
}
