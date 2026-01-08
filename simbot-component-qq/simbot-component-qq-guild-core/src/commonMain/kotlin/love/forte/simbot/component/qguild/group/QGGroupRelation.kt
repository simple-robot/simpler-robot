/*
 * Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.group

import love.forte.simbot.bot.GroupRelation
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.emptyCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.suspendrunner.ST
import kotlin.jvm.JvmSynthetic


/**
 * QQ组件中与QQ群相关的操作。
 * QQ群实际上没有开放任何相关能力的API，因此：
 * - 列表获取将永远得到空结果。
 * - ID查询将是一种“伪”操作，会直接将提供的 `id` 包装为一个 [QGGroup] 返回。
 * 如果此ID不是真实存在的，则进行某些操作时（比如主动发送消息）将会抛出异常。
 *
 * @author ForteScarlet
 */
public interface QGGroupRelation : GroupRelation {

    /**
     * 无法获取列表，将始终得到空结果。
     */
    override val groups: Collectable<QGGroup>
        get() = emptyCollectable()

    /**
     * 使用 [id] 直接包装为一个伪 [QGGroup]，
     * **不会**校验其真实性。
     * 如果此ID对应的群不是真实存在的，
     * 则进行某些操作时（比如主动发送消息）将会抛出异常。
     */
    @ST(
        blockingBaseName = "getGroup",
        blockingSuffix = "",
        asyncBaseName = "getGroup",
        reserveBaseName = "getGroup"
    )
    override suspend fun group(id: ID): QGGroup?

    /**
     * 无法得知已加入的群的总数，始终得到 `-1`。
     */
    @JvmSynthetic
    override suspend fun groupCount(): Int = -1
}
