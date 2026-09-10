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

package love.forte.simbot.component.qguild.internal.message

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.message.*
import love.forte.simbot.qguild.api.message.group.GroupMessageSendResult
import love.forte.simbot.qguild.api.message.user.UserMessageSendResult
import love.forte.simbot.qguild.model.Message


private class QGSingleMessageReceiptImpl(override val messageResult: Message) : QGSingleMessageReceipt() {
    override val id: ID = messageResult.id.ID
}

@PublishedApi
internal fun Message.asReceipt(): QGSingleMessageReceipt = QGSingleMessageReceiptImpl(this)


private class QGAggregatedMessageReceiptImpl(private val messages: List<QGSingleMessageReceiptImpl>) :
    QGAggregatedMessageReceipt() {
    override val size: Int get() = messages.size

    override fun get(index: Int): QGSingleMessageReceipt = messages[index]

    override fun iterator(): Iterator<QGSingleMessageReceipt> = messages.iterator()
}

internal fun Iterable<Message>.asReceipt(): QGAggregatedMessageReceipt =
    QGAggregatedMessageReceiptImpl(this.map { QGSingleMessageReceiptImpl(it) })

private class QGGroupMessageReceiptImpl(
    private val bot: QGBot,
    private val groupOpenid: String,
    override val id: ID,
) : QGSingleIdMessageReceipt() {
    override suspend fun delete(vararg options: DeleteOption) {
        bot.deleteGroupMessage(groupOpenid, id.literal, *options)
    }
}

private class QGUserMessageReceiptImpl(
    private val bot: QGBot,
    private val userOpenid: String,
    override val id: ID,
) : QGSingleIdMessageReceipt() {
    override suspend fun delete(vararg options: DeleteOption) {
        bot.deleteUserMessage(userOpenid, id.literal, *options)
    }
}

private class QGAggregatedIdMessageReceiptImpl(private val messages: List<QGSingleIdMessageReceipt>) :
    QGAggregatedIdMessageReceipt() {
    override val size: Int get() = messages.size
    override fun get(index: Int): QGSingleIdMessageReceipt = messages[index]
    override fun iterator(): Iterator<QGSingleIdMessageReceipt> = messages.iterator()
}

@PublishedApi
internal fun GroupMessageSendResult.asReceipt(
    bot: QGBot,
    groupOpenid: String,
): QGSingleIdMessageReceipt = QGGroupMessageReceiptImpl(bot, groupOpenid, id.ID)

@PublishedApi
internal fun Iterable<GroupMessageSendResult>.asGroupReceipt(
    bot: QGBot,
    groupOpenid: String,
): QGAggregatedIdMessageReceipt =
    QGAggregatedIdMessageReceiptImpl(
        map { QGGroupMessageReceiptImpl(bot, groupOpenid, it.id.ID) }
    )

@PublishedApi
internal fun UserMessageSendResult.asReceipt(
    bot: QGBot,
    userOpenid: String,
): QGSingleIdMessageReceipt = QGUserMessageReceiptImpl(bot, userOpenid, id.ID)


@PublishedApi
internal fun Iterable<UserMessageSendResult>.asUserReceipt(
    bot: QGBot,
    userOpenid: String,
): QGAggregatedIdMessageReceipt =
    QGAggregatedIdMessageReceiptImpl(
        map { QGUserMessageReceiptImpl(bot, userOpenid, it.id.ID) }
    )
