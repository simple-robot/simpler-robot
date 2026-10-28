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

package love.forte.simbot.qguild.api.announces

import love.forte.simbot.qguild.api.DeleteQQGuildApi
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.SimpleDeleteApiDescription
import kotlin.jvm.JvmStatic


/**
 *
 * [删除子频道公告](https://bot.q.qq.com/wiki/develop/api/openapi/announces/delete_channel_announces.html)
 *
 * 机器人删除指定子频道公告
 *
 * @author ForteScarlet
 */
public class DeleteAnnouncesApi private constructor(
    channelId: String,
    messageId: String,
) : QQGuildApiWithoutResult, DeleteQQGuildApi<Unit>() {

    public companion object Factory : SimpleDeleteApiDescription(
        "/channels/{channel_id}/announces/{message_id}"
    ) {

        /**
         * 构造 [DeleteAnnouncesApi]
         */
        @JvmStatic
        public fun create(channelId: String, messageId: String): DeleteAnnouncesApi =
            DeleteAnnouncesApi(channelId, messageId)
    }

    override val path: Array<String> = arrayOf("channels", channelId, "announces", messageId)
}
