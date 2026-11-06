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

import io.ktor.client.engine.mock.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.test.runTest
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.QQGuildApi
import love.forte.simbot.qguild.api.group.GetGroupJoinApprovalStrategyListApi
import love.forte.simbot.qguild.stdlib.*
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BotRequestsReactiveTests {
    private val response = """{"strategies":[],"next_cursor":""}"""

    @Test
    fun botRequestsAreLazyRepeatableAndUseBotClient() = runTest {
        var count = 0
        var fail = false
        val bot = BotFactory.create(Bot.Ticket("app", "token", "secret"), ConfigurableBotConfiguration().apply {
            apiClientEngine = MockEngine { request ->
                count++
                assertEquals("app", request.headers["X-Union-Appid"])
                if (fail) respond("""{"code":11253,"message":"no permission"}""", HttpStatusCode.Forbidden)
                else respond(response, HttpStatusCode.OK)
            }
            wsClientEngine = MockEngine { respondOk() }
        })
        try {
            val api = GetGroupJoinApprovalStrategyListApi.create()
            val data = bot.requestDataReactive(api)
            assertEquals(0, count)
            assertTrue(data.awaitSingle().strategies.isEmpty())
            assertTrue(data.awaitSingle().strategies.isEmpty())
            assertEquals(response, bot.requestTextReactive(api).awaitSingle())
            val raw = bot.requestReactive(api).awaitSingle()
            assertEquals(HttpStatusCode.OK, raw.status)
            assertEquals(response, raw.bodyAsText())
            assertEquals(4, count)
            fail = true
            assertEquals("""{"code":11253,"message":"no permission"}""", bot.requestTextReactive(api).awaitSingle())
            assertEquals(11253, assertFailsWith<QQGuildApiException> { bot.requestDataReactive(api).awaitSingle() }.info?.code)
            assertEquals(HttpStatusCode.Forbidden, bot.requestReactive(api).awaitSingle().status)
        } finally { bot.apiClient.close(); bot.cancel() }
    }

    @Test
    fun cancellingSubscriptionCancelsBotRequest() = runTest {
        val started = CompletableDeferred<Unit>()
        val cancelled = CompletableDeferred<Unit>()
        val bot = BotFactory.create(Bot.Ticket("app", "token", "secret"), ConfigurableBotConfiguration().apply {
            apiClientEngine = MockEngine {
                started.complete(Unit)
                try { awaitCancellation() } finally { cancelled.complete(Unit) }
            }
            wsClientEngine = MockEngine { respondOk() }
        })
        try {
            val subscription = CompletableDeferred<Subscription>()
            bot.requestDataReactive(GetGroupJoinApprovalStrategyListApi.create()).subscribe(object : Subscriber<Any> {
                override fun onSubscribe(s: Subscription) { subscription.complete(s); s.request(1) }
                override fun onNext(item: Any) = Unit
                override fun onError(error: Throwable) = Unit
                override fun onComplete() = Unit
            })
            withContext(Dispatchers.Default) {
                withTimeout(5_000) { started.await(); subscription.await().cancel(); cancelled.await() }
            }
        } finally { bot.apiClient.close(); bot.cancel() }
    }

    @Test
    fun javaStaticEntrypointsAreAvailable() {
        val requests = Class.forName("love.forte.simbot.qguild.stdlib.BotRequests")
        for (name in listOf("requestReactive", "requestTextReactive", "requestDataReactive")) {
            val method = requests.getMethod(name, Bot::class.java, QQGuildApi::class.java)
            assertEquals(Publisher::class.java, method.returnType)
        }
    }
}
