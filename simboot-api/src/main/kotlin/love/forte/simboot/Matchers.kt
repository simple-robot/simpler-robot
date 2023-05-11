/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot


/**
 * 一个匹配器，提供一个 [T] 作为被匹配的目标，[R] 为匹配原则/规则，
 * 并得到一个匹配结果。
 */
public fun interface Matcher<T, R> {

    /**
     * 通过匹配规则，对目标进行匹配检测。
     */
    public fun match(target: T, rule: R): Boolean
}

