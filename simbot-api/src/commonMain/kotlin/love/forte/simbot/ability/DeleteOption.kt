/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.ability

/**
 * [DeleteSupport.delete] 的可选选项。
 *
 * [DeleteOption] 可以自由扩展，且如果遇到不支持的实现则会将其忽略。
 * 但是所有 [DeleteSupport.delete] 都应当尽可能支持 [StandardDeleteOption]
 * 中提供的标准选项，并在不支持某些标准选项的时候提供相关的说明。
 *
 * @see StandardDeleteOption
 */
public interface DeleteOption
