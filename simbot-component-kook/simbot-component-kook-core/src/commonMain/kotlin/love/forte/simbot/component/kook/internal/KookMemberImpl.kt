/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import love.forte.simbot.ability.*
import love.forte.simbot.ability.StandardDeleteOption.Companion.standardAnalysis
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.exception.initExceptionCause
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.TimeUnit
import love.forte.simbot.component.kook.KookMember
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.bot.internal.MuteJob
import love.forte.simbot.component.kook.kookGuildNotExistsException
import love.forte.simbot.component.kook.message.KookMessageReceipt
import love.forte.simbot.component.kook.role.internal.KookGuildRoleImpl
import love.forte.simbot.component.kook.role.internal.KookMemberRoleImpl
import love.forte.simbot.component.kook.util.requestDataBy
import love.forte.simbot.component.kook.util.requestResultBy
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.kook.api.ApiResponseException
import love.forte.simbot.kook.api.guild.CreateGuildMuteApi
import love.forte.simbot.kook.api.guild.DeleteGuildMuteApi
import love.forte.simbot.kook.api.member.KickoutMemberApi
import love.forte.simbot.kook.api.user.GetUserViewApi
import love.forte.simbot.kook.objects.User
import love.forte.simbot.logger.isDebugEnabled
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds


/**
 *
 * @author ForteScarlet
 */
internal class KookMemberImpl(
    private val bot: KookBotImpl,
    override val source: User,
    internal val guildIdValue: String
) : KookMember {
    override val coroutineContext: CoroutineContext
        get() = bot.subContext

    override val guildId: ID
        get() = guildIdValue.ID

    private val guildValue
        get() = bot.internalGuild(guildIdValue) ?: throw kookGuildNotExistsException(guildIdValue)


    override suspend fun send(message: Message): KookMessageReceipt {
        return asContact().send(message)
    }

    override suspend fun send(messageContent: MessageContent): KookMessageReceipt {
        return asContact().send(messageContent)
    }

    /**
     * 发送消息。
     */
    override suspend fun send(messageContent: MessageContent, vararg options: SendOption): KookMessageReceipt =
        send(InteractionMessage.valueOf(messageContent), options.toList())

    /**
     * 直接使用用户 ID 发送，避免内部委托丢失成员或会话这一调用来源。
     */
    private suspend fun send(message: InteractionMessage, options: List<SendOption>): KookMessageReceipt =
        bot.interceptMessageSend(MessageSendContext.DefaultSend(bot, this, message, options)) {
            sendDirectMessage(bot, source.id, it)
        }

    @ExperimentalSimbotAPI
    override val roles: Collectable<KookMemberRoleImpl>
        get() = flow {
            val view = GetUserViewApi.create(source.id, guildIdValue).requestDataBy(bot)
            val guildRoleMap = mutableMapOf<Long, KookGuildRoleImpl>()

            guildValue.roles.collect { r ->
                guildRoleMap[r.source.roleId] = r
            }

            view.roles?.forEach { rid ->
                val role = guildRoleMap[rid] ?: return@forEach
                emit(KookMemberRoleImpl(bot, this@KookMemberImpl, guildIdValue, role))
            }
        }.asCollectable()

    /**
     * 踢出成员。
     */
    override suspend fun delete(vararg options: DeleteOption) {
        val stdOpts = options.standardAnalysis()

        val result = try {
            KickoutMemberApi.create(guildIdValue, source.id).requestResultBy(bot)
        } catch (respEx: ApiResponseException) {
            if (respEx.response.status.value == HttpStatusCode.NotFound.value) {
                if (stdOpts.isIgnoreOnNoSuchTarget) {
                    return
                }

                throw NoSuchElementException(
                    "Kick member(id=${source.id}) on failure. Http status is ${HttpStatusCode.NotFound}: " +
                        "${respEx.response.status}"
                ).also {
                    it.initExceptionCause(respEx)
                }
            }
            if (stdOpts.isIgnoreOnFailure) {
                return
            }

            throw DeleteFailureException(
                "Kick member(id=${source.id}) on failure. " +
                    "HTTP status: ${respEx.response.status.value}): ${respEx.message}",
                respEx
            )
        }

        if (!result.isSuccess) {
            if (stdOpts.isIgnoreOnFailure) {
                return
            }

            throw DeleteFailureException(
                "Kick member(id=${source.id}) on failure. result.code is not successful: $result)"
            )
        }
    }

    private suspend fun mute0(type: Int, duration: Duration) {
        val userId = source.id
        val guildIdStr = guildIdValue

        val api = CreateGuildMuteApi.create(guildIdStr, userId, type)
        api.requestDataBy(bot)

        if (duration <= Duration.ZERO) {
            return
        }

        val logger = bot.logger
        val name = source.username
        val delayJob = bot.launch(start = CoroutineStart.LAZY) {
            delay(duration)
            // delay and do unmute
            runCatching {
                if (logger.isDebugEnabled) {
                    logger.debug(
                        "After a delay of {}, unmute(type={}) for member(id={}, username={}) in guild(id={})",
                        duration.toString(),
                        type,
                        userId,
                        name,
                        guildIdStr
                    )
                }
                DeleteGuildMuteApi.create(guildIdStr, userId, type).requestDataBy(bot)
            }.onFailure { e ->
                if (e is CancellationException) {
                    ensureActive()
                }

                logger.error(
                    "Can not unmute(type={}) for member(id={}, username={}) in guild(id={}) because of: {}",
                    type,
                    userId,
                    name,
                    guildIdStr,
                    e.message,
                    e
                )
            }
        }

        val muteJob = MuteJob(delayJob)

        bot.internalSetMuteJob(guildIdStr, userId, muteJob)?.cancel()

        delayJob.invokeOnCompletion {
            bot.internalRemoveMuteJob(guildIdStr, userId, muteJob)
        }

        delayJob.start()

    }


    override suspend fun mute(type: Int): Boolean = true.also { mute0(type, Duration.ZERO) }


    @ExperimentalSimbotAPI
    override suspend fun mute(type: Int, duration: Duration): Boolean =
        true.also { mute0(type, duration) }

    @ExperimentalSimbotAPI
    override suspend fun mute(type: Int, time: Long, timeUnit: TimeUnit): Boolean =
        true.also { mute0(type, timeUnit.toNanos(time).nanoseconds) }

    override suspend fun unmute(type: Int): Boolean {
        val userId = source.id
        DeleteGuildMuteApi.create(guildIdValue, userId, type).requestDataBy(bot)
        bot.internalRemoveMuteJob(guildIdValue, userId)?.cancel()
        return true
    }

    private suspend fun asContact(): KookUserChatImpl = bot.contactRelation.contact(id)
}
