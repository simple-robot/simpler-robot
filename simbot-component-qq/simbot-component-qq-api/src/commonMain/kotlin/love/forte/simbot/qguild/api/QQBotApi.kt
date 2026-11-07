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

package love.forte.simbot.qguild.api

/**
 * 一个 QQ Bot API。
 *
 * 在早期，QQ 官方 API 仅支持 QQ 频道相关的功能，因此整个组件当时仅面向 QQ 频道。
 * 但是后来官方直接在原本的 API 基础上扩充了有关 QQ 群聊、C2C 单聊等相关的内容。
 * 为了兼容性，类型命名风格、包路径都仍然延续了之前使用的 `QG` 的风格，并适当提供部分语意更准确的类型别名。
 *
 * @see QQGuildApi
 * @since 5.0
 */
public typealias QQBotApi<R> = QQGuildApi<R>

/**
 * 没有返回值的 [QQBotApi] 子类型。
 *
 * @see QQBotApi
 * @see QQGuildApiWithoutResult
 * @since 5.0
 */
public typealias QQBotApiWithoutResult = QQGuildApiWithoutResult

/**
 * 请求方式为 GET 的 [QQBotApi] 子类型。
 *
 * @see QQBotApi
 * @since 5.0
 */
public typealias GetQQBotApi<R> = GetQQGuildApi<R>

/**
 * 请求方式为 POST 的 [QQBotApi] 子类型。
 *
 * @see QQBotApi
 * @since 5.0
 */
public typealias PostQQBotApi<R> = PostQQGuildApi<R>

/**
 * 请求方式为 PUT 的 [QQBotApi] 子类型。
 *
 * @see QQBotApi
 * @since 5.0
 */
public typealias PutQQBotApi<R> = PutQQGuildApi<R>

/**
 * 请求方式为 DELETE 的 [QQBotApi] 子类型。
 *
 * @see QQBotApi
 * @since 5.0
 */
public typealias DeleteQQBotApi<R> = DeleteQQGuildApi<R>

/**
 * 请求方式为 PATCH 的 [QQBotApi] 子类型。
 *
 * @see QQBotApi
 * @since 5.0
 */
public typealias PatchQQBotApi<R> = PatchQQGuildApi<R>
