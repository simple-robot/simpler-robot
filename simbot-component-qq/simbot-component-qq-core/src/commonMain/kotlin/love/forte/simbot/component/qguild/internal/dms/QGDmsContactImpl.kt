/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.dms

import love.forte.simbot.ability.SendOption
import love.forte.simbot.component.qguild.dms.QGDmsContact
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.newSupervisorCoroutineContext
import love.forte.simbot.component.qguild.internal.message.interceptSend
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.sendDmsMessage
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.addStackTrace
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.model.User
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class QGDmsContactImpl(
    private val bot: QGBotImpl,
    override val source: User,
    private val guildId: String,
    private val currentMsgId: String?,
) : QGDmsContact {
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()

    override suspend fun send(text: String): MessageReceipt {
        return emitPreSendEventAndSend(InteractionMessage.valueOf(text))
    }

    /**
     * 保留发送选项，并让本次操作只进入一次拦截链。
     */
    override suspend fun send(text: String, vararg options: SendOption): QGMessageReceipt =
        bot.interceptSend(
            source = this,
            message = InteractionMessage.valueOf(text),
            options = options.toList(),
            send = ::sendByInteractionMessage
        )

    /**
     * 保留发送选项，并让本次操作只进入一次拦截链。
     */
    override suspend fun send(message: Message, vararg options: SendOption): QGMessageReceipt =
        bot.interceptSend(
            source = this,
            message = InteractionMessage.valueOf(message),
            options = options.toList(),
            send = ::sendByInteractionMessage
        )

    /**
     * 保留发送选项，并让本次操作只进入一次拦截链。
     */
    override suspend fun send(messageContent: MessageContent, vararg options: SendOption): QGMessageReceipt =
        bot.interceptSend(
            source = this,
            message = InteractionMessage.valueOf(messageContent),
            options = options.toList(),
            send = ::sendByInteractionMessage
        )
        bot.emitMessagePreSendEvent(event)
        val message = event.useMessage()
        return sendByInteractionMessage(message)
    }

    private suspend fun sendByInteractionMessage(message: InteractionMessage): MessageReceipt {
        fun MessageSendApi.Body.Builder.configMsgId() {
            if (msgId == null) {
                msgId = currentMsgId
            }
        }

        try {
            return when (message) {
                is InteractionMessage.Message -> {
                    bot.sendDmsMessage(guildId, message.message) {
                        configMsgId()
                    }
                }

                is InteractionMessage.MessageContent -> {
                    bot.sendDmsMessage(guildId, message.messageContent) {
                        configMsgId()
                    }
                }

                is InteractionMessage.Text -> {
                    bot.sendDmsMessage(guildId, message.text) {
                        configMsgId()
                    }
                }

                is InteractionMessage.Extension -> error("Unknown type of InteractionMessage: $message")
            }.alsoEmitPostSendEvent(bot, this, message)
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "dmsContact.send" }
        }
    }

    override fun toString(): String {
        return "QGDmsContact(id=$id, name=$name, source=$source)"
    }
}

internal fun User.toDmsContact(
    bot: QGBotImpl,
    guildId: String,
    currentMsgId: String? = null,
): QGDmsContactImpl = QGDmsContactImpl(
    bot = bot,
    source = this,
    guildId = guildId,
    currentMsgId = currentMsgId
)
