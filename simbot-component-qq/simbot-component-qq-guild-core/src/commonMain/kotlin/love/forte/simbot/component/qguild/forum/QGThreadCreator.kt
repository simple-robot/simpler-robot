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

import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.forum.ThreadPublishFormat
import love.forte.simbot.qguild.api.forum.ThreadPublishResult
import love.forte.simbot.suspendrunner.ST


/**
 * 用于构建并发布帖子的构建器。
 *
 * @author ForteScarlet
 */
public interface QGThreadCreator {
    //// DSL API

    /**
     * 帖子标题
     */
    public var title: String?

    /**
     * 帖子内容
     */
    public var content: String?

    /**
     * 帖子文本格式
     */
    public var format: Int?

    /**
     * 帖子文本格式
     *
     * @see format
     */
    public var formatType: ThreadPublishFormat?

    //// Java API

    /**
     * 帖子标题
     */
    public fun title(title: String): QGThreadCreator = also { this.title = title }

    /**
     * 帖子内容
     */
    public fun content(content: String): QGThreadCreator = also { this.content = content }

    /**
     * 帖子文本格式
     */
    public fun format(format: Int): QGThreadCreator = also { this.format = format }

    /**
     * 帖子文本格式
     */
    public fun format(format: ThreadPublishFormat): QGThreadCreator = also { this.formatType = format }

    //// Common API

    /**
     * 使用 [ThreadPublishFormat.TEXT] 作为 [format]
     */
    public fun textFormat(): QGThreadCreator = also { format(ThreadPublishFormat.TEXT) }

    /**
     * 使用 [ThreadPublishFormat.HTML] 作为 [format]
     */
    public fun htmlFormat(): QGThreadCreator = also { format(ThreadPublishFormat.HTML) }

    /**
     * 使用 [ThreadPublishFormat.MARKDOWN] 作为 [format]
     */
    public fun markdownFormat(): QGThreadCreator = also { format(ThreadPublishFormat.MARKDOWN) }

    /**
     * 使用 [ThreadPublishFormat.JSON] 作为 [format]
     */
    public fun jsonFormat(): QGThreadCreator = also { format(ThreadPublishFormat.JSON) }

    /**
     * 发布此帖子。
     *
     * @throws QQGuildApiException api请求异常
     * @throws IllegalArgumentException 某属性未设置值时
     *
     * @return 帖子发布后的回执
     */
    @ST
    public suspend fun publish(): ThreadPublishResult

}
