/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.bot

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.internal.channel.QGTextChannelImpl
import love.forte.simbot.component.qguild.internal.channel.toTextChannel
import love.forte.simbot.component.qguild.internal.event.*
import love.forte.simbot.component.qguild.internal.guild.QGGuildImpl
import love.forte.simbot.component.qguild.internal.guild.QGGuildImpl.Companion.qgGuild
import love.forte.simbot.component.qguild.internal.guild.QGMemberImpl
import love.forte.simbot.event.Event
import love.forte.simbot.event.onEachError
import love.forte.simbot.qguild.api.channel.GetChannelApi
import love.forte.simbot.qguild.event.*
import love.forte.simbot.qguild.model.isCategory
import love.forte.simbot.qguild.stdlib.DisposableHandle
import love.forte.simbot.qguild.stdlib.requestDataBy

private fun QGBotImpl.guild0(eventGuild: EventGuild): QGGuildImpl = qgGuild(this, eventGuild)

private suspend fun QGBotImpl.channel0(eventChannel: EventChannel, currentMsgId: String? = null): QGTextChannelImpl {
    val channel = GetChannelApi.create(eventChannel.id).requestDataBy(source)
    return channel.toTextChannel(this, currentMsgId = currentMsgId)
}

private fun QGBotImpl.member0(eventMember: EventMember): QGMemberImpl =
    QGMemberImpl(this, eventMember, eventMember.guildId.ID)

/**
 * simbot针对QQ频道注册的时候只注册一个普通处理函数。
 */
