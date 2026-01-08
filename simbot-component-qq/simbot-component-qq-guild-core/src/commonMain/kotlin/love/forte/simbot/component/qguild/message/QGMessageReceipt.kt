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

package love.forte.simbot.component.qguild.message

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.message.AggregatedMessageReceipt
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.message.SingleMessageReceipt
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.model.Message
import kotlin.jvm.JvmSynthetic


/**
 * QQ频道中消息发送后的回执。可能代表一个 [单回执][QGSingleMessageReceipt]
 * 或一个 [聚合回执][QGAggregatedMessageReceipt]。
 *
 * 在发送消息时，如果消息链中出现了无法在同一次 [MessageSendApi] 中就请求的内容，
 * 则会将请求拆分为多个请求，并将多个消息合并为一个 [QGAggregatedMessageReceipt]。
 *
 * 更多说明参考 [sendMessage][love.forte.simbot.component.qguild.message.sendMessage] 的文档说明。
 *
 * @see QGSingleMessageReceipt
 * @see QGAggregatedMessageReceipt
 *
 * @author ForteScarlet
 */
public interface QGMessageReceipt : MessageReceipt {
    /**
     * 消息暂时不支持撤回。
     * 如果 [options] 不包含 [StandardDeleteOption.IGNORE_ON_UNSUPPORTED]
     * 则会抛出 [UnsupportedOperationException] 异常。
     */
    @JvmSynthetic
    public override suspend fun delete(vararg options: DeleteOption) {
        if (options.none { it == StandardDeleteOption.IGNORE_ON_UNSUPPORTED }) {
            throw UnsupportedOperationException("QGMessageReceipt.delete")
        }
    }
}

/**
 * 代表为一次消息发送请求后的回执结果，是 [QGAggregatedMessageReceipt] 的元素类型。
 */
public abstract class QGSingleMessageReceipt : SingleMessageReceipt(), QGMessageReceipt {
    /**
     * QQ频道消息发送api发送消息后得到的回执，也就是消息对象。
     */
    public abstract val messageResult: Message
}

/**
 * 多个 [QGSingleMessageReceipt] 聚合后的聚合回执。
 *
 */
public abstract class QGAggregatedMessageReceipt : AggregatedMessageReceipt(), QGMessageReceipt {
    /**
     * 聚合内容的数量，通常来讲会 `> 1` 。
     */
    abstract override val size: Int

    /**
     * 获取指定索引位的 [QGSingleMessageReceipt]。
     *
     * @throws IndexOutOfBoundsException 索引越界
     */
    abstract override fun get(index: Int): QGSingleMessageReceipt

    /**
     * 得到所有的 [QGSingleMessageReceipt] 的迭代器。
     */
    abstract override fun iterator(): Iterator<QGSingleMessageReceipt>

    override suspend fun delete(vararg options: DeleteOption) {
        super<AggregatedMessageReceipt>.delete(*options)
    }
}

/**
 * 仅有ID信息的 [QGMessageReceipt]. 通常来自发送群消息或好友消息。
 */
public abstract class QGSingleIdMessageReceipt : SingleMessageReceipt(), QGMessageReceipt {
    abstract override val id: ID
}

/**
 * 多个 [QGSingleIdMessageReceipt] 的集合回执。
 */
public abstract class QGAggregatedIdMessageReceipt : AggregatedMessageReceipt(), QGMessageReceipt {
    abstract override val size: Int

    abstract override fun get(index: Int): QGSingleIdMessageReceipt

    abstract override fun iterator(): Iterator<QGSingleIdMessageReceipt>

    override suspend fun delete(vararg options: DeleteOption) {
        super<AggregatedMessageReceipt>.delete(*options)
    }
}
