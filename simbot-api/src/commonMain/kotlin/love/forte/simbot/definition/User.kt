/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.definition


/**
 * 可以用来代表一个“用户”的行为对象，例如某个频道中的成员 [Member]。
 *
 * [User] 不一定代表真人，也可能是对应平台下的其他 bot
 * 或者某种默认的系统用户。
 *
 * @author ForteScarlet
 */
public interface User : Actor {
    /**
     * 这个人的名称，或者说昵称。
     * 但是是与任何组织范围无关的名称。
     */
    public val name: String

    /**
     * 这个人的头像链接。
     * 如果没有头像链接、无法获取或者头像信息无法通过
     * [String] 表达则得到 `null`。
     */
    public val avatar: String?
}
