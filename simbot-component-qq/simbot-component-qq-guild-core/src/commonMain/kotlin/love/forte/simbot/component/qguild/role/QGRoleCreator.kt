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

import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.suspendrunner.ST


/**
 *
 * [QGRole] 的构造器，用于在指定的频道下创建一个身份组。
 * 通过 [QGGuild.roleCreator] 获取。
 *
 * @author ForteScarlet
 */
@ExperimentalQGApi
public interface QGRoleCreator {

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
    public fun name(value: String): QGRoleCreator = also { name = value }

    /**
     * ARGB 的 HEX 十六进制颜色值转换后的十进制数值(非必填)
     */
    public fun color(value: Int): QGRoleCreator = also { color = value }

    /**
     * 在成员列表中单独展示(非必填)
     */
    public fun isHoist(value: Boolean): QGRoleCreator = also { isHoist = value }

    /**
     * 构造一个 [QGRole]
     *
     * @throws IllegalArgumentException 没有设置任何参数
     * @throws QQGuildApiException API异常
     */
    @ST
    public suspend fun create(): QGGuildRole
}
