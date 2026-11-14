/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.internal

import io.ktor.http.*
import love.forte.simbot.ability.*
import love.forte.simbot.ability.StandardDeleteOption.Companion.standardAnalysis
import love.forte.simbot.common.exception.initExceptionCause
import love.forte.simbot.component.kook.KookUserChat
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.message.KookMessageReceipt
import love.forte.simbot.component.kook.message.KookMessageSendContext
import love.forte.simbot.component.kook.message.KookMessageSendTarget
import love.forte.simbot.component.kook.util.requestResultBy
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.kook.api.ApiResponseException
import love.forte.simbot.kook.api.userchat.DeleteUserChatApi
import love.forte.simbot.kook.api.userchat.UserChatView
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author ForteScarlet
 */
internal class KookUserChatImpl(
    private val bot: KookBotImpl, override val source: UserChatView,
) : KookUserChat {
    override val coroutineContext: CoroutineContext
        get() = bot.subContext

    override fun toString(): String {
        return "KookUserChat(code=${source.code}, target=${source.targetInfo})"
    }

    override suspend fun send(text: String): KookMessageReceipt =
        send(InteractionMessage.valueOf(text), emptyList())

    /**
     * 保留本次发送选项并进入共享拦截链。
     */
    override suspend fun send(text: String, vararg options: SendOption): KookMessageReceipt =
        send(InteractionMessage.valueOf(text), options.toList())

    override suspend fun send(message: Message): KookMessageReceipt =
        send(InteractionMessage.valueOf(message), emptyList())

    /**
     * 保留本次发送选项并进入共享拦截链。
     */
    override suspend fun send(message: Message, vararg options: SendOption): KookMessageReceipt =
        send(InteractionMessage.valueOf(message), options.toList())

    override suspend fun send(messageContent: MessageContent): KookMessageReceipt =
        send(InteractionMessage.valueOf(messageContent), emptyList())

    /**
     * 保留本次发送选项并进入共享拦截链。
     */
    override suspend fun send(messageContent: MessageContent, vararg options: SendOption): KookMessageReceipt =
        send(InteractionMessage.valueOf(messageContent), options.toList())

    /**
     * 直接使用用户 ID 发送，避免内部委托丢失成员或会话这一调用来源。
     */
    private suspend inline fun send(
        message: InteractionMessage,
        options: List<SendOption>
    ): KookMessageReceipt =
        bot.interceptMessageSend(
            KookMessageSendContext.create(
                bot = bot,
                source = this,
                message = message,
                destination = KookMessageSendTarget.user(source.targetInfo.id),
                options = options,
                target = { this },
            )
        ) {
            sendDirectMessage(bot, it)
        }

    override suspend fun delete(vararg options: DeleteOption) {
        val stdOpts = options.standardAnalysis()

        val result = try {
            DeleteUserChatApi.create(source.code).requestResultBy(bot)
        } catch (respEx: ApiResponseException) {
            val response = respEx.response
            if (response.status.value == HttpStatusCode.NotFound.value) {
                if (stdOpts.isIgnoreOnNoSuchTarget) {
                    return
                }

                throw NoSuchElementException(
                    "Delete user chat(code=${source.code}, " +
                        "target=${source.targetInfo.id}, ${source.targetInfo.username}) " +
                        "on failure: response status is 404: ${respEx.message}"
                ).also {
                    it.initExceptionCause(respEx)
                }
            }

            if (stdOpts.isIgnoreOnFailure) {
                return
            }

            throw DeleteFailureException(
                "Delete user chat(code=${source.code}, target=${source.targetInfo.id}, ${source.targetInfo.username})" +
                    " on failure: response status is not successful: ${response.status}",
                respEx
            )
        }

        if (!result.isSuccess) {
            if (stdOpts.isIgnoreOnFailure) {
                return
            }

            throw DeleteFailureException(
                "Delete user chat(code=${source.code}, target=${source.targetInfo.id}, ${source.targetInfo.username})" +
                    " on failure: result code is not successful: $result"
            )
        }
    }
}
