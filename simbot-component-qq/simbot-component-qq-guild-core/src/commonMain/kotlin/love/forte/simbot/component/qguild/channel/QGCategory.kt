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

package love.forte.simbot.component.qguild.channel

import love.forte.simbot.common.id.ID
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.definition.Category
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.model.Channel
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP
import love.forte.simbot.qguild.model.Channel as QGSourceChannel


/**
 * 当一个频道的 [QGSourceChannel.type] 的值等于 [ChannelType.CATEGORY] 时，
 * 此频道代表为一个分组。
 *
 * [QGCategory] 是一个**仅存在ID**的 QQ频道子频道分组实现。QQ频道对于子频道分组类型的变更不会推送事件，
 * 因此无法内建缓存，而如果每次事件都要实时查询channel的分组则可能有些多余 —— 毕竟分组可能并不是一个高频使用的对象。
 *
 * 因此在 [QGTextChannel.category] 中我们仅提供 [QGCategory] 类型来直接提供分组ID，
 * 并在有需要的时候通过 [resolveToChannel] 查询并获取真正的对象实例。
 *
 * [QGCategory] 中 [id] 为子频道分组的ID，[name] 由于需要通过API查询，
 * 此处将直接返回 [id] 的字符串值。
 *
 * 通过 [QGGuild.categories] 或 [QGGuild.category] 获取的结果均为实时查询结果，
 * 它们是具体的 [QGCategory] 类型，其中已经包含了具体信息。
 *
 * @see QGCategory
 */
public interface QGCategory : Category {

    /**
     * 当前子频道分组ID
     */
    override val id: ID

    /**
     * 当前子频道分组的名称。会通过 [resolveToChannel]
     * 获取子频道信息后返回名称。
     *
     * @see resolveToChannel
     */
    @STP
    override suspend fun name(): String?

    /**
     * 根据当前ID**初始化**并得到一个具体的分组频道 [QGNonTextChannel] 对象实例。
     *
     * @throws NoSuchElementException 当未查询到结果（例如查询时分组已被删除）
     * @throws QQGuildApiException api查询期间得到的任何异常
     */
    @ST
    public suspend fun resolveToChannel(): QGCategoryChannel

}

/**
 * 一个用来表示“分组”的频道类型。
 * 通过 [QGCategory.resolveToChannel] 得到。
 *
 */
public interface QGCategoryChannel : QGNonTextChannel {
    override val source: Channel

    /**
     * 分组类型的子频道的分组就是自己所代表的分组。
     */
    override val category: QGCategory
}
