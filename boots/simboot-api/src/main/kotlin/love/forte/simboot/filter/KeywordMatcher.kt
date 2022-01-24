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

package love.forte.simboot.filter

import love.forte.simboot.Matcher

/**
 * 使用 [Keyword] 作为匹配规则的匹配器。
 * @author ForteScarlet
 */
public fun interface KeywordMatcher : Matcher<String, Keyword>


/**
 * 使用 keyword 进行的正则匹配.
 *
 * 如果你想使用 [StringMatcher] 作为 [KeywordMatcher] 使用，参考 [asKeywordMatcher].
 *
 */
public enum class KeywordRegexMatchers(private val matcher: KeywordMatcher) :
    KeywordMatcher by matcher {
    /**
     * 完整正则匹配
     */
    MATCHES({ t, r -> r.regex.matches(t) }),

    /**
     * 包含正则匹配
     */
    CONTAINS({ t, r -> r.regex.containsMatchIn(t) }),

}


public fun StringMatcher.toKeywordMatcher(): KeywordMatcher = KeywordMatcher { t: String, r: Keyword -> match(t, r.text) }
