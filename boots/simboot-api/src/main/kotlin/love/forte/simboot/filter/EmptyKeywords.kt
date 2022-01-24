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

public object EmptyFilterParameterMatcher : MatcherValue {
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
