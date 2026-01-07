/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.internal.message

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.onebot.v11.core.api.DeleteMsgApi
import love.forte.simbot.component.onebot.v11.core.api.SendMsgResult
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.message.OneBotMessageReceipt


/**
 * @author ForteScarlet
 */
internal class OneBotMessageReceiptImpl(
    override val messageId: ID,
    private val bot: OneBotBotImpl,
) : OneBotMessageReceipt {
    override suspend fun delete(vararg options: DeleteOption) {
        kotlin.runCatching {
            bot.executeData(DeleteMsgApi.create(messageId))
        }.onFailure { ex ->
            if (StandardDeleteOption.IGNORE_ON_FAILURE !in options) {
                throw ex
            }
        }
    }
}


internal fun SendMsgResult.toReceipt(bot: OneBotBotImpl): OneBotMessageReceiptImpl =
    OneBotMessageReceiptImpl(messageId, bot)
