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

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import love.forte.simbot.annotations.FragileSimbotAPI
import love.forte.simbot.component.onebot.v11.core.OneBot11
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.seconds

@OptIn(FragileSimbotAPI::class, ExperimentalStdlibApi::class)
suspend fun main() {
    val host = Url("ws://localhost:3001")
    val client = HttpClient(Java) {
        WebSockets {}
    }

    val job = SupervisorJob()
    val json = OneBot11.DefaultJson

    val scope = CoroutineScope(EmptyCoroutineContext)

    val session = client.webSocketSession {
        url { takeFrom(host) }
    }

    scope.launch {
        delay(5.seconds)
        job.cancel()
    }

    println(session.coroutineContext[Job])
    println(session.coroutineContext[CoroutineDispatcher])

    job.invokeOnCompletion {
        session.cancel("Cancelled by job")
    }

    try {
        session.incoming.receiveAsFlow()
            .catch { e ->
                println("incoming Err: $e")
                e.printStackTrace()
            }
            .collect {
                println("Received $it")
            }
    } catch (e: Exception) {
        println("withContext Err: $e")
        e.printStackTrace()
    }

    println(session)
    println(session.isActive)

    session.close()

    println(session)
    println(session.isActive)

}
