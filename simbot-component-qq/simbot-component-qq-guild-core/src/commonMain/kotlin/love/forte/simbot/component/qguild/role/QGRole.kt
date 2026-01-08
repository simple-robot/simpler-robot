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

package love.forte.simbot.component.qguild.role

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.QGObjectiveContainer
import love.forte.simbot.definition.Role
import love.forte.simbot.qguild.model.Role.Companion.isDefault
import love.forte.simbot.qguild.model.Role as QGSourceRole

/**
 * QQ频道中所使用的基础角色
 * （[身份组](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html)）
 * 类型。
 *
 * 对于角色所处环境的不同 [QGRole] 分为 [QGGuildRole] 和 [QGMemberRole] 两个子类型。
 *
 * @see QGGuildRole
 * @see QGMemberRole
 *
 * @author ForteScarlet
 */
@ExperimentalQGApi
public interface QGRole : Role, CoroutineScope, QGObjectiveContainer<QGSourceRole> {
    /**
     * 身份组ID
     */
    override val id: ID

    /**
     * 角色名称
     */
    override val name: String get() = source.name

    /**
     * 判断是拥有管理员权限。
     *
     * 判断标准为是某个 [默认角色][QGSourceRole.isDefault]
     * 且不是 [全体成员][QGSourceRole.DEFAULT_ID_ALL_MEMBER]。
     *
     */
    override val isAdmin: Boolean get() = source.isDefault && source.id != QGSourceRole.DEFAULT_ID_ALL_MEMBER

    /**
     * 原始的身份组对象。
     */
    override val source: QGSourceRole

    /**
     * 当前角色所属频道服务器ID。
     */
    public val guildId: ID

    /**
     * ARGB的HEX十六进制颜色值转换后的十进制数值。
     * 当不支持获取时（例如当前身份组信息是离线构造出来的默认类型）则可能会得到一个默认值 `0`。
     */
    public val color: Int get() = source.color

    /**
     * 是否在成员列表中单独展示。
     * 当不支持获取时（例如当前身份组信息是离线构造出来的默认类型）则可能会得到一个默认值 `false`。
     */
    public val isHoist: Boolean get() = source.isHoistBool

    /**
     * 人数。
     * 当不支持获取时（例如当前身份组信息是离线构造出来的默认类型）则可能会得到一个默认值 `-1`。
     */
    public val number: Int get() = source.number

    /**
     * 成员上限。
     * 当不支持获取时（例如当前身份组信息是离线构造出来的默认类型）则可能会得到一个默认值 `-1`。
     */
    public val memberLimit: Int get() = source.memberLimit

    /**
     * 基于当前 [QGRole] 得到一个 [QGRoleUpdater]。
     *
     * 得到的 [QGRoleUpdater] 会与当前角色对象关联，当 [QGRoleUpdater.update]
     * 成功时会同时刷新当前对象内的对应属性。
     * 此刷新只会对当前对象实例和其可能关联的 [QGMemberRole] (对 [QGGuildRole] 来说) 有效。
     *
     */
    public fun updater(): QGRoleUpdater
}

/**
 * 更新指定的 [QGRole] 信息。
 *
 */
@ExperimentalQGApi
public suspend inline fun QGRole.update(block: QGRoleUpdater.() -> Unit) {
    updater().also(block).update()
}
