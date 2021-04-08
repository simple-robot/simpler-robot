/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     RegexFilterParameterMatcher.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

@file:JvmName("RegexFilterParameterMatchers")

package love.forte.simbot.core.filter

import love.forte.simbot.filter.FilterParameterMatcher
import love.forte.simbot.filter.FilterParameters
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * 正则参数提取器。
 */
public class RegexFilterParameterMatcher(private val originalValue: String) : FilterParameterMatcher {

    private val regex: Regex by lazy {
        val regexValue = originalValue.toDynamicParametersRegexValue()
        println("regex value: $regexValue")
        Regex(regexValue)
    }

    private val _pattern: Pattern get() = regex.toPattern()


    /**
     * 获取原始字符串
     * @return 原始字符串
     */
    override fun getOriginal(): String = originalValue

    /**
     * 获取用于匹配的正则
     * @return 匹配正则
     */
    override fun getPattern(): Pattern = _pattern


    override fun matches(text: String): Boolean = regex.containsMatchIn(text)

    /**
     * 根据变量名称获取一个动态参数。
     * 此文本需要符合正则表达式。
     * @param name 变量名称
     * @param text 文本
     * @return 得到的参数
     */
    override fun getParam(name: String, text: String): String? {
        return getParameters(text)[name]
    }

    /**
     * 从一段匹配的文本中提取出需要的参数。
     * 此文本需要符合正则表达式。
     * @param text 匹配的文本
     * @return 得到的参数
     */
    @Deprecated("Use getParameters()", ReplaceWith("getParameters()"))
    override fun getParams(text: String?): Map<String, String> {
        return emptyMap()
    }

    /**
     * 从一段匹配的文本中提取出需要的参数。
     *
     * 此文本需要符合正则表达式, 否则得到null。
     *
     *
     * TODO 需要考虑优化。
     * @param text 匹配文本
     * @return 参数提取器。
     */
    override fun getParameters(text: String?): FilterParameters {
        return text?.let { t -> MatcherParameters(pattern.matcher(t)) } ?: EmptyFilterParameters
    }
}


/**
 * [Matcher] 对应的匹配值Map。
 */
private class MatcherParameters(private val matcher: Matcher) : FilterParameters {

    init {
        matcher.find()
    }

    /**
     * 根据指定参数名称获取对应的提取参数。
     * @param key
     * @return
     */
    override fun get(key: String): String? = matcher.group(key)
}


/**
 * 将一个可能携带动态参数的正则字符串转化为普通的正则字符串。
 *
 *
 * for example:
 * `xxx{{name,\\d+}}` -> `xxx(?<name>\\d+)`
 *
 *
 */
private fun String.toDynamicParametersRegexValue(): String {
    val builder = StringBuilder(length)
    val temp = StringBuilder(16)
    // 忽略本次（上次为转义符
    var ignoreThis = false
    // 上一个也是 {
    var last: Char? = null
    // 正在获取
    var on = false
    val iter = this.iterator()

    var startNum = 0

    while (iter.hasNext()) {
        val c = iter.nextChar()
        if (on) {
            // if ignore
            if (ignoreThis) {
                ignoreThis = false
                temp.append(c)
                last = c
                continue
            }
            // if is end.
            if (c == '{') {
                startNum++
                temp.append(c)
                last = c

            } else if (c == '}') {
                if (startNum == 0) {
                    // if next also
                    if (iter.hasNext() && iter.nextChar() == '}') {
                        builder.append(temp.toString().dynamicParameters())
                        temp.clear()
                        on = false
                    }
                } else {
                    startNum--
                    temp.append(c)
                    last = c
                }
            } else {
                temp.append(c)
                last = c
            }

        } else {
            if (ignoreThis) {
                ignoreThis = false
                builder.append(c)
                last = c
                continue
            }
            if (c == '}') {
                startNum--
                builder.append(c)
                last = c
            } else if (c == '{') {
                if (startNum == 0) {
                    // if next also
                    if (iter.hasNext() && iter.nextChar() == '{') {
                        on = true
                    }
                } else {
                    startNum++
                    builder.append(c)
                    last = c
                }
            } else {
                builder.append(c)
                last = c
            }
        }
    }

    if (on) {
        throw IllegalStateException("There is no end flag for dynamic parameters.")
    }


    return builder.toString()
}


/**
 * 将动态参数中的内容转化为正则分组的格式。
 * `name[,regex]` -> `(?<name>regex)`
 */
private fun String.dynamicParameters(): String {
    val sp = split(',', limit = 2)
    val name: String = sp[0]
    val regex: String = sp.takeIf { s -> s.size > 1 }?.get(1) ?: ".+"

    return "(?<$name>$regex)"

}