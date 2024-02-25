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

package love.forte.simbot.spring.test.apptest

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.application.Application
import love.forte.simbot.application.onLaunch
import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.event.*
import love.forte.simbot.message.Face
import love.forte.simbot.message.messagesOf
import love.forte.simbot.message.toText
import love.forte.simbot.plugin.PluginInstaller
import love.forte.simbot.plugin.createPlugin
import love.forte.simbot.quantcat.common.annotations.ContentTrim
import love.forte.simbot.quantcat.common.annotations.Filter
import love.forte.simbot.quantcat.common.annotations.Listener
import love.forte.simbot.spring.EnableSimbot
import love.forte.simbot.spring.configuration.SimbotPluginInstaller
import love.forte.simbot.test.event.TestMessageEvent
import love.forte.simbot.test.message.TestMessageContent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import kotlin.coroutines.resume

@EnableSimbot
@SpringBootApplication
open class AppTestMain

private val eventChannel = Channel<Event> { }
private lateinit var continuationDeferred: CompletableDeferred<CancellableContinuation<String?>>

private const val VALUE = " 你好 "

/**
 *
 * @author ForteScarlet
 */
@SpringBootTest(
    classes = [AppTestMain::class, EventListener::class, TestPluginInstaller::class],
    properties = [
        "logging.level.love.forte.simbot=DEBUG"
    ]
)
class AppTest {

    @Autowired(required = false)
    lateinit var application: Application

    @Test
    fun launchApplication() {
        runTest {
            val resumed = suspendCancellableCoroutine {
                continuationDeferred = CompletableDeferred(it)
                val event = TestMessageEvent().apply {
                    messageContent = TestMessageContent(
                        messages = messagesOf(VALUE.toText())
                    )
                }
                application.launch { eventChannel.send(event) }
            }

            Assertions.assertEquals(VALUE.trim(), resumed)
        }
    }

    @Test
    fun launchApplicationDoubleText() {
        runTest {
            val resumed = suspendCancellableCoroutine {
                continuationDeferred = CompletableDeferred(it)
                val event = TestMessageEvent().apply {
                    messageContent = TestMessageContent(
                        messages = messagesOf(VALUE.toText(), Face(1.ID), VALUE.toText())
                    )
                }
                application.launch { eventChannel.send(event) }
            }

            Assertions.assertEquals((VALUE + VALUE).trim(), resumed)
        }
    }
}

@Component
private class TestPluginInstaller : SimbotPluginInstaller {
    @OptIn(ExperimentalSimbotAPI::class)
    override fun install(installer: PluginInstaller) {
        val plugin = createPlugin("spring.test") {
            val dispatcher = eventDispatcher
            applicationEventRegistrar.onLaunch { app ->
                app.launch {
                    eventChannel.consumeEach { event ->
                        app.launch {
                            dispatcher.push(event)
                                .onEachError { it.content.printStackTrace(System.out) }
                                .onEachError { Assertions.assertInstanceOf(InternalTestError::class.java, it.content) }
                                .filterNotError()
                                .collect { result ->
                                    Assertions.assertFalse(result is StandardEventResult.Error)

                                    println("Event result: $result")
                                }
                        }
                    }
                }
            }
        }

        installer.install(plugin)
    }
}


@Component
private class EventListener(private val application: Application) {

    @Listener
    @ContentTrim
    private suspend fun Event.handle(context: EventListenerContext, name: String? = "forte") {
        println("Event.id: $id")
        println("EventListenerContext: $context")
        println("EventListenerContext.plainText: '${context.plainText}'")
        Assertions.assertEquals("forte", name)
        println("On Event: $this, app: $application")
        val continuation = continuationDeferred.await()
        suspendCancellableCoroutine {
            it.resume(Unit)
            continuation.resume(context.plainText)
        }
    }

    @Listener
    @Filter("hello")
    private fun handleWithErrorButBeFiltered(e: Event) {
        throw InternalTestError()
    }

    @Listener
    private fun handleWithError(e: Event) {
        throw InternalTestError()
    }
}

private class InternalTestError : RuntimeException()
