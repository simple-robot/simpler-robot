/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot

/**
 * 优先级常量类。
 */
@Suppress("MemberVisibilityCanBePrivate")
public object PriorityConstant {

    // 前九个 "较为优先"级别。

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
     * 普通的默认值。存在优先级属性相关的内容，默认使用次值。
     */
    public const val NORMAL: Int = 500

    /**
     * 常量中，最高优先级定义为 0，而不是 [Int.MIN_VALUE].
     */
    public const val FIRST: Int = 0

    /**
     * 最后的优先级。其优先级比 [Int.MAX_VALUE] 优先级会高。
     */
    public const val LAST: Int = Int.MAX_VALUE - 100

    // 后九个 "较为次后"级别。

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