/*
 *  Copyright (c) 2020-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */
@file:JvmName("CoreKeywords")

package love.forte.simboot.filter


/**
 * 一个普通的value值构建为 [Keyword] 实例。
 */
internal class KeywordImpl(override val text: String) : Keyword {
    override val matcherValue: MatcherValue
    override val regex: Regex

    init {
        val regexParameterMatcher = RegexMatcherValue(text)
        matcherValue = regexParameterMatcher
        regex = regexParameterMatcher.regex
    }
}


internal object EmptyFilterParameterMatcher : MatcherValue {
    override val original: String get() = ""
    override val regex: Regex = Regex("")
    override fun matches(text: String): Boolean = false
    override fun getParam(name: String, text: String): String? = null
    override fun getParameters(text: String?): MatchParameters = EmptyFilterParameters
}


public object EmptyKeyword : Keyword {
    override val regex: Regex
        get() = EmptyFilterParameterMatcher.regex

    override val text: String
        get() = ""

    override val matcherValue: MatcherValue
        get() = EmptyFilterParameterMatcher
}


public object EmptyFilterParameters : MatchParameters {
    override fun get(key: String): String? = null
}
