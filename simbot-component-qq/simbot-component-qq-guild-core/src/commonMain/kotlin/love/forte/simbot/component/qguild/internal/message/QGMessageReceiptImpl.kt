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

package love.forte.simbot.component.qguild.internal.message

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.message.QGAggregatedIdMessageReceipt
import love.forte.simbot.component.qguild.message.QGAggregatedMessageReceipt
import love.forte.simbot.component.qguild.message.QGSingleIdMessageReceipt
import love.forte.simbot.component.qguild.message.QGSingleMessageReceipt
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

private class QGSingleIdMessageReceiptImpl(override val id: ID) : QGSingleIdMessageReceipt()

private class QGAggregatedIdMessageReceiptImpl(private val messages: List<QGSingleIdMessageReceiptImpl>) :
    QGAggregatedIdMessageReceipt() {
    override val size: Int get() = messages.size
    override fun get(index: Int): QGSingleIdMessageReceipt = messages[index]
    override fun iterator(): Iterator<QGSingleIdMessageReceipt> = messages.iterator()
}

@PublishedApi
internal fun GroupMessageSendResult.asReceipt(): QGSingleIdMessageReceipt = QGSingleIdMessageReceiptImpl(id.ID)

@PublishedApi
internal fun Iterable<GroupMessageSendResult>.asGroupReceipt(): QGAggregatedIdMessageReceipt =
    QGAggregatedIdMessageReceiptImpl(
        map { QGSingleIdMessageReceiptImpl(it.id.ID) }
    )

@PublishedApi
internal fun UserMessageSendResult.asReceipt(): QGSingleIdMessageReceipt = QGSingleIdMessageReceiptImpl(id.ID)


@PublishedApi
internal fun Iterable<UserMessageSendResult>.asUserReceipt(): QGAggregatedIdMessageReceipt =
    QGAggregatedIdMessageReceiptImpl(
        map { QGSingleIdMessageReceiptImpl(it.id.ID) }
    )
