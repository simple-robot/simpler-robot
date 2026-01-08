/*
 * Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.friend

import love.forte.simbot.common.id.ID
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.message.QGMedia
import love.forte.simbot.definition.Contact
import love.forte.simbot.qguild.ExperimentalQGMediaApi
import love.forte.simbot.resource.Resource
import love.forte.simbot.suspendrunner.ST
import kotlin.coroutines.CoroutineContext


/**
 * 一个QQ的C2C单聊用户目标。
 * 比较类似QQ好友的概念，但实际上并不太一样，
 * [QGFriend] 通常来自消息事件，且只能得知其 openid 信息。
 *
 * @author ForteScarlet
 */
public interface QGFriend : Contact {
    override val coroutineContext: CoroutineContext
    override val id: ID

    /**
     * QQ单聊场景下无法获取用户名，始终得到空字符串。
     */
    override val name: String
        get() = ""

    /**
     * QQ单聊场景下无法获取用户头像，始终得到 `null`。
     */
    override val avatar: String?
        get() = null

    /**
     * 上传一个资源为用于向QQ单聊发送的 [QGMedia], 可用于后续的发送。
     *
     * 目前上传仅支持使用链接，QQ平台会对此链接进行转存。
     *
     * @param url 目标链接
     * @param type 媒体类型。
     *
     * > 1 图片，2 视频，3 语音，4 文件（暂不开放） 资源格式要求: 图片：png/ jpg，视频：mp4，语音：silk
     *
     * @see QGBot.uploadUserMedia
     */
    @ST
    public suspend fun uploadMedia(
        url: String,
        type: Int,
    ): QGMedia

    /**
     * 上传一个资源为用于向QQ单聊发送的 [QGMedia], 可用于后续的发送。
     *
     * @param resource 目标数据。如果是 `URIResource` 则会使用 `url`，否则通过 `file_data` 上传。
     * @param type 媒体类型。
     *
     * > 1 图片，2 视频，3 语音，4 文件（暂不开放） 资源格式要求: 图片：png/ jpg，视频：mp4，语音：silk
     *
     * @see QGBot.uploadUserMedia
     *
     * @since 4.1.1
     */
    @ST
    @ExperimentalQGMediaApi
    public suspend fun uploadMedia(
        resource: Resource,
        type: Int,
    ): QGMedia
}
