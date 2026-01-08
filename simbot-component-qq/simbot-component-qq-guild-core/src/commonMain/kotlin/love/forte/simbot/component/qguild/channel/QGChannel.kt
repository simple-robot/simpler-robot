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

package love.forte.simbot.component.qguild.channel

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.QGObjectiveContainer
import love.forte.simbot.definition.*
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.Channel as QGSourceChannel

/**
 * 一个QQ频道中的子频道 [Channel] 类型，
 * 提供部分来自 [source channel][QGSourceChannel] 的属性获取。
 *
 * [QGChannel] 的主要类型有两个：
 * - [QGTextChannel]
 * - [QGNonTextChannel]
 *
 * 这两个类型分别代表了它们的类型是否属于 [ChannelType.TEXT]。
 *
 * 而 [QGNonTextChannel] 下可能会有更多细分类型，详情参阅其文档注释的说明。
 *
 * @author ForteScarlet
 */
public interface QGChannel : CoroutineScope, QGObjectiveContainer<QGSourceChannel>, Channel {
    /**
     * 原始的子频道信息
     */
    override val source: QGSourceChannel

    /**
     * 子频道ID
     */
    override val id: ID
        get() = source.id.ID

    /**
     * 子频道名称
     */
    override val name: String
        get() = source.name

    /**
     * 子频道分组**的ID**。
     *
     * 子频道分组ID实例 [QGCategory] 是一个仅包含 `id` 信息的未初始化实例，
     * 其 [id][QGCategory.id] 和 [name][QGCategory.name]
     * 的值都是 [source.parentId][love.forte.simbot.qguild.model.Channel.parentId] 的值，
     * 即分组ID的字符串值。
     *
     * 如果希望获取完整信息，使用 [QGCategory.resolveToChannel].
     *
     * @see QGCategory
     */
    override val category: QGCategory
}
