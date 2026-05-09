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

package love.forte.simbot.component.kook.message

import io.ktor.http.*
import love.forte.simbot.ability.*
import love.forte.simbot.ability.StandardDeleteOption.Companion.standardAnalysis
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.component.kook.util.requestResultBy
import love.forte.simbot.kook.api.message.DeleteChannelMessageApi
import love.forte.simbot.kook.api.message.DeleteDirectMessageApi
import love.forte.simbot.kook.api.message.SendMessageResult
import love.forte.simbot.message.AggregatedMessageReceipt
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.message.SingleMessageReceipt
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * Kook 进行消息回复、发送后得到的回执。
 *
 * @see SingleKookMessageReceipt
 * @see KookAggregatedMessageReceipt
 */
public interface KookMessageReceipt : MessageReceipt

/**
 * 用于表示 [SingleMessageReceipt] 的 [KookMessageReceipt] 实现。
 *
 * @see KookMessageCreatedReceipt
 * @see KookApiRequestedReceipt
 */
public abstract class SingleKookMessageReceipt : SingleMessageReceipt(), KookMessageReceipt {
    /**
     * 此次消息发送的回执内容。
     *
     */
    public abstract val result: Any?

    /**
     * 是否为私聊消息。
     */
    public abstract val isDirect: Boolean
}

/**
 * 消息创建后的回执实例。
 *
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
public class KookMessageCreatedReceipt(
    override val result: SendMessageResult,
    override val isDirect: Boolean,
    private val bot: KookBot,
) : SingleKookMessageReceipt() {
    override val id: ID
        get() = result.msgId.ID

    public val nonce: String? get() = result.nonce

    /**
     * 消息发送时间(服务器时间戳)
     */
    public val timestamp: Timestamp
        get() = Timestamp.ofMilliseconds(result.msgTimestamp)

    /**
     * 尝试删除（撤回）发送的这条消息。
     */
    @JvmSynthetic
    override suspend fun delete(vararg options: DeleteOption) {
        val api =
            if (isDirect) {
                DeleteDirectMessageApi.create(result.msgId)
            } else {
                DeleteChannelMessageApi.create(result.msgId)
            }

        val stdOpts = options.standardAnalysis()

        val apiResult = api.requestResultBy(bot)

        if (apiResult.isHttpSuccess && apiResult.isSuccess) {
            return
        }

        val httpStatus = apiResult.httpStatus
        if (httpStatus.value == HttpStatusCode.NotFound.value) {
            if (stdOpts.isIgnoreOnNoSuchTarget) {
                return
            }

            throw NoSuchElementException(
                "Delete target (msgId=${result.msgId}) not found: HTTP code 404 with result $apiResult"
            )
        }

        if (stdOpts.isIgnoreOnFailure) {
            return
        }

        throw DeleteFailureException(
            "Delete message (msgId=${result.msgId}) failed. HTTP status: $httpStatus, result: $apiResult"
        )
    }

    public companion object {
        internal fun SendMessageResult.asReceipt(
            direct: Boolean,
            bot: KookBot,
        ): KookMessageCreatedReceipt = KookMessageCreatedReceipt(this, direct, bot)
    }

}

/**
 * 消息发送后的回执。
 *
 * 与 [KookMessageCreatedReceipt] 不同的是，
 * [KookApiRequestedReceipt] 很可能并不是通过执行普通的消息api得到的，
 * 例如通过 [KookApiMessage] 执行了一个任意地请求。
 *
 * 也正因此，此回执不支持 [删除][delete] 操作。当使用 [delete] 时将会恒返回 `false`.
 *
 */
public class KookApiRequestedReceipt(
    override val result: Any?,
    override val isDirect: Boolean,
) : SingleKookMessageReceipt() {
    override val id: ID = UUID.random()

    /**
     * 不支持撤回，将会始终得到 `false`。
     *
     */
    override suspend fun delete(vararg options: DeleteOption) {
        if (options.any { it == StandardDeleteOption.IGNORE_ON_UNSUPPORTED }) {
            return
        }

        throw UnsupportedOperationException(
            buildString {
                append("KookApiRequestedReceipt.delete(")
                options.joinTo(buffer = this, separator = ",", prefix = "[", postfix = "]")
                append(")")
            }
        )
    }
}

/**
 * 多条消息发送后的回执，其中会包含多个 [KookMessageReceipt]。
 */
@ExperimentalSimbotAPI
public class KookAggregatedMessageReceipt private constructor(
    private val receipts: List<SingleKookMessageReceipt>,
) : AggregatedMessageReceipt(), KookMessageReceipt {
    override val size: Int
        get() = receipts.size

    override fun get(index: Int): SingleKookMessageReceipt = receipts[index]

    override fun iterator(): Iterator<SingleKookMessageReceipt> = receipts.iterator()

    public companion object {
        /**
         * 将多个 [KookMessageReceipt] 合并为一个聚合的回执。
         *
         * @throws IllegalArgumentException 如果 receiver 中元素为空
         */
        @JvmStatic
        public fun Collection<SingleKookMessageReceipt>.merge(): KookMessageReceipt {
            require(isNotEmpty()) { "Unable to merge empty element iterator" }
            return KookAggregatedMessageReceipt(toList())
        }
    }

}
