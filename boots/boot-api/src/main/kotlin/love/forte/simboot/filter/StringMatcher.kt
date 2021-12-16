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

package love.forte.simboot.filter

import love.forte.simboot.Matcher


/**
 * 字符串之间的匹配器。
 */
public fun interface StringMatcher : Matcher<String, String>


/**
 * 常见的字符串匹配器。
 */
public enum class StringMatchers(private val matcher: StringMatcher) :
    StringMatcher by matcher {

    /**
     * 全等匹配
     */
    EQUALS({ t, r -> t == r }),

    /**
     * 忽略大小写的全等匹配
     */
    EQUALS_IGNORE_CASE({ t, r -> t.equals(r, true) }),


    /**
     * 首部匹配
     */
    STARTS_WITH({ t, r -> t.startsWith(r) }),

    /**
     * 尾部匹配.
     *
     */
    ENDS_WITH({ t, r -> t.endsWith(r) }),

    /**
     * 包含匹配.
     *
     */
    CONTAINS({ target, rule -> rule in target }),

}


