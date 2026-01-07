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

package love.forte.simbot.component.kook.bot.internal

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import love.forte.simbot.annotations.FragileSimbotAPI
import love.forte.simbot.common.collection.computeValue
import love.forte.simbot.component.kook.event.UnsupportedKookEvent
import love.forte.simbot.component.kook.event.internal.*
import love.forte.simbot.component.kook.internal.*
import love.forte.simbot.component.kook.util.requestDataBy
import love.forte.simbot.event.Event
import love.forte.simbot.event.onEachError
import love.forte.simbot.kook.DiscreetKookApi
import love.forte.simbot.kook.api.user.GetUserViewApi
import love.forte.simbot.kook.event.*
import love.forte.simbot.kook.objects.SimpleUser
import love.forte.simbot.kook.stdlib.SubscribeSequence
import love.forte.simbot.kook.event.Event as KEvent
import love.forte.simbot.kook.objects.User as KUser

/**
 * 如果 Guild 是一个正在初始化过程中的任务，等待任务结果后执行 [inGuild]；
 * 否则从 [KookBotImpl.internalGuild] 中获取，如果获取不到则返回 `false` 并输出一个警告日志。
 */
private suspend inline fun KookBotImpl.inGuildValue(
    id: String,
    event: Any?,
    crossinline inGuild: suspend (KookGuildImpl) -> Unit
): Boolean {
    val initialJob = initialingGuildJob(id)
    if (initialJob != null) {
        launch {
            inGuild(initialJob.await())
        }
        return true
    }

    val internalGuild = internalGuild(id)
    if (internalGuild != null) {
        inGuild(internalGuild)
        return true
    }

    logger.warn("Unknown guild {} in event {}", id, event)
    return false
}

