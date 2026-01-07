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
import love.forte.simbot.ability.DeleteFailureException
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption.Companion.standardAnalysis
import love.forte.simbot.ability.isIgnoreOnFailure
import love.forte.simbot.ability.isIgnoreOnNoSuchTarget
import love.forte.simbot.common.exception.initExceptionCause
import love.forte.simbot.component.kook.KookUserChat
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.message.*
import love.forte.simbot.component.kook.message.KookMessageCreatedReceipt.Companion.asReceipt
import love.forte.simbot.component.kook.util.requestDataBy
import love.forte.simbot.component.kook.util.requestResultBy
import love.forte.simbot.kook.api.ApiResponseException
import love.forte.simbot.kook.api.message.SendDirectMessageApi
import love.forte.simbot.kook.api.userchat.DeleteUserChatApi
import love.forte.simbot.kook.api.userchat.UserChatView
import love.forte.simbot.kook.messages.MessageType
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

    override suspend fun send(text: String): KookMessageCreatedReceipt {
        return SendDirectMessageApi
            .createByTargetId(source.targetInfo.id, text, MessageType.TEXT.type, null, null)
            .requestDataBy(bot)
            .asReceipt(true, bot)
    }

    override suspend fun send(message: Message): KookMessageReceipt {
        return message.sendToDirectByTargetId(bot, source.targetInfo.id, null, null, null)
            ?: throw IllegalArgumentException("Valid messages must not be empty.")
    }

    override suspend fun send(messageContent: MessageContent): KookMessageReceipt {
        return when (messageContent) {
            is KookReceiveMessageContent -> {
                val source = messageContent.source
                SendDirectMessageApi.createByTargetId(
                    targetId = this.source.targetInfo.id,
                    content = source.content,
                    type = source.type?.value,
                    quote = null,
                    nonce = null,
                ).requestDataBy(bot).asReceipt(true, bot)
            }

            is KookChannelMessageDetailsContent -> {
                val details = messageContent.details
                SendDirectMessageApi.createByTargetId(
                    targetId = this.source.targetInfo.id,
                    content = details.content,
                    type = details.type,
                    quote = details.quote?.id,
                    nonce = null,
                ).requestDataBy(bot).asReceipt(true, bot)
            }

            else -> {
                send(messageContent.messages)
            }
        }
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
                    "Delete user chat(code=${source.code}, target=${source.targetInfo.id}, ${source.targetInfo.username})" +
                            " on failure: response status is 404: ${respEx.message}"
                ).also {
                    it.initExceptionCause(respEx)
                }
            }

            if (stdOpts.isIgnoreOnFailure) {
                return
            }

            throw DeleteFailureException(
                "Delete user chat(code=${source.code}, target=${source.targetInfo.id}, ${source.targetInfo.username})" +
                        " on failure: response status is not successful: ${response.status}", respEx
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