internal fun QGBotImpl.registerEventProcessor(): DisposableHandle {
    val bot = this
    return source.subscribe { raw ->
        when (val event = this) {
            //region Guild相关
            is GuildCreate -> {
                val guild = guild0(event.data)
                pushEvent {
                    QGGuildCreateEventImpl(raw, event.data, bot, guild)
                }
            }

            is GuildUpdate -> {
                val guild = guild0(event.data)
                pushEvent {
                    QGGuildUpdateEventImpl(raw, event.data, bot, guild)
                }
            }

            is GuildDelete -> {
                val guild = guild0(event.data)
                pushEvent {
                    QGGuildDeleteEventImpl(raw, event.data, bot, guild)
                }
            }
            //endregion

            //region Channel相关
            is ChannelCreate -> {
                if (event.data.type.isCategory) {
                    bot.logger.warn(
                        "Received category create event [raw={}]. report this log to issues https://github.com/simple-robot/simbot-component-qq-guild/issues/new/choose",
                        raw
                    )
                }

                val channel = channel0(event.data)
                pushEvent {
                    QGChannelCreateEventImpl(raw, event.data, bot, channel)
                }

            }

            is ChannelUpdate -> {
                if (event.data.type.isCategory) {
                    bot.logger.warn(
                        "Received category update event [raw={}]. report this log to issues https://github.com/simple-robot/simbot-component-qq-guild/issues/new/choose",
                        raw
                    )
                }
                val channel = channel0(event.data)
                pushEvent {
                    QGChannelUpdateEventImpl(raw, event.data, bot, channel)
                }
            }

            is ChannelDelete -> {
                if (event.data.type.isCategory) {
                    bot.logger.warn(
                        "Received category delete event [raw={}]. report this log to issues https://github.com/simple-robot/simbot-component-qq-guild/issues/new/choose",
                        raw
                    )
                }

                val channel = channel0(event.data)
                pushEvent {
                    QGChannelDeleteEventImpl(raw, event.data, bot, channel)
                }
            }
            //endregion

            //region Member相关
            is GuildMemberAdd -> {
                val member = member0(event.data)
                pushEvent {
                    QGMemberAddEventImpl(bot, raw, event.data, member)
                }
            }

            is GuildMemberUpdate -> {
                val member = member0(event.data)
                pushEvent {
                    QGMemberUpdateEventImpl(bot, raw, event.data, member)
                }
            }

            is GuildMemberRemove -> {
                val member = member0(event.data)
                pushEvent {
                    QGMemberRemoveEventImpl(bot, raw, event.data, member)
                }
            }
            //endregion


            // 消息
            /// 频道
            is AtMessageCreate -> {
                pushEvent { QGAtMessageCreateEventImpl(bot, raw, event.data, event.id) }
            }
            /// 频道单聊
            is DirectMessageCreate -> {
                pushEvent { QGDirectMessageCreateEventImpl(bot, raw, event.data, event.id) }
            }
            /// 群&单聊
            is GroupAtMessageCreate -> {
                pushEvent { QGGroupAtMessageCreateEventImpl(bot, raw, event, event.id) }
            }

            is C2CMessageCreate -> {
                pushEvent { QGC2CMessageCreateEventImpl(bot, raw, event, event.id) }
            }

            // 群聊：managements
            is GroupAddRobot -> {
                pushEvent { QGGroupAddRobotEventImpl(event.id, bot, raw, event.data) }
            }
            is GroupDelRobot -> {
                pushEvent { QGGroupDelRobotEventImpl(event.id, bot, raw, event.data) }
            }
            is GroupMsgReject -> {
                pushEvent { QGGroupMsgRejectEventImpl(event.id, bot, raw, event.data) }
            }
            is GroupMsgReceive -> {
                pushEvent { QGGroupMsgReceiveEventImpl(event.id, bot, raw, event.data) }
            }
            // C2C: managements
            is FriendAdd -> {
                pushEvent { QGFriendAddEventImpl(event.id, bot, raw, event.data) }
            }
            is FriendDel -> {
                pushEvent { QGFriendDelEventImpl(event.id, bot, raw, event.data) }
            }
            is C2CMsgReject -> {
                pushEvent { QGC2CMsgRejectEventImpl(event.id, bot, raw, event.data) }
            }
            is C2CMsgReceive -> {
                pushEvent { QGC2CMsgReceiveEventImpl(event.id, bot, raw, event.data) }
            }

            // OpenForum
            is OpenForumDispatch -> when (event) {
                is OpenForumThreadCreate -> pushEvent { QGOpenForumThreadCreateEventImpl(bot, raw, event.data) }
                is OpenForumThreadDelete -> pushEvent { QGOpenForumThreadDeleteEventImpl(bot, raw, event.data) }
                is OpenForumThreadUpdate -> pushEvent { QGOpenForumThreadUpdateEventImpl(bot, raw, event.data) }
                is OpenForumPostCreate -> pushEvent { QGOpenForumPostCreateEventImpl(bot, raw, event.data) }
                is OpenForumPostDelete -> pushEvent { QGOpenForumPostDeleteEventImpl(bot, raw, event.data) }
                is OpenForumReplyCreate -> pushEvent { QGOpenForumReplyCreateEventImpl(bot, raw, event.data) }
                is OpenForumReplyDelete -> pushEvent { QGOpenForumReplyDeleteEventImpl(bot, raw, event.data) }
            }

            // Forum
            is ForumDispatch -> when (event) {
                is ForumPostCreate -> pushEvent { QGForumPostCreateEventImpl(bot, raw, event.data) }
                is ForumPostDelete -> pushEvent { QGForumPostDeleteEventImpl(bot, raw, event.data) }
                is ForumReplyCreate -> pushEvent { QGForumReplyCreateEventImpl(bot, raw, event.data) }
                is ForumReplyDelete -> pushEvent { QGForumReplyDeleteEventImpl(bot, raw, event.data) }
                is ForumThreadCreate -> pushEvent { QGForumThreadCreateEventImpl(bot, raw, event.data) }
                is ForumThreadDelete -> pushEvent { QGForumThreadDeleteEventImpl(bot, raw, event.data) }
                is ForumThreadUpdate -> pushEvent { QGForumThreadUpdateEventImpl(bot, raw, event.data) }
                is ForumPublishAuditResult -> pushEvent { QGForumPublishAuditResultEventImpl(bot, raw, event.data) }
            }


            // unsupported
            else -> {
                pushEvent { QGUnsupportedEventImpl(bot, event, raw) }
            }
        }
    }


}


private inline fun QGBotImpl.pushEvent(crossinline block: () -> Event): Job {
    return launch {
        val event = block()
        eventDispatcher
            .push(event)
            .onEachError { e ->
                logger.error("Event {} process failed: {}", event, e, e.content)
            }
            .collect()
    }
}
