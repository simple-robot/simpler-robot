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

package love.forte.simbot.component.qguild.bot

/**
 * 一个 QQ 机器人。
 *
 * 在早期，QQ 官方 API 仅支持 QQ 频道相关的功能，因此整个组件当时仅面向 QQ 频道。
 * 但是后来官方直接在原本的 API 基础上扩充了有关 QQ 群聊、C2C 单聊等相关的内容，
 * 因此 QGBot 已经不在仅仅承载频道相关的内容了，但是为了兼容性，类型命名风格、包路径都仍然延续了之前使用的 `QG` 的风格。
 *
 * @since 5.0
 */
public typealias QQBot = QGBot
