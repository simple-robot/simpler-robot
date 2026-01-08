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

package love.forte.simbot.qguild.api.user

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import love.forte.simbot.qguild.api.ApiDescription
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.api.user.GetBotInfoApi.Description
import love.forte.simbot.qguild.model.User


/**
 *
 * [获取用户详情](https://bot.q.qq.com/wiki/develop/api/openapi/user/me.html)
 *
 * 用于获取当前用户（机器人）详情。
 *
 * 由于 [GetBotInfoApi] 本身为 `object` 类型, 因此 [ApiDescription] 由内部对象 [Description] 提供而不是伴生对象。
 *
 * [GetBotInfoApi] 得到的 [User] 中，[User.isBot] 始终为 `true`。
 *
 * @author ForteScarlet
 */
public object GetBotInfoApi : GetQQGuildApi<User>() {
    /**
     * [GetBotInfoApi] 的 [ApiDescription] 实现。
     */
    public object Description : SimpleGetApiDescription("/users/@me")

    override val path: Array<String> = arrayOf("users", "@me")
    override val resultDeserializationStrategy: DeserializationStrategy<User> = BotInfoDeserializationStrategy
}

private object BotInfoDeserializationStrategy : DeserializationStrategy<User> {
    private val serializer = User.serializer()
    override val descriptor: SerialDescriptor = serializer.descriptor

    override fun deserialize(decoder: Decoder): User {
        return serializer.deserialize(decoder).copy(isBot = true)
    }
}
