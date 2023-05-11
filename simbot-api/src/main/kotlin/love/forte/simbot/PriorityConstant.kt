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

package love.forte.simbot

/**
 * 优先级常量类。
 */
@Suppress("MemberVisibilityCanBePrivate")
public object PriorityConstant {

    // 前九个 "较为优先" 级别。
    // 优先级次序为 PRIORITIZED_1 > PRIORITIZED_2 > ... > PRIORITIZED_9
    
    public const val PRIORITIZED_1: Int = 10
    public const val PRIORITIZED_2: Int = PRIORITIZED_1 + 10
    public const val PRIORITIZED_3: Int = PRIORITIZED_2 + 10
    public const val PRIORITIZED_4: Int = PRIORITIZED_3 + 10
    public const val PRIORITIZED_5: Int = PRIORITIZED_4 + 10
    public const val PRIORITIZED_6: Int = PRIORITIZED_5 + 10
    public const val PRIORITIZED_7: Int = PRIORITIZED_6 + 10
    public const val PRIORITIZED_8: Int = PRIORITIZED_7 + 10
    public const val PRIORITIZED_9: Int = PRIORITIZED_8 + 10

    /**
     * 普通地默认值。存在优先级属性相关的内容，默认使用次值。
     */
    public const val NORMAL: Int = 500

    /**
     * 最高的优先级.
     *
     * 常量中，最高优先级定义为 0，而不是 [Int.MIN_VALUE].
     */
    public const val FIRST: Int = 0

    /**
     * 最低的优先级.
     *
     * 其优先级会比 [Int.MAX_VALUE] 高。
     */
    public const val LAST: Int = Int.MAX_VALUE - 100

    // 后九个 "较为次后" 级别。
    // 优先级次序为 AFTER_1 > AFTER_2 > ... > AFTER_9

    public const val AFTER_1: Int = 1100
    public const val AFTER_2: Int = AFTER_1 + 10
    public const val AFTER_3: Int = AFTER_2 + 10
    public const val AFTER_4: Int = AFTER_3 + 10
    public const val AFTER_5: Int = AFTER_4 + 10
    public const val AFTER_6: Int = AFTER_5 + 10
    public const val AFTER_7: Int = AFTER_6 + 10
    public const val AFTER_8: Int = AFTER_7 + 10
    public const val AFTER_9: Int = AFTER_8 + 10
}
