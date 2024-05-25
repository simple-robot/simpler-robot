/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.quantcat.common.binder

/**
 * Binder管理器。
 */
public interface BinderManager {

    /**
     * 普通的binder工厂的数量。
     */
    public val normalBinderFactorySize: Int

    /**
     * 全局性的binder工厂的数量。
     */
    public val globalBinderFactorySize: Int

    /**
     * 根据ID获取一个指定的普通binder工厂。
     */
    public operator fun get(id: String): ParameterBinderFactory?

    /**
     * 获取所有的全局binder工厂。
     */
    public val globals: List<ParameterBinderFactory>
}
