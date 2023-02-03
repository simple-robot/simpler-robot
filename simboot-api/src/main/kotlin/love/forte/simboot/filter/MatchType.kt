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

/**
 *
 * @author ForteScarlet
 */
public enum class MatchType(
    /**
     * Keyword匹配器。
     */
    private val matcher: KeywordMatcher,

    /**
     * 代表此匹配模式是否为纯文本匹配而并非通过正则的匹配。
     */
    public val isPlainText: Boolean
) : KeywordMatcher by matcher {
    /**
     * 全等匹配
     *
     * @see StringMatchers.EQUALS
     */
    TEXT_EQUALS(StringMatchers.EQUALS, true),

    /**
     * 忽略大小写的全等匹配
     *
     * @see StringMatchers.EQUALS_IGNORE_CASE
     */
    TEXT_EQUALS_IGNORE_CASE(StringMatchers.EQUALS_IGNORE_CASE, true),


    /**
     * 首部匹配
     *
     * @see StringMatchers.STARTS_WITH
     */
    TEXT_STARTS_WITH(StringMatchers.STARTS_WITH, true),

    /**
     * 尾部匹配.
     *
     * @see StringMatchers.ENDS_WITH
     */
    TEXT_ENDS_WITH(StringMatchers.ENDS_WITH, true),

    /**
     * 包含匹配.
     *
     * @see StringMatchers.CONTAINS
     */
    TEXT_CONTAINS(StringMatchers.CONTAINS, true),

    /**
     * 正则完全匹配. `regex.matches(...)`
     *
     * @see KeywordRegexMatchers.MATCHES
     */
    REGEX_MATCHES(KeywordRegexMatchers.MATCHES, false),


    /**
     * 正则包含匹配. `regex.find(...)`
     *
     * @see KeywordRegexMatchers.CONTAINS
     */
    REGEX_CONTAINS(KeywordRegexMatchers.CONTAINS, false),

    ;

    constructor(matcher: StringMatcher, isPlainText: Boolean) : this(matcher.toKeywordMatcher(), isPlainText)

}
