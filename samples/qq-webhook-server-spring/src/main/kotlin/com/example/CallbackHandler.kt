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

import love.forte.simbot.application.Application
import love.forte.simbot.component.qguild.bot.emitEvent
import love.forte.simbot.component.qguild.filterIsQQGuildBotManagers
import love.forte.simbot.qguild.stdlib.Ed25519SignatureVerification
import love.forte.simbot.qguild.stdlib.EmitResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

private const val SIGNATURE_HEAD = "X-Signature-Ed25519"
private const val TIMESTAMP_HEAD = "X-Signature-Timestamp"

/**
 * 处理所有qq机器人的回调请求的处理器。
 */
@RestController("/callback")
class CallbackHandler(
    private val application: Application
) {
    /**
     * 处理 `/callback/qq/{appId}` 的事件回调请求，
     * 找到对应的 bot 并向其推送事件。
     */
    @PostMapping("/qq/{appId}")
    suspend fun handleEvent(
        @PathVariable("appId") appId: String,
        @RequestHeader(SIGNATURE_HEAD) signature: String,
        @RequestHeader(TIMESTAMP_HEAD) timestamp: String,
        @RequestBody payload: String,
    ): ResponseEntity<Any?> {
        // 寻找指定 `appId` 的 QGBot
        val targetBot = application.botManagers
            .filterIsQQGuildBotManagers()
            .firstNotNullOfOrNull {
                it.all().firstOrNull { bot ->
                    bot.source.ticket.appId == appId
                }
            }

        // 如果找不到，响应 404 异常
        if (targetBot == null) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "app $appId not found"
            )
        }

        val result = targetBot.emitEvent(
            payload,
        ) {
            // 配置 ed25519SignatureVerification, 即代表进行签名校验
            ed25519SignatureVerification = Ed25519SignatureVerification(
                signature,
                timestamp
            )
        }

        val body: Any? = when (result) {
            is EmitResult.Verified -> result.verified
            else -> null
        }

        // 响应结果
        return ResponseEntity.ok(body)
    }
}
