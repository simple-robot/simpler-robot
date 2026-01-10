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

package love.forte.simbot.component.qguild.channel

import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.qguild.forum.QGThread
import love.forte.simbot.component.qguild.forum.QGThreadCreator
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.forum.ThreadPublishResult
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.forum.Thread
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.qguild.model.Channel as QGSourceChannel

/**
 * 一个论坛类型的频道。
 * 是 [QGChannel] 的子类型之一。
 *
 * [QGForumChannel] 内包含一个类型为 [ChannelType.FORUM] 的频道信息。
 *
 * [QGForumChannel] 基于 [子频道][QGSourceChannel] 类型，
 * 提供有关帖子的相关功能，包括查询帖子、发帖、删贴等。
 *
 * 需要注意的是，[QGForumChannel] 只会在构建时检测频道类型，当一个 [QGForumChannel] 被持有，
 * 而在此期间此频道类型发生了改变，那么后续继续调用API则可能会引发 [IllegalStateException] 异常。
 *
 * @author ForteScarlet
 */
public interface QGForumChannel : QGNonTextChannel {
    /**
     * 表示此帖子频道的源频道。
     */
    override val source: QGSourceChannel

    /**
     * 查询当前子频道中的所有 [主题帖][QGThread]
     *
     * @throws QQGuildApiException api请求异常
     */
    public val threads: Collectable<QGThread>

    /**
     * 根据ID查询 [主题帖][QGThread]
     *
     * @throws QQGuildApiException api请求异常
     */
    @ST(blockingBaseName = "getThread", blockingSuffix = "", asyncBaseName = "getThread", reserveBaseName = "getThread")
    public suspend fun thread(id: ID): QGThread?

    /**
     * 获取一个用于发布帖子的 [QGThreadCreator]。
     *
     * @see QGThreadCreator
     */
    public fun threadCreator(): QGThreadCreator
}


/**
 * 发布一个 [Thread] 并得到其回执 [ThreadPublishResult]。
 *
 * @see QGForumChannel.threadCreator
 */
public suspend inline fun QGForumChannel.createThread(block: QGThreadCreator.() -> Unit): ThreadPublishResult {
    return threadCreator().apply(block).publish()
}
