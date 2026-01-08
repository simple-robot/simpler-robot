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

package love.forte.simbot.component.qguild.role

import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.suspendrunner.ST


/**
 *
 * [QGRole] 的更新器，通过 [QGRole.updater] 获取。
 *
 *
 * @author ForteScarlet
 */
public interface QGRoleUpdater {

    // DSL API

    /**
     * 名称(非必填)
     */
    public var name: String?

    /**
     * ARGB 的 HEX 十六进制颜色值转换后的十进制数值(非必填)
     */
    public var color: Int?

    /**
     * 在成员列表中单独展示(非必填)
     */
    public var isHoist: Boolean?

    // Java API

    /**
     * 名称(非必填)
     */
    public fun name(value: String): QGRoleUpdater = also { name = value }

    /**
     * ARGB 的 HEX 十六进制颜色值转换后的十进制数值(非必填)
     */
    public fun color(value: Int): QGRoleUpdater = also { color = value }

    /**
     * 在成员列表中单独展示(非必填)
     */
    public fun isHoist(value: Boolean): QGRoleUpdater = also { isHoist = value }

    /**
     * 发起针对当前更新器内 [QGRole] 的更新。
     * 更新完成后会同时刷新内部关联的 [QGRole] 的属性。
     *
     * @throws IllegalArgumentException 没有设置任何修改参数
     * @throws QQGuildApiException API异常
     */
    @ST
    public suspend fun update()
}
