/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.kook.stdlib

import io.ktor.websocket.*
import love.forte.simbot.kook.stdlib.internal.readToTextWithDeflated

/**
 * [BotConfiguration] 中 [BotConfiguration.isCompress] 的默认值，
 * 默认**关闭**，但是**可以开启**，通过 [Frame.Binary.readToTextWithDeflated] 的平台实现支持。
 *
 * @see Frame.Binary.readToTextWithDeflated
 */
public actual const val DEFAULT_COMPRESS: Boolean = false

