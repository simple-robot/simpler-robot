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

/**
 *
 * @author ForteScarlet
 */
public enum class MatchType(
    private val matcher: KeywordMatcher
) : KeywordMatcher by matcher {
    /**
     * 全等匹配
     *
     * @sample StringMatchers.EQUALS
     */
    TEXT_EQUALS(StringMatchers.EQUALS),

    /**
     * 忽略大小写的全等匹配
     *
     * @sample StringMatchers.EQUALS_IGNORE_CASE
     */
    TEXT_EQUALS_IGNORE_CASE(StringMatchers.EQUALS_IGNORE_CASE),


    /**
     * 首部匹配
     *
     * @sample StringMatchers.STARTS_WITH
     */
    TEXT_STARTS_WITH(StringMatchers.STARTS_WITH),

    /**
     * 尾部匹配.
     *
     * @sample StringMatchers.ENDS_WITH
     */
    TEXT_ENDS_WITH(StringMatchers.ENDS_WITH),

    /**
     * 包含匹配.
     *
     * @sample StringMatchers.CONTAINS
     */
    TEXT_CONTAINS(StringMatchers.CONTAINS),

    /**
     * 正则完全匹配. `regex.matches(...)`
     *
     * @sample KeywordRegexMatchers.MATCHES
     */
    REGEX_MATCHES(KeywordRegexMatchers.MATCHES),


    /**
     * 正则包含匹配. `regex.find(...)`
     *
     * @sample KeywordRegexMatchers.CONTAINS
     */
    REGEX_CONTAINS(KeywordRegexMatchers.CONTAINS),

    ;

    constructor(matcher: StringMatcher) : this(matcher.toKeywordMatcher())

}