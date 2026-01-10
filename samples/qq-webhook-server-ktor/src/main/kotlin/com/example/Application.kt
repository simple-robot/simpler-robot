/*
 * Copyright (c) 2024. ForteScarlet.
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

package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import love.forte.simbot.component.qguild.bot.emitEvent
import love.forte.simbot.component.qguild.filterIsQQGuildBotManagers
import love.forte.simbot.component.qguild.useQQGuild
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.qguild.stdlib.Ed25519SignatureVerification
import love.forte.simbot.qguild.stdlib.EmitResult

private const val SIGNATURE_HEAD = "X-Signature-Ed25519"
private const val TIMESTAMP_HEAD = "X-Signature-Timestamp"

// 你也可以考虑直接把注册好的 bot 保存起来，而不只是保存 application
// 或者使用一些DI方案，都可以。
lateinit var simbotApplication: love.forte.simbot.application.Application

suspend fun main() {
    // 启动simbot application,
    // 然后启动内嵌的 HTTP 服务
    // 当然，具体的启动顺序或逻辑根据你的项目需求而定。
    simbotApplication = launchSimbot()

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)

}

/**
 * 启动 simbot application
 */
suspend fun launchSimbot(): love.forte.simbot.application.Application {
    val application = launchSimpleApplication {
        useQQGuild()
    }

    // 这里配置你的 bot、事件监听等...
    // 你也可以考虑直接把注册好的 bot 保存起来，而不只是保存 application

    return application
}

fun Application.module() {
    configureRouting()
}

fun Application.configureRouting() {
    routing {
        post("/callback/qq/{appId}") {
            val appId = call.parameters["appId"]

            // 寻找指定 `appId` 的 QGBot
            val targetBot = simbotApplication.botManagers
                .filterIsQQGuildBotManagers()
                .firstNotNullOfOrNull {
                    it.all().firstOrNull { bot ->
                        bot.source.ticket.appId == appId
                    }
                }

            // 如果找不到，响应 404 异常
            if (targetBot == null) {
                call.respond(HttpStatusCode.NotFound)
                return@post
            }

            // 准备参数
            val signature = call.request.header(SIGNATURE_HEAD)
                ?: run {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Required header $SIGNATURE_HEAD is missing"
                    )
                    return@post
                }

            val timestamp = call.request.header(TIMESTAMP_HEAD)
                ?: run {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Required header $TIMESTAMP_HEAD is missing"
                    )
                    return@post
                }
            val payload = call.receiveText()

            val result = targetBot.emitEvent(
                payload,
            ) {
                // 配置 ed25519SignatureVerification, 即代表进行签名校验
                ed25519SignatureVerification = Ed25519SignatureVerification(
                    signature,
                    timestamp
                )
            }

            val respond: String? = when (result) {
                is EmitResult.Verified ->
                    // 如果你安装了插件 ContentNegotiation,
                    // 那么也可以直接响应对象。
                    // 这里懒得装了，所以提前序列化成JSON字符串
                    Json.encodeToString(result.verified)

                else -> null
            }

            call.respondText(
                respond ?: "{}",
                ContentType.Application.Json
            )
        }
    }
}
