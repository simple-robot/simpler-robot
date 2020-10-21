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

package love.forte.simbot.filter
/**
 * 关键词参数。代表了对 [love.forte.simbot.core.annotation.Filter.value] 的关键词解析结果。
 */
public interface Keyword {
    /** 匹配关键字对应的正则实例。 */
    val regex: Regex
    /** 匹配关键字对应的原始文本。 */
    val text: String
    /** 匹配关键字对应的参数提取器。 */
    val parameterMatcher: FilterParameterMatcher
}




