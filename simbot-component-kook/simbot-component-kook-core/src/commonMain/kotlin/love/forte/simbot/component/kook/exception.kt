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

package love.forte.simbot.component.kook

/**
 * 当某个行为对象（例如 [KookMember] ）无法从缓存中获取指定 Guild 时产生的异常。
 *
 * 这通常发生在 [KookGuild] 已经在缓存中被清理（例如删除事件）而仍然有过期的行为对象尝试获取它时产生。
 */
public class KookGuildNotExistsException : NoSuchElementException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
}

/**
 * 通过 [guildId] 构建一个基础的 [KookGuildNotExistsException] 实例。
 */
public fun kookGuildNotExistsException(guildId: String): KookGuildNotExistsException =
    KookGuildNotExistsException("KookGuild(id=$guildId) not exists")

/**
 * 当某个行为对象（例如 [KookGuild] ）无法从缓存中获取指定 Member 时产生的异常。
 *
 * 这通常发生在 [KookMember] 或其对应的 [KookGuild] 已经在缓存中被清理（例如删除事件）而仍然有过期的行为对象尝试获取它时产生。
 */
public class KookMemberNotExistsException : NoSuchElementException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
}

/**
 * 通过 [memberId] 构建一个基础的 [KookMemberNotExistsException] 实例。
 */
public fun kookMemberNotExistsException(memberId: String): KookMemberNotExistsException =
    KookMemberNotExistsException("KookMember(id=$memberId) not exists")
