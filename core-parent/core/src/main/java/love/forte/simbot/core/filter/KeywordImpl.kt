/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     KeywordImpl.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.filter

import java.util.regex.Pattern


/**
 * 一个普通的value值构建为 [Keyword] 实例。
 */
public class TextKeyword(text: String) : Keyword {
    override val regex: Regex = Regex(text)
    override val text: String = text
    override val parameterMatcher: FilterParameterMatcher = FilterParameterMatcherImpl.compile(text)
}


internal val EmptyRegex = Regex("")


internal object EmptyFilterParameterMatcher : FilterParameterMatcher {
    override fun getOriginal(): String = ""
    override fun getPattern(): Pattern = EmptyRegex.toPattern()
    override fun getParams(text: String?): MutableMap<String, String> = mutableMapOf()
}


public object EmptyKeyword : Keyword {
    override val regex: Regex
        get() = EmptyRegex
    override val text: String
        get() = ""
    override val parameterMatcher: FilterParameterMatcher
        get() = EmptyFilterParameterMatcher
}


