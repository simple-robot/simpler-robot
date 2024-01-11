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

package love.forte.simbot.common

/**
 * 优先级常量类。
 *
 * 优先级以 `0` 为中心向两侧扩张，
 * 负数值越小越趋近于 “最优先”，直到达到 [Int.MIN_VALUE];
 * 正数值越大越趋近于 “最滞后”，直到达到 [Int.MAX_VALUE]。
 *
 * 分别提供正负9个“较为优先/滞后”的值，各自从 `100`/`-100`向正/负延伸。
 *
 * 默认的优先级为 [DEFAULT] 也就是 `0`。
 *
 * 优先级次序为：
 * ```
 *               <<<<---- 更优先
 * Int.MIN_VALUE
 * -> ...
 *   -> PRIORITIZE_1
 *     -> PRIORITIZE_2
 *       -> ...
 *         -> PRIORITIZE_9
 *           -> DEFAULT
 *             -> DE_PRIORITIZE_1
 *               -> DE_PRIORITIZE_2
 *                 -> ...
 *                   -> DE_PRIORITIZE_9
 *                     -> ...
 *                       -> Int.MAX_VALUE
 *        更滞后 ---->>>>
 * ```
 *
 */
@Suppress("MemberVisibilityCanBePrivate")
public object PriorityConstant {

    // 前九个 "较为优先" 级别。
    // 优先级次序为 PRIORITIZED_1 > PRIORITIZED_2 > ... > PRIORITIZED_9

    /**
     * 第9个 “较为” 优先的值: `-100`
     */
    public const val PRIORITIZE_9: Int = -100

    /**
     * 第8个 “较为” 优先的值: `-200`
     */
    public const val PRIORITIZE_8: Int = -200

    /**
     * 第7个 “较为” 优先的值: `-300`
     */
    public const val PRIORITIZE_7: Int = -300

    /**
     * 第6个 “较为” 优先的值: `-400`
     */
    public const val PRIORITIZE_6: Int = -400

    /**
     * 第5个 “较为” 优先的值: `-500`
     */
    public const val PRIORITIZE_5: Int = -500

    /**
     * 第4个 “较为” 优先的值: `-600`
     */
    public const val PRIORITIZE_4: Int = -600

    /**
     * 第3个 “较为” 优先的值: `-700`
     */
    public const val PRIORITIZE_3: Int = -700

    /**
     * 第2个 “较为” 优先的值: `-800`
     */
    public const val PRIORITIZE_2: Int = -800

    /**
     * 第1个 “较为” 优先的值: `-900`
     */
    public const val PRIORITIZE_1: Int = -900

    /**
     * 默认值，整个优先级常量的中心 `0`。
     */
    public const val DEFAULT: Int = 0

    // 后九个 "较为滞后" 级别。
    // 优先级次序为 DE_PRIORITIZE_1 > DE_PRIORITIZE_2 > ... > DE_PRIORITIZE_9

    /**
     * 第1个“较为”滞后的优先级，值为 `100`
     */
    public const val DE_PRIORITIZE_1: Int = 100

    /**
     * 第2个“较为”滞后的优先级，值为 `200`
     */
    public const val DE_PRIORITIZE_2: Int = 200

    /**
     * 第3个“较为”滞后的优先级，值为 `300`
     */
    public const val DE_PRIORITIZE_3: Int = 300

    /**
     * 第4个“较为”滞后的优先级，值为 `400`
     */
    public const val DE_PRIORITIZE_4: Int = 400

    /**
     * 第5个“较为”滞后的优先级，值为 `500`
     */
    public const val DE_PRIORITIZE_5: Int = 500

    /**
     * 第6个“较为”滞后的优先级，值为 `600`
     */
    public const val DE_PRIORITIZE_6: Int = 600

    /**
     * 第7个“较为”滞后的优先级，值为 `700`
     */
    public const val DE_PRIORITIZE_7: Int = 700

    /**
     * 第8个“较为”滞后的优先级，值为 `800`
     */
    public const val DE_PRIORITIZE_8: Int = 800

    /**
     * 第9个“较为”滞后的优先级，值为 `900`
     */
    public const val DE_PRIORITIZE_9: Int = 900
}