@OptIn(FragileSimbotAPI::class)
internal fun KookBotImpl.registerEvent() {
    val thisBot = this


    sourceBot.subscribe(SubscribeSequence.PREPARE) { rawEvent ->
        val event = this

        when (val ex = extra) {
            is TextExtra -> {
                val isBotSelf = thisBot.botUserInfo.id == event.authorId
                // 消息事件
                when (channelType) {
                    love.forte.simbot.kook.event.Event.ChannelType.PERSON -> {
                        if (isBotSelf) {
                            pushAndLaunch(KookBotSelfMessageEventImpl(thisBot, this.doAs(), rawEvent))
                        } else {
                            pushAndLaunch(KookContactMessageEventImpl(thisBot, this.doAs(), rawEvent))
                        }
                    }

                    love.forte.simbot.kook.event.Event.ChannelType.GROUP -> {
                        val guildId = ex.guildId!!

                        inGuildValue(guildId, event) { guild ->
                            val channel = internalChatChannel(targetId)
                                ?: run {
                                    logger.warn("Unknown channel {} in event {}", targetId, event)
                                    return@inGuildValue
                                }

                            val author = internalMember(guildId, authorId)
                                ?: run {
                                    logger.warn("Unknown member {} in event {}", authorId, event)
                                    return@inGuildValue
                                }

                            if (isBotSelf) {
                                pushAndLaunch(
                                    KookBotSelfChannelMessageEventImpl(
                                        thisBot,
                                        event.doAs(),
                                        channel,
                                        author,
                                        guild,
                                        rawEvent
                                    )
                                )
                            } else {
                                pushAndLaunch(
                                    KookChannelMessageEventImpl(
                                        thisBot,
                                        event.doAs(),
                                        author,
                                        channel,
                                        guild,
                                        rawEvent
                                    )
                                )
                            }
                        }
                    }

                    else -> {
                        logger.warn("Unknown channelType: {}", channelType)
                    }
                }
            }

            is SystemExtra -> {
                when (ex) {
                    // 某人退出频道服务器
                    is ExitedGuildEventExtra -> {
                        val guildId = event.targetId

                        inGuildValue(guildId, event) { guild ->
                            val removedMember = inCacheModify {
                                removeMember(guildId, ex.body.userId)
                            } ?: run {
                                logger.warn("No member ({}) removed in event {}", ex.body.userId, event)
                                return@inGuildValue
                            }

                            pushAndLaunch(
                                KookMemberExitedGuildEventImpl(
                                    thisBot,
                                    event.doAs(),
                                    guild,
                                    removedMember,
                                    rawEvent
                                )
                            )
                        }

                    }

                    // 某人加入频道服务器
                    is JoinedGuildEventExtra -> {
                        val guildId = event.targetId
                        val userId = ex.body.userId

                        inGuildValue(guildId, event) { guild ->
                            val userInfo = GetUserViewApi.create(userId, guildId)
                                .requestDataBy(thisBot)

                            val newMember = inCacheModify {
                                val newMember = KookMemberImpl(thisBot, userInfo, guildId)
                                setMember(guildId, userId, newMember)
                                newMember
                            }

                            pushAndLaunch(
                                KookMemberJoinedGuildEventImpl(
                                    thisBot,
                                    event.doAs(),
                                    guild,
                                    newMember,
                                    rawEvent
                                )
                            )
                        }

                    }

                    // 某成员进入某子频道
                    is JoinedChannelEventExtra -> {
                        val guildId = event.targetId
                        val userId = ex.body.userId
                        val channelId = ex.body.channelId

                        inGuildValue(guildId, event) { guild ->
                            val channel = internalChatChannel(channelId)
                                ?: run {
                                    logger.warn("Unknown channel {} in event {}", channelId, event)
                                    return@inGuildValue
                                }

                            val member = thisBot.internalMember(guildId, userId)
                                ?: run {
                                    logger.warn("Unknown member {} in event {}", userId, event)
                                    return@inGuildValue
                                }

                            pushAndLaunch(
                                KookMemberJoinedChannelEventImpl(
                                    thisBot,
                                    event.doAs(),
                                    guild,
                                    channel,
                                    member,
                                    rawEvent
                                )
                            )
                        }
                    }

                    // 某成员离开某子频道
                    is ExitedChannelEventExtra -> {
                        val guildId = event.targetId
                        val userId = ex.body.userId
                        val channelId = ex.body.channelId

                        inGuildValue(guildId, event) { guild ->
                            val channel = internalChatChannel(channelId)
                                ?: run {
                                    logger.warn("Unknown channel {} in event {}", channelId, event)
                                    return@inGuildValue
                                }

                            val member = thisBot.internalMember(guildId, userId)
                                ?: run {
                                    logger.warn("Unknown member {} in event {}", userId, event)
                                    return@inGuildValue
                                }

                            pushAndLaunch(
                                KookMemberExitedChannelEventImpl(
                                    thisBot,
                                    event.doAs(),
                                    guild,
                                    channel,
                                    member,
                                    rawEvent
                                )
                            )
                        }
                    }

                    // Bot 加入服务器
                    is SelfJoinedGuildEventExtra -> {
                        // init guild and await
                        val guild = thisBot.initialNewGuild(ex).await()

                        pushAndLaunch(
                            KookBotSelfJoinedGuildEventImpl(
                                thisBot,
                                event.doAs(),
                                guild,
                                rawEvent
                            )
                        )
                    }

                    // Bot 离开服务器
                    is SelfExitedGuildEventExtra -> {
                        val guildId = ex.body.guildId

                        val guild = inCacheModify {
                            // remove guilds
                            val removedGuild = guilds.remove(guildId)
                                ?: run {
                                    logger.warn("Unknown guild {} in event {}", guildId, event)
                                    return@inCacheModify null
                                }
                            // remove channels
                            channels.entries.removeAll { (_, v) -> v.source.guildId == guildId }
                            // remove members
                            members.entries.removeAll { (k, _) -> k.guildId == guildId }

                            removedGuild
                        } ?: return@subscribe

                        pushAndLaunch(
                            KookBotSelfExitedGuildEventImpl(
                                thisBot,
                                event.doAs(),
                                guild,
                                rawEvent
                            )
                        )
                    }

                    // 成员上线
                    is GuildMemberOnlineEventExtra -> {
                        pushAndLaunch(KookMemberOnlineEventImpl(thisBot, event.doAs(), rawEvent))
                    }

                    // 成员下线
                    is GuildMemberOfflineEventExtra -> {
                        pushAndLaunch(KookMemberOfflineEventImpl(thisBot, event.doAs(), rawEvent))
                    }

                    // 新增频道
                    is AddedChannelEventExtra -> {
                        val channelBody = ex.body
                        inGuildValue(channelBody.guildId, event) { guild ->
                            if (channelBody.isCategory) {
                                val category = inCacheModify {
                                    KookCategoryChannelImpl(
                                        bot = thisBot,
                                        source = channelBody,
                                        sourceCategory = KookCategoryImpl(thisBot, channelBody)
                                    ).also {
                                        categories[channelBody.id] = it
                                    }
                                }

                                pushAndLaunch(
                                    KookAddedCategoryEventImpl(
                                        thisBot,
                                        event.doAs(),
                                        guild,
                                        category,
                                        rawEvent
                                    )
                                )
                            } else {
                                val channel = inCacheModify {
                                    channelBody.toChatChannel(thisBot).also {
                                        channels[channelBody.id] = it
                                    }
                                }

                                pushAndLaunch(
                                    KookAddedChannelEventImpl(
                                        thisBot,
                                        event.doAs(),
                                        guild,
                                        channel,
                                        rawEvent
                                    )
                                )
                            }
                        }
                    }

                    // 更新频道
                    is UpdatedChannelEventExtra -> {
                        val channelBody = ex.body

                        inGuildValue(channelBody.guildId, event) { guild ->
                            if (channelBody.isCategory) {
                                val category = inCacheModify {
                                    // update category value
                                    KookCategoryChannelImpl(
                                        bot = thisBot,
                                        source = channelBody,
                                        sourceCategory = KookCategoryImpl(thisBot, channelBody)
                                    ).also {
                                        categories[channelBody.id] = it
                                    }
                                }

                                pushAndLaunch(
                                    KookUpdatedCategoryEventImpl(
                                        thisBot,
                                        event.doAs(),
                                        guild,
                                        category,
                                        rawEvent
                                    )
                                )
                            } else {
                                val channel = inCacheModify {
                                    channelBody.toChatChannel(thisBot).also {
                                        channels[channelBody.id] = it
                                    }
                                }

                                pushAndLaunch(
                                    KookUpdatedChannelEventImpl(
                                        thisBot,
                                        event.doAs(),
                                        guild,
                                        channel,
                                        rawEvent
                                    )
                                )
                            }
                        }
                    }

                    // 删除频道
                    is DeletedChannelEventExtra -> {
                        inGuildValue(event.targetId, event) { guild ->
                            val removed = inCacheModify {
                                channels.remove(ex.body.id) ?: categories.remove(ex.body.id)
                            } ?: run {
                                logger.warn("No channel or category ({}) removed in event {}", ex.body.id, event)
                                return@inGuildValue
                            }

                            when (removed) {
                                is KookChatChannelImpl -> {
                                    pushAndLaunch(
                                        KookDeletedChannelEventImpl(
                                            thisBot,
                                            event.doAs(),
                                            guild,
                                            removed,
                                            rawEvent
                                        )
                                    )
                                }

                                is KookCategoryChannelImpl -> {
                                    pushAndLaunch(
                                        KookDeletedCategoryEventImpl(
                                            thisBot,
                                            event.doAs(),
                                            guild,
                                            removed,
                                            rawEvent
                                        )
                                    )
                                }

                                else -> {
                                    logger.warn(
                                        "No chat channel or category ({}) removed in event {}",
                                        ex.body.id,
                                        event
                                    )
                                }
                            }
                        }
                    }

                    // 置顶消息
                    is PinnedMessageEventExtra -> {
                        val channelId = ex.body.channelId
                        inGuildValue(event.targetId, event) { guild ->
                            val channel = internalChatChannel(channelId)
                                ?: run {
                                    logger.warn("Unknown channel {} in event {}", channelId, event)
                                    return@inGuildValue
                                }

                            pushAndLaunch(
                                KookPinnedMessageEventImpl(
                                    thisBot,
                                    event.doAs(),
                                    guild,
                                    channel,
                                    rawEvent
                                )
                            )
                        }
                    }

                    // 取消置顶消息
                    is UnpinnedMessageEventExtra -> {
                        val channelId = ex.body.channelId
                        inGuildValue(event.targetId, event) { guild ->
                            val channel = internalChatChannel(channelId)
                                ?: run {
                                    logger.warn("Unknown channel {} in event {}", channelId, event)
                                    return@inGuildValue
                                }

                            pushAndLaunch(
                                KookUnpinnedMessageEventImpl(
                                    thisBot,
                                    event.doAs(),
                                    guild,
                                    channel,
                                    rawEvent
                                )
                            )
                        }
                    }

                    // 频道消息删除
                    is DeletedMessageEventExtra -> {
                        pushAndLaunch(KookDeletedChannelMessageEventImpl(thisBot, event.doAs(), rawEvent))
                    }

                    // 频道消息更新
                    is UpdatedMessageEventExtra -> {
                        pushAndLaunch(KookUpdatedChannelMessageEventImpl(thisBot, event.doAs(), rawEvent))
                    }

                    // 按钮点击
                    is MessageBtnClickEventExtra -> {
                        pushAndLaunch(
                            KookMessageBtnClickEventImpl(
                                thisBot,
                                event.doAs(),
                                rawEvent
                            )
                        )
                    }

                    // 服务器成员信息更新
                    is UpdatedGuildMemberEventExtra -> {
                        inGuildValue(event.targetId, event) { guild ->
                            var oldMember: KookMemberImpl? = null
                            val newNickname = ex.body.nickname

                            val newMember = thisBot.inCacheModify {
                                val key = memberCacheId(event.targetId, ex.body.userId)
                                members.computeValue(key) { _, old ->
                                    if (old == null) {
                                        return@computeValue null
                                    }

                                    // copy source
                                    oldMember = old
                                    val newSource = old.source.copyWithNewNickname(newNickname)
                                    KookMemberImpl(thisBot, newSource, old.guildIdValue)
                                }
                            }

                            if (oldMember == null || newMember == null) {
                                logger.warn("Unknown member {} in event {} for update.", event.targetId, event)
                                return@inGuildValue
                            }

                            pushAndLaunch(
                                KookMemberUpdatedEventImpl(
                                    thisBot,
                                    event.doAs(),
                                    guild,
                                    newMember,
                                    oldMember,
                                    rawEvent
                                )
                            )
                        }
                    }

                    // 用户信息更新
                    is UserUpdatedEventExtra -> {
                        pushAndLaunch(
                            KookUserUpdatedEventImpl(
                                thisBot,
                                event.doAs(),
                                rawEvent
                            )
                        )
                    }

                    // 私聊消息删除
                    is DeletedPrivateMessageEventExtra -> {
                        pushAndLaunch(
                            KookDeletedPrivateMessageEventImpl(
                                thisBot,
                                event.doAs(),
                                rawEvent
                            )
                        )
                    }

                    // 私聊消息更新
                    is UpdatedPrivateMessageEventExtra -> {
                        pushAndLaunch(
                            KookUpdatedPrivateMessageEventImpl(
                                thisBot,
                                event.doAs(),
                                rawEvent
                            )
                        )
                    }

                    else -> pushUnsupported(event, rawEvent)
                }


            }

            is UnknownExtra -> {
                pushUnsupported(event, rawEvent)
            }
        }


    }
}

@OptIn(DiscreetKookApi::class)
private fun KookBotImpl.pushUnsupported(event: KEvent<EventExtra>, sourceEventJson: String) {
    pushAndLaunch(
        UnsupportedKookEvent(
            this,
            event,
            sourceEventJson
        )
    )
}


private fun KookBotImpl.pushAndLaunch(event: Event): Job {
    return launch {
        eventProcessor.push(event)
            .onEachError { er ->
                logger.error("Event {} process on failure: {}", event, er.content.message, er.content)
            }
            .collect()
    }
}

@Throws(ClassCastException::class)
@Suppress("UNCHECKED_CAST")
private inline fun <reified T : EventExtra> KEvent<*>.doAs(): KEvent<T> = this as KEvent<T>


private fun KUser.copyWithNewNickname(nickname: String): KUser {
    if (this is UserCopyWithNewNickname) {
        return copy(nickname = nickname)
    }

    if (this is SimpleUser) {
        return copy(nickname = nickname)
    }

    return UserCopyWithNewNickname(nickname, this)
}

private data class UserCopyWithNewNickname(override val nickname: String, val source: KUser) : KUser by source
