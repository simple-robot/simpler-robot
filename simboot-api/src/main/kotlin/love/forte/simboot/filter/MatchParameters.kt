/*
 * Copyright (c) 2020-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */
package love.forte.simboot.filter

/**
 * 匹配器动态参数获取器
 * @author ForteScarlet
 */
public interface MatchParameters {

    /**
     * 根据指定参数名称获取对应的提取参数。
     * @param key Key
     * @return Value or null.
     */
    public operator fun get(key: String): String?
}
