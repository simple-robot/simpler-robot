/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Keyword.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.filter
/**
 * 关键词参数。代表了对 [love.forte.simbot.core.annotation.Filter.value] 的关键词解析结果。
 */
public interface Keyword {
    val regex: Regex
    val text: String
    val parameterMatcher: FilterParameterMatcher
}




