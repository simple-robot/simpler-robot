/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

@file:JvmName("AvatarUtil")

package love.forte.simbot.component.onebot.v11.common.utils

import love.forte.simbot.component.onebot.common.annotations.InternalOneBotAPI
import kotlin.jvm.JvmName

/**
 * 得到 `s=640` 的QQ头像。
 *
 * @param id QQ号
 */
@InternalOneBotAPI
public fun qqAvatar640(id: String): String =
    "https://q1.qlogo.cn/g?b=qq&nk=$id&s=640"

/**
 * 得到 `s=100` 的QQ头像。
 *
 * @param id QQ号
 */
@InternalOneBotAPI
public fun qqAvatar100(id: String): String =
    "https://q1.qlogo.cn/g?b=qq&nk=$id&s=100"
