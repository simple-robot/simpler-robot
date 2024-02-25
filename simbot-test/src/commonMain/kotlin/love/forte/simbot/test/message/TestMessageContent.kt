/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.test.message

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.Messages
import love.forte.simbot.message.PlainText
import love.forte.simbot.message.emptyMessages


/**
 *
 * @author ForteScarlet
 */
public class TestMessageContent(
    override var id: ID = UUID.random(),
    override var messages: Messages = emptyMessages(),
    override var plainText: String? = messages.asSequence().filterIsInstance<PlainText>().joinToString("") { it.text },
    public var onDelete: (Array<out DeleteOption>) -> Unit = {}
) : MessageContent {
    override suspend fun delete(vararg options: DeleteOption) {
        onDelete(options)
    }
}
