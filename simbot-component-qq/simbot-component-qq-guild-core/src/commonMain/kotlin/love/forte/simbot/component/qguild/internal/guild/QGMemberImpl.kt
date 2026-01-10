/*
 * Copyright (c) 2022-2025. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.guild

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.common.time.TimeUnit
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.newSupervisorCoroutineContext
import love.forte.simbot.component.qguild.internal.event.QGMemberSendSupportPreSendEventImpl
import love.forte.simbot.component.qguild.internal.message.asReceipt
import love.forte.simbot.component.qguild.internal.role.toGuildRole
import love.forte.simbot.component.qguild.internal.role.toMemberRole
import love.forte.simbot.component.qguild.message.MessageParsers
import love.forte.simbot.component.qguild.message.QGMessageContent
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.sendMessage
import love.forte.simbot.component.qguild.role.QGMemberRole
import love.forte.simbot.component.qguild.utils.alsoEmitPostSendEvent
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.addStackTrace
import love.forte.simbot.qguild.api.guild.mute.MuteMemberApi
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.api.message.direct.CreateDmsApi
import love.forte.simbot.qguild.api.message.direct.DmsSendApi
import love.forte.simbot.qguild.model.DirectMessageSession
import love.forte.simbot.qguild.model.Role
import love.forte.simbot.qguild.stdlib.requestDataBy
import kotlin.concurrent.Volatile
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmSynthetic
import kotlin.time.Duration
import love.forte.simbot.qguild.model.Member as QGSourceMember

/**
 *
 * @author ForteScarlet
 */
internal class QGMemberImpl(
    private val bot: QGBotImpl,
    override val source: QGSourceMember,
    private val guildId: ID,
    private val sourceGuild: QGGuildImpl? = null
) : QGMember {
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()
    private val user get() = source.user
    private val roleIdSet = source.roles.toSet()

    private val dmsInitLock = Mutex()

    @Volatile
    private lateinit var dms: DirectMessageSession

    private suspend fun getDms(): DirectMessageSession {
        if (::dms.isInitialized) {
            return dms
        } else {
            dmsInitLock.withLock {
                if (::dms.isInitialized) {
                    return dms
                }
                return CreateDmsApi.create(user.id, guildId.literal).requestDataBy(bot.source).also {
                    dms = it
                }
            }
        }
    }

    @ExperimentalQGApi
    override val roles: Collectable<QGMemberRole>
        get() =
            bot.queryMemberRoles(
                guildId = guildId.literal,
                roleIds = roleIdSet,
                memberId = source.user.id.ID,
                sourceGuild = sourceGuild
            ).asCollectable()

    @ExperimentalQGApi
    override val defaultRoles: List<QGMemberRole>
        get() = source.roles.mapNotNull { rid ->
            if (!Role.isDefault(rid)) return@mapNotNull null
            Role.defaultRole(rid).toGuildRole(
                bot,
                guildId,
                sourceGuild
            ).toMemberRole(
                bot,
                source.user.id.ID
            )
        }

    @JvmSynthetic
    override suspend fun send(message: Message): QGMessageReceipt {
        val event = QGMemberSendSupportPreSendEventImpl(
            bot = bot,
            content = this,
            message = InteractionMessage.valueOf(message),
        )
        bot.emitMessagePreSendEvent(event)
        val message = event.useMessage()

        return sendByInteractionMessage(message)
    }

    override suspend fun send(text: String): QGMessageReceipt {
        val event = QGMemberSendSupportPreSendEventImpl(
            bot = bot,
            content = this,
            message = InteractionMessage.valueOf(text),
        )
        bot.emitMessagePreSendEvent(event)
        val message = event.useMessage()

        return sendByInteractionMessage(message)
    }


    override suspend fun send(messageContent: MessageContent): QGMessageReceipt {
        val event = QGMemberSendSupportPreSendEventImpl(
            bot = bot,
            content = this,
            message = InteractionMessage.valueOf(messageContent),
        )
        bot.emitMessagePreSendEvent(event)
        val message = event.useMessage()

        return sendByInteractionMessage(message)
    }

    private suspend fun sendByInteractionMessage(message: InteractionMessage): QGMessageReceipt {
        return when (message) {
            is InteractionMessage.Text -> {
                val dms = getDms()
                bot.sendMessage(dms.guildId, message.text)
            }

            is InteractionMessage.Message -> {
                val builder = MessageParsers.parse(message.message)
                send0(builder)
            }

            is InteractionMessage.MessageContent -> {
                val messageContent = message.messageContent
                if (messageContent is QGMessageContent) {
                    val body = MessageSendApi.Body {
                        fromMessage(messageContent.sourceMessage)
                    }
                    return send0(body)
                }

                val builder = MessageParsers.parse(messageContent.messages)
                send0(builder)
            }

            else -> error("Unknown type of InteractionMessage: $message")
        }.alsoEmitPostSendEvent(bot, this, message)
    }


    private suspend fun send0(body: MessageSendApi.Body): QGMessageReceipt {
        val dms = getDms()
        return DmsSendApi.create(dms.guildId, body).requestDataBy(bot.source).asReceipt()
    }

    private suspend fun send0(bodyList: List<MessageSendApi.Body.Builder>): QGMessageReceipt {
        val dms = getDms()

        if (bodyList.size == 1) {
            val body = bodyList[0].build()
            return DmsSendApi.create(dms.guildId, body).requestDataBy(bot.source).asReceipt()
        }

        return try {
            bodyList.map {
                DmsSendApi.create(dms.guildId, it.build()).requestDataBy(bot.source)
            }.asReceipt()
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "member.send" }
        }
    }

    override suspend fun mute(duration: Duration) {
        MuteMemberApi.create(
            guildId.literal,
            user.id,
            duration
        ).requestDataBy(bot.source)
    }

    override suspend fun mute(duration: Long, unit: TimeUnit) {
        MuteMemberApi.create(
            guildId.literal,
            user.id,
            duration,
            unit
        ).requestDataBy(bot.source)
    }

    override suspend fun unmute() {
        MuteMemberApi.createUnmute(
            guildId.literal,
            user.id,
        ).requestDataBy(bot.source)
    }


    override fun toString(): String {
        return "QGMember(id=$id, name=$name, nick=$nick, guildId=$guildId)"
    }
}
