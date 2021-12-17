/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
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

