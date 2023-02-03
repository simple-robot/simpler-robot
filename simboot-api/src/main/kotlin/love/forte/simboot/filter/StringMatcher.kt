/*
 * Copyright (c) 2021-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
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


