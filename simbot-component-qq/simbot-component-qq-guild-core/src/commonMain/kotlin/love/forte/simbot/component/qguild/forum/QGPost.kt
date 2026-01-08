/*
 * Copyright (c) 2023-2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.forum

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.IDContainer
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.QGObjectiveContainer
import love.forte.simbot.component.qguild.event.QGForumPostEvent
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.component.qguild.utils.toTimestamp
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.model.forum.Post
import love.forte.simbot.suspendrunner.STP


/**
 * 在 [QGForumPostEvent] 获取到的帖子评论信息。
 *
 * @author ForteScarlet
 */
@STP
public interface QGPost : QGObjectiveContainer<Post>,
    QGForumInfoContainer, IDContainer {
    /**
     * 评论信息的源信息
     */
    override val source: Post

    /**
     * 此回复的ID
     */
    override val id: ID
        get() = source.postInfo.postId.ID

    /**
     * 频道ID
     */
    override val guildId: ID
        get() = source.guildId.ID

    /**
     * 子频道ID
     */
    override val channelId: ID
        get() = source.channelId.ID

    /**
     * 作者ID
     */
    override val authorId: ID
        get() = source.authorId.ID

    /**
     * 此评论对应的主题帖ID
     */
    public val threadId: ID
        get() = source.postInfo.threadId.ID

    /**
     * 内容
     */
    public val content: String
        get() = source.postInfo.content

    /**
     * 评论时间
     */
    @OptIn(ExperimentalQGApi::class)
    public val datetime: Timestamp
        get() = source.postInfo.dateTime.toTimestamp()

    /**
     * 在频道中的评论者
     *
     * @throws QQGuildApiException API请求产生异常
     * @throws NoSuchElementException 如果此成员已不存在
     */
    public suspend fun author(): QGMember

    /**
     * 此评论所属的主题
     *
     * @throws QQGuildApiException API请求产生异常
     * @throws NoSuchElementException 所属主题已不存在
     */
    public suspend fun thread(): QGThread
}
