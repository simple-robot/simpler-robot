/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */
@file:JvmName("CoreKeywords")
package love.forte.simbot.core.filter

import love.forte.simbot.filter.FilterParameterMatcher
import love.forte.simbot.filter.Keyword
import java.util.regex.Pattern


/**
 * 一个普通的value值构建为 [Keyword] 实例。
 */
public class TextKeyword(override val text: String) : Keyword {
    override val regex: Regex = Regex(text)
    override val parameterMatcher: FilterParameterMatcher = CoreFilterParameterMatcher.compile(text)
}


internal val EmptyRegex = Regex("")


internal object EmptyFilterParameterMatcher : FilterParameterMatcher {
    override fun getOriginal(): String = ""
    override fun getPattern(): Pattern = EmptyRegex.toPattern()
    override fun getParam(name: String?, text: String?): String? = null
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


