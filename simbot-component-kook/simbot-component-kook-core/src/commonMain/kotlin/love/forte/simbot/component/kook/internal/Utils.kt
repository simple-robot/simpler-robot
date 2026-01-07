/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.component.kook.KookCategory
import love.forte.simbot.component.kook.KookChatChannel
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.util.requestData
import love.forte.simbot.kook.api.channel.DeleteChannelApi
import love.forte.simbot.kook.objects.Channel
import love.forte.simbot.kook.objects.typeValueOrNull

/**
 * 删除指定ID的频道
 */
internal suspend fun KookBot.deleteChannel(id: String, options: Array<out DeleteOption>) {
    val api = DeleteChannelApi.create(id)
    // 如果设置了忽略失败选项，则捕获异常
    if (StandardDeleteOption.IGNORE_ON_FAILURE in options) {
        runCatching { requestData(api) }
    } else {
        requestData(api)
    }
}

/**
 * 获取频道所属的分类
 * @return 分类对象，如果没有父分类则返回null
 */
internal fun Channel.category(bot: KookBotImpl): KookCategory? {
    return parentId.takeIf { it.isNotBlank() }
        ?.let { bot.internalCategory(it) }?.category
}


/**
 * 根据频道类型将 Channel 转换为对应的聊天频道实现
 * @param bot KookBot实现实例
 * @param type 频道类型，null时返回null
 * @return 对应的聊天频道实现，不支持的类型返回null
 */
internal fun Channel.toChatChannel(bot: KookBotImpl, type: Channel.Type? = typeValueOrNull): KookChatChannel {
    return when (type) {
        Channel.Type.VOICE -> toVoiceChannel(bot) // 语音频道
        else -> toSimpleChatChannel(bot) // 文字或其他任何未知类型
    }
}
